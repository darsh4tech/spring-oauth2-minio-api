package com.keeneye.task.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.keeneye.task.advice.DuplicateFoundException;
import com.keeneye.task.dto.MyUserPrincipal;
import com.keeneye.task.dto.UserDto;
import com.keeneye.task.entity.UserData;
import com.keeneye.task.repository.IUserRepository;
import com.keeneye.task.service.IUserService;
import com.keeneye.task.utils.ObjectMapperUtils;

/**
 * 
 * @author darsh {@summary implementation for CRUD operation for User Entity}
 */

@Service
@Transactional
public class UserServiceImpl implements UserDetailsService, IUserService {

	private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

	private final IUserRepository userRepository;

	private final BCryptPasswordEncoder encoder;

	private final ObjectMapperUtils objectMapperUtils;

	public UserServiceImpl(IUserRepository userRepository, BCryptPasswordEncoder encoder,
			ObjectMapperUtils objectMapperUtils) {
		this.userRepository = userRepository;
		this.encoder = encoder;
		this.objectMapperUtils = objectMapperUtils;
	}

	/**
	 * @param username
	 * @return {@code UserDetails} Spring USer Entity this function used by spring
	 *         framework to validate logged in user whether it is registered or not
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		log.info("loadUserByUsername - username: {}", username);
		log.info("loadUserByUsername - pass Encoder: {}", encoder.encode("1234"));
		Optional<UserData> byUsername = userRepository.findByUsernameAndDeletedIsFalse(username);
		log.info("byUsername: {}", byUsername);

		UserData user = userRepository.findByUsernameAndDeletedIsFalse(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
		log.info("User: {}", user);
		return new MyUserPrincipal(user);
	}

	/**
	 * encode user password before mapping it to entity then add it into DB
	 */
	@Override
	public UserDto createUser(UserDto userDto) {
		
		try {
			Optional<UserData> findByUsernameOptional = userRepository.findByUsernameAndDeletedIsFalse(userDto.getUsername());
			Optional<UserData> findByEmailOptional = userRepository.findByEmailAndDeletedIsFalse(userDto.getEmail());
			if (findByUsernameOptional.isEmpty() && findByEmailOptional.isEmpty()) {
				userDto.setPassword(encoder.encode(userDto.getPassword()));
				UserData userData = objectMapperUtils.map(userDto, UserData.class);
				userData = userRepository.save(userData);
				return objectMapperUtils.map(userData, UserDto.class);
			}else {
				throw new DuplicateFoundException("UserName or Email may Exist");
			}
		} catch (Exception e) {
			log.error("createUSer-error: ", e);
			throw e;
		}
	}

	/**
	 * encode user password before mapping it to entity then update it into DB
	 */
	@Override
	public UserDto updateUser(UserDto userDto) {
		try {
			Pattern BCRYPT_PATTERN = Pattern.compile("\\A\\$2a?\\$\\d\\d\\$[./0-9A-Za-z]{53}");
			
			// if password is plain text and not bcrypt encoded format
			if (!BCRYPT_PATTERN.matcher(userDto.getPassword()).matches()) {
				userDto.setPassword(encoder.encode(userDto.getPassword()));
			}
			UserData userData = objectMapperUtils.map(userDto, UserData.class);
			userData = userRepository.save(userData);
			return objectMapperUtils.map(userData, UserDto.class);
		} catch (Exception e) {
			throw e;
		}

	}

	/**
	 * soft delete for user entity by changing the field {@code delete}
	 */
	@Override
	public void deleteUser(String username) {
		try {
			Optional<UserData> findByUsernameOptional = userRepository.findByUsernameAndDeletedIsFalse(username);
			findByUsernameOptional.ifPresent(userData -> {
				userData.setDeleted(true);
				userRepository.save(userData);
			});
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * fetch all users from DBs
	 */
	@Override
	@Transactional(readOnly = true)
	public List<UserDto> listAllUsers() {
		try {
			List<UserDto> allUsers = objectMapperUtils.mapAll(userRepository.findByDeletedIsFalse(), UserDto.class);
			return allUsers;
		} catch (Exception e) {
			throw e;
		}
	}

}
