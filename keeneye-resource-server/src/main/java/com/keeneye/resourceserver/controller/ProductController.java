package com.keeneye.resourceserver.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.keeneye.resourceserver.dto.ProductDto;
import com.keeneye.resourceserver.service.IProductService;

@RestController
@RequestMapping("/products")
public class ProductController {

    private IProductService productService;
    
    public ProductController(IProductService productService) {
		this.productService = productService;
	}
 
    /**
	 * 
	 * @param ProductDto
	 * @return created product
	 */
	@PostMapping
	public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto, @RequestHeader(value = "Authorization") String token) {
		System.out.println("Token: "+token);
		return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(productDto));
	}

	/**
	 * 
	 * @param ProductDto
	 * @return updated product
	 */
	@PutMapping
	public ResponseEntity<ProductDto> updateProduct(@RequestBody ProductDto productDto) {
		return ResponseEntity.ok(productService.updateProduct(productDto));
	}

	/**
	 * 
	 * @param prodId
	 * @return 200 OK for successful deletion
	 */
	@DeleteMapping("/{prodId}")
	public ResponseEntity<Void> deleteProduct(@PathVariable Integer prodId) {
		productService.deleteProduct(prodId);
		return ResponseEntity.ok().build();
	}

	/**
	 * 
	 * @return list all non deleted products
	 */
	@GetMapping
	public ResponseEntity<List<ProductDto>> listAllProducts() {
		return ResponseEntity.ok(productService.listAllProducts());
	}
    
}
