package com.exam.financial.repository;

import com.exam.financial.model.Account;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class AccountRepository {

	private List<Account> accounts = new ArrayList<>();

	public Account add(Account account) {
		account.setId((long) (accounts.size()+1));
		accounts.add(account);
		return account;
	}

	public Account update(Account account) {
		accounts.set(account.getId().intValue() - 1, account);
		return account;
	}

	public Account findById(String number) {
		Optional<Account> account = accounts.stream().filter(a -> a.getNumber().equals(number)).findFirst();
		return account.orElse(null);
	}

	public void deleteAll() {
		accounts = new ArrayList<>();
	}

}
