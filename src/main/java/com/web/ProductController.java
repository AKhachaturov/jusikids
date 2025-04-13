package com.web;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.Product.Type;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path="/api/products",
				produces="application/json")
@CrossOrigin
@Validated
public class ProductController {
	
	private ProductRepository productRep;
	private MinioService minioService;
	private ProductService productService;
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	public ProductController(ProductRepository rep, MinioService minioService, ProductService productService) {
		productRep = rep;
		this.minioService = minioService;
		this.productService = productService;
	}
	
	@GetMapping
	public Page<ProductDTO> getAllProducts(@RequestParam(required=false) String type, @PageableDefault(size=20) @MaxPageSize(value=20) Pageable pageable) throws Exception{
		return productService.findAllProducts(type, pageable);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ProductDTO> getProduct(@PathVariable("id") Long id) {
		Optional<Product> product = productRep.findByIdWithImages(id);
		if(!product.isPresent()) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
		ProductDTO response = new ProductDTO(product.get(), minioService);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping(consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public ProductDTO addProduct(@RequestPart("product") String productJSON, @RequestPart("images") List<MultipartFile> images) throws Exception{
		ProductRequest productRequest = objectMapper.readValue(productJSON, ProductRequest.class);
		return productService.addProduct(new Product(productRequest), images);
	}
	
	@PatchMapping(path="/{id}", consumes="application/json")
	public ResponseEntity<ProductDTO> editProduct(@PathVariable("id") Long id, @RequestBody ProductRequest productRequest) {
		return productService.editProduct(id, productRequest);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteProduct(@PathVariable Long id) {
		Optional<Product> product = productRep.findById(id);
		if(!product.isPresent()) {
			return;
		}
		product.get().getImages().stream().forEach(img -> minioService.deleteFile(img.getFileName()));
		productRep.deleteById(id);
	}
	
	
	
	
}
