package com.keeneye.task.advice;

import java.io.Serializable;
import java.time.Instant;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ApiError implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private HttpStatus status;
	private Instant timestamp;
	private String message;

}
