package com.tethrnet.manage.util;

import java.util.ArrayList;
import java.util.List;

import com.dadynent.sa.inventory.exception.RetrieveInventoryException;
import com.dadynent.sa.inventory.model.Account;
import com.dadynent.sa.inventory.model.VirtualMachine;
import com.dadynet.sa.inventory.factory.DmsInventory;
import com.dadynet.sa.inventory.factory.DmsInventoryFactory;
import com.tethrnet.common.util.AppConfig;
import com.tethrnet.manage.db.SystemDB;
import com.tethrnet.manage.model.HostSystem;
import com.tethrnet.manage.model.User;

public class InventoryUtil {
	
	public static void createOrUpdateHostSystem(User user)
	{
		String module = AppConfig.getProperty("inventoryModule");
		String conn_url = AppConfig.getProperty("inventoryConn");
		DmsInventory mgr = DmsInventoryFactory.createDmsInventoryMgr(module, conn_url);
	}
	public static List<HostSystem> listHostSystem(String userName)
	{
		String module = AppConfig.getProperty("inventoryModule");
		String conn_url = AppConfig.getProperty("inventoryConn");
		DmsInventory mgr = DmsInventoryFactory.createDmsInventoryMgr(module, conn_url);
		try
		{
			Account account = mgr.getAccountByName(userName);
			List<VirtualMachine> vms = mgr.listVmByAccount(account);
			List<HostSystem> hosts = fromVirtualMachine(vms);
			return hosts;
//			for (HostSystem hostSystem: hosts)
//			{
//				//hostSystem = SSHUtil.authAndAddPubKey(hostSystem, "", "");
//
//		        if (hostSystem.getId() != null) {
//		            SystemDB.updateSystem(hostSystem);
//		        } else {
//		            hostSystem.setId(SystemDB.insertSystem(hostSystem));
//		        }
//			}
		}
		catch(RetrieveInventoryException e)
		{
			e.printStackTrace();
		}
		return new ArrayList<HostSystem>();
	}
	
	private static List<HostSystem> fromVirtualMachine(List<VirtualMachine> vms)
	{
		List<HostSystem> hosts = new ArrayList<HostSystem>();
		for(VirtualMachine vm : vms)
		{
			HostSystem host = new HostSystem();
			host.setUser(vm.getUser());
			host.setHost(vm.getManageip());
			host.setPort(22);
			host.setDisplayNm(vm.getManageip()+"-"+vm.getServicetype());
			hosts.add(host);
			
		}
		return hosts;
	}
}
