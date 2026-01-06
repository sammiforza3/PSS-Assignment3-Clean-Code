package com.adobe.prj.entity;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;


 /* Refactoring generale
  Rinominati addressLine1 ed addressLine2 in firstAddress e secondAddress
 */
public class Address implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotEmpty(message = "First Address cannot be empty")
	private String firstAddress;

	private String secondAddress;

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


	public String getfirstAddress() {
		return firstAddress;
	}


	public void setfirstAddress(String firstAddress) {
		this.firstAddress = firstAddress;
	}

	
	public String getsecondAddress() {
		return secondAddress;
	}


	public void setsecondAddress(String secondAddress) {
		this.secondAddress = secondAddress;
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

	public Address(AddressData data){
		super()
		this.firstAddress = data.getfirstAddress();
		this.secondAddress = data.getsecondAddress();
		this.city = data.getCity();
		this.state = data.getState();
		this.country = data.getCountry();
		this.zipcode = data.getZipcode();
		this.telephone1 = data.getTelephone1();
		this.telephone2 = data.getTelephone2();
		this.fax = data.getFax();
	}

	
	public Address() {

	}


	@Override
	public String toString(){
		return "Address [firstAddress=" + firstAddress
			+ ", secondAddress=" + secondAddress
			+ ", city=" + city
			+ ", state=" + state
			+ ", country=" + country
			+ ", zipcode=" + zipcode
			+ ", telephone1=" + telephone1
			+ ", telephone2=" + telephone2
			+ ", fax=" + fax + "]";
	}

}
