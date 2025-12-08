package com.example.aqua.Useraqua;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User,Long>{

		Optional<User> findByMail(String mail);

	    Optional<User> findByMailAndPassword(String mail, String password);
	    boolean existsByMail(String email);

}