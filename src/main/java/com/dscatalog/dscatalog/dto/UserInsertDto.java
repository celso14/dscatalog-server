package com.dscatalog.dscatalog.dto;

public class UserInsertDto extends UserDto{
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
