package com.dscatalog.dscatalog.dto;

import java.io.Serializable;

import com.dscatalog.dscatalog.services.validation.UserInsertValid;

@UserInsertValid
public class UserInsertDto extends UserDto implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String password;
	
	UserInsertDto() {}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
