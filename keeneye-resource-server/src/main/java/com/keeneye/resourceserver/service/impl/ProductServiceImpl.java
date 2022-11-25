package com.keeneye.resourceserver.service.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.keeneye.resourceserver.dto.ProductDto;
import com.keeneye.resourceserver.entity.Product;
import com.keeneye.resourceserver.repository.IProductRepository;
import com.keeneye.resourceserver.service.IProductService;
import com.keeneye.resourceserver.utils.ObjectMapperUtils;

@Service
@Transactional
public class ProductServiceImpl implements IProductService {

	private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

	private final IProductRepository productRepository;
	private final ObjectMapperUtils objectMapperUtils;
	private final MinIOFileService minIOFileService;

	public ProductServiceImpl(IProductRepository productRepository, ObjectMapperUtils objectMapperUtils,
			MinIOFileService minIOFileService) {
		this.productRepository = productRepository;
		this.objectMapperUtils = objectMapperUtils;
		this.minIOFileService = minIOFileService;
	}

	/**
	 * @param productDto to be created
	 */
	@Override
	public ProductDto createProduct(ProductDto productDto) {
		Product product = objectMapperUtils.map(productDto, Product.class);
		product = productRepository.save(product);
		return objectMapperUtils.map(product, ProductDto.class);
	}

	/**
	 * @param productDto to be updated
	 */
	@Override
	public ProductDto updateProduct(ProductDto productDto) {
		Product product = objectMapperUtils.map(productDto, Product.class);
		product = productRepository.save(product);
		return objectMapperUtils.map(product, ProductDto.class);
	}

	/**
	 * soft delete for product entity by changing the field {@code delete}
	 */
	@Override
	public void deleteProduct(Integer prodId) {
		try {
			Optional<Product> findByProdIdOptional = productRepository.findByProdIdAndDeletedIsFalse(prodId);
			findByProdIdOptional.ifPresent(product -> {
				Boolean removeFile = this.minIOFileService.removeFile(product.getImageUrl());
				log.info("is file removed ? : {}", removeFile);
				product.setDeleted(true);
				product.setImageUrl("");
				productRepository.save(product);
			});
		} catch (Exception e) {
			log.error("Error: {}", e);
			throw e;
		}
	}

	/**
	 * fetch all products from DBs
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ProductDto> listAllProducts() {
		return objectMapperUtils.mapAll(productRepository.findByDeletedIsFalse(), ProductDto.class);
	}

	/**
	 * @param prodId
	 * find by prodId
	 */
	@Override
	public ProductDto findById(Integer prodId) {
		Optional<Product> findByIdOptional = productRepository.findById(prodId);
		if (findByIdOptional.isPresent()) {
			return objectMapperUtils.map(findByIdOptional.get(), ProductDto.class);
		}
		return null;
	}

	/**
	 * @param imageUrl
	 * find by imageUrl
	 */
	@Override
	public ProductDto findByImageUrl(String imageUrl) {
		Optional<Product> findByIdOptional = productRepository.findByImageUrl(imageUrl);
		if (findByIdOptional.isPresent()) {
			return objectMapperUtils.map(findByIdOptional.get(), ProductDto.class);
		}
		return null;
	}

}
