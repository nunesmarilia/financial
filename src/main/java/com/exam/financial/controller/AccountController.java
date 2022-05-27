package com.exam.financial.controller;

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
		String returnEvent;

		switch ( event.getType().toUpperCase() ) {
			case "DEPOSIT":
				if (event.getDestination() != null && !event.getDestination().isEmpty()) {
					String returnDeposit = service.deposit(event);
					returnEvent = returnDeposit != null ? "{" + returnDeposit + "}" : null;
					break;

				} else {
					return new ResponseEntity<>("Destination Account is empty", HttpStatus.BAD_REQUEST);
				}

			case "WITHDRAW":
				if (event.getOrigin() != null && !event.getOrigin().isEmpty()) {
					String returnWithdraw = service.withdraw(event);
					returnEvent = returnWithdraw != null ? "{" + returnWithdraw + "}" : null;
					break;

				} else {
					return new ResponseEntity<>("Origin Account is empty", HttpStatus.BAD_REQUEST);
				}

			case "TRANSFER":
				if ((event.getDestination() != null && !event.getDestination().isEmpty()) && (event.getOrigin() != null && !event.getOrigin().isEmpty())) {
					returnEvent = service.transfer(event);
					break;

				} else {
					return new ResponseEntity<>("Destination Account is empty or Origin Account is empty", HttpStatus.BAD_REQUEST);
				}

			default:
				return new ResponseEntity<>( "Invalid transaction option", HttpStatus.BAD_REQUEST);
		}

		if (returnEvent == null)
			return new ResponseEntity<>("0", HttpStatus.NOT_FOUND);
		else if (!returnEvent.equalsIgnoreCase("{}")) {
			return new ResponseEntity<>(returnEvent, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>("{\"message\":\"Insufficient funds\"}", HttpStatus.BAD_REQUEST);
		}
	}
}
