package com.exam.financial.model;

import java.util.Objects;

public class Account {

	private Long id;
	private Long number;
	private int balance;

	public Account() {

	}

	public Account(Long number, int balance) {
		this.number = number;
		this.balance = balance;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getNumber() {
		return number;
	}

	public void setNumber(Long number) {
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
