package com.keeneye.resourceserver.advice;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class ProductAdvice extends ResponseEntityExceptionHandler {

	@ExceptionHandler(Exception.class)
	ResponseEntity<ApiError> handleStatusException(Exception ex, WebRequest request) {
		log.info("Exception - Error: {}",ex);
		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ApiError.builder().status(HttpStatus.INTERNAL_SERVER_ERROR)
						.message(ex.getMessage())
						.timestamp(Instant.now())
						.build());
	}
	
	@ExceptionHandler(InvalidTokenException.class)
	ResponseEntity<ApiError> handleHttpServerErrorException(InvalidTokenException ex, WebRequest request) {
		log.info("InvalidTokenException - Error: {}",ex);
		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ApiError.builder().status(HttpStatus.INTERNAL_SERVER_ERROR)
						.message(ex.getMessage())
						.timestamp(Instant.now())
						.build());
	}
	
}
