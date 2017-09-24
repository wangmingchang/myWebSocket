package com.wmc.mywebsocket.pojo;

public class WSBean {
	private String message;
	private String from;
	private String to;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public WSBean(String message, String from, String to) {
		super();
		this.message = message;
		this.from = from;
		this.to = to;
	}

	public WSBean() {
		super();
	}

	@Override
	public String toString() {
		return "WSBean [message=" + message + ", from=" + from + ", to=" + to + "]";
	}

}
