package com.web;

import java.math.BigDecimal;

import com.web.Product.Type;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequest {
	
	private String name;
	
	private Type type;
	
	private Integer minCount;
	
	private String color;
	
	private Integer itemsCount;
	
	private String fromAge;
	
	private String country;
	
	private String content;
	
	private String description;
	
	private Integer weight;
	
	private Integer length;
	
	private Integer width;
	
	private Integer height;
	
	private BigDecimal price;
	
	private String material;
}
