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

	public JSONObject deposit(Event event) throws JSONException {
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

	public JSONObject withdraw(Event event) throws JSONException {
		Account account = findById( event.getOrigin() );
		if ( account == null ){
			return null;
		}

		if( event.getAmount() > account.getBalance() ){
			JSONObject jsonBalance  = new JSONObject();
			jsonBalance.put("message", "Insufficient funds");
			return jsonBalance;
		}

		account.setBalance( account.getBalance() - event.getAmount() );
		update( account );

		return mountJson("origin", account);
	}

	public JSONObject transfer(Event event) throws JSONException {
		Account accountDestination = findById( event.getDestination() );
		if ( accountDestination == null ){
			return null;
		}

		JSONObject jsonTransfer = new JSONObject();

		JSONObject jsonDeposit  = deposit( event );
		JSONObject jsonWithdraw = withdraw( event );

		jsonTransfer.put("destination", jsonDeposit.get("destination"));
		jsonTransfer.put("origin", jsonWithdraw.get("origin"));

		return jsonTransfer;
	}

	public JSONObject mountJson(String type, Account account) throws JSONException {
		JSONObject jsonAccount  = new JSONObject();

		jsonAccount.put("id", account.getNumber() );
		jsonAccount.put("balance", account.getBalance() );

		JSONObject jsonMount = new JSONObject();
		jsonMount.putOpt(type, jsonAccount);

		return jsonMount;
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
