package com.dadynet.auth;


import java.io.Serializable;
import java.security.Principal;

public class SimplePrinciple implements Principal,Serializable{
	
    private String name;
    
	public SimplePrinciple(String name) {
		if (name == null)
		    throw new NullPointerException("illegal null input");

		this.name = name;
	    }


	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "SimplePrinciple: " + name ;
	}
	
	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if (o == null)
		    return false;

	        if (this == o)
	            return true;
	 
	        if (!(o instanceof SimpleAuthModule))
	            return false;
	        SimplePrinciple that = (SimplePrinciple)o;

		if (this.getName().equals(that.getName()))
		    return true;
		return false;
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return name.hashCode();
	}

}
