package com.web;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.web.Product.Type;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
	
	private Long id;
	
	private String name;
	
	private Type type;
	
	private Set<ImageDTO> images;

	private int minCount;
	
	private String color;
	
	private int itemsCount;
	
	private String fromAge;
	
	private String country;
	
	private String content;
	
	private String description;
	
	private int weight;
	
	private int length;
	
	private int width;
	
	private int height;
	
	@JsonSerialize(using = ToStringSerializer.class)
	private BigDecimal price;
	
	private String material;
	
	public ProductDTO(Product product, MinioService minioService) {
		this.id = product.getId();
		this.name = product.getName();
		this.type = product.getType();
		this.minCount = product.getMinCount();
		this.color = product.getColor();
		this.itemsCount = product.getItemsCount();
		this.fromAge = product.getFromAge();
		this.country = product.getCountry();
		this.content = product.getContent();
		this.description = product.getDescription();
		this.weight = product.getWeight();
		this.length = product.getLength();
		this.width = product.getWidth();
		this.height = product.getHeight();
		this.price = product.getPrice();
		this.material = product.getMaterial();
		
		this.images = product.getImages().stream().map(img -> {
			ImageDTO image = new ImageDTO();
			image.setUrl(minioService.getPresignedUrl(img.getFileName()));
			image.setPriority(img.getPriority());
			return image;
			}
		)
				.collect(Collectors.toSet());
	}
	
}
