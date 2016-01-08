<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:url value="${product.url}" var="productUrl" />
<c:set var="hideBreadcrumb" value="true" scope="request" />
<template:page pageTitle="${pageTitle}">
	<div>
		<div id="globalMessages">
			<common:globalMessages />
		</div>
		<div class="backProduct">
		<a href="${product.url}" class="ui-link">«&nbsp;Back</a>
		</div>
		<div class="mailheading innerContent">
			<h1>
				<spring:theme code="mobile.product.email.friend" />
			</h1>
			<p>
				<spring:theme code="mobile.review.required1" />
				<span class="mandatory">
					<spring:theme code="mobile.review.required.mark" />
				</span>
				<spring:theme code="mobile.review.required2" />
			</p>
		</div>		
		<div  class="innerContent">
		
			<ul class="mFormList" data-inset="true" data-theme="d">
				
					
				<c:url value="${product.url}/emailFriend" var="emailForm" />
                    <form:form method="post" action="${emailForm}" commandName="v2NotifyCustomerForm" id="emailFriend">
                        <div class="modal-body forgottenPwd clearfix" style="padding-top: 0px !important;">
                            <form:hidden path="name" idKey="customerNotification.name" />
                            <form:hidden path="currentUserName" idKey="customerNotification.currentUserName" />
                            <form:hidden path="currentUserEmailId" idKey="customerNotification.currentUserEmailId" />
                            <form:hidden path="mediaUrl" idKey="customerNotification.mediaUrl" />
                            <form:hidden path="productPrice" idKey="customerNotification.productPrice" />
                            <form:hidden path="productCode" idKey="customerNotification.productCode" />
                            <div class="control-group">
                                <formElement:formInputBox readOnly="true" labelKey="Product Link" idKey="url" path="url" mandatory="true"
                                    inputCSS="disabled" />
                            </div>
                            <div class="control-group">
                                <formElement:formInputBox idKey="emailId" labelKey="Friends' Email ID" path="emailId" placeholder="Email ID"
                                    mandatory="true" inputCSS="clearcontent" />
                            </div>
                            <div class="control-group">
                                   <formElement:formTextArea idKey="message" labelKey="Message" path="message" mandatory="true"
                                    areaCSS="clearcontent" />
                            </div>

                        </div>

                        <div >
                            <button id="sendMailFriend" class="btn btn-red" type="submit" data-theme="b" >Send Email</button>
                        </div>
                    </form:form>
			</ul>
		</div>
		
	</div>
</template:page>
