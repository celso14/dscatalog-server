package com.dscatalog.dscatalog.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dscatalog.dscatalog.dto.CategoryDto;
import com.dscatalog.dscatalog.entities.Category;
import com.dscatalog.dscatalog.repositories.CategoryRepository;
import com.dscatalog.dscatalog.services.exceptions.DbException;
import com.dscatalog.dscatalog.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

// Essa notation registra essa classe como um componente de injeção de dependência automaticamente
@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository repository;
	
	@Transactional(readOnly = true)
	public Page<CategoryDto> findAllPaged(PageRequest pageRequest){
		
		Page<Category> list = repository.findAll(pageRequest);
		
		return list.map(x -> new CategoryDto(x));
	}

	//Optional serve para evitar se trabalhar com objetos nulos
	@Transactional(readOnly = true)
	public CategoryDto findById(Long id) {
		
		Optional<Category> obj = repository.findById(id);
		
		Category entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		
		return new CategoryDto(entity);
	}

	@Transactional
	public CategoryDto insert(CategoryDto dto) {
		
		Category entity = new Category();
		
		entity.setName(dto.getName());
		
		entity = repository.save(entity);
		
		return new CategoryDto(entity);
	}

	@Transactional
	public CategoryDto update(Long id, CategoryDto dto) {
		try {
			
			Category entity = repository.getReferenceById(id);
			
			entity.setName(dto.getName());
			
			entity = repository.save(entity);
			
			return new CategoryDto(entity);
		}
		catch (EntityNotFoundException e) {
			
			throw new ResourceNotFoundException("Id not Found" + id);
		}
	}

	public void delete(Long id) {
		try {
			repository.deleteById(id);
		}
		catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not Found" + id);
		}
		catch (DataIntegrityViolationException e) {
			throw new DbException("Integrity violation");
		}
	}
}
