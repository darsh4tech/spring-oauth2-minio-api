package com.keeneye.resourceserver.unittest.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.keeneye.resourceserver.dto.ProductDto;
import com.keeneye.resourceserver.entity.Product;
import com.keeneye.resourceserver.repository.IProductRepository;
import com.keeneye.resourceserver.service.impl.ProductServiceImpl;
import com.keeneye.resourceserver.utils.ObjectMapperUtils;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ProductServiceTest {

	@Mock
	ObjectMapperUtils mapperUtils;
	
	@Mock
	IProductRepository productRepository;
	
	@InjectMocks
	ProductServiceImpl productServiceImpl;

	ProductDto productDto = null;
	Product product = null;

	@BeforeEach
	public void init() {
		
		product = Product.builder().category("category_1").prodName("prod_1").imageUrl("1/123123qweqweqe_myPhoto.jpg")
				.price(12.5f).minQuantity(5).discountRate(0.5f).deleted(false).build();

		productDto = ProductDto.builder().category("category_1").prodName("prod_1").imageUrl("1/123123qweqweqe_myPhoto.jpg")
				.price(12.5f).minQuantity(5).discountRate(0.5f).build();
		
	}
	
	@Test
    public void ProductService_CreateUser_ReturnsproductDto() {

		when(mapperUtils.map(Mockito.any(ProductDto.class), Mockito.any())).thenReturn(product);
		when(productRepository.save(Mockito.any(Product.class))).thenReturn(product);
		when(mapperUtils.map(Mockito.any(Product.class), Mockito.any())).thenReturn(productDto);

        ProductDto productDto_saved = productServiceImpl.createProduct(productDto);

        Assertions.assertThat(productDto_saved).isNotNull();
        assertEquals(productDto.getCategory(), productDto_saved.getCategory());
        assertEquals(productDto.getDiscountRate(), productDto_saved.getDiscountRate());
        assertEquals(productDto.getImageUrl(), productDto_saved.getImageUrl());
        assertEquals(productDto.getMinQuantity(), productDto_saved.getMinQuantity());
    }
	
	@Test
    public void ProductService_listAllProducts_ReturnsProductDto_List() {
		
        when(productRepository.findByDeletedIsFalse()).thenReturn(List.of(product));

        List<ProductDto> allUsersRS = productServiceImpl.listAllProducts();

        Assertions.assertThat(allUsersRS).isNotNull();

    }
	
	@Test
    public void ProductService_UpdateProduct_ReturnProductDto() {
        
		when(mapperUtils.map(Mockito.any(ProductDto.class), Mockito.any())).thenReturn(product);
		when(productRepository.save(Mockito.any(Product.class))).thenReturn(product);
		
		ProductDto productDtoUpdated = ProductDto.builder().category("category_2").prodName("prod_2").imageUrl("1/123123qweqweqe_myPhoto.jpg")
				.price(12.5f).minQuantity(5).discountRate(0.5f).build();

		when(mapperUtils.map(Mockito.any(Product.class), Mockito.any())).thenReturn(productDtoUpdated);
		
        ProductDto dto = productServiceImpl.updateProduct(productDtoUpdated);

        Assertions.assertThat(dto).isNotNull();
        assertNotEquals(productDto.getCategory(), dto.getCategory());
        assertNotEquals(productDto.getProdName(), dto.getProdName());

    }
	
	@Test
    public void ProductService_DeleteProductById_ReturnVoid() {

		when(productRepository.findByProdIdAndDeletedIsFalse(Mockito.anyInt())).thenReturn(Optional.ofNullable(product));
        doNothing().when(productRepository).delete(product);

        assertAll(() -> productServiceImpl.deleteProduct(product.getProdId()));
    }
}
