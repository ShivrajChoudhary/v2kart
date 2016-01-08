<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="actionNameKey" required="true" type="java.lang.String"%>
<%@ attribute name="action" required="true" type="java.lang.String"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>

<div data-content-theme="d" data-theme="e" class="registerPage">
    <!-- <div class="fakeHR"></div> -->
    <div class="heading">
    	<h1>
    		<spring:theme code="mobile.register.new.customer" />
    	</h1>
    	<p><spring:theme code="mobile.register.description" /></p>
    	<p><spring:theme code="form.required1" /><span class="mandatory"><spring:theme code="form.required.mark" /></span><spring:theme code="form.required2" /></p>
    </div>
    <div class="innerContent">
    <form:form method="post" id="registerForm" commandName="registerForm" action="${action}" data-ajax="false">
        <common:errors />
        <ul id="registerFormList" class="mFormList" data-theme="a" data-content-theme="a">
            <%-- <li><formElement:formSelectBox idKey="register.title" labelKey="register.title" path="titleCode" mandatory="true"
                    skipBlank="false" skipBlankMessageKey="form.select.empty" items="${titles}" /></li> --%>
            <li><formElement:formInputBox idKey="register.firstName" labelKey="register.firstName" path="firstName" inputCSS="text"
                    mandatory="true" /></li>
            <li><formElement:formInputBox idKey="register.lastName" labelKey="register.lastName" path="lastName" inputCSS="text"
                    mandatory="true" /></li>
            <li><formElement:formInputBox idKey="register.email" labelKey="register.email" path="email" inputCSS="text"
                    mandatory="true" /></li>
            <li>
            	<template:errorSpanField path="mobileNumber">
                   <ycommerce:testId code="register.mobileNumber">
                   <div data-role="fieldcontain" data-theme="c" data-content-theme="c" class="ui-field-contain ui-body ui-br">
                       <label for="register.mobileNumber"> <spring:theme code="register.mobileNumber" /> <span class="mandatory">
                               <spring:theme code="login.required" var="loginrequiredText" /> *
                       </span>
                       </label>
                       <div class="ui-grid-a">
                           <div class="isoCode readOnly_inputBox">
                               <form:input id="" path="" cssClass="text" readOnly="true" value="+91" />
                           </div>
                           <div class="mobileNo">
                               <form:input cssClass="text" id="register.mobileNumber" class="text ui-input-text ui-body-c" data-theme="c" type="text" path="mobileNumber" />
                           </div>
                           <span class="skip"><form:errors path="mobileNumber" /></span>
                       </div>
                       </div>
                   </ycommerce:testId>
               </template:errorSpanField>
            </li>
            <li><formElement:formPasswordBox idKey="password" labelKey="register.pwd" path="pwd" inputCSS="text password strength"
                    mandatory="true" /></li>
            <li><formElement:formPasswordBox idKey="register.checkPwd" labelKey="register.checkPwd" path="checkPwd"
                    inputCSS="text password" mandatory="true" /></li>
            <input type="hidden" id="recaptchaChallangeAnswered" value="${requestScope.recaptchaChallangeAnswered}" />

            <li><span style="display: block; clear: both;"> <ycommerce:testId code="register_Register_button">
                        <button data-theme="b" class="form">
                            <spring:theme code='${actionNameKey}' />
                        </button>
                    </ycommerce:testId>
            </span></li>
        </ul>
    </form:form>
    </div>
</div>
