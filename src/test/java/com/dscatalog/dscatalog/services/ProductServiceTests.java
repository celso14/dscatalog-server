package com.dscatalog.dscatalog.services;



import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.dscatalog.dscatalog.dto.ProductDto;
import com.dscatalog.dscatalog.entities.Category;
import com.dscatalog.dscatalog.entities.Product;
import com.dscatalog.dscatalog.repositories.CategoryRepository;
import com.dscatalog.dscatalog.repositories.ProductRepository;
import com.dscatalog.dscatalog.services.exceptions.DbException;
import com.dscatalog.dscatalog.services.exceptions.ResourceNotFoundException;
import com.dscatalog.dscatalog.tests.Factory;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {
	
    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;
    
    @Mock
    private CategoryRepository categoryRepository;
	
	private Long existingId;
	private Long nonExistingId;
	private Long dependentId;
	private PageImpl<Product> page;
	private Product product;
	private ProductDto productDto;
	private Category category;
	
	@BeforeEach
	void setUp() throws Exception{
		
		existingId = 1L;
		nonExistingId = 1000L;
		dependentId = 4L;
		product = Factory.createProduct();
		productDto = Factory.createProductDTO();
		category = Factory.createCategory();
		page = new PageImpl<>(List.of(product));
		
		Mockito.when(repository.findAll
			((Pageable)ArgumentMatchers.any()))
			.thenReturn(page);
		
		Mockito.when(repository.findById(existingId))
			.thenReturn(Optional.of(product));
		Mockito.when(repository.findById(nonExistingId))
			.thenReturn(Optional.empty());
		
		Mockito.when(repository.save
			(ArgumentMatchers.any()))
			.thenReturn(product);
		
        Mockito.when(repository.getReferenceById(existingId))
        	.thenReturn(product);
        Mockito.when(repository.getReferenceById(existingId))
    		.thenThrow(EntityNotFoundException.class);
        
        Mockito.when(categoryRepository.getReferenceById(existingId))
    		.thenReturn(category);
        Mockito.when(categoryRepository.getReferenceById(nonExistingId))
    		.thenThrow(EntityNotFoundException.class);
		
        Mockito.doNothing()
        	.when(repository)
        	.deleteById(existingId);
        Mockito.doThrow(EntityNotFoundException.class)
        	.when(repository)
        	.getReferenceById(nonExistingId);
        Mockito.doThrow(DataIntegrityViolationException.class)
        	.when(repository)
        	.deleteById(dependentId);
        
        
	}
	
	@Test
	public void findAllPagedShouldReturnPage() {
		
		Pageable pageable = PageRequest.of(0, 10);
		
		Page<ProductDto> result = service.findAllPaged(pageable);
		
		Assertions.assertNotNull(result);
		
		Mockito.verify(repository).findAll(pageable);
	}
	
	@Test
	public void findByIdShouldReturnProductDtoWhenIdExists() {
        Assertions.assertDoesNotThrow(() -> {
        	this.service.findById(existingId);
        });
        
        Mockito.verify(repository).findById(existingId);
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExits() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            this.service.findById(nonExistingId);
        });

        Mockito.verify(repository).findById(nonExistingId);

	}
	
    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            this.service.update(nonExistingId, productDto);
        });
        
		Mockito.verify(repository).getReferenceById(nonExistingId);
    }
	
	@Test
	public void updateShouldReturnProductDTOWhenIdDoesExists(){
		ProductDto result = this.service.update(existingId, productDto);
		
		Mockito.verify(repository).getReferenceById(existingId);
        Mockito.verify(categoryRepository).getReferenceById(existingId);
		Mockito.verify(repository).save(ArgumentMatchers.any());
		
        Assertions.assertNotNull(result);
	}
	
    @Test
    public void deleteShouldDoNothingWhenIdDoesExists() {
        Assertions.assertDoesNotThrow(() -> {
        	this.service.delete(existingId);
        });

        Mockito.verify(repository).deleteById(existingId);
    }

    @Test
    public void deleteShouldThrowAResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            this.service.delete(nonExistingId);
        });

        Mockito.verify(repository).deleteById(nonExistingId);
    } 

    @Test
    public void deleteShouldThrowADataBaseExceptionWhenDependentId() {
        Assertions.assertThrows(DbException.class, () -> {
            this.service.delete(dependentId);
        });

        Mockito.verify(repository).deleteById(dependentId);
    }
}
