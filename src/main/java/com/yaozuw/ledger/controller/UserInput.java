package com.yaozuw.ledger.controller;


import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.yaozuw.ledger.annotation.ValueOfEnum;
import com.yaozuw.ledger.entities.SummaryFrequency;
import com.yaozuw.ledger.entities.User;

@Configurable
public class UserInput {

	//The first letter couldn't be _. Only contains a-zA-Z0-9_
	@NotNull
	@Size(min=3, max=30)
	@Pattern(regexp="^(?!_)\\w+$")
	private String username;
	
	//Must contain a lower letter, a upper letter, a number and a special character
	@NotNull
	@Size(min=8, max=20)
	@Pattern(regexp="^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[*.!@$%^&(){}\\[\\]:;<>,.?/~_+\\-=|\\\\]).*$")
	//Modified from https://www.ocpsoft.org/tutorials/regular-expressions/password-regular-expression/
	//^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[*.!@$%^&(){}\[\]:;<>,.?/~_+\-=|\\]).*$
	private String password;
	
	@NotNull
	@ValueOfEnum(enumClass = SummaryFrequency.class)
	private String frequency;
	
	public User convertedToUser(PasswordEncoder encoder) {
		
		User user = new User();
		return this.updateUser(user, encoder);
	}
	
	public User updateUser(User user, PasswordEncoder encoder) {
		
		user.setUsername(this.getUsername());
		
		String encodedPassword = encoder.encode(this.getPassword());
		user.setPassword(encodedPassword);
		
		SummaryFrequency summaryFrequency = SummaryFrequency.valueOf(this.getFrequency());
		user.setFrequency(summaryFrequency);
		
		return user;
		
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return the frequency
	 */
	public String getFrequency() {
		return frequency;
	}

}
