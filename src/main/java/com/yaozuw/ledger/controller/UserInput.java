package com.yaozuw.ledger.controller;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.yaozuw.ledger.annotation.ValueOfEnum;
import com.yaozuw.ledger.entities.Record;
import com.yaozuw.ledger.entities.SummaryFrequency;
import com.yaozuw.ledger.entities.User;

public class UserInput {

	@NotNull
	@Size(min=3, max=30)
	@Pattern(regexp="^(?!_)\\w+$")
	private String username;
	
	@NotNull
	@Size(min=8, max=20)
	@Pattern(regexp="^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[*.!@$%^&(){}[]:;<>,.?/~_+-=|\\])$")
	//Adapted from https://www.ocpsoft.org/tutorials/regular-expressions/password-regular-expression/
	private String password;
	
	@NotNull
	@ValueOfEnum(enumClass = SummaryFrequency.class)
	private String frequency;
	
	@Autowired
	private PasswordEncoder encoder;
	
	public User convertedToUser() {
		
		User user = new User();
		return this.updateUser(user);
	}
	
	public User updateUser(User user) {
		
		user.setUsername(this.username);
		
		String encodedPassword = encoder.encode(this.password);
		user.setPassword(encodedPassword);
		
		SummaryFrequency summaryFrequency = SummaryFrequency.valueOf(this.frequency);
		user.setFrequency(summaryFrequency);
		
		return user;
		
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

}
