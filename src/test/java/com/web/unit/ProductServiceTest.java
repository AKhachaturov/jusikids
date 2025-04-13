package com.web.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.web.MinioService;
import com.web.Product;
import com.web.ProductDTO;
import com.web.ProductRepository;
import com.web.ProductRequest;
import com.web.ProductService;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
	@Mock
	private ProductRepository productRepository;
	
	@Mock
	private MinioService minioService;
	
	@InjectMocks
	private ProductService productService;
	
	@Test
	public void addProduct_ShouldReturnCreatedProduct() throws Exception {
		
		Product product = new Product();
		product.setId(1L);
		product.setName("test");
		product.setColor("blue");
		
		MockMultipartFile file1 = new MockMultipartFile(
	            "images", 
	            "image1.jpg", 
	            "image/jpeg", 
	            "content1".getBytes()
	        );
	        
	    MockMultipartFile file2 = new MockMultipartFile(
	            "images", 
	            "image2.jpg", 
	            "image/jpeg", 
	            "content2".getBytes()
	        );
	    
	    Mockito.when(productRepository.save(any(Product.class))).thenReturn(product);
	    Mockito.when(minioService.uploadFile(any(MultipartFile.class))).thenReturn("value");
		Mockito.when(minioService.getPresignedUrl(any(String.class))).thenReturn("url");
		
		ProductDTO result = productService.addProduct(product, List.of(file1, file2));
		
		assertEquals(1L, result.getId());
		assertEquals("test", result.getName());
		assertEquals("blue", result.getColor());
	}
	
}
