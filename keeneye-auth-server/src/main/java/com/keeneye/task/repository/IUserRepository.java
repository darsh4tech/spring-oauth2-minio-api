package com.keeneye.task.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.keeneye.task.entity.UserData;

/**
 * 
 * @author darsh
 *
 */
@Repository
public interface IUserRepository extends JpaRepository<UserData, Integer>{

	Optional<UserData> findByUsernameAndDeletedIsFalse(String username);
	Optional<UserData> findByEmailAndDeletedIsFalse(String email);
	List<UserData> findByDeletedIsFalse();
	
}
