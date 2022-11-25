package com.keeneye.task.unittest.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keeneye.task.controller.AdminUserController;
import com.keeneye.task.dto.UserDto;
import com.keeneye.task.entity.UserData;
import com.keeneye.task.service.impl.UserServiceImpl;

@WebMvcTest(controllers = AdminUserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class AdminUserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserServiceImpl userService;

	@Autowired
	private ObjectMapper objectMapper;

	UserData userData;
	UserDto userDto;
	
	@BeforeEach
    public void init() {
		
		userData = UserData.builder().username("test1").password("encoded_pass")
				.email("test@test.com").deleted(false).build();
		userDto = UserDto.builder().username("test1").password("1234").email("test@test.com").build();
	}

	@Test
    public void AdminUserController_CreateUser_ReturnCreated() throws Exception {
        
		when(userService.createUser(any())).thenReturn(userDto);

        mockMvc.perform(post("/admin/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
        		.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("$.username").value(userDto.getUsername()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

        Mockito.verify(userService).createUser(Mockito.any(UserDto.class));
                
    }
	
	@Test
    public void AdminUserController_GetAllUsers_ReturnUserDto() throws Exception {
        
		List<UserDto> list = List.of(userDto);
        when(userService.listAllUsers()).thenReturn(list);

        mockMvc.perform(get("/admin/users")
                .contentType(MediaType.APPLICATION_JSON))
        		.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(list.size())))
                .andExpect(jsonPath("$[0].username").value(userDto.getUsername()))
                .andExpect(jsonPath("$[0].email").value(userDto.getEmail()));
    }
	
	@Test
    public void AdminUserController_UpdateAdminUser_ReturnAdminUserDto() throws Exception {
        
        when(userService.updateUser(any())).thenReturn(userDto);

        mockMvc.perform(put("/admin/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
        		.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.username").value(userDto.getUsername()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
        
    }
	
	@Test
    public void AdminUserController_DeleteAdminUser_ReturnString() throws Exception {
        
        doNothing().when(userService).deleteUser(anyString());

        mockMvc.perform(delete("/admin/users/{username}",userDto.getUsername())
                .contentType(MediaType.APPLICATION_JSON))
        		.andExpect(MockMvcResultMatchers.status().isOk());
        
    }
}
