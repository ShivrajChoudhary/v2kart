<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="idKey" required="true" type="java.lang.String"%>
<%@ attribute name="labelKey" required="true" type="java.lang.String"%>
<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="items" required="true" type="java.util.Collection"%>
<%@ attribute name="itemValue" required="false" type="java.lang.String"%>
<%@ attribute name="itemLabel" required="false" type="java.lang.String"%>
<%@ attribute name="mandatory" required="false" type="java.lang.Boolean"%>
<%@ attribute name="labelCSS" required="false" type="java.lang.String"%>
<%@ attribute name="selectCSSClass" required="false" type="java.lang.String"%>
<%@ attribute name="skipBlank" required="false" type="java.lang.Boolean"%>
<%@ attribute name="skipBlankMessageKey" required="false" type="java.lang.String"%>
<%@ attribute name="selectedValue" required="false" type="java.lang.String"%>
<%@ attribute name="tabindex" required="false" rtexprvalue="true"%>
<%@ attribute name="isList" required="false" type="java.lang.Boolean"%>
<%@ attribute name="fieldcontainCSSClass" required="false" type="java.lang.String"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template" %>

<template:errorSpanField path="${path}">
	<ycommerce:testId code="LoginPage_Item_${idKey}">
		<div data-role="fieldcontain" data-theme="b" class="${fieldcontainCSSClass}">
			<label class="${labelCSS}" for="${idKey}">
				<spring:theme code="${labelKey}" />
				<c:if test="${mandatory != null && mandatory == true}">
					<span class="mandatory"> <spring:theme code="login.required" var="loginrequiredText" />
						*
					</span>
				</c:if>
			</label>
			<form:select id="${idKey}" path="${path}" cssClass="${selectCSSClass}" tabindex="${tabindex}" data-theme="c" data-icon="drop-arrow">
				<c:if test="${skipBlank == null || skipBlank == false}">
					<option value="" disabled="disabled" selected="${empty selectedValue ? 'selected' : ''}">
						<spring:theme code='${skipBlankMessageKey}' />
					</option>
				</c:if>
                
                <c:choose>
                    <c:when test="${isList}">
                        <form:options items="${items}" />
                    </c:when>
                    <c:otherwise>
                        <form:options items="${items}" itemValue="${not empty itemValue ? itemValue :'code'}" itemLabel="${not empty itemLabel ? itemLabel :'name'}" />
                    </c:otherwise>
                </c:choose>
			</form:select>
		</div>
	</ycommerce:testId>
</template:errorSpanField>
