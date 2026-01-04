package com.adobe.prj.exceptions;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import org.postgresql.util.PSQLException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import io.jsonwebtoken.JwtException;


/*
	Refactoring generale
	Migliorata l'indentazione delle funzioni
	Creato il metodo buildResponse per evitare ripetizioni di new ResponseEntity<>
	Rimosso il Redundant Comment in cima alla classe
*/
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	private ResponseEntity<Object> buildResponse(HttpStatus status, Object body) {
    return ResponseEntity.status(status).body(body);
	}

	@ExceptionHandler(SQLIntegrityConstraintViolationException.class)
	public ResponseEntity<String> handleSQLException(
					SQLIntegrityConstraintViolationException ex) {
		return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(PSQLException.class)
	public ResponseEntity<String> handlePSQLException(
					PSQLException ex) {
		return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(SQLException.class)
	public ResponseEntity<String> handleSQLException(SQLException ex) {
		return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<String> handleNotFoundException(
					NotFoundException ex) {
		return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<String> handleAccessDeniedException(
					AccessDeniedException ex) {
		return buildResponse(ex.getMessage(), HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<String> handleBadRequestException(BadRequestException ex) {
		return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<String> handleBadCredentialsException(
					BadCredentialsException ex) {
		return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(JwtException.class)
	// Refactoring
	// rinominata la funzione da handleExpiredJwtException a handleJwtException
	// in quanto non tratta solo JWT scaduti
	public ResponseEntity<String> handleJwtException(JwtException ex) {
		System.out.println(ex.getMessage());
		return buildResponse(ex.getMessage(), HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Object> handleConstraintViolationException(
					ConstraintViolationException ex) {
		System.out.println("");
		List<String> errors = ex.getConstraintViolations().stream()
													.map(x -> x.getMessage())
													.collect(Collectors.toList());

		return buildResponse(errors, HttpStatus.BAD_REQUEST);
	}

	@Override
	public ResponseEntity<Object> handleMethodArgumentNotValid(
					MethodArgumentNotValidException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {

		List<String> errors = ex.getBindingResult().getFieldErrors()
													.stream().map(x -> x.getDefaultMessage())
													.collect(Collectors.toList());
		return buildResponse(errors, HttpStatus.BAD_REQUEST);
	}
}
