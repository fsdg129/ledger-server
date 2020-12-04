package com.yaozuw.ledger.config;
//The following code is modified from https://github.com/fsdg129/logistics-server-spring-boot,
//which is a project developed by the author before

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

import com.yaozuw.ledger.entities.User;
import com.yaozuw.ledger.service.UserRepository;


@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableGlobalMethodSecurity(
		  prePostEnabled = true, 
		  securedEnabled = true, 
		  jsr250Enabled = true)
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {
	
	@Autowired
	UserRepository userRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        
        http
        	.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    		// Configure CORS
        	.and()
        		.cors()
	    	//Disable CSRF protection	
	        .and()
        		.csrf().disable()
        	// Configure Spring Security to require HTTPS requests	
        		.requiresChannel()
        		.anyRequest()
        		.requiresSecure()
        	//Configure the method of authentication
        	.and()
        		.httpBasic()
        	.and()
        		.authorizeRequests()
        			.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        			.antMatchers(HttpMethod.POST, "/users").permitAll()
        			.antMatchers(HttpMethod.GET, "/users/usernames/**").permitAll()
        			.anyRequest().authenticated();


    }
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    	
    	UserDetailsService userDetailsService = new UserDetailsService() {
    		
    		@Override
			public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
    			
    			List<User> userList = userRepository.findByUsername(username);
    			if(userList.isEmpty()) {
    				throw new UsernameNotFoundException(username);
    			}
    			User user = userList.get(0);
    			return new UserAdapter(user);
    		}
    		
    		
    	};
        auth.userDetailsService(userDetailsService).passwordEncoder(
        		PasswordEncoderFactories.createDelegatingPasswordEncoder()
        		);

          
    }

}
