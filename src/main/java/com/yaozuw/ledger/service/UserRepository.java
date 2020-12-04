package com.yaozuw.ledger.service;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

import com.yaozuw.ledger.entities.User;

public interface UserRepository extends CrudRepository<User, Long> {

	List<User> findByUsername(String username);
	
	@Override
	@Cacheable(value="user", key="#id")
	Optional<User> findById(Long id);
	
	@Override
	@CachePut(value="user", key="#entity.id", condition="#entity.id != null")
	<S extends User> S save(S entity);
	
	@Override
	@CacheEvict(value="user", key="#entity.id")
	void delete(User entity);
}
