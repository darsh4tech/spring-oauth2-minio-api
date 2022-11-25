package com.keeneye.resourceserver.service.impl;

import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.keeneye.resourceserver.dto.ProductDto;
import com.keeneye.resourceserver.service.IProductService;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MinIOFileService {

	@Value("${minio.bucket}")
	private String bucketName;
	
	private final IProductService productService;
	
    private MinioClient minioClient;

    public MinIOFileService(@Value("${minio.endpoint}") String endpoint,
                            @Value("${minio.accessKey}") String accessKey,
                            @Value("${minio.secretKey}") String secretKey, @Lazy IProductService productService) {
        initializeClient(endpoint, accessKey, secretKey);
        this.productService = productService;
    }

    private void initializeClient(String endpoint, String accessKey, String secretKey) {
        try {
            this.minioClient = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();
        } catch (Exception e) {
            log.error("can't initialize minIO client for this reason -> {}", e.getMessage());
        }
    }

    public String uploadFile(String fileName, Integer productId, InputStream inputStream) {
    	String objectId = "";
        try {
           if(! minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())){
               minioClient.makeBucket(MakeBucketArgs.builder()
                       .bucket(bucketName)
                       .build());
           }
           
           /**
            * check if there is a product for provided productId 
            */
           ProductDto productDto = this.productService.findById(productId);
           if(productDto == null) {
        	   throw  new ResponseStatusException(HttpStatus.BAD_REQUEST,"Can't UPLOAD FILE as No Product registerd for this file");
           }
           
           objectId = productId + "/" + UUID.randomUUID()+ "_" + fileName;
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(objectId).stream(
                            inputStream, inputStream.available(), -1)
                            .build());
            productDto.setImageUrl(objectId);
            this.productService.updateProduct(productDto);
       } catch (Exception e) {
            log.error("can't upload this file with id-> {} , for this reason -> {}", fileName, e.getMessage());
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST,"Can't UPLOAD FILE");
        }
        return objectId;
    }

    public Optional<InputStream> retrieveFile(String objectId) {

        try {
            InputStream inputStream =
                    minioClient.getObject(
                            GetObjectArgs.builder().bucket(bucketName).object(objectId).build());

            return Optional.of(inputStream);
        } catch (Exception e) {
            log.error("can't retrieve this file with id -> {} , for this reason -> {}", objectId, e.getMessage());
        }

        return Optional.empty();
    }

    public Boolean removeFile(String objectId) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectId).build());
            ProductDto productDto = this.productService.findByImageUrl(objectId);
            productDto.setImageUrl("");
            this.productService.updateProduct(productDto);
            return true;
        } catch (Exception e) {
            log.error("can't remove this file with id -> {} , for this reason -> {}", objectId, e.getMessage());
            return false;
        }
    }
}
