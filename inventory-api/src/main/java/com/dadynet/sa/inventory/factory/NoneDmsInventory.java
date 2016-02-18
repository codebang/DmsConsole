package com.dadynet.sa.inventory.factory;

import java.util.ArrayList;
import java.util.List;

import com.dadynent.sa.inventory.exception.RetrieveInventoryException;
import com.dadynent.sa.inventory.model.Account;
import com.dadynent.sa.inventory.model.VirtualMachine;

public class NoneDmsInventory implements DmsInventory {
	
	public void setConnectUrl(String url) {
		// TODO Auto-generated method stub
		
	}
	
	public Account getAccountByName(String name) throws RetrieveInventoryException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public List<Account> listAccounts() throws RetrieveInventoryException {
		// TODO Auto-generated method stub
		return new ArrayList<Account>();
	}
	
	public List<VirtualMachine> listVmByAccount(Account account) {
		// TODO Auto-generated method stub
		return null;
	}

}
