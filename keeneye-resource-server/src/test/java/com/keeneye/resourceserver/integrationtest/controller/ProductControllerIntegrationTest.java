package com.keeneye.resourceserver.integrationtest.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keeneye.resourceserver.config.OAuthHelper;
import com.keeneye.resourceserver.dto.ProductDto;
import com.keeneye.resourceserver.entity.Product;
import com.keeneye.resourceserver.repository.IProductRepository;
import com.keeneye.resourceserver.service.impl.ProductServiceImpl;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class ProductControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ProductServiceImpl productServiceImpl;

	@Autowired
	private IProductRepository productRepository;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
    private OAuthHelper helper;
	
	ProductDto productDto = null;
	Product product = null;
	RequestPostProcessor bearerToken = null;
	
	@BeforeEach
	public void init() {
		
		product = Product.builder().category("category_1").prodName("prod_1").imageUrl("1/123123qweqweqe_myPhoto.jpg")
				.price(12.5f).minQuantity(5).discountRate(0.5f).deleted(false).build();

		productDto = ProductDto.builder().category("category_1").prodName("prod_1").imageUrl("1/123123qweqweqe_myPhoto.jpg")
				.price(12.5f).minQuantity(5).discountRate(0.5f).build();
		bearerToken = helper.bearerToken("keeneye-client");
		
	}
	
	@AfterEach
	public void clear() {
		productRepository.deleteAll();
	}
	
	@Test
    public void ProductController_CreateProduct_ReturnCreated() throws Exception {
        
        mockMvc.perform(post("/products").with(bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDto)))
        		.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("$.category").value(productDto.getCategory()))
                .andExpect(jsonPath("$.prodName").value(productDto.getProdName()))
                .andExpect(jsonPath("$.imageUrl").value(productDto.getImageUrl()))
                .andExpect(jsonPath("$.price").value(productDto.getPrice()))
                .andExpect(jsonPath("$.minQuantity").value(productDto.getMinQuantity()))
                .andExpect(jsonPath("$.discountRate").value(productDto.getDiscountRate()));
                
    }
	
	@Test
    public void ProductController_GetAllUsers_ReturnUserDto() throws Exception {
        
		productServiceImpl.createProduct(productDto);
		
        mockMvc.perform(get("/products").with(bearerToken)
                .contentType(MediaType.APPLICATION_JSON))
        		.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].category").value(productDto.getCategory()))
                .andExpect(jsonPath("$[0].prodName").value(productDto.getProdName()))
                .andExpect(jsonPath("$[0].imageUrl").value(productDto.getImageUrl()))
                .andExpect(jsonPath("$[0].price").value(productDto.getPrice()))
                .andExpect(jsonPath("$[0].minQuantity").value(productDto.getMinQuantity()))
                .andExpect(jsonPath("$[0].discountRate").value(productDto.getDiscountRate()));
    }
	
	@Test
    public void ProductController_UpdateProduct_ReturnProductDto() throws Exception {
		
		productServiceImpl.createProduct(productDto);
		
		ProductDto productDtoUpdated = ProductDto.builder().category("category_updated")
						.prodName("prod_up").imageUrl("1/123123qweqweqe_myPhoto.jpg")
				.price(12.5f).minQuantity(10).discountRate(0.5f).build();


        mockMvc.perform(put("/products").with(bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDtoUpdated)))
        		.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.category").value(productDtoUpdated.getCategory()))
                .andExpect(jsonPath("$.prodName").value(productDtoUpdated.getProdName()))
                .andExpect(jsonPath("$.imageUrl").value(productDtoUpdated.getImageUrl()))
                .andExpect(jsonPath("$.price").value(productDtoUpdated.getPrice()))
                .andExpect(jsonPath("$.minQuantity").value(productDtoUpdated.getMinQuantity()))
                .andExpect(jsonPath("$.discountRate").value(productDtoUpdated.getDiscountRate()));
        
    }
	
	@Test
    public void ProductController_DeleteProduct_ReturnNothing() throws Exception {

		ProductDto productDtoCreated = productServiceImpl.createProduct(productDto);
		System.out.println(productDtoCreated);
        mockMvc.perform(delete("/products/{prodId}",productDtoCreated.getProdId()).with(bearerToken)
                .contentType(MediaType.APPLICATION_JSON))
        		.andExpect(MockMvcResultMatchers.status().isOk());
        
    }
}
