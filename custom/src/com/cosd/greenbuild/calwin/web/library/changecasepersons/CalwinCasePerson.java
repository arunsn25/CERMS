package com.cosd.greenbuild.calwin.web.library.changecasepersons;

public class CalwinCasePerson {
	
	private String firstname;
	private String lastname;
	private String middlename;
	private String cwin;
	private String ssn;
	private String suffix;
	private String dob;
	private String cin;
	
	public CalwinCasePerson(){
		
	}
	
	public CalwinCasePerson(String firstname, String lastname, String middlename, String cwin, String ssn, String suffix, String dob, String cin) {
		this.firstname = firstname;
		this.lastname = lastname;
		this.middlename = middlename;
		this.cwin = cwin;		
		this.ssn = ssn;
		this.suffix = suffix;
		this.dob = dob;
		this.cin = cin;		
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getMiddlename() {
		return middlename;
	}

	public void setMiddlename(String middlename) {
		this.middlename = middlename;
	}

	public String getCwin() {
		return cwin;
	}

	public void setCwin(String cwin) {
		this.cwin = cwin;
	}

	public String getSsn() {
		return ssn;
	}

	public void setSsn(String ssn) {
		this.ssn = ssn;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getCin() {
		return cin;
	}

	public void setCin(String cin) {
		this.cin = cin;
	}
	
	
	
	
}
