package com.web;

import java.math.BigDecimal;
import java.util.Set;

import com.web.Product.Type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImageDTO {
	private String url;
	private int priority;
}
