package com.example.aqua.Useraqua;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aqua.DTO.UserUpdateDTO;
import com.example.aqua.category.Category;
import com.example.aqua.exception.NotFoundException;

import jakarta.transaction.Transactional;

@Service
public class UserService {

	
	@Autowired
    private UserRepository userRepository;
	
	  // Get all users
    //@Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get user by ID
    //@Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    
    
    public User createuser(User user) {
        return userRepository.save(user);	
    }


    public User update(Long id, User updatedCategory) {
    	System.out.println(updatedCategory);
        return userRepository.findById(id)
                .map(user -> {
                	user.setNom(updatedCategory.getNom());
                	user.setPrenom(updatedCategory.getPrenom());
                	user.setTelephone(updatedCategory.getTelephone());
                	user.setAdresse(updatedCategory.getAdresse());

                   // category.setUser(updatedCategory.getUser());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new NotFoundException("Category not found with id " + id));
  	    }

    // Delete user
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Userr not found with id " + id);
        }
        userRepository.deleteById(id);
    }
	
    
}
