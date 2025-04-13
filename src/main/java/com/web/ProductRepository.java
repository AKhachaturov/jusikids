package com.web;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.web.Product.Type;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>{
	
	@Query("SELECT p FROM Product p LEFT JOIN FETCH p.images WHERE p.id = :id")
	Optional<Product> findByIdWithImages(@Param("id") Long id);
	
	@EntityGraph(attributePaths = {"images"})
	Page<Product> findAllByType(Type type, Pageable pageable);
	
	@EntityGraph(attributePaths = {"images"})
	Page<Product> findAll(Pageable pageable);
}
