package com.keeneye.task.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.keeneye.task.dto.UserDto;

public interface IUserService {

	public UserDto createUser(UserDto userDto);
	
	public UserDto updateUser(UserDto userDto);
	
	public void deleteUser(String username);
	
	public List<UserDto> listAllUsers();
	
}
