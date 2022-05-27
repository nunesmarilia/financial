package com.exam.financial.model;

import java.util.Objects;

public class Event {

	private String destination;
	private String origin;
	private int amount;
	private String type;

	public Event() {

	}

	public Event(String type, String destination, String origin, int amount) {
		this.type           = type;
		this.destination    = destination;
		this.amount         = amount;
		this.origin     = origin;
	}

	public String getDestination() {
		return destination;
	}

	public String getOrigin() {
		return origin;
	}

	public int getAmount() {
		return amount;
	}

	public String getType() {
		return type;
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
