package com.dscatalog.dscatalog.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dscatalog.dscatalog.dto.CategoryDto;
import com.dscatalog.dscatalog.entities.Category;
import com.dscatalog.dscatalog.repositories.CategoryRepository;
import com.dscatalog.dscatalog.services.exceptions.EntityNotFoundException;

// Essa notation registra essa classe como um componente de injeção de dependência automaticamente
@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository repository;
	
	@Transactional(readOnly = true)
	public List<CategoryDto> findAll(){
		
		List<Category> list = repository.findAll();
		
		List<CategoryDto> listDto = list.stream().map(x -> new CategoryDto(x)).toList();
		
		return listDto;
	}

	//Optional serve para evitar se trabalhar com objetos nulos
	@Transactional(readOnly = true)
	public CategoryDto findById(Long id) {
		
		Optional<Category> obj = repository.findById(id);
		
		Category entity = obj.orElseThrow(() -> new EntityNotFoundException("Entity not found"));
		
		return new CategoryDto(entity);
	}

	public CategoryDto insert(CategoryDto dto) {
		
		Category entity = new Category();
		
		entity.setName(dto.getName());
		
		entity = repository.save(entity);
		
		return new CategoryDto(entity);
	}
	
}
