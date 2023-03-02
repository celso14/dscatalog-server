package com.dscatalog.dscatalog.dto;

import java.io.Serializable;
import java.util.Objects;
import com.dscatalog.dscatalog.entities.Role;

public class RoleDto implements Serializable{
		private static final long serialVersionUID = 1L;
	
	private Long id;
	private String authority;
	
	public RoleDto() {}
	
	public RoleDto(Long id, String authority) {
		super();
		this.id = id;
		this.authority = authority;
	}

	public RoleDto(Role role) {
		super();
		this.id = role.getId();
		this.authority = role.getAuthority();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
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
		RoleDto other = (RoleDto) obj;
		return Objects.equals(id, other.id);
	}
}
