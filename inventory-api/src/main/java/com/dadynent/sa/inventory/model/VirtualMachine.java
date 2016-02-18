package com.dadynent.sa.inventory.model;

import java.util.Map;

import com.dadynent.sa.inventory.exception.InvalidObjectProperty;
import com.dadynent.sa.inventory.util.Utils;

public class VirtualMachine {
	private String stackid;
	private String publicip;
	private String serviceip;
	private String manageip;
	private String user;
	private String servicetype;
	private Account parent;
	
	public VirtualMachine(String id, String pubip,String svcip,String mangeip,String user)
	{
		this.stackid = id;
		this.publicip = pubip;
		this.serviceip = svcip;
		this.manageip = mangeip;
		this.user = user;
	}
	
	
	public String getStackid() {
		return stackid;
	}

	public void setStackid(String stackid) {
		this.stackid = stackid;
	}

	public String getPublicip() {
		return publicip;
	}

	public void setPublicip(String publicip) {
		this.publicip = publicip;
	}

	public String getServiceip() {
		return serviceip;
	}

	public void setServiceip(String serviceip) {
		this.serviceip = serviceip;
	}

	public String getManageip() {
		return manageip;
	}

	public void setManageip(String manageip) {
		this.manageip = manageip;
	}

	public String getUser() {
		return user;
	}


	public void setUser(String user) {
		this.user = user;
	}

	public String getServicetype() {
		return servicetype;
	}


	public void setServicetype(String servicetype) {
		this.servicetype = servicetype;
	}
	public Account getParent() {
		return parent;
	}


	public void setParent(Account parent) {
		this.parent = parent;
	}

	public static VirtualMachine fromMap(Map properties) throws InvalidObjectProperty
	{
		String stackid = Utils.safeGet(properties, "stackid");
		String publicip = Utils.safeGet(properties, "publicip");
		String serviceip = Utils.safeGet(properties, "serviceip");
		String manageip = Utils.safeGet(properties, "manageip");
		String user = Utils.safeGet(properties, "user");

		return new VirtualMachine(stackid, publicip, serviceip, manageip,user);
	}
	
}
