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
import com.tethrnet.manage.db.SystemDB;
import com.tethrnet.manage.db.SystemStatusDB;
import com.tethrnet.manage.model.Auth;
import com.tethrnet.manage.model.HostSystem;
import com.tethrnet.manage.model.SchSession;
import com.tethrnet.manage.model.UserSchSessions;
import com.tethrnet.manage.util.DBUtils;
import com.tethrnet.manage.util.SSHUtil;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.interceptor.ServletRequestAware;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DowloadAction extends ActionSupport implements ServletRequestAware  {

    private static Logger log = LoggerFactory.getLogger(DowloadAction.class);
    HttpServletRequest servletRequest;
    List<String>remoteFileList;
    String fileName;
    FileInputStream fileInputStream;
    HostSystem  hostSystem;
    List<Long> idList = new ArrayList<Long>();
    
    public List<Long> getIdList() {
		return idList;
	}


	public void setIdList(List<Long> idList) {
		this.idList = idList;
	}


	SchSession session = null; 
    
    
    public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public List<String> getRemoteFileList() {
		return remoteFileList;
	}


	public void setRemoteFileList(List<String> remoteFileList) {
		this.remoteFileList = remoteFileList;
	}


	@Action(value = "/admin/setDownload",
    		results = {
    				@Result(name="success",location="/admin/download.jsp")
    		})
    public String setDownload() throws Exception{
    	remoteFileList = new ArrayList<String>();
    	hostSystem = SystemDB.getSystem(idList.get(0));

        Long sessionId=AuthUtil.getSessionId(servletRequest.getSession());
        for(Integer instanceId : SecureShellAction.getUserSchSessionMap().get(sessionId).getSchSessionMap().keySet()) {
            
            //if host system id matches pending system then upload
            if(hostSystem.getId().equals(SecureShellAction.getUserSchSessionMap().get(sessionId).getSchSessionMap().get(instanceId).getHostSystem().getId())){
                session = SecureShellAction.getUserSchSessionMap().get(sessionId).getSchSessionMap().get(instanceId);
                servletRequest.getSession().setAttribute("download_instanceid", instanceId);
            }
        }
        if (session != null)
        {
        	remoteFileList = SSHUtil.listDir(hostSystem, session); 
        }

    	return SUCCESS;
    }
    
    
    @Action(value = "/admin/download",
    		results = {
    				@Result(name="success",type="stream",params={"contentType", "application/octet-stream", "inputName","fileInputStream",    
    						"contentDisposition","attachment; filename=\"${fileName}\"", "bufferSize", "1024" })
    		})
    public String download() throws Exception{
    	Integer instanceId =(Integer) servletRequest.getSession().getAttribute("download_instanceid");
    	Long sessionId=AuthUtil.getSessionId(servletRequest.getSession());
    	session = SecureShellAction.getUserSchSessionMap().get(sessionId).getSchSessionMap().get(instanceId);
    	String path = SSHUtil.downloadFile(session, fileName);
    	fileInputStream = new FileInputStream(new File(path));
    	return SUCCESS;
    }


	public FileInputStream getFileInputStream() {
		return fileInputStream;
	}


	public void setFileInputStream(FileInputStream fileInputStream) {
		this.fileInputStream = fileInputStream;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.servletRequest = request;
	}



}
