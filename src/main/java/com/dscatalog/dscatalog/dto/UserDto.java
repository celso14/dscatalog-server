package com.dscatalog.dscatalog.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.dscatalog.dscatalog.entities.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	
	@NotBlank(message = "Campo obrigatório")
	private String firstName;
	private String lastName;
	
	@Email(message = "E-mail inválido")
	private String email;
	
	Set<RoleDto> roles = new HashSet<>();

	public UserDto() {
	}

	public UserDto(Long id, String firstName, String lastName, String email, String password, List<RoleDto> roles) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}

	public UserDto(User entity) {
		super();
		this.id = entity.getId();
		this.firstName = entity.getFirstName();
		this.lastName = entity.getLastName();
		this.email = entity.getEmail();
		entity.getRoles().forEach(role -> this.roles.add(new RoleDto(role)));
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Set<RoleDto> getRoles() {
		return roles;
	}

	public void setRoles(Set<RoleDto> roles) {
		this.roles = roles;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserDto other = (UserDto) obj;
		return Objects.equals(id, other.id);
	}
}
