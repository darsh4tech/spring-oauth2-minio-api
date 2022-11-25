package com.keeneye.resourceserver.unittest.controller;

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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keeneye.resourceserver.controller.ProductController;
import com.keeneye.resourceserver.dto.ProductDto;
import com.keeneye.resourceserver.entity.Product;
import com.keeneye.resourceserver.service.impl.ProductServiceImpl;

@WebMvcTest(controllers = ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ProductServiceImpl productServiceImpl;

	@Autowired
	private ObjectMapper objectMapper;
	
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
    public void ProductController_CreateProduct_ReturnCreated() throws Exception {
        
		when(productServiceImpl.createProduct(any())).thenReturn(productDto);

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDto)))
        		.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("$.category").value(productDto.getCategory()))
                .andExpect(jsonPath("$.prodName").value(productDto.getProdName()))
                .andExpect(jsonPath("$.imageUrl").value(productDto.getImageUrl()))
                .andExpect(jsonPath("$.price").value(productDto.getPrice()))
                .andExpect(jsonPath("$.minQuantity").value(productDto.getMinQuantity()))
                .andExpect(jsonPath("$.discountRate").value(productDto.getDiscountRate()));

        Mockito.verify(productServiceImpl).createProduct(Mockito.any(ProductDto.class));
                
    }
	
	@Test
    public void ProductController_GetAllUsers_ReturnUserDto() throws Exception {
        
		List<ProductDto> list = List.of(productDto);
        when(productServiceImpl.listAllProducts()).thenReturn(list);
        System.out.println("list: "+list);
        mockMvc.perform(get("/products")
                .contentType(MediaType.APPLICATION_JSON))
        		.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(list.size())))
                .andExpect(jsonPath("$[0].category").value(productDto.getCategory()))
                .andExpect(jsonPath("$[0].prodName").value(productDto.getProdName()))
                .andExpect(jsonPath("$[0].imageUrl").value(productDto.getImageUrl()))
                .andExpect(jsonPath("$[0].price").value(productDto.getPrice()))
                .andExpect(jsonPath("$[0].minQuantity").value(productDto.getMinQuantity()))
                .andExpect(jsonPath("$[0].discountRate").value(productDto.getDiscountRate()));
    }
	
	@Test
    public void ProductController_UpdateProduct_ReturnProductDto() throws Exception {
        
        when(productServiceImpl.updateProduct(any())).thenReturn(productDto);

        mockMvc.perform(put("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDto)))
        		.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.category").value(productDto.getCategory()))
                .andExpect(jsonPath("$.prodName").value(productDto.getProdName()))
                .andExpect(jsonPath("$.imageUrl").value(productDto.getImageUrl()))
                .andExpect(jsonPath("$.price").value(productDto.getPrice()))
                .andExpect(jsonPath("$.minQuantity").value(productDto.getMinQuantity()))
                .andExpect(jsonPath("$.discountRate").value(productDto.getDiscountRate()));
        
    }
	
	@Test
    public void ProductController_DeleteProduct_ReturnNothing() throws Exception {
        
        doNothing().when(productServiceImpl).deleteProduct(anyInt());

        mockMvc.perform(delete("/products/{prodId}",product.getProdId())
                .contentType(MediaType.APPLICATION_JSON))
        		.andExpect(MockMvcResultMatchers.status().isOk());
        
    }
}
