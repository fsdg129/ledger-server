package com.yaozuw.ledger.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.repository.CrudRepository;

import com.yaozuw.ledger.entities.Record;
import com.yaozuw.ledger.entities.User;

public interface RecordRepository extends CrudRepository<Record, Long>{
	
	List<Record> findAllByDateBetween(LocalDate dateStart, LocalDate dateEnd);
	
	@Cacheable("recordsFromUser#a0.id")
	List<Record> findAllByUserAndDateBetween(User user, LocalDate dateStart, LocalDate dateEnd);
	
	@Override
	@Cacheable("record")
	Optional<Record> findById(Long id);
	
	@Override
	@CachePut("record")
	@CacheEvict(value="recordsFromUser#a0.user.id", allEntries=true)
	<S extends Record> S save(S entity);
	
	@Override
	@Caching(evict = { @CacheEvict("record"), @CacheEvict(value="recordsFromUser#a0.user.id", allEntries=true) })
	void delete(Record entity);

}
