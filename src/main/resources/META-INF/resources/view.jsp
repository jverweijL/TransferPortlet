<%@ include file="/init.jsp" %>

<portlet:actionURL var="uploadURL" name="uploadDocument"></portlet:actionURL>
<portlet:resourceURL id="/transfer/download" var="downloadURL" />
<portlet:renderURL var="downloaderURL">
	<portlet:param name="mvcRenderCommandName" value="/transfer/r/download" />
	<portlet:param name="uuid" value="xyz" />
</portlet:renderURL>

<b>Please Upload a Document</b>

<aui:form action="<%= uploadURL %>" method="post" name="fm" enctype="multipart/form-data">
	<aui:fieldset-group markupView="lexicon">
		<aui:fieldset label="">
			<aui:input label="File" name="uploadedFile" type="file" required="true"/>
			<aui:input label="To" name="to" type="email" required="true"/>
			<aui:input label="From" name="from" type="email" required="true"/>
			<aui:input label="Message" name="message" type="textarea" />
		</aui:fieldset>
		<aui:fieldset label="Optional" collapsed="<%= true %>" collapsible="<%= true %>" >
			<aui:select label="Expires" name="expires">
				<aui:option label="1 day" value="1" />
				<aui:option label="3 days" value="3" selected="<%= true %>"/>
				<aui:option label="7 days" value="7" />
				<aui:option label="15 days" value="15" />
			</aui:select>
			<aui:input label="Password" name="password" type="password" />
		</aui:fieldset>
	</aui:fieldset-group>

	<aui:button-row>
		<aui:button cssClass="btn-lg" type="submit"/>
	</aui:button-row>
</aui:form>

<%= downloadURL %>
<%= downloaderURL %>