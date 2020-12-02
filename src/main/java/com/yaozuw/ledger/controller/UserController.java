package com.yaozuw.ledger.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.yaozuw.ledger.config.UserAdapter;
import com.yaozuw.ledger.entities.User;
import com.yaozuw.ledger.service.UserRepository;


@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	UserRepository userRepository;
	
	@PreAuthorize("permitAll()")
	@PostMapping("/")
	public ResponseTemplate register(
			@Valid @RequestBody UserInput userInput, 
			BindingResult result) {
		
		this.verifyInput(result, userInput.getUsername());
		
		User user = userInput.convertedToUser();	
		
		return this.saveUser(user);
				
	}
	
	@PreAuthorize("hasAuthority('VISITOR')")
	@GetMapping("/user")
	public ResponseTemplate getCurrentUser(@AuthenticationPrincipal UserAdapter userAdapter) {
		
		User wantedUser = userAdapter.getCostomerUser();
		
		return new ResponseTemplate("succeeded", "", "", 0, wantedUser);

	}	
	
	@PreAuthorize("hasAuthority('VISITOR') and #userId == #userAdapter.costomerUser.id")
	@GetMapping("/{userId:\\d+}")
	public ResponseTemplate getUserById(
			@PathVariable("userId") Long userId, 
			@AuthenticationPrincipal UserAdapter userAdapter) {
		User fetchedUser = this.fetchUserById(userId); 
		return new ResponseTemplate("succeeded", "", "", 0, fetchedUser);
	}
	
	@PreAuthorize("permitAll()")
	@GetMapping("/usernames/{username}")
	public ResponseTemplate getUserByUsername(@PathVariable String username) {

		List<User> userList = userRepository.findByUsername(username);
		if(userList.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "the username doesn't exist");
		} else {
			return new ResponseTemplate("succeeded", "", "", 0, username);
		}
	}
	
	@PreAuthorize("hasAuthority('VISITOR') and #userId == #userAdapter.costomerUser.id")
	@DeleteMapping("/{userId:\\d{1,18}}")
	public ResponseTemplate deleteUser(
			@PathVariable("userId") Long userId, 
			@AuthenticationPrincipal UserAdapter userAdapter) {
		User fetchedUser = this.fetchUserById(userId); 
		userRepository.delete(fetchedUser);
		return new ResponseTemplate("succeeded", "", "", 0, new Object());
	}
	
	@PreAuthorize("hasAuthority('VISITOR') and #userId == #userAdapter.costomerUser.id")
	@PutMapping("/{userId:\\d{1,18}}")
	public ResponseTemplate updateUsername(
			@PathVariable("userId") Long userId, 
			@AuthenticationPrincipal UserAdapter userAdapter,
			@Valid @RequestBody UserInput userInput, 
			BindingResult result) {
		
		this.verifyInput(result, userInput.getUsername());
		User fetchedUser = this.fetchUserById(userId);	
		User updatedUser = userInput.updateUser(fetchedUser);	
		
		return this.saveUser(updatedUser);
	}
	
	private ResponseTemplate saveUser(User user) {
		
		User savedUser;
		try {
			savedUser = userRepository.save(user);
		} catch(Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error");
		}	
		
		return new ResponseTemplate("succeeded", "", "", 0, savedUser);
	}
	
	private void verifyInput(BindingResult result, String username) {
		
		//Check input
		if(result.hasErrors()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The input is not valid");
		}
		
		//Check whether the username is repeated
		List<User> duplicatedUserList = userRepository.findByUsername(username);
		if(duplicatedUserList.isEmpty() == false) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The username has been used");
		}
		
		return;
	}
	
	private User fetchUserById(Long userId) {
		
		Optional<User> wantedUser = userRepository.findById(userId);
		if(wantedUser.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "the ID doesn't exist");
		} 
		return wantedUser.get();
	}
	
}
