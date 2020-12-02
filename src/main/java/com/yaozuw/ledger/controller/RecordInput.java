package com.yaozuw.ledger.controller;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.yaozuw.ledger.entities.User;
import com.yaozuw.ledger.entities.Record;

public class RecordInput {

	@NotNull
	@Digits(integer = 10, fraction = 2)
	private BigDecimal amount;
	
	@Pattern(regexp="^\\w+$")
	@Size(max=20)
	private String tag;
	
	@Pattern(regexp="^\\w+$")
	@Size(max=50)
	private String description;
	
	@NotNull
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private LocalDate date;
	
	public Record convertedToRecord(User user) {
		
		Record r = new Record(user);
		return this.updateRecord(r);
		
	}
	
	public Record updateRecord(Record record) {
		
		record.setAmount(this.amount);
		record.setDate(this.date);
		record.setTag(this.tag);
		record.setDescription(this.description);
		
		return record;
		
	}
	
	
	
}
