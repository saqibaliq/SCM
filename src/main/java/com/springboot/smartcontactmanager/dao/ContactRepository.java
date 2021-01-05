package com.springboot.smartcontactmanager.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.springboot.smartcontactmanager.entities.Contact;
import com.springboot.smartcontactmanager.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer> {

	@Query(value = "SELECT * FROM Contact c WHERE c.user_user_Id = :user_Id ORDER BY c.c_id DESC", nativeQuery = true)
	public Page<Contact> findContactByUser(@Param("user_Id") int userId, Pageable pageable);

	public List<Contact> findBycNameContainingAndUser(String name, User user);
}
