<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="urn:jsptagdir:/WEB-INF/tags"%>

<acme:form>
	<jstl:choose>
		<jstl:when test="${command == 'create'}">
			<acme:input-textbox code="inventor.dertim.form.label.code" path="code"/>
			<acme:input-select code="inventor.dertim.form.label.item" path="items">
            	<jstl:forEach items="${items}" var="items">
                	<acme:input-option code="${items}" value="${items}" selected="${items}"/>
            	</jstl:forEach>
        	</acme:input-select>
		</jstl:when>
		<jstl:when test="${acme:anyOf(command, 'show, update, delete')}">
			<acme:input-textbox code="inventor.dertim.form.label.code" path="code" readonly="true"/>
			<acme:input-textbox code="inventor.dertim.form.label.item" path="item" readonly="true"/>
			<acme:input-moment code="inventor.dertim.form.label.creationMoment" path="creationMoment" readonly="true"/>
		</jstl:when>
	</jstl:choose>
    <acme:input-textbox code="inventor.dertim.form.label.subject" path="subject"/>
    <acme:input-textarea code="inventor.dertim.form.label.summary" path="summary"/>
    <acme:input-moment code="inventor.dertim.form.label.initialPeriod" path="initialPeriod"/>
    <acme:input-moment code="inventor.dertim.form.label.endPeriod" path="endPeriod"/>
    <acme:input-money code="inventor.dertim.form.label.provision" path="provision"/>
    <acme:input-url code="inventor.dertim.form.label.additionalInfo" path="additionalInfo"/>
    
    	<jstl:choose>
			<jstl:when test="${acme:anyOf(command, 'show, update, delete')}">
				<acme:submit code="inventor.dertim.form.button.update" action="/inventor/dertim/update"/>
				<acme:submit code="inventor.dertim.form.button.delete" action="/inventor/dertim/delete"/>
			</jstl:when>	
		
			<jstl:when test="${command == 'create'}">
				<acme:submit code="inventor.dertim.form.button.create" action="/inventor/dertim/create"/>
			</jstl:when>
		</jstl:choose>
    
</acme:form>