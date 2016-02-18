package com.dadynet.auth;


import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import javax.security.auth.callback.*;

public class SimpleAuthModule implements LoginModule{
	
	// initial state
    private Subject subject;
    private CallbackHandler callbackHandler;
    private Map sharedState;
    private Map options;


    // the authentication status
    private boolean succeeded = false;
    private boolean commitSucceeded = false;

    // username and password
    private String username;
    private char[] password;

    private SimplePrinciple simplePrinciple;

    
    
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
			Map<String, ?> options) {
		// TODO Auto-generated method stub
		this.subject = subject;
		this.callbackHandler = callbackHandler;
		this.sharedState = sharedState;
		this.options = options;		
	}
	

	public boolean login() throws LoginException {
		// TODO Auto-generated method stub
		// prompt for a user name and password
		if (callbackHandler == null)
		    throw new LoginException("Error: no CallbackHandler available " +
				"to garner authentication information from the user");

		Callback[] callbacks = new Callback[2];
		callbacks[0] = new NameCallback("user name: ");
		callbacks[1] = new PasswordCallback("password: ", false);
	 
		try {
		    callbackHandler.handle(callbacks);
		    username = ((NameCallback)callbacks[0]).getName();
		    char[] tmpPassword = ((PasswordCallback)callbacks[1]).getPassword();
		    if (tmpPassword == null) {
			// treat a NULL password as an empty password
			tmpPassword = new char[0];
		    }
		    password = new char[tmpPassword.length];
		    System.arraycopy(tmpPassword, 0,
				password, 0, tmpPassword.length);
		    ((PasswordCallback)callbacks[1]).clearPassword();
	 
		} catch (java.io.IOException ioe) {
		    throw new LoginException(ioe.toString());
		} catch (UnsupportedCallbackException uce) {
		    throw new LoginException("Error: " + uce.getCallback().toString() +
			" not available to garner authentication information " +
			"from the user");
		}



		succeeded = true;
		return true;
	}
	
	

	public boolean commit() throws LoginException {
		// TODO Auto-generated method stub
		if (succeeded == false) {
		    return false;
		} else {
		    // add a Principal (authenticated identity)
		    // to the Subject

		    // assume the user we authenticated is the SamplePrincipal
		    simplePrinciple = new SimplePrinciple(username);
		    if (!subject.getPrincipals().contains(simplePrinciple))
		    	subject.getPrincipals().add(simplePrinciple);

		    // in any case, clean out state
		    username = null;
		    for (int i = 0; i < password.length; i++)
			password[i] = ' ';
		    password = null;

		    commitSucceeded = true;
		    return true;
		}
	}
	

	public boolean abort() throws LoginException {
		// TODO Auto-generated method stub
		if (succeeded == false) {
		    return false;
		} else if (succeeded == true && commitSucceeded == false) {
		    // login succeeded but overall authentication failed
		    succeeded = false;
		    username = null;
		    if (password != null) {
			for (int i = 0; i < password.length; i++)
			    password[i] = ' ';
			password = null;
		    }
		    simplePrinciple = null;
		} else {
		    // overall authentication succeeded and commit succeeded,
		    // but someone else's commit failed
		    logout();
		}
		return true;
	}
	

	public boolean logout() throws LoginException {
		// TODO Auto-generated method stub
		subject.getPrincipals().remove(simplePrinciple);
		succeeded = false;
		succeeded = commitSucceeded;
		username = null;
		if (password != null) {
		    for (int i = 0; i < password.length; i++)
			password[i] = ' ';
		    password = null;
		}
		simplePrinciple = null;
		return true;
	}

}
