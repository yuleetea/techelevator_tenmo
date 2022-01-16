package com.techelevator.tenmo.model;

import io.cucumber.java.sl.In;

public class User {

	private int id;
	// had to set a property of accountId in user to set user in transaction
	// window in App
	private int accountId;
	private String username;

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", username='" + username + '\'' +
				'}';
	}
}
