package com.dscatalog.tests;

import java.time.Instant;

import com.dscatalog.dto.ProductDTO;
import com.dscatalog.entities.Category;
import com.dscatalog.entities.Product;

public class Factory {

	public static Product createdProduct() {
		Product product = new Product(1L, "Phone", "Good Phone" , 800.00, "https://img.png", Instant.parse("2020-10-20T03:00:00Z"));
		product.getCategories().add(new Category(2L, "Eletronics"));
		return product;
	}
	
	public static ProductDTO createdProductDTO() {
		Product product = createdProduct();
		return new ProductDTO();
	}
}
