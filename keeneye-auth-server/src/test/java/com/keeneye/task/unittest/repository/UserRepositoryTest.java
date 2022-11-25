package com.keeneye.task.unittest.repository;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.keeneye.task.entity.UserData;
import com.keeneye.task.repository.IUserRepository;

@DataJpaTest(properties = { "spring.liquibase.enabled=false" })
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTest {

	@Autowired
	private IUserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Test
	public void UserRepository_SaveAll_ReturnSavedUser() {

		UserData userData = UserData.builder().username("test1").password(passwordEncoder.encode("1234"))
				.email("test@test.com").deleted(false).build();

		UserData userDataSaved = userRepository.save(userData);

		// Assert
		Assertions.assertThat(userDataSaved).isNotNull();
		Assertions.assertThat(userDataSaved.getUserId()).isGreaterThan(0);

	}

	@Test
	public void UserRepository_GetAll_ReturnMoreThenOneUser() {

		UserData userData_1 = UserData.builder().username("test1").password(passwordEncoder.encode("1234"))
				.email("test@test.com").deleted(false).build();

		userRepository.save(userData_1);

		UserData userData_2 = UserData.builder().username("test_2").password(passwordEncoder.encode("1234"))
				.email("test_2@test.com").deleted(false).build();

		userRepository.save(userData_2);

		List<UserData> list = userRepository.findAll();

		Assertions.assertThat(list).isNotNull();
		Assertions.assertThat(list.size()).isEqualTo(2);

	}

	@Test
    public void UserRepository_FindByName_ReturnUserNotNull() {

		UserData userData_1 = UserData.builder().username("test1").password(passwordEncoder.encode("1234"))
				.email("test@test.com").deleted(false).build();

		userRepository.save(userData_1);

        UserData userData = userRepository.findByUsernameAndDeletedIsFalse(userData_1.getUsername()).get();

        Assertions.assertThat(userData).isNotNull();
        
    }
	
	@Test
    public void UserRepository_UserData_Delete_ReturnUserIsEmpty() {

		UserData userData_1 = UserData.builder().username("test1").password(passwordEncoder.encode("1234"))
				.email("test@test.com").deleted(false).build();

		UserData userData = userRepository.save(userData_1);

		userRepository.deleteById(userData.getUserId());
        Optional<UserData> userDataOptional = userRepository.findById(userData.getUserId());

        Assertions.assertThat(userDataOptional).isEmpty();
    }
}
