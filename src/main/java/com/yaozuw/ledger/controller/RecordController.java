package com.yaozuw.ledger.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.yaozuw.ledger.config.UserAdapter;
import com.yaozuw.ledger.entities.User;
import com.yaozuw.ledger.entities.Record;
import com.yaozuw.ledger.service.RecordRepository;
import com.yaozuw.ledger.service.UserRepository;

@RestController
@RequestMapping("/users/{userId:\\d{1,18}}/records")

public class RecordController {
	
	@Autowired
	RecordRepository recordRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@PreAuthorize("hasAuthority('VISITOR') and #userId == #userAdapter.costomerUser.id")
	@PostMapping("")
	public ResponseTemplate createRecord(
			@AuthenticationPrincipal UserAdapter userAdapter,
			@PathVariable("userId") Long userId,
			@Valid @RequestBody RecordInput recordInput, 
			BindingResult result) {
		
		this.verifyInput(result);
		Optional<User> wantedUser = userRepository.findById(userId);
		if(wantedUser.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "the ID doesn't exist");
		} 
		Record record = recordInput.convertedToRecord(wantedUser.get());		 
		
		return this.saveRecord(record);
				
	}
	
	
	@PreAuthorize("hasAuthority('VISITOR') and #userId == #userAdapter.costomerUser.id")
	@GetMapping("")
	public ResponseTemplate getRecords(
			@AuthenticationPrincipal UserAdapter userAdapter,
			@PathVariable("userId") Long userId,
			@DateTimeFormat(iso = ISO.DATE) @RequestParam("dateStart") LocalDate localDateStart,
			@DateTimeFormat(iso = ISO.DATE) @RequestParam("dateEnd") LocalDate localDateEnd) {
		
		Optional<User> wantedUser = userRepository.findById(userId);
		if(wantedUser.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "the ID doesn't exist");
		}
		User user = wantedUser.get();
		
		List<Record> records = recordRepository.findAllByUserAndDateBetween(user, localDateStart, localDateEnd);
		if(records.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "the records doesn't exist");
		}  
		
		return new ResponseTemplate("succeeded", "", "", 0, records);
				
	}
	
	@PreAuthorize("hasAuthority('VISITOR') and #userId == #userAdapter.costomerUser.id")
	@GetMapping("/{recordId:\\d{1,18}}")
	public ResponseTemplate getRecord(
			@AuthenticationPrincipal UserAdapter userAdapter,
			@PathVariable("userId") Long userId,
			@PathVariable("recordId") Long recordId) {
		//Check input
		 
		Record record = this.fetchRecordById(recordId);
		if(record.getUser().getId().equals(userId) == false) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "the record is not belonged to you");
		}
		return new ResponseTemplate("succeeded", "", "", 0, record);
				
	}
	
	@PreAuthorize("hasAuthority('VISITOR') and #userId == #userAdapter.costomerUser.id")
	@PutMapping("/{recordId:\\d{1,18}}")
	public ResponseTemplate updateRecord(
			@AuthenticationPrincipal UserAdapter userAdapter,
			@PathVariable("userId") Long userId,
			@PathVariable("recordId") Long recordId,
			@Valid @RequestBody RecordInput recordInput, 
			BindingResult result) {
		  
		this.verifyInput(result);
		Record record = this.fetchRecordById(recordId);
		if(record.getUser().getId().equals(userId) == false) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "the record is not belonged to you");
		}		
		Record newRecord = recordInput.updateRecord(record);
		
		return this.saveRecord(newRecord);
				
	}
	
	@PreAuthorize("hasAuthority('VISITOR') and #userId == #userAdapter.costomerUser.id")
	@DeleteMapping("/{recordId:\\d{1,18}}")
	public ResponseTemplate deleteRecord(
			@AuthenticationPrincipal UserAdapter userAdapter,
			@PathVariable("userId") Long userId,
			@PathVariable("recordId") Long recordId) {
		 
		Record record = this.fetchRecordById(recordId);
		if(record.getUser().getId().equals(userId) == false) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "the record is not belonged to you");
		}
		recordRepository.delete(record);
		
		return new ResponseTemplate("succeeded", "", "", 0, "");
				
	}
	
	private ResponseTemplate saveRecord (Record record) {
		Record savedRecord;
		try {
			savedRecord = recordRepository.save(record);
		} catch(Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error");
		}		 
		
		return new ResponseTemplate("succeeded", "", "", 0, savedRecord);
		
	}
	
	private void verifyInput(BindingResult result) {
		
		//Check input
		if(result.hasErrors()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The input is not valid");
		}
		
		return;
	}

	private Record fetchRecordById(Long recordId) {
		Optional<Record> wantedRecord = recordRepository.findById(recordId);
		if(wantedRecord.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "the ID doesn't exist");
		}  
		
		return wantedRecord.get();
	}
}
