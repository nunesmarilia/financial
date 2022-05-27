package com.exam.financial.services;

import com.exam.financial.dto.ReturnEvent;
import com.exam.financial.model.Account;
import com.exam.financial.model.Event;
import com.exam.financial.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AccountService {

	@Autowired
	AccountRepository repository;

	public ReturnEvent event(Event event){
		ReturnEvent returnEvent = new ReturnEvent();

		if( event.getType().equalsIgnoreCase("DEPOSIT") ) {
			if (event.getDestination() != null && !event.getDestination().isEmpty()) {
				returnEvent = deposit(event) ;
				returnEvent.setMessageSucess( "{" + returnEvent.getMessageSucess() +"}" );
				return returnEvent;

			} else {
				returnEvent.setMessageError("Destination Account is empty");
			}

		} else if( event.getType().equalsIgnoreCase("WITHDRAW") ){
			if (event.getOrigin() != null && !event.getOrigin().isEmpty()) {
				returnEvent = withdraw(event);
				returnEvent.setMessageSucess( "{" + returnEvent.getMessageSucess() +"}" );
				return returnEvent;

			} else {
				returnEvent.setMessageError("Origin Account is empty");
			}

		} else if( event.getType().equalsIgnoreCase("TRANSFER") ) {
			if ((event.getDestination() != null && !event.getDestination().isEmpty()) && (event.getOrigin() != null && !event.getOrigin().isEmpty())) {
				return transfer(event);

			} else {
				returnEvent.setMessageError("Destination Account is empty or Origin Account is empty");
			}

		} else {
			returnEvent.setMessageError("Invalid transaction option");
		}

		return returnEvent;
	}

	public ReturnEvent deposit(Event event) {
		ReturnEvent returnEvent = new ReturnEvent();
		Account account         = findById( event.getDestination() );
		if ( account == null ){
			account = new Account();
			account.setNumber( event.getDestination() );
			account = add( account );
		}

		account.setBalance( account.getBalance() + event.getAmount() );
		update( account );

		returnEvent.setMessageSucess("\"destination\":" + mountJson(account ));
		return returnEvent;
	}

	public ReturnEvent withdraw(Event event) {
		ReturnEvent returnEvent = new ReturnEvent();

		Account account = findById( event.getOrigin() );
		if ( account == null ){
			returnEvent.setMessageNotFound("0");
			return returnEvent;
		}

		if( event.getAmount() > account.getBalance() ){
			returnEvent.setMessageError("{\"message\":\"Insufficient funds\"}");
			return returnEvent;
		}

		account.setBalance( account.getBalance() - event.getAmount() );
		update( account );

		returnEvent.setMessageSucess("\"origin\":" + mountJson(account));
		return returnEvent;
	}

	public ReturnEvent transfer(Event event) {
		ReturnEvent returnEvent = new ReturnEvent();
		Account account         = findById( event.getOrigin() );
		if ( account == null ){
			returnEvent.setMessageNotFound("0");
			return returnEvent;
		}

		if( event.getOrigin().equals(event.getDestination()) ){
			returnEvent.setMessageError("{\"message\":\"Same destination and origin account\"}");
			return returnEvent;
		}

		if( event.getAmount() > account.getBalance() ) {
			returnEvent.setMessageError("{\"message\":\"Insufficient funds\"}");
			return returnEvent;
		}

		ReturnEvent returnDeposit  = deposit( event );
		ReturnEvent returnWithdraw = withdraw( event );
		returnEvent.setMessageSucess("{"+ returnWithdraw.getMessageSucess() +","+ returnDeposit.getMessageSucess() +"}");

		return returnEvent;
	}

	public String mountJson(Account account) {
		return "{\"id\":\""+ account.getNumber() + "\",\"balance\":"+ account.getBalance() +"}";
	}

	public Account add(Account account){
		return repository.add(account);
	}

	public Account findById(String number) {
		return repository.findById( number );
	}

	public void update(Account account) {
		repository.update(account);
	}

	public void deleteAll() {
		repository.deleteAll();
	}
}
