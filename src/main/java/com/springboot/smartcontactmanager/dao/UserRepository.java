package com.springboot.smartcontactmanager.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.springboot.smartcontactmanager.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	@Query(value = "SELECT * FROM User u WHERE u.user_Email = :user_Email", nativeQuery = true)
	public User getUserByUserName(@Param("user_Email") String userEmail);
}
