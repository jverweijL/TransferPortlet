<%@ page import="transfer.configuration.TransferPortletConfiguration" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<liferay-theme:defineObjects />

<portlet:defineObjects />
<%
TransferPortletConfiguration portletInstanceConfiguration = portletDisplay.getPortletInstanceConfiguration(TransferPortletConfiguration.class);
String basedir = portletInstanceConfiguration.basedir();
pageContext.setAttribute("basedir",basedir);
String maxfilesize = portletInstanceConfiguration.maxfilesize();
pageContext.setAttribute("maxfilesize",maxfilesize);
String emailtemplate = portletInstanceConfiguration.emailtemplate();
pageContext.setAttribute("emailtemplate",emailtemplate);
%>