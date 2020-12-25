package com.yaozuw.ledger.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import com.yaozuw.ledger.messaging.RegistrationMessage;
import com.yaozuw.ledger.service.MessagingService;
import com.yaozuw.ledger.service.UserRepository;


@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder encoder;
	
	@Autowired
	private MessagingService messagingService;
	
	@PreAuthorize("permitAll()")
	@PostMapping("")
	public ResponseTemplate register(
			@Valid @RequestBody UserInput userInput, 
			BindingResult result) {
		this.verifyInput(result);
		this.checkRepeatedUsername(userInput.getUsername());
		
		User user = userInput.convertedToUser(this.getEncoder());	
		
		User registedUser = this.saveUser(user);
		messagingService.sendRegistrationMessage(RegistrationMessage.of(registedUser));
		
		return new ResponseTemplate("succeeded", "", "", 0, registedUser);
				
	}
	
	@PreAuthorize("hasAuthority('VISITOR')")
	@GetMapping("/user")
	public ResponseTemplate getCurrentUser(@AuthenticationPrincipal UserAdapter userAdapter) {
		
		User wantedUser = userAdapter.getCostomerUser();
		
		return new ResponseTemplate("succeeded", "", "", 0, wantedUser);

	}	
	
	@PreAuthorize("hasAuthority('VISITOR') and #userId == #userAdapter.costomerUser.id")
	@GetMapping("/{userId:\\d{1,18}}")
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
		this.getUserRepository().delete(fetchedUser);
		return new ResponseTemplate("succeeded", "", "", 0, "");
	}
	
	@PreAuthorize("hasAuthority('VISITOR') and #userId == #userAdapter.costomerUser.id")
	@PutMapping("/{userId:\\d{1,18}}")
	public ResponseTemplate updateUser(
			@PathVariable("userId") Long userId, 
			@AuthenticationPrincipal UserAdapter userAdapter,
			@Valid @RequestBody UserInput userInput, 
			BindingResult result) {
		
		this.verifyInput(result);
		User fetchedUser = this.fetchUserById(userId);	
		if(! userInput.getUsername().equals(fetchedUser.getUsername()) ) {
			this.checkRepeatedUsername(userInput.getUsername());
		}
		User updatedUser = userInput.updateUser(fetchedUser, this.getEncoder());	
		
		return new ResponseTemplate("succeeded", "", "", 0, this.saveUser(updatedUser));
	}
	
	private User saveUser(User user) {
		
		User savedUser;
		try {
			savedUser = this.getUserRepository().save(user);
		} catch(Exception e) {
			System.out.println(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error");
		}	
		
		return savedUser;
	}
	
	private void verifyInput(BindingResult result) {
		
		//Check input
		if(result.hasErrors()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The input is not valid");
		}

		return;
	}
	
	private void checkRepeatedUsername(String username) {
		//Check whether the username is repeated
		List<User> duplicatedUserList = this.getUserRepository().findByUsername(username);
		if(duplicatedUserList.isEmpty() == false) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The username has been used");
		}
		
		return;
	}
	
	private User fetchUserById(Long userId) {
		
		Optional<User> wantedUser = this.getUserRepository().findById(userId);
		if(wantedUser.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "the ID doesn't exist");
		} 
		return wantedUser.get();
	}

	/**
	 * @return the userRepository
	 */
	public UserRepository getUserRepository() {
		return userRepository;
	}

	/**
	 * @return the encoder
	 */
	public PasswordEncoder getEncoder() {
		return encoder;
	}
	
	
	
}
