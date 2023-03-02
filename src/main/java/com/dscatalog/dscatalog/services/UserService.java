package com.dscatalog.dscatalog.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dscatalog.dscatalog.dto.RoleDto;
import com.dscatalog.dscatalog.dto.UserDto;
import com.dscatalog.dscatalog.dto.UserInsertDto;
import com.dscatalog.dscatalog.entities.Role;
import com.dscatalog.dscatalog.entities.User;
import com.dscatalog.dscatalog.repositories.RoleRepository;
import com.dscatalog.dscatalog.repositories.UserRepository;
import com.dscatalog.dscatalog.services.exceptions.DbException;
import com.dscatalog.dscatalog.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {
	
	@Autowired
	private UserRepository repository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
		
	@Transactional(readOnly = true)
	public Page<UserDto> findAllPaged(Pageable pageable){
		Page<User> list = repository.findAll(pageable);
		
		return list.map(x -> new UserDto(x));
	}

	@Transactional(readOnly = true)
	public UserDto findById(Long id) {
		
		Optional<User> obj = repository.findById(id);
		
		User entity = obj.orElseThrow(() -> new ResourceNotFoundException("Produto não Encontrado!"));
		
		return new UserDto(entity);
	}

	@Transactional
	public UserDto insert(UserInsertDto dto) {
		
		User entity = new User();
		
		copyDtoToEntity(dto, entity);
		
		entity.setPassword(passwordEncoder.encode(dto.getPassword()));
		entity = repository.save(entity);
		
		return new UserDto(entity);
	}

	@Transactional
	public UserDto update(Long id, UserDto dto) {
		try {
			
			User entity = repository.getReferenceById(id);
			
			copyDtoToEntity(dto, entity);
			
			entity = repository.save(entity);
			
			return new UserDto(entity);
		}
		catch (EntityNotFoundException e) {
			
			throw new ResourceNotFoundException("Produto não encontrado para atualização");
		}
	}

	public void delete(Long id) {
		try {
			repository.deleteById(id);
		}
		catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Produto não encontrado para exclusão");
		}
		catch (DataIntegrityViolationException e) {
			throw new DbException("Integrity violation");
		}
	}
	
	private void copyDtoToEntity(UserDto dto, User entity) {
		
		entity.setFirstName(dto.getFirstName());
		entity.setLastName(dto.getLastName());
		entity.setEmail(dto.getEmail());
		
		entity.getRoles().clear();
		for(RoleDto roleDto : dto.getRoles()) {
			Role role = roleRepository.getReferenceById(roleDto.getId());
			entity.getRoles().add(role);
		}
		
	}
}
