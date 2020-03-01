package com.sh.log.viewer.app.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class HandlerException {
	
	private static Logger LOGGER = LoggerFactory.getLogger(HandlerException.class);

	@ExceptionHandler(value=AppNotFoundException.class)
	public ResponseEntity<Object> handlerEx(AppNotFoundException ex){
		
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("message", "Application not found");
		body.put("status", HttpStatus.BAD_REQUEST.value());
		LOGGER.error("Error: ",ex);
		return new ResponseEntity<Object>(body, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(value=Exception.class)
	public ResponseEntity<Object> handlerEx(Exception ex){
		
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("message", "Internal server error");
		body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
		LOGGER.error("Error: ",ex);
		return new ResponseEntity<Object>(body, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
}
