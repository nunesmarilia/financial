package com.exam.financial.dto;

public class ReturnEvent {

	String messageError;
	String messageSucess;
	String messageNotFound;

	public void setMessageError(String messageError) {
		this.messageError = messageError;
	}

	public void setMessageSucess(String messageSucess) {
		this.messageSucess = messageSucess;
	}

	public String getMessageError() {
		return messageError;
	}

	public String getMessageSucess() {
		return messageSucess;
	}

	public String getMessageNotFound() {
		return messageNotFound;
	}

	public void setMessageNotFound(String messageNotFound) {
		this.messageNotFound = messageNotFound;
	}
}
