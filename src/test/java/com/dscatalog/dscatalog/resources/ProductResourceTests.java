package com.dscatalog.dscatalog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import com.dscatalog.dscatalog.dto.ProductDto;
import com.dscatalog.dscatalog.services.ProductService;
import com.dscatalog.dscatalog.services.exceptions.DbException;
import com.dscatalog.dscatalog.services.exceptions.ResourceNotFoundException;
import com.dscatalog.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {
	
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;
    
    @MockBean
    private ProductService service;
    
    private ProductDto productDto;
    private PageImpl<ProductDto> page;
    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;
    
	@BeforeEach
	void setUp() throws Exception{
		
		productDto = Factory.createProductDTO();
		page = new PageImpl<>(List.of(productDto));
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
        
        when(service.insert(any())).thenReturn(productDto);

        doThrow(ResourceNotFoundException.class)
                .when(service).delete(nonExistingId);

        doThrow(DbException.class).when(service).delete(dependentId);

        doNothing().when(service).delete(existingId);

        when(service.update(eq(existingId), any())).thenReturn(productDto);

        doThrow(ResourceNotFoundException.class)
                .when(service).update(eq(nonExistingId), any());
        
        when(service.findById(existingId)).thenReturn(productDto);
        
        doThrow(ResourceNotFoundException.class)
        	.when(service).findById(nonExistingId);
		
		when(service.findAllPaged(any())).thenReturn(page);
	}
	
    @Test
    public void deleteShouldReturnBadRequestWhenDependentId() throws Exception {
        ResultActions result = mockMvc
                .perform(delete("/products/{id}", dependentId)
                .accept(MediaType.APPLICATION_JSON));

        verify(service).delete(dependentId);
        result.andExpect(status().isBadRequest());
    }

    @Test
    public void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        ResultActions result = mockMvc
                .perform(delete("/products/{id}", nonExistingId)
                .accept(MediaType.APPLICATION_JSON));

        verify(service).delete(nonExistingId);
        result.andExpect(status().isNotFound());
    }

    @Test
    public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
        ResultActions result = mockMvc
                .perform(delete("/products/{id}", existingId)
                .accept(MediaType.APPLICATION_JSON));

        verify(service).delete(existingId);
        result.andExpect(status().isNoContent());
    }

    @Test
    public void insertShouldReturnProductDTOCreated() throws Exception {
        String jsonBody = mapper.writeValueAsString(productDto);

        ResultActions result = mockMvc.perform(post("/products")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        verify(service).insert(any());

        result.andExpect(status().isCreated());

        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
        result.andExpect(jsonPath("$.price").exists());
        result.andExpect(jsonPath("$.imgUrl").exists());
    }

    @Test
    public void updateShouldNotFoundWhenIdDoesNotExist() throws Exception {
        String jsonBody = mapper.writeValueAsString(productDto);

        ResultActions result = mockMvc
                .perform(put("/products/{id}", nonExistingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        verify(service).update(eq(nonExistingId), any());
        result.andExpect(status().isNotFound());
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExists() throws Exception {
        /*Converte o ProductDTO para um formato Json!*/
        String jsonBody = mapper.writeValueAsString(productDto);

        ResultActions result = mockMvc
                .perform(put("/products/{id}", existingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        verify(service).update(eq(existingId), any());

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
        result.andExpect(jsonPath("$.price").exists());
        result.andExpect(jsonPath("$.imgUrl").exists());
    }
	
    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists() throws Exception {
    	
        ResultActions result = mockMvc
                .perform(get("/products/{id}", existingId)
                .accept(MediaType.APPLICATION_JSON));

        verify(service).findById(existingId);

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
        result.andExpect(jsonPath("$.price").exists());
        result.andExpect(jsonPath("$.imgUrl").exists());
    }
    
    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        ResultActions result = mockMvc
                .perform(get("/products/{idProduct}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON));

        verify(service).findById(nonExistingId);
        result.andExpect(status().isNotFound());
    }
	
	@Test
	public void findAllShouldReturnPage() throws Exception{
		
		ResultActions result = mockMvc.perform(get("/products")
				.accept(MediaType.APPLICATION_JSON)
				);
		
		verify(service).findAllPaged(any());
		
		result.andExpect(status().isOk());
	}

}
