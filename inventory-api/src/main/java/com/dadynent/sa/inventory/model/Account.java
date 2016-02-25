package com.dadynent.sa.inventory.model;

import java.util.List;
import java.util.Map;

import com.dadynent.sa.inventory.exception.InvalidObjectProperty;
import com.dadynent.sa.inventory.exception.RetrieveInventoryException;

public  class Account {
	
	private String accountId;
	private String accountName;
	
	public Account(String id, String name)
	{
		this.accountId = id;
		this.accountName = name;
	}
	
	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public static Account fromMap(Map properties) throws InvalidObjectProperty{
		
		if (! properties.containsKey("accountName"))
		{
			throw new InvalidObjectProperty("Account","accountName","missing");
		}
		else if (! properties.containsKey("accountId")){
			throw new InvalidObjectProperty("Account","accountId","missing");
		}
		else 
		{
			String name = properties.get("accountName").toString();
			String id = properties.get("accountId").toString();
			return new Account(id, name);
		}
	
	}
	

}
