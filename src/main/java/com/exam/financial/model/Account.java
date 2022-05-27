package com.exam.financial.model;

import java.util.Objects;

public class Account {

	private Long id;
	private String number;
	private int balance;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Account)) return false;
		Account account = (Account) o;
		return Objects.equals(number, account.number);
	}

	@Override
	public int hashCode() {
		return Objects.hash(number);
	}
}
