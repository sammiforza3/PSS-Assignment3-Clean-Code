package com.adobe.prj.entity;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;

/**
 * This is the class containing details of Address added by the employee.
 */
public class Address implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotEmpty(message = "Address Line 1 cannot be empty")
	private String addressLine1;

	private String addressLine2;

	@NotEmpty(message = "City cannot be empty")
	private String city;

	@NotEmpty(message = "State cannot be empty")
	private String state;

	@NotEmpty(message = "Country cannot be empty")
	private String country;

	@NotEmpty(message = "Zipcode cannot be empty")
	private String zipcode;

	@NotEmpty(message = "Telephone 1 cannot be empty")
	private String telephone1;

	private String telephone2;

	private String fax;

	
	public String getAddressLine1() {
		return addressLine1;
	}

	
	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	
	public String getAddressLine2() {
		return addressLine2;
	}

	
	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	
	public String getCity() {
		return city;
	}

	
	public void setCity(String city) {
		this.city = city;
	}


	public String getState() {
		return state;
	}

	
	public void setState(String state) {
		this.state = state;
	}

	
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	
	public String getZipcode() {
		return zipcode;
	}

	
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	
	public String getTelephone1() {
		return telephone1;
	}

	
	public void setTelephone1(String telephone1) {
		this.telephone1 = telephone1;
	}

	
	public String getTelephone2() {
		return telephone2;
	}


	public void setTelephone2(String telephone2) {
		this.telephone2 = telephone2;
	}

	
	public String getFax() {
		return fax;
	}

	
	public void setFax(String fax) {
		this.fax = fax;
	}

	
	public Address(String addressLine1, String addressLine2, String city, String state, String country, String zipcode,
			String telephone1, String telephone2, String fax) {
		super();
		this.addressLine1 = addressLine1;
		this.addressLine2 = addressLine2;
		this.city = city;
		this.state = state;
		this.country = country;
		this.zipcode = zipcode;
		this.telephone1 = telephone1;
		this.telephone2 = telephone2;
		this.fax = fax;
	}

	
	public Address() {

	}

	
	@Override
	public String toString() {
		return "Address [addressLine1=" + addressLine1 + ", addressLine2=" + addressLine2 + ", city=" + city
				+ ", state=" + state + ", country=" + country + ", zipcode=" + zipcode + ", telephone1=" + telephone1
				+ ", telephone2=" + telephone2 + ", fax=" + fax + "]";
	}

}
