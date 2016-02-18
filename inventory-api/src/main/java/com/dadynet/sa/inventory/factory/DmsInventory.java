package com.dadynet.sa.inventory.factory;

import java.util.List;

import com.dadynent.sa.inventory.exception.RetrieveInventoryException;
import com.dadynent.sa.inventory.model.Account;
import com.dadynent.sa.inventory.model.VirtualMachine;

public interface DmsInventory {
	public List<Account> listAccounts() throws RetrieveInventoryException;
	public Account getAccountByName(String name) throws RetrieveInventoryException;
	public List<VirtualMachine> listVmByAccount(Account account) throws RetrieveInventoryException;
	public void setConnectUrl(String url);
}
