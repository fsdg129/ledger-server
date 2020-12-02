package com.yaozuw.ledger.controller;

public class ResponseTemplate {
	public String status;
	public String developMessage;
	public String displayMessage;
	public int errorCode;
	public Object payload;
	
	public ResponseTemplate(String status, String developMessage, String displayMessage, 
			int errorCode, Object payload) {
		this.status = status;
		this.developMessage = developMessage;
		this.displayMessage = displayMessage;
		this.errorCode = errorCode;
		this.payload = payload;
	}
	
}
