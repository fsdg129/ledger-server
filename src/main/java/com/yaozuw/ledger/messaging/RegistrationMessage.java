package com.yaozuw.ledger.messaging;

import com.yaozuw.ledger.entities.User;

public class RegistrationMessage {
	
	private String email;
	private String username;
	private long timestamp;
	
	public static RegistrationMessage of(User user) {
		var msg = new RegistrationMessage(user);
		return msg;
	}
	
	/**
	 * 
	 */
	public RegistrationMessage(User user) {
		super();
		this.username = user.getUsername();
		this.email = user.getEmail();
		this.timestamp = System.currentTimeMillis();
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return String.format("[RegistrationMessage: email=%s, name=%s, timestamp=%s]", email, username, timestamp);
	}
	
	
}
