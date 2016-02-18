package com.dadynet.sa.inventory.zk;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import com.dadynent.sa.inventory.exception.InvalidObjectProperty;
import com.dadynent.sa.inventory.exception.RetrieveInventoryException;
import com.dadynent.sa.inventory.model.Account;
import com.dadynent.sa.inventory.model.VirtualMachine;
import com.dadynent.sa.inventory.util.Utils;
import com.dadynet.sa.inventory.factory.DmsInventory;
import com.dadynet.sa.inventory.factory.DmsInventoryFactory;

public class ZooKeeperInventory implements DmsInventory {
	
	private ZooKeeper zkclient = null;
	
	private String path = "/dso/accounts";
	
	public Account getAccountByName(String arg0) throws RetrieveInventoryException {
		// TODO Auto-generated method stub
		List<Account> accounts = listAccounts();
		for (Account account: accounts)
		{
			if (account.getAccountName().equals(arg0))
			{
				return account;
			}
		}
		return null;
	}
	
	public List<Account> listAccounts() throws RetrieveInventoryException {
		// TODO Auto-generated method stub
		Map<String,String> accountProp = new HashMap<String, String>();
		accountProp.put("account_name", "accountName");
		accountProp.put("id", "accountId");
		List<Account> ret = new ArrayList<Account>();
		try
		{
			List<String> children = zkclient.getChildren(path, false, null);
			for(String child : children)
			{
				Path child_path = Paths.get(path,child);
				byte[] data = zkclient.getData(child_path.toString(), false, null);
				System.out.println(util.toMap(new String(data),accountProp));
				ret.add(Account.fromMap(util.toMap(new String(data),accountProp)));
			}
			
		}
		catch(InterruptedException ie)
		{
			throw new RetrieveInventoryException();
		}
		catch(KeeperException ke)
		{
			throw new RetrieveInventoryException();
		}
		catch(InvalidObjectProperty ip)
		{
			ip.printStackTrace();
			throw new RetrieveInventoryException();
		}
		return ret;
	
	}
	
	public List<VirtualMachine> listVmByAccount(Account account) throws RetrieveInventoryException{
		// TODO Auto-generated method stub
		Path svcpath = Paths.get(path,account.getAccountId(),"services");
		List<VirtualMachine> ret = new ArrayList<VirtualMachine>();
		Map<String,String> convert = new HashMap<String, String>();
		convert.put("id", "stackid");
		convert.put("publicip", "publicip");
		convert.put("manageip", "manageip");
		convert.put("serviceip", "serviceip");
		convert.put("user", "user");
		try
		{
			List<String> services = zkclient.getChildren(svcpath.toString(), false,null);
			for (String service: services)
			{
				Path spath = Paths.get(svcpath.toString(),service,"instances");
				List<String> vmids = zkclient.getChildren(spath.toString(), false,null);
				for (String id : vmids)
				{
					Path vpath = Paths.get(spath.toString(),id);
					byte[] data = zkclient.getData(vpath.toString(), false, null);
					VirtualMachine vm = VirtualMachine.fromMap(util.toMap(new String(data), convert));
					vm.setParent(account);
					vm.setServicetype(service);
					ret.add(vm);
					
				}
			}
		}
		catch(InterruptedException ie)
		{
			throw new RetrieveInventoryException();
		}
		catch(KeeperException ke)
		{
			ke.printStackTrace();
			throw new RetrieveInventoryException();
		}
		catch(InvalidObjectProperty ip)
		{
			ip.printStackTrace();
			throw new RetrieveInventoryException();
		}
		return ret;
	}
	
	public void setConnectUrl(String connUrl) {
		// TODO Auto-generated method stub
		try{
			zkclient = new ZooKeeper(connUrl,3000,null);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	

	
	public static void main(String[] args) throws Exception{
		
		DmsInventory zi = DmsInventoryFactory.createDmsInventoryMgr("com.dadynet.sa.inventory.zk.ZooKeeperInventory", "10.74.113.102:2181");
		
		System.out.println(zi.listVmByAccount(zi.getAccountByName("sa.com")));
	}


}


