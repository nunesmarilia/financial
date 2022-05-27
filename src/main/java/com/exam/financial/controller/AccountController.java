package com.exam.financial.controller;

import com.exam.financial.dto.ReturnEvent;
import com.exam.financial.services.AccountService;

import com.exam.financial.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.exam.financial.model.Account;

@RestController
public class AccountController {

	@Autowired
	AccountService service;

	@PostMapping("/reset")
	public ResponseEntity<String> reset() {
		service.deleteAll();
		return new ResponseEntity<>("OK" , HttpStatus.OK);
	}

	@GetMapping("/balance")
	public ResponseEntity<String> balance(@RequestParam("account_id") String number) {

		Account account = service.findById( number );
		if (account == null)
			return new ResponseEntity<>("0", HttpStatus.NOT_FOUND);

		return new ResponseEntity<>( String.valueOf( account.getBalance() ), HttpStatus.OK);
	}

	@PostMapping("/event")
	public ResponseEntity<String> event(@RequestBody Event event) {
		ReturnEvent returnEvent = service.event(event);

		if (returnEvent.getMessageError() != null && !returnEvent.getMessageError().isEmpty())
			return new ResponseEntity<>(returnEvent.getMessageError(), HttpStatus.BAD_REQUEST);

		if (returnEvent.getMessageNotFound() != null && !returnEvent.getMessageNotFound().isEmpty())
			return new ResponseEntity<>(returnEvent.getMessageNotFound(), HttpStatus.NOT_FOUND);

		return new ResponseEntity<>(returnEvent.getMessageSucess(), HttpStatus.CREATED);
	}
}
