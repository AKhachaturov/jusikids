package com.web;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;

@Service
public class MinioService {
	
	private final MinioClient minioClient;
	private final String bucket;
	private final int expiration;
	
	public MinioService(@Value("${minio.url}") String url,
			@Value("${minio.accessKey}") String accessKey,
			@Value("${minio.secretKey}") String secretKey,
			@Value("${minio.bucket}") String bucket,
			@Value("${minio.expiration}") int expiration) {
		this.bucket = bucket;
		this.expiration = expiration;
		this.minioClient = MinioClient.builder()
				.endpoint(url).credentials(accessKey, secretKey).build();
	}
	
	public String uploadFile(MultipartFile file) {
		try {
			String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
			minioClient.putObject(
				PutObjectArgs.builder()
					.bucket(bucket)
					.object(fileName)
					.stream(file.getInputStream(), file.getSize(), -1)
					.contentType(file.getContentType()).build()
			);
			return fileName;
		}catch(Exception exc) {
			throw new RuntimeException("File upload exception", exc);
		}
	}
	
	public String getPresignedUrl(String fileName){
		try {
			return minioClient.getPresignedObjectUrl(
				GetPresignedObjectUrlArgs.builder()
					.bucket(bucket)
					.object(fileName)
					.expiry(expiration, TimeUnit.SECONDS)
					.method(Method.GET)
					.build());
		}catch(Exception exc) {
			throw new RuntimeException("Url generation exception", exc);
		}
	}
	
	public void deleteFile(String fileName) {
		try {
			minioClient.removeObject(
				RemoveObjectArgs.builder()
					.bucket(bucket)
					.object(fileName)
					.build()
					
			);
		}catch(Exception exc) {
			throw new RuntimeException("Removing file exception", exc);
		}
	}
}





