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
import com.dscatalog.dscatalog.dto.ProductDto;
import com.dscatalog.dscatalog.entities.Category;
import com.dscatalog.dscatalog.entities.Product;
import com.dscatalog.dscatalog.repositories.CategoryRepository;
import com.dscatalog.dscatalog.repositories.ProductRepository;
import com.dscatalog.dscatalog.services.exceptions.DbException;
import com.dscatalog.dscatalog.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

// Essa notation registra essa classe como um componente de injeção de dependência automaticamente
@Service
public class ProductService {
	
	@Autowired
	private ProductRepository repository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Transactional(readOnly = true)
	public Page<ProductDto> findAllPaged(PageRequest pageRequest){
		
		Page<Product> list = repository.findAll(pageRequest);
		
		return list.map(x -> new ProductDto(x));
	}

	//Optional serve para evitar se trabalhar com objetos nulos
	@Transactional(readOnly = true)
	public ProductDto findById(Long id) {
		
		Optional<Product> obj = repository.findById(id);
		
		Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		
		return new ProductDto(entity, entity.getCategories());
	}

	@Transactional
	public ProductDto insert(ProductDto dto) {
		
		Product entity = new Product();
		
		copyDtoToEntity(dto, entity);
		
		entity = repository.save(entity);
		
		return new ProductDto(entity);
	}

	@Transactional
	public ProductDto update(Long id, ProductDto dto) {
		try {
			
			Product entity = repository.getReferenceById(id);
			
			copyDtoToEntity(dto, entity);
			
			entity = repository.save(entity);
			
			return new ProductDto(entity);
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
	
	private void copyDtoToEntity(ProductDto dto, Product entity) {
		
		entity.setName(dto.getName());
		entity.setDescription(dto.getDescription());
		entity.setImgUrl(dto.getImgUrl());
		entity.setPrice(dto.getPrice());
		entity.setDate(dto.getDate());
		
		entity.getCategories().clear();
		for(CategoryDto categoryDto : dto.getCategories()) {
			Category category = categoryRepository.getReferenceById(categoryDto.getId());
			entity.getCategories().add(category);
		}
		
	}
}
