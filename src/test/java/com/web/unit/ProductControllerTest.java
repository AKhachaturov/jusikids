package com.web.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.test.context.support.WithMockUser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.Image;
import com.web.MinioService;
import com.web.Product;
import com.web.Product.Type;
import com.web.ProductController;
import com.web.ProductDTO;
import com.web.ProductRepository;
import com.web.ProductRequest;
import com.web.ProductService;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc
@SuppressWarnings("removal")
public class ProductControllerTest {
	@Autowired
	private MockMvc mockMvc;
		
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private ProductRepository repo;
	
	@MockBean
	private ProductService productService;
	
	@MockBean
	private MinioService minioService;
	
	ProductRequest productRequest;
	Product product;
	ProductDTO productDTO;
	
	@BeforeEach
	void setUp() {
		product = new Product();
		product.setId(1L);
		product.setName("Bib");
		product.setColor("blue");
		product.setMinCount(100);
		
		productDTO = new ProductDTO();
		productDTO.setId(1L);
		productDTO.setColor("blue");
		productDTO.setName("Bib");
		productDTO.setMinCount(100);
		
	}
	
	@Test
	@WithMockUser(authorities = "SCOPE_admin")
	void getAllProducts_ShouldReturnAllProducts() throws Exception{
		Pageable pageable = Pageable.ofSize(1);
		List<ProductDTO> products = List.of(productDTO);
		Mockito.when(productService.findAllProducts(any(String.class), any(Pageable.class))).thenReturn(new PageImpl<>(products, pageable, products.size()));
		mockMvc.perform(get("/api/products"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].color").value("blue"))
				.andExpect(jsonPath("$.content[0].name").value("Bib"))
				.andExpect(jsonPath("$.content[0].minCount").value(100));
	}
	
	@Test
	@WithMockUser(authorities = "SCOPE_admin")
	void getProductById_ShouldReturnProduct() throws Exception{
		Mockito.when(repo.findByIdWithImages(any(Long.class))).thenReturn(Optional.of(product));
		Mockito.when(minioService.getPresignedUrl(any(String.class))).thenReturn("url");
		
		mockMvc.perform(get("/api/products/1"))
		.andExpect(status().isOk())
		.andExpect(content().contentType("application/json"))
		.andExpect(jsonPath("$.id").value(1))
		.andExpect(jsonPath("$.name").value("Bib"))
		.andExpect(jsonPath("$.color").value("blue"))
		.andExpect(jsonPath("$.minCount").value(100));
	}
	
	@Test
	@WithMockUser(authorities = "SCOPE_admin")
	void addProduct_ShouldReturnCreatedProduct() throws Exception{
		
		Mockito.when(productService.addProduct(any(Product.class), anyList())).thenReturn(productDTO);
		String json = new String(Files.readAllBytes(Paths.get("src/test/java/com/web/unit/user.json")));
		
		
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
		
				
		mockMvc.perform(MockMvcRequestBuilders.multipart("/api/products")
				.file(file1)
				.file(file2)
				.part(new MockPart("product", json.getBytes(StandardCharsets.UTF_8)))
				.accept(MediaType.APPLICATION_JSON)
				.with(csrf()))
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Bib"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.color").value("blue"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.minCount").value(100));
	}
	
	@Test
	@WithMockUser(authorities = "SCOPE_admin")
	void delteProduct_ShouldReturnNoContent() throws Exception{
		Mockito.doNothing().when(repo).deleteById(any(Long.class));
		
		mockMvc.perform(delete("/api/products/{id}", 1L).with(csrf()))
			.andExpect(status().isNoContent());
	}
	
	@Test
	@WithMockUser(authorities = "SCOPE_admin")
	void editProduct_ShouldReturnNotFound_WhenInvalidId() throws Exception {
		Mockito.when(repo.findById(any(Long.class))).thenReturn(Optional.ofNullable(null));
		
		String json = new String(Files.readAllBytes(Paths.get("src/test/java/com/web/unit/user.json")));

		mockMvc.perform(patch("/api/products/{id}", 2L).contentType(MediaType.APPLICATION_JSON).content(json)
				.with(csrf()))
			.andExpect(status().isNotFound());
	}
	
}














