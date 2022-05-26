package com.exam.financial.services;

import com.exam.financial.model.Account;
import com.exam.financial.model.Event;
import com.exam.financial.repository.AccountRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AccountService {

	@Autowired
	AccountRepository repository;

	public String deposit(Event event) throws JSONException {
		Account account = findById( event.getDestination() );
		if ( account == null ){
			account = new Account();
			account.setNumber( event.getDestination() );
			account = add( account );
		}

		account.setBalance( account.getBalance() + event.getAmount() );
		update( account );

		return mountJson("destination", account);
	}

	public String withdraw(Event event) {
		Account account = findById( event.getOrigin() );
		if ( account == null ){
			return null;
		}

		if( event.getAmount() > account.getBalance() ){
			return "{\"message\":\"Insufficient funds\"}";
		}

		account.setBalance( account.getBalance() - event.getAmount() );
		update( account );

		return mountJson("origin", account);
	}

	public String transfer(Event event) throws JSONException {
		String strDeposit  = deposit( event );
		String strWithdraw = withdraw( event );

		if( strWithdraw == null )
			return null;

		String strTransfer = "{"+ strWithdraw +","+ strDeposit +"}";

		return strTransfer;
	}

	public String mountJson(String type, Account account) {
		String jsonStr  = "{\""+type+"\":{\"id\":\""+ account.getNumber() + "\", \"balance\":"+ account.getBalance() +"}}";

		return jsonStr;
	}

	public Account add(Account account){
		return repository.add(account);
	}

	public Account findById(Long number) {
		return repository.findById( number );
	}

	public Account update(Account account) {
		return repository.update(account);
	}

	public void deleteAll() {
		repository.deleteAll();
	}
}
