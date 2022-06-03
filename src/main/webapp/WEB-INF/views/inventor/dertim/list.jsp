<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="urn:jsptagdir:/WEB-INF/tags"%>

<acme:list>
    <acme:list-column code="inventor.dertim.list.label.code" path="code" width="35%"/>
    <acme:list-column code="inventor.dertim.list.label.subject" path="subject" width="30%"/>
    <acme:list-column code="inventor.dertim.list.label.item" path="item" width="35%"/>
</acme:list>

<acme:button code="inventor.dertim.list.button.create" action="/inventor/dertim/create"/>