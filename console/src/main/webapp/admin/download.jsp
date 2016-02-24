<%@ taglib prefix="s" uri="/struts-tags" %>
<html>

<body>
<h1>Remote File download</h1>

<s:url id="fileDownload" namespace="/admin" action="download" ></s:url>
<s:if test="idList == null || idList.isEmpty()">
	<p class="error">No host selected for download</p>
</s:if>
<s:elseif test="idList.size() == 1">
<s:iterator var="file" value="remoteFileList" status="stat">
</div></div><h2>Download file - <s:a href="download.action?fileName=%{file}"><s:property value="#file" /></s:a>
</h2>
</s:iterator>
</s:elseif>
<s:else>
<p class="error">Cannot download from more than one host</p>
</s:else>
	
</body>
</html>