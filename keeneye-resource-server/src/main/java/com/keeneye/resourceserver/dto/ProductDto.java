package com.keeneye.resourceserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

	private Integer prodId;
	private String category;
	private String prodName;
	private String imageUrl;
	private Float price;
	private Integer minQuantity;
	private Float discountRate;

}
