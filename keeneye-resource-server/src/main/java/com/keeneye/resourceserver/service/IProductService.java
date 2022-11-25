package com.keeneye.resourceserver.service;

import java.util.List;

import com.keeneye.resourceserver.dto.ProductDto;

public interface IProductService {

	public ProductDto createProduct(ProductDto userDto);
	
	public ProductDto updateProduct(ProductDto userDto);
	
	public void deleteProduct(Integer prodId);
	
	public List<ProductDto> listAllProducts();
	
	public ProductDto findById(Integer prodId);
	
	public ProductDto findByImageUrl(String imageUrl);
}
