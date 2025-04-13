package com.web;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Product {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long Id;
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private Type type;
	
	@Column(nullable=false)
	private int minCount;
	
	@Column(nullable=false)
	private String color;
	
	@Column(nullable=false)
	private int itemsCount;
	
	@Column(nullable=false)
	private String fromAge;
	
	@Column(nullable=false)
	private String country;
	
	@Column(nullable=false)
	private String content;
	
	@Column(nullable=false, columnDefinition="TEXT")
	private String description;
	
	@Column(nullable=false)
	private int weight;
	
	@Column(nullable=false)
	private int length;
	
	@Column(nullable=false)
	private int width;
	
	@Column(nullable=false)
	private int height;
	
	@Column(nullable=false)
	private String material;
	
	@Column(nullable=false, precision=10, scale=2)
	private BigDecimal price;
	
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	@Column(nullable = true)
	@JsonManagedReference
	private Set<Image> images = new HashSet<>();
	
	
	public void addImage(Image image) {
		images.add(image);
		image.setProduct(this);
	}
	
	public enum Type {
		bib, set
	}
	
	public Product(ProductRequest req) {
		this.color = req.getColor();
		this.content = req.getContent();
		this.country = req.getCountry();
		this.description = req.getDescription();
		this.fromAge = req.getFromAge();
		this.height = req.getHeight();
		this.itemsCount = req.getItemsCount();
		this.length = req.getLength();
		this.width = req.getWidth();
		this.weight = req.getWeight();
		this.minCount = req.getMinCount();
		this.type = req.getType();
		this.price = req.getPrice();
		this.name = req.getName();
		this.material = req.getMaterial();
	}

}












