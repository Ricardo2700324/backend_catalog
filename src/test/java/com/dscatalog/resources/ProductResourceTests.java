package com.dscatalog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import com.dscatalog.services.exceptions.DatabaseException;
import com.dscatalog.services.exceptions.ResourceNotFoundException;
import com.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private ProductService service;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private long existingId;
	private long dependentId;
	private long nonExistingId;
	private ProductDTO productDTO;
	private PageImpl<ProductDTO> page;
	
	@BeforeEach
	void setUp() throws Exception {
	
		existingId = 1L;
		nonExistingId = 2L;
		dependentId = 3L;
		
		productDTO = Factory.createdProductDTO();
		page = new PageImpl<>(List.of(productDTO));
		
		when(service.findAllPaged(any())).thenReturn(page);
		
		when(service.findId(existingId)).thenReturn(productDTO);
		when(service.findId(nonExistingId)).thenThrow(ResourceNotFoundException.class);
		
		when(service.update(eq(existingId), any())).thenReturn(productDTO);
		when(service.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);
		
		doNothing().when(service).delete(existingId);
		doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
		doThrow(DatabaseException.class).when(service).delete(dependentId);
		
	}
	
	
	@Test
	public void updateShouldReturnProductDTOWhenIdExists()throws Exception{
	
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result =
				mockMvc.perform(put("/products/{id}", existingId)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
		
	}
	
	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExist()throws Exception{
	String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result =
				mockMvc.perform(put("/products/{id}", nonExistingId)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
		
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
