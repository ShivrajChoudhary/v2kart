<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="idKey" required="true" type="java.lang.String"%>
<%@ attribute name="labelKey" required="true" type="java.lang.String"%>
<%@ attribute name="disabled" required="false" type="java.lang.String" %>
<%@ attribute name="placeholder" required="false" type="java.lang.String" %>
<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="mandatory" required="false" type="java.lang.Boolean"%>
<%@ attribute name="labelCSS" required="false" type="java.lang.String"%>
<%@ attribute name="inputCSS" required="false" type="java.lang.String"%>
<%@ attribute name="tabindex" required="false" rtexprvalue="true"%>
<%@ attribute name="autocomplete" required="false" type="java.lang.String" %>
<%@ attribute name="maxLength" required="false" type="java.lang.String"%>
<%@ attribute name="readOnly" required="false" type="java.lang.Boolean"%>
<%@ attribute name="className" required="false" type="java.lang.String"%>
<%@ attribute name="showLabel" required="false" type="java.lang.Boolean"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template" %>


<template:errorSpanField path="${path}">
	<ycommerce:testId code="LoginPage_Item_${idKey}">
		<div data-role="fieldcontain" data-theme="c" data-content-theme="c">
			<c:if test="${showLabel == null || showLabel == true}">
				<label class="${labelCSS}" for="${idKey}"> <spring:theme code="${labelKey}" />
				<c:if test="${mandatory != null && mandatory == true}">
					<span class="mandatory"> <spring:theme code="login.required" var="loginrequiredText" />
						*
					</span>
				</c:if>
				</label>
			</c:if>
			
			<div class="${className}">
			 	<c:if test="${inlineError}">
                	<span class="inlineError skipInline" style=""><form:errors path="${path}" /></span>
            	</c:if>
				<form:input type="date" cssClass="${inputCSS}" id="${idKey}" path="${path}" tabindex="${tabindex}"
			 		autocomplete="${autocomplete}" data-theme="c"  maxlength="${maxLength}"  placeholder = "${placeholder}"/>
				<c:if test="${!inlineError}">
                	<span class="noInlineError skip" style=""><form:errors path="${path}" /></span>
            	</c:if>
			</div>
			
		</div>
	</ycommerce:testId>
</template:errorSpanField>