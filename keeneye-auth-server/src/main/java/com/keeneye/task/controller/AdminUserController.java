package com.keeneye.task.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.keeneye.task.dto.UserDto;
import com.keeneye.task.service.IUserService;

/**
 * 
 * @author darsh
 */

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

	private final IUserService userService;

	public AdminUserController(IUserService userService) {
		this.userService = userService;
	}

	/**
	 * 
	 * @param userDto
	 * @return created user
	 */
	@PostMapping
	public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userDto));
	}

	/**
	 * 
	 * @param userDto
	 * @return updated user
	 */
	@PutMapping
	public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto) {
		return ResponseEntity.ok(userService.updateUser(userDto));
	}

	/**
	 * 
	 * @param username
	 * @return 200 OK for successful deletion
	 */
	@DeleteMapping("/{username}")
	public ResponseEntity<Void> deleteUser(@PathVariable String username) {
		userService.deleteUser(username);
		return ResponseEntity.ok().build();
	}

	/**
	 * 
	 * @return all non deleted user
	 */
	@GetMapping
	public ResponseEntity<List<UserDto>> listAllUsers() {
		return ResponseEntity.ok(userService.listAllUsers());
	}

}
