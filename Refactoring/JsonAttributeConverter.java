package com.adobe.prj.util;

import java.io.IOException;

import javax.persistence.AttributeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * JPA converter class which converts any POJO like object to a JSON string and
 * stores it in the database table column. The JSON string contains the class
 * name of the POJO and its JSON format string. <br/>
 *
 * Note: An abstract class is not needed as this class takes care of all the
 * JSON serialization and deserialization. You only need to extend this class so
 * that you can use your POJO class for conversion. The extended class should be
 * annotated with the {@literal @}Converter annotation
 *
 * @author Sunit Katkar, sunitkatkar@gmail.com
 *         (https://sunitkatkar.blogspot.com/)
 * @since ver 1.0 (May 2018)
 * @version 1.0
 * @param <X>
 */

 /*Refactoring generale
 	Migliorata l'indentazione delle funzioni per evitare eccessiva Horizontal Length
 	Indentate le parentesi con maggiore efficacia per una migliore lettura
	Cambiato il nome da GenericJsonAttributeConverter a JsonAttributeConverter
 */
public class JsonAttributeConverter<X> implements AttributeConverter<X, String> {

    protected static final Logger LOG =
            LoggerFactory.getLogger(JsonAttributeConverter.class);
		/**
		 * To 'write' the attribute POJO as a JSON string
		 */
    private final ObjectWriter writer;
		/**
		 * To 'read' the JSON string and convert to the attribute POJO
		 */
    private final ObjectReader reader;

		//Refactoring
		//Rimossi noise comments
    public JsonAttributeConverter() {

        ObjectMapper mapper = new ObjectMapper();

        mapper.setSerializationInclusion(Include.ALWAYS);

        mapper.setVisibility(
                mapper.getSerializationConfig()
                      .getDefaultVisibilityChecker()
                      .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                      .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                      .withIsGetterVisibility(
                              JsonAutoDetect.Visibility.NONE)
        );

        reader = mapper.reader().forType(JsonTypedWrapper.class);
        writer = mapper.writer().forType(JsonTypedWrapper.class);
    }

    //Refactor
    //Rimosso Redundant Comment
    @Override
		//Refactoring
		//spostato attribute == null all'inizio per efficienza e maggiore lettura
    public String convertToDatabaseColumn(X attribute) {

        if (attribute == null) {
            return null;
        }

        try {
            JsonTypedWrapper<X> wrapper =
                    new JsonTypedWrapper<>(attribute, writer);
            return writer.writeValueAsString(wrapper);
        } catch (JsonProcessingException e) {
            LOG.error("Failed to serialize object into JSON: {}",attribute,e);
            throw new RuntimeException(e);
        }
    }

 		//Refactoring
 		//spostato attribute == null all'inizio per efficienza e maggiore lettura
    //cambiato convertToEntityAttribute in converDBtColumnIntoAttributex
    public X convertDBColumnIntoAttribute(String dbData) {

        if (dbData == null) {
            return null;
        }

        try {
            JsonTypedWrapper<X> wrapper =
                    reader.readValue(dbData);
            return wrapper.readValue(reader);
        } catch (IOException e) {
            LOG.error("Failed to deserialize object from JSON: {}",
                    dbData,e);
            throw new RuntimeException(e);
        }
    }

  	/**
  	 * The concrete type is needed for Jackson to serialize or deserialize. This
  	 * class is created to wrap the entity type &lt;Y&gt; so that the Jackson
  	 * {@link ObjectReader#forType(Class)} and {@link ObjectWriter#forType(Class))}
  	 * can be used to get the concrete type of the attribute being
  	 * serialized/deserialized.
  	 *
  	 * @author Sunit Katkar, sunitkatkar@gmail.com
  	 *
  	 * @param <Y>
  	 */
		//Refactoring
		//Rinominato JsonTypeLike in JsonTypedWrapper
		//Rimossi i noise comment
    public static class JsonTypedWrapper<Y> {

        private String entityType;
        private String entityValue;

        public JsonTypedWrapper() {
        }


				/**
				 * Constructor which helps initialize the ObjectWriter by providing the concrete
				 * class type to the writer
				 *
				 * @param obj
				 * @param writer
				 */
        @SuppressWarnings("unchecked")
        public JsonTypedWrapper(Y obj, ObjectWriter writer) {

            Class<Y> classType = (Class<Y>) obj.getClass();
            this.entityType = classType.getName();

            try {
                this.entityValue =
                        writer.forType(classType)writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                LOG.error("Failed serializing object to JSON: {}",obj,e);
            }
        }

				/**
				 * Read the JSON format string and create the target Java POJO object
				 *
				 * @param reader
				 * @return
				 */
				//Refactoring
				//destinationClassType invece di clazz
        public Y readValue(ObjectReader reader) {

            try {
                Class<?> destinationClassType = Class.forName(this.entityType);
                return reader.forType(destinationClassType).readValue(this.entityValue);
            } catch (ClassNotFoundException | IOException e) {
                LOG.error("Failed deserializing object from JSON: {}",this.entityValue,e);
                return null;
            }
        }

        public String getEntityType() {
            return entityType;
        }

        public void setEntityType(String entityType) {
            this.entityType = entityType;
        }

        public String getEntityValue() {
            return entityValue;
        }

        public void setEntityValue(String entityValue) {
            this.entityValue = entityValue;
        }
    }
}
