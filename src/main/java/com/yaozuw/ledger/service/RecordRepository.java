package com.yaozuw.ledger.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

import com.yaozuw.ledger.entities.Record;
import com.yaozuw.ledger.entities.User;

public interface RecordRepository extends CrudRepository<Record, Long>{
	
	List<Record> findAllByDateBetween(LocalDate dateStart, LocalDate dateEnd);
	
	List<Record> findAllByUserAndDateBetween(User user, LocalDate dateStart, LocalDate dateEnd);
	
	@Override
	@Cacheable(value="record", key="#id")
	Optional<Record> findById(Long id);
	
	@Override
	@CachePut(value="record", key="#entity.id", condition="#entity.id != null")
	<S extends Record> S save(S entity);
	
	@Override
	@CacheEvict(value="record", key="#entity.id")
	void delete(Record entity);

}
