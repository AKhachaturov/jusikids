package com.web;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.web.Product.Type;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;


@Service
public class ProductService {
	
	MinioService minioService;
	ProductRepository productRepository;
	
	public ProductService(MinioService minioService, ProductRepository productRepository) {
		this.minioService = minioService;
		this.productRepository = productRepository;
	}
	
	public Page<ProductDTO> findAllProducts(String type, Pageable pageable) throws Exception{
		Set<String> allowedFields = Set.of("price");
	    
	    for (Sort.Order order : pageable.getSort()) {
	        if (!allowedFields.contains(order.getProperty())) {
	            throw new IllegalArgumentException("Invalid sorting");
	        }   
	    }
		if(type != null ) {
			try {
				Type _type = Type.valueOf(type);
				return productRepository.findAllByType(_type, pageable).map(p -> new ProductDTO(p, minioService));
			}catch(Exception exc) {
				return Page.empty();
			}
		}
		
		return productRepository.findAll(pageable).map(p -> new ProductDTO(p, minioService));	
	}
	
	@Transactional
	public ProductDTO addProduct(Product product, List<MultipartFile> images) throws Exception{
		setImages(product, images);
		product = productRepository.save(product);
		
		return new ProductDTO(product, minioService);
	}
	
	public ResponseEntity<ProductDTO> editProduct(Long id, ProductRequest productRequest){
		Product product = productRepository.findById(id).orElse(null);
		if(product == null) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
		
		if(productRequest.getName() != null) {
			product.setName(productRequest.getName());
		}
		
		if(productRequest.getType() != null) {
			product.setType(productRequest.getType());
		}
		
		if(productRequest.getColor() != null) {
			product.setColor(productRequest.getColor());
		}
		
		if(productRequest.getContent() != null) {
			product.setContent(productRequest.getContent());
		}
		
		if(productRequest.getCountry() != null) {
			product.setCountry(productRequest.getCountry());
		}
		
		if(productRequest.getDescription() != null) {
			product.setDescription(productRequest.getDescription());
		}
		
		if(productRequest.getFromAge() != null) {
			product.setFromAge(productRequest.getFromAge());
		}
		
		if(productRequest.getHeight() != null) {
			product.setHeight(productRequest.getHeight());
		}
		
		if(productRequest.getItemsCount() != null) {
			product.setItemsCount(productRequest.getItemsCount());
		}
		
		if(productRequest.getLength() != null) {
			product.setLength(productRequest.getLength());
		}
		
		if(productRequest.getMaterial() != null) {
			product.setMaterial(productRequest.getMaterial());
		}
		
		if(productRequest.getMinCount() != null) {
			product.setMinCount(productRequest.getMinCount());
		}
		
		if(productRequest.getPrice() != null) {
			product.setPrice(productRequest.getPrice());
		}
		
		if(productRequest.getWeight() != null) {
			product.setWeight(productRequest.getWeight());
		}
		
		if(productRequest.getWidth() != null) {
			product.setWidth(productRequest.getWidth());
		}
		
		return new ResponseEntity<>(new ProductDTO(productRepository.save(product), minioService), HttpStatus.OK);
	}
	
	public void setImages(Product product, List<MultipartFile> images) {
		Set<Image> imgs = createImages(images);
		imgs.stream().forEach(img -> product.addImage(img));
	}
	
	public Set<Image> createImages(List<MultipartFile> images){
		Set<Image> imgs = new HashSet<>();
		int i = 0;
		for(MultipartFile file : images) {
			String fileName = minioService.uploadFile(file);
			Image img = new Image();
			img.setFileName(fileName);
			img.setPriority(++i);
			imgs.add(img);
		}
		
		return imgs;
	}
}
