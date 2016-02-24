/**
 * Copyright 2013 Sean Kavanagh - sean.p.kavanagh6@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tethrnet.manage.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionSupport;
import com.tethrnet.common.util.AppConfig;
import com.tethrnet.common.util.AuthUtil;
import com.tethrnet.manage.db.AuthDB;
import com.tethrnet.manage.db.ProfileDB;
import com.tethrnet.manage.db.UserProfileDB;
import com.tethrnet.manage.model.Auth;
import com.tethrnet.manage.model.Profile;
import com.tethrnet.manage.model.User;
import com.tethrnet.manage.util.InventoryUtil;
import com.tethrnet.manage.util.RefreshAuthKeyUtil;


/**
 * Action to auth to keybox
 */
public class LoginAction extends ActionSupport implements ServletRequestAware, ServletResponseAware {

    private static Logger loginAuditLogger = LoggerFactory.getLogger("com.keybox.manage.action.LoginAudit");
    HttpServletResponse servletResponse;
    HttpServletRequest servletRequest;
    Auth auth;
    private final String AUTH_ERROR="Authentication Failed : Login credentials are invalid";
    private final String AUTH_ERROR_NO_PROFILE="Authentication Failed : There are no profiles assigned to this account";
    //check if otp is enabled
    boolean otpEnabled = ("required".equals(AppConfig.getProperty("oneTimePassword")) || "optional".equals(AppConfig.getProperty("oneTimePassword")));

    @Action(value = "/login",
            results = {
                    @Result(name = "success", location = "/login.jsp")
            }
    )
    public String login() {

        return SUCCESS;
    }

    @Action(value = "/admin/menu",
            results = {
                    @Result(name = "success", location = "/admin/menu.jsp")
            }
    )
    public String menu() {

        return SUCCESS;
    }


    @Action(value = "/loginSubmit",
            results = {
                    @Result(name = "input", location = "/login.jsp"),
                    @Result(name = "change_password", location = "/admin/userSettings.action", type = "redirect"),
                    @Result(name = "success", location = "/admin/viewSystems.action", type = "redirect")
            }
    )
    public String loginSubmit() {
        String retVal = SUCCESS;

        String authToken = AuthDB.login(auth);

        //get client IP
        String clientIP = null;
        if (StringUtils.isNotEmpty(AppConfig.getProperty("clientIPHeader"))) {
            clientIP = servletRequest.getHeader(AppConfig.getProperty("clientIPHeader"));
        }
        if (StringUtils.isEmpty(clientIP)) {
            clientIP = servletRequest.getRemoteAddr();
        }
        if (authToken != null) {

            User user = AuthDB.getUserByAuthToken(authToken);
            if(user!=null) {
//               String sharedSecret = null;
//                if (otpEnabled) {
//                    sharedSecret = AuthDB.getSharedSecret(user.getId());
//                    if (StringUtils.isNotEmpty(sharedSecret) && (auth.getOtpToken() == null || !OTPUtil.verifyToken(sharedSecret, auth.getOtpToken()))) {
//                        loginAuditLogger.info(auth.getUsername() + " (" + clientIP + ") - "  + AUTH_ERROR);
//                        addActionError(AUTH_ERROR);
//                        return INPUT;
//                    }
//                }
//                //check to see if admin has any assigned profiles
//                if(!User.MANAGER.equals(user.getUserType()) && (user.getProfileList()==null || user.getProfileList().size()<=0)){
//                    loginAuditLogger.info(auth.getUsername() + " (" + clientIP + ") - " + AUTH_ERROR_NO_PROFILE);
//                    addActionError(AUTH_ERROR_NO_PROFILE);
//                    return INPUT;
//                }

                AuthUtil.setAuthToken(servletRequest.getSession(), authToken);
                AuthUtil.setUserId(servletRequest.getSession(), user.getId());
                AuthUtil.setAuthType(servletRequest.getSession(), user.getAuthType());
                AuthUtil.setTimeout(servletRequest.getSession());

//                //for first time login redirect to set OTP
//                if (otpEnabled && StringUtils.isEmpty(sharedSecret)) {
//                    retVal = "otp";
//                } else if ("changeme".equals(auth.getPassword())  && Auth.AUTH_BASIC.equals(user.getAuthType())) {
//                    retVal = "change_password";
//                }
                
                String profilename = user.getUsername()+"_"+"serviceVm";
                Profile profile = ProfileDB.getProfileByName(profilename);
                if (profile == null)
                {
                	profile = new Profile();
                	profile.setNm(profilename);
                    profile.setDesc("just list service vms(firewall,vrouter,vpn,etc)");
                    ProfileDB.insertProfile(profile);
                    profile = ProfileDB.getProfileByName(profilename);
                }

                List<Long> ids = new ArrayList<Long>();
                ids.add(user.getId());
                UserProfileDB.setUsersForProfile(profile.getId(), ids);
                
                loginAuditLogger.info(auth.getUsername() + " (" + clientIP + ") - Authentication Success");
                
                
            }

        } else {
            loginAuditLogger.info(auth.getUsername() + " (" + clientIP + ") - " + AUTH_ERROR);
            addActionError(AUTH_ERROR);
            retVal = INPUT;
        }

        return retVal;
    }

    @Action(value = "/logout",
            results = {
                    @Result(name = "success", location = "/login.action", type = "redirect")
            }
    )
    public String logout() {
        AuthUtil.deleteAllSession(servletRequest.getSession());
        return SUCCESS;
    }

    /**
     * Validates fields for auth submit
     */
    public void validateLoginSubmit() {
        if (auth.getUsername() == null ||
                auth.getUsername().trim().equals("")) {
            addFieldError("auth.username", "Required");
        }
        if (auth.getPassword() == null ||
                auth.getPassword().trim().equals("")) {
            addFieldError("auth.password", "Required");
        }


    }


    public boolean isOtpEnabled() {
        return otpEnabled;
    }

    public void setOtpEnabled(boolean otpEnabled) {
        this.otpEnabled = otpEnabled;
    }

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public HttpServletResponse getServletResponse() {
        return servletResponse;
    }

    public void setServletResponse(HttpServletResponse servletResponse) {
        this.servletResponse = servletResponse;
    }

    public HttpServletRequest getServletRequest() {
        return servletRequest;
    }

    public void setServletRequest(HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
    }
}
