package com.exam.financial.controller;

import com.exam.financial.services.AccountService;
import org.json.JSONException;
import org.json.JSONObject;

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
	public ResponseEntity<String> balance(@RequestParam("account_id") Long number) {

		Account account = service.findById( number );
		if (account == null)
			return new ResponseEntity<>("0", HttpStatus.NOT_FOUND);

		return new ResponseEntity<>( String.valueOf( account.getBalance() ), HttpStatus.OK);
	}

	@PostMapping("/event")
	public ResponseEntity<String> event(@RequestBody Event event) {
		String returnEvent;

		try {
			switch ( event.getType().toUpperCase() ) {
				case "DEPOSIT":
					if (event.getDestination() == 0)
						return new ResponseEntity<>("Destination Account is empty", HttpStatus.BAD_REQUEST);

					returnEvent = "{" + service.deposit(event) + "}";
					break;

				case "WITHDRAW":
					if (event.getOrigin() == 0)
						return new ResponseEntity<>("Origin Account is empty", HttpStatus.BAD_REQUEST);

					returnEvent = "{" + service.withdraw(event) + "}";
					break;

				case "TRANSFER":
					if (event.getDestination() == 0 || event.getOrigin() == 0)
						return new ResponseEntity<>("Destination Account is empty or Origin Account is empty", HttpStatus.BAD_REQUEST);

					returnEvent = service.transfer(event);
					break;

				default:
					return new ResponseEntity<>( "Invalid transaction option", HttpStatus.BAD_REQUEST);
			}

		} catch (JSONException e) {
			return new ResponseEntity<>( "Process error", HttpStatus.BAD_REQUEST);
		}

		if (returnEvent != null)
			return new ResponseEntity<>(returnEvent, HttpStatus.CREATED);
		else
			return new ResponseEntity<>("0", HttpStatus.NOT_FOUND);
	}
}
