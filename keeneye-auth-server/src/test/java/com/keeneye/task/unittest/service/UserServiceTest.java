package com.keeneye.task.unittest.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.keeneye.task.advice.DuplicateFoundException;
import com.keeneye.task.dto.UserDto;
import com.keeneye.task.entity.UserData;
import com.keeneye.task.repository.IUserRepository;
import com.keeneye.task.service.impl.UserServiceImpl;
import com.keeneye.task.utils.ObjectMapperUtils;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceTest {

	@Mock
	BCryptPasswordEncoder passwordEncoder;
	
	@Mock
	ObjectMapperUtils mapperUtils;
	
	@Mock
	IUserRepository userRepository;
	
	@InjectMocks
	UserServiceImpl userServiceImpl;

	UserDto userDto = null;
	UserData userData = null;

	@BeforeEach
	public void init() {
		
		userData = UserData.builder().username("test1").password("encoded_pass")
				.email("test@test.com").deleted(false).build();
		userDto = UserDto.builder().username("test1").password("1234").email("test@test.com").build();
		
	}
	
	@Test
    public void UserService_CreateUser_ReturnsUserDto() {

		when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encoded_value");
		when(mapperUtils.map(Mockito.any(UserDto.class), Mockito.any())).thenReturn(userData);
		when(userRepository.save(Mockito.any(UserData.class))).thenReturn(userData);
		when(mapperUtils.map(Mockito.any(UserData.class), Mockito.any())).thenReturn(userDto);

        UserDto userDto_saved = userServiceImpl.createUser(userDto);

        Assertions.assertThat(userDto_saved).isNotNull();
        assertEquals(userDto.getUsername(), userDto_saved.getUsername());
        assertEquals(userDto.getEmail(), userDto_saved.getEmail());
    }

	@Test
    public void UserService_CreateUser_ReturnsThrowDuplicateException() {

		when(userRepository.findByUsernameAndDeletedIsFalse(Mockito.anyString())).thenReturn(Optional.of(userData));
		when(userRepository.findByEmailAndDeletedIsFalse(Mockito.anyString())).thenReturn(Optional.of(userData));

        assertThrows(DuplicateFoundException.class, () -> userServiceImpl.createUser(userDto));
        
    }
	
	@Test
    public void UserService_listAllUsers_ReturnsResponseDto() {
		
        when(userRepository.findByDeletedIsFalse()).thenReturn(List.of(userData));

        List<UserDto> allUsersRS = userServiceImpl.listAllUsers();

        Assertions.assertThat(allUsersRS).isNotNull();

    }
	
	@Test
    public void UserService_UpdateUser_ReturnUserDto() {
        
		when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encoded_value");
		when(mapperUtils.map(Mockito.any(UserDto.class), Mockito.any())).thenReturn(userData);
		when(userRepository.save(Mockito.any(UserData.class))).thenReturn(userData);
		
		UserDto userDtoUpdated = UserDto.builder().username("test-update").password("1234").email("test@test.com").build();
		when(mapperUtils.map(Mockito.any(UserData.class), Mockito.any())).thenReturn(userDtoUpdated);
		
        UserDto dto = userServiceImpl.updateUser(userDtoUpdated);

        Assertions.assertThat(dto).isNotNull();
        assertNotEquals(userDto.getUsername(), dto.getUsername());
        assertEquals(userDto.getEmail(), dto.getEmail());

    }
	
	@Test
    public void PokemonService_DeletePokemonById_ReturnVoid() {

		when(userRepository.findByUsernameAndDeletedIsFalse(Mockito.anyString())).thenReturn(Optional.ofNullable(userData));
        doNothing().when(userRepository).delete(userData);

        assertAll(() -> userServiceImpl.deleteUser(userDto.getUsername()));
    }
}
