package com.keeneye.resourceserver.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.keeneye.resourceserver.entity.Product;

@Repository
public interface IProductRepository extends JpaRepository<Product, Integer>{

	Optional<Product> findByProdIdAndDeletedIsFalse(Integer prodId);
	List<Product> findByDeletedIsFalse();
	Optional<Product> findByImageUrl(String imageUrl);
}
