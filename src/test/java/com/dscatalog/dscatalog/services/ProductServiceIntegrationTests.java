package com.dscatalog.dscatalog.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import com.dscatalog.dscatalog.dto.ProductDto;
import com.dscatalog.dscatalog.repositories.ProductRepository;
import com.dscatalog.dscatalog.services.exceptions.ResourceNotFoundException;
import com.dscatalog.dscatalog.tests.Factory;

@SpringBootTest
@Transactional
public class ProductServiceIntegrationTests {
	
    private Long existingId;
    private Long nonExistingId;
    private Long totalProducts;
    private ProductDto productDto;
    
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;
    
    @BeforeEach
    void setup() {
        existingId = 1L;
        nonExistingId = 1000L;
        totalProducts = 25L;
        productDto = Factory.createProductDTO();
    }
    
    @Test
    public void findAllPagedShouldReturnPage() {
        PageRequest page = PageRequest.of(0, 5);
        Page<ProductDto> result = productService.findAllPaged(page);

        assertNotNull(result);

        assertEquals(0, result.getNumber());
        assertEquals(5, result.getSize());
        assertEquals(totalProducts, result.getTotalElements());
        assertEquals(ProductDto.class, result.getContent().get(0).getClass());
    }
    
    @Test
    public void findAllPagedShouldBeEmptyWhenPageDoesNotExist() {
        int nonExistingPage = 100;

        PageRequest page = PageRequest.of(nonExistingPage, 5);
        Page<ProductDto> result = productService.findAllPaged(page);

        assertTrue(result.isEmpty());
    }
    
    @Test
    public void deleteShouldDeleteProductWhenIdExists() {
        productService.delete(existingId);
        assertEquals(totalProducts - 1, productRepository.count());
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Throwable exception =  assertThrows(ResourceNotFoundException.class, () ->
                productService.delete(nonExistingId)
        );

        assertEquals("Produto não encontrado para exclusão", exception.getMessage());
    }
    
}
