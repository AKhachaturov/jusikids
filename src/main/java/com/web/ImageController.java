package com.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="/api/images",
				produces="application/json")
public class ImageController {
	
	private ImageRepository imageRep;
	
	public ImageController(ImageRepository imageRep) {
		this.imageRep = imageRep;
	}
	
	@GetMapping
	public List<Image> getAllImages(){
		return imageRep.findAll();
	}
}
