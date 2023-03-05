package com.dscatalog.dscatalog.dto;

import java.io.Serializable;

import com.dscatalog.dscatalog.services.validation.UserUpdateValid;

@UserUpdateValid
public class UserUpdateDto extends UserDto implements Serializable{
	private static final long serialVersionUID = 1L;

}
