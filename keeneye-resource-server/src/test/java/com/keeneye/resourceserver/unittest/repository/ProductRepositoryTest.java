package com.keeneye.resourceserver.unittest.repository;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.keeneye.resourceserver.entity.Product;
import com.keeneye.resourceserver.repository.IProductRepository;

@DataJpaTest(properties = { "spring.liquibase.enabled=false" })
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ProductRepositoryTest {

	@Autowired
	private IProductRepository productRepository;
	
	@Test
	public void ProductRepository_SaveAll_ReturnSavedProduct() {

		Product product = Product.builder().category("category_1").prodName("prod_1").imageUrl("1/123123qweqweqe_myPhoto.jpg")
						.price(12.5f).minQuantity(5).discountRate(0.5f).deleted(false).build();
		
		
		Product productSaved = productRepository.save(product);

		// Assert
		Assertions.assertThat(productSaved).isNotNull();
		Assertions.assertThat(productSaved.getProdId()).isGreaterThan(0);

	}

	@Test
	public void ProductRepository_GetAll_ReturnMoreThanOneProduct() {

		Product product_1 = Product.builder().category("category_1").prodName("prod_1").imageUrl("1/123123qweqweqe_myPhoto.jpg")
				.price(12.5f).minQuantity(5).discountRate(0.5f).deleted(false).build();

		productRepository.save(product_1);

		Product product_2 = Product.builder().category("category_2").prodName("prod_2").imageUrl("2/123123qweqweqe_myPhoto.jpg")
				.price(13f).minQuantity(9).discountRate(1f).deleted(false).build();

		productRepository.save(product_2);

		List<Product> list = productRepository.findAll();

		Assertions.assertThat(list).isNotNull();
		Assertions.assertThat(list.size()).isEqualTo(2);

	}

	@Test
    public void ProductRepository_FindById_ReturnProductNotNull() {

		Product product_1 = Product.builder().category("category_1").prodName("prod_1").imageUrl("1/123123qweqweqe_myPhoto.jpg")
				.price(12.5f).minQuantity(5).discountRate(0.5f).deleted(false).build();

		Product productSaved = productRepository.save(product_1);

		Assertions.assertThat(productSaved.getProdId()).isGreaterThan(0);
		
        Product product = productRepository.findByProdIdAndDeletedIsFalse(productSaved.getProdId()).get();

        Assertions.assertThat(product).isNotNull();
        Assertions.assertThat(product.getCategory()).isEqualTo(product_1.getCategory());
    }
	
	@Test
    public void ProductRepository_Product_Delete_ReturnProductIsEmpty() {

		Product product_1 = Product.builder().category("category_1").prodName("prod_1").imageUrl("1/123123qweqweqe_myPhoto.jpg")
				.price(12.5f).minQuantity(5).discountRate(0.5f).deleted(false).build();

		Product productSaved = productRepository.save(product_1);

		productRepository.deleteById(productSaved.getProdId());
        Optional<Product> userDataOptional = productRepository.findById(productSaved.getProdId());

        Assertions.assertThat(userDataOptional).isEmpty();
    }

}
