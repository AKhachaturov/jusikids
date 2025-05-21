package com.web.security;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import com.google.common.net.HttpHeaders;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtUtils {
	
	@Value("${jwt.access_secret}")
	private String access_secret;
	
	@Value("${jwt.refresh_secret}")
	private String refresh_secret;
	
	@Value("${jwt.access_token_expiration}")
	private int access_token_expiration;
	
	@Value("${jwt.refresh_token_expiration}")
	private int refresh_token_expiration;
	
	private Key getSigningKey(String secret) {
		return Keys.hmacShaKeyFor(secret.getBytes());
	}
	
	public String generateAccessToken(String username) {
		return generateToken(username, access_secret, access_token_expiration);
	}
	
	public String generateRefreshToken(String username) {
		return generateToken(username, refresh_secret, refresh_token_expiration);
	}
	
	private String generateToken(String username, String secret, int expiration) {
		return Jwts.builder()
				.setSubject(username)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L))
				.signWith(getSigningKey(secret))
				.compact();
	}
	
	
	public boolean validateAccessToken(String token) {
		return validateToken(token, access_secret);
	}
	
	public boolean validateRefreshToken(String token) {
		return validateToken(token, refresh_secret);
	}
	
	private boolean validateToken(String token, String secret) {
		try {
			Jwts.parserBuilder()
				.setSigningKey(getSigningKey(secret))
				.build()
				.parseClaimsJws(token);
			return true;
		} catch(Exception exc) {
			return false;
		}
	}
	
	public String getUsernameFromAccessToken(String token) {
		return getUsernameFromToken(token, access_secret);
	}
	
	public String getUsernameFromRefreshToken(String token) {
		return getUsernameFromToken(token, refresh_secret);
	}
	
	private String getUsernameFromToken(String token, String secret) {
		return Jwts.parserBuilder()
				.setSigningKey(getSigningKey(secret))
				.build()
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
	}
	
	public void addTokenToCookie(HttpServletResponse response, String token) {
		ResponseCookie cookie = ResponseCookie.from("authToken", token)
				.secure(true) //Change to "true"
				.path("/api/auth/refresh-token")
				.maxAge(refresh_token_expiration)
				.sameSite("Strict")
				.httpOnly(true)
				.build();
		
		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
	}
	
}



