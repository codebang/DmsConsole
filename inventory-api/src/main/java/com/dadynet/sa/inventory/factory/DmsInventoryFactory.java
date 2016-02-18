package com.dadynet.sa.inventory.factory;

public class DmsInventoryFactory {
	public static DmsInventory createDmsInventoryMgr(String cls,String connect_url)
	{
		try
		{
			Class mgr = Class.forName(cls);
			Class[] interfaces = mgr.getInterfaces();
			for (Class inter : interfaces)
			{
				if (inter.equals(DmsInventory.class))
				{
					DmsInventory inventory =  (DmsInventory)mgr.newInstance();
					inventory.setConnectUrl(connect_url);
					return inventory;
				}
			}	
			System.err.println("the dms inventory class provider has not implementd the interface");
			return new NoneDmsInventory();
			
		}
		catch(ClassNotFoundException cfe)
		{
			System.err.println(cfe);
			return new NoneDmsInventory();
		}
		catch(IllegalAccessException iae)
		{
			System.err.println(iae);
			return new NoneDmsInventory();
		}
		catch(InstantiationException ie)
		{
			System.err.println(ie);
			return new NoneDmsInventory();
		}
	}

}
