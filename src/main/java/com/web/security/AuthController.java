package com.web.security;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.google.common.net.HttpHeaders;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {
	
	private JwtUtils jwtUtils;
	private CustomUserDetailsService userDetailsService;
	private PasswordEncoder encoder;
	
	public AuthController(JwtUtils jwtUtils, CustomUserDetailsService userService, PasswordEncoder encoder) {
		this.jwtUtils = jwtUtils;
		userDetailsService = userService;
		this.encoder = encoder;
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response){
		if(Objects.isNull(request) || Objects.isNull(request.getUsername()) || Objects.isNull(request.getPassword())) {
			return ResponseEntity.badRequest().build();
		}
		try {
			UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
			if(BCrypt.checkpw(request.getPassword(), userDetails.getPassword())) {
				String access_token = jwtUtils.generateAccessToken(userDetails.getUsername());
				String refresh_token = jwtUtils.generateRefreshToken(userDetails.getUsername());
				jwtUtils.addTokenToCookie(response, refresh_token);
				return ResponseEntity.ok().body(access_token);
			} else {
				return ResponseEntity.badRequest().build();
			}
		}catch(UsernameNotFoundException exc) {
			return ResponseEntity.notFound().build();
		}
	}
	
	@PostMapping("/refresh-token")
	public ResponseEntity<?> refreshToken(@CookieValue(name="authToken", required = false) String refreshToken){
		if(Objects.isNull(refreshToken) || !jwtUtils.validateRefreshToken(refreshToken)) {
			return ResponseEntity.badRequest().body("Invalid token");
		}
		String username = jwtUtils.getUsernameFromRefreshToken(refreshToken);
		String newAccessToken = jwtUtils.generateAccessToken(username);
		return ResponseEntity.ok(newAccessToken);
	}
	
	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletResponse response){
		ResponseCookie cookie = ResponseCookie.from("authToken", null)
				.secure(true) //Change to "true"
				.path("/api/auth/refresh-token")
				.maxAge(0)
				.sameSite("Strict")
				.httpOnly(true)
				.build();

		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
	}
	
	@GetMapping("/check")
	public ResponseEntity<?> checkAuth(){
		return ResponseEntity.ok().build();
	}
	
	
	
}
