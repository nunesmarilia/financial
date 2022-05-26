package com.exam.financial.model;

import java.util.Objects;

public class Event {

	private Long destination;
	private Long origin;
	private int amount;
	private String type;

	public Event() {

	}

	public Long getDestination() {
		return destination;
	}

	public void setDestination(Long destination) {
		this.destination = destination;
	}

	public Long getOrigin() {
		return origin;
	}

	public void setOrigin(Long origin) {
		this.origin = origin;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Event)) return false;
		Event event = (Event) o;
		return Objects.equals(destination, event.destination) &&
				Objects.equals(origin, event.origin) &&
				Objects.equals(type, event.type);
	}

	@Override
	public int hashCode() {
		return Objects.hash(destination, origin, type);
	}
}
