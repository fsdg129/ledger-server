package com.yaozuw.ledger.config;
//The following code is modified from https://github.com/fsdg129/logistics-server-spring-boot,
//which is a project developed by the author before

import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class UserAdapter extends User {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8362685011489625650L;
	
	private com.yaozuw.ledger.entities.User costomerUser;
	
	private static Set<GrantedAuthority> authoritise = Set.of(new SimpleGrantedAuthority("VISITOR"));
	
	public UserAdapter(com.yaozuw.ledger.entities.User user) {
		
		super( user.getUsername(), user.getPassword(), UserAdapter.authoritise );
		this.costomerUser = user;
	}
	
	public com.yaozuw.ledger.entities.User getCostomerUser() {
		return this.costomerUser;
	}
}
