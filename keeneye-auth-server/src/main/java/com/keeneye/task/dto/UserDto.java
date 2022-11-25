package com.keeneye.task.dto;

import java.time.Instant;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class UserDto {
	
	@NotBlank
	private String username;
	@NotBlank
	private String password;
	@Email
	@NotBlank
	private String email;
	private Instant lastLoginTime;
	
}
