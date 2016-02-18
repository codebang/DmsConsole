package com.dadynent.sa.inventory.exception;

import java.text.MessageFormat;

public class InvalidObjectProperty extends Exception{
	
	private String obj;
	
	private String prop;
	
	private String action;
	
	public InvalidObjectProperty(String obj,String prop,String action)
	{
		this.obj = obj;
		this.prop = prop;
		this.action = action;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return MessageFormat.format("object({0}) property({1}) reason({2})", obj, prop, action);
	}

}
