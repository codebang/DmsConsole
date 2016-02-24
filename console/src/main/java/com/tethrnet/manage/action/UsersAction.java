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


import com.opensymphony.xwork2.ActionSupport;
import com.tethrnet.common.util.AuthUtil;
import com.tethrnet.manage.db.PublicKeyDB;
import com.tethrnet.manage.db.ScriptDB;
import com.tethrnet.manage.db.UserDB;
import com.tethrnet.manage.model.Auth;
import com.tethrnet.manage.model.Script;
import com.tethrnet.manage.model.SortedSet;
import com.tethrnet.manage.model.User;
import com.tethrnet.manage.util.PasswordUtil;
import com.tethrnet.manage.util.RefreshAuthKeyUtil;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.interceptor.ServletRequestAware;

import javax.servlet.http.HttpServletRequest;

/**
 * Action to manage users
 */
public class UsersAction extends ActionSupport  implements ServletRequestAware {

    SortedSet sortedSet=new SortedSet();
    User user = new User();
    Script script=null;
    HttpServletRequest servletRequest;
    boolean resetSharedSecret=false;


    @Action(value = "/manage/viewUsers",
            results = {
                    @Result(name = "success", location = "/manage/view_users.jsp")
            }
    )
    public String viewUsers() {
        Long userId= AuthUtil.getUserId(servletRequest.getSession());
        sortedSet = UserDB.getUserSet(sortedSet);
        if(script!=null && script.getId()!=null){
            script=ScriptDB.getScript(script.getId(),userId);
        }
        user.setId(userId);
        return SUCCESS;
    }

    @Action(value = "/manage/saveUser",
            results = {
                    @Result(name = "input", location = "/manage/view_users.jsp"),
                    @Result(name = "success", location = "/manage/viewUsers.action?sortedSet.orderByDirection=${sortedSet.orderByDirection}&sortedSet.orderByField=${sortedSet.orderByField}", type="redirect")
            }
    )
    public String saveUser() {

        if (user.getId() != null) {
            if(user.getPassword()==null || user.getPassword().trim().equals("")){
                UserDB.updateUserNoCredentials(user);
            }
            else {
                UserDB.updateUserCredentials(user);
            }
            //check if reset is set
            if(resetSharedSecret){
                UserDB.resetSharedSecret(user.getId());
            }
        } else {
            UserDB.insertUser(user);
        }


        return SUCCESS;
    }

    @Action(value = "/manage/deleteUser",
            results = {
                    @Result(name = "success", location = "/manage/viewUsers.action?sortedSet.orderByDirection=${sortedSet.orderByDirection}&sortedSet.orderByField=${sortedSet.orderByField}", type="redirect")
            }
    )
    public String deleteUser() {

        if (user.getId() != null && user.getId()!=AuthUtil.getUserId(servletRequest.getSession())) {
            UserDB.disableUser(user.getId());
            PublicKeyDB.deleteUserPublicKeys(user.getId());
            RefreshAuthKeyUtil.refreshAllSystems();
        }
        return SUCCESS;
    }

    /**
     * Validates all fields for adding a user
     */
    public void validateSaveUser() {
        if (user == null
                || user.getUsername() == null
                || user.getUsername().trim().equals("")) {
            addFieldError("user.username", "Required");
        }

        
        if (user != null && user.getPassword() != null && !user.getPassword().trim().equals("")){
            
            if(!user.getPassword().equals(user.getPasswordConfirm())) {
                    addActionError("Passwords do not match");
            } else if(!PasswordUtil.isValid(user.getPassword())) {
                    addActionError(PasswordUtil.PASSWORD_REQ_ERROR_MSG);
            }
        }
        if(user!=null && user.getId()==null && !Auth.AUTH_EXTERNAL.equals(user.getAuthType()) && (user.getPassword()==null || user.getPassword().trim().equals(""))){
            addActionError("Password is required");
        }

        if(user!=null && !UserDB.isUnique(user.getId(),user.getUsername())){
            addActionError("Username has been taken");
        }
        if (!this.getFieldErrors().isEmpty()||!this.getActionErrors().isEmpty()) {
            sortedSet = UserDB.getUserSet(sortedSet);
        }
    }


    public SortedSet getSortedSet() {
        return sortedSet;
    }

    public void setSortedSet(SortedSet sortedSet) {
        this.sortedSet = sortedSet;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    public Script getScript() {
        return script;
    }

    public void setScript(Script script) {
        this.script = script;
    }

    public HttpServletRequest getServletRequest() {
        return servletRequest;
    }

    public void setServletRequest(HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
    }

    public boolean isResetSharedSecret() {
        return resetSharedSecret;
    }

    public void setResetSharedSecret(boolean resetSharedSecret) {
        this.resetSharedSecret = resetSharedSecret;
    }
}
