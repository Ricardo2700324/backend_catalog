package com.dscatalog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.dscatalog.dto.ProductDTO;
import com.dscatalog.resource.ProductResource;
import com.dscatalog.services.ProductService;
import com.dscatalog.services.exceptions.ResourceNotFoundException;
import com.dscatalog.tests.Factory;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private ProductService service;
	
	private long existingId;
	private long nonExistingId;
	private ProductDTO productDTO;
	private PageImpl<ProductDTO> page;
	
	@BeforeEach
	void setUp() throws Exception {
	
		existingId = 1L;
		nonExistingId = 2L;
		
		productDTO = Factory.createdProductDTO();
		page = new PageImpl<>(List.of(productDTO));
		
		when(service.findAllPaged(any())).thenReturn(page);
		
		when(service.findId(existingId)).thenReturn(productDTO);
		when(service.findId(nonExistingId)).thenThrow(ResourceNotFoundException.class);
	}
	
	@Test
	public void findAllShoudReturnPage() throws Exception {
		mockMvc.perform(get("/products")).andExpect(status().isOk());
		
		
	}
	
	@Test
	public void findIdShouldReturnProductWhenIdExists() throws Exception {
		ResultActions result = mockMvc.perform(get("/products/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON));
				
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
	}
	
	@Test
	public void findIdShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
		ResultActions result = mockMvc.perform(get("/products/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON));
				
		result.andExpect(status().isNotFound());
	}
}
