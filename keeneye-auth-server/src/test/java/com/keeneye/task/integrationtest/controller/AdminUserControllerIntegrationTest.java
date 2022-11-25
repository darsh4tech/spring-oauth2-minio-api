package com.keeneye.task.integrationtest.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keeneye.task.dto.UserDto;
import com.keeneye.task.entity.UserData;
import com.keeneye.task.repository.IUserRepository;
import com.keeneye.task.service.impl.UserServiceImpl;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class AdminUserControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserServiceImpl userService;

	@Autowired
	private IUserRepository userRepository;
	
	UserData userData;
	UserDto userDto;
	UserDto userDtoUpdated;

	@BeforeEach
	public void init() {

		userData = UserData.builder().username("test1").password("encoded_pass").email("test@test.com").deleted(false)
				.build();
		userDto = UserDto.builder().username("test1").password("1234").email("test@test.com").build();
	}

	@AfterEach
	public void clear() {
		userRepository.deleteAll();
	}
	
	@Test
    public void AdminUserController_CreateUser_ReturnCreated() throws Exception {
        
        mockMvc.perform(post("/admin/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
        		.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("$.username").value(userDto.getUsername()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

                
    }
	
	@Test
    public void AdminUserController_GetAllUsers_ReturnUserDto() throws Exception {
        
		userService.createUser(userDto);
		
        mockMvc.perform(get("/admin/users")
                .contentType(MediaType.APPLICATION_JSON))
        		.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].username").value(userDto.getUsername()))
                .andExpect(jsonPath("$[0].email").value(userDto.getEmail()));
    }
	
	@Test
    public void AdminUserController_updateUser() throws Exception {
        
		userService.createUser(userDto);

		userDtoUpdated = UserDto.builder().username("test-update").password("1234").email("test-update@test.com").build();
		
        mockMvc.perform(put("/admin/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDtoUpdated)))
        		.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.username").value(userDtoUpdated.getUsername()))
                .andExpect(jsonPath("$.email").value(userDtoUpdated.getEmail()));
        
    }
	
	@Test
    public void PokemonController_DeleteUser() throws Exception {
        
		userService.createUser(userDto);
		
        mockMvc.perform(delete("/admin/users/{username}",userDto.getUsername())
                .contentType(MediaType.APPLICATION_JSON))
        		.andExpect(MockMvcResultMatchers.status().isOk());
        
    }
	
}
