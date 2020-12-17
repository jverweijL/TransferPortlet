<%@ include file="/init.jsp" %>

<portlet:resourceURL id="/transfer/download" var="downloadURL"/>

<b>Please Upload a Document</b>

<aui:form action="<%= downloadURL %>" method="post" name="fm" target="_blank">
	<aui:input name="uuid" type="hidden" value="abc"/>
	<aui:fieldset-group markupView="lexicon">
		<aui:fieldset label="">
			<aui:input label="Password" name="password" type="password" />
		</aui:fieldset>
	</aui:fieldset-group>

	<aui:button-row>
		<aui:button cssClass="btn-lg" type="submit" value="Download"/>
	</aui:button-row>
</aui:form>

<%= downloadURL %>