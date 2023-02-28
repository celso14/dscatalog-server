package com.dscatalog.dscatalog.tests;

import java.time.Instant;

import com.dscatalog.dscatalog.dto.ProductDto;
import com.dscatalog.dscatalog.entities.Category;
import com.dscatalog.dscatalog.entities.Product;

public class Factory {
	
	public static Product createProduct() {
		Product product = new Product(1L, "Phone", "Good Phone", 800.0, "https:/img.com/img.png", Instant.parse("2023-02-25T00:00:00Z"));
		product.getCategories().add(Factory.createCategory());
		return product;
	}
	
	public static ProductDto createProductDTO() {
		Product product = createProduct();
		return new ProductDto(product, product.getCategories());
	}
	
	public static Category createCategory() {
		return new Category(2L, "Eletronics");
	}
}
