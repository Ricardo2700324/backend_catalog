package com.dscatalog.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.dscatalog.repositories.ProductRepository;
import com.dscatalog.services.exceptions.DatabaseException;
import com.dscatalog.services.exceptions.ResourceNotFoundException;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	private long existingId;
	private long nonexistingId;
	private long dependentId;
	
	@BeforeEach
	void setUp() throws Exception {
	  existingId = 1L;	
	  nonexistingId = 1000L;
	  dependentId = 4L;
	  
	  Mockito.doNothing().when(repository).deleteById(existingId);
	  
	  Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonexistingId);
	  
	  Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
	}
	
	//DataIntegrityViolationException
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenIdExist() {

		Assertions.assertThrows(DatabaseException.class,() ->{
			service.delete(dependentId);
		}) ;
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

		Assertions.assertThrows(ResourceNotFoundException.class,() ->{
			service.delete(nonexistingId);
		}) ;
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(nonexistingId);
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExist() {

		Assertions.assertDoesNotThrow(() ->{
			service.delete(existingId);
		}) ;
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
	}
	
}
