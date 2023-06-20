package org.springframework.samples.petclinic.user;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


public interface UserRepository extends  CrudRepository<User, String>{
    List<User> findAll();

	@Query("SELECT u.username FROM User u ")
	public List<String> findUsernames();

	@Query("SELECT a.user FROM Authorities a WHERE a.authority = ?1")
	public List<User> findUserWithAuthority(String authority);
}
