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
		<div class="backProduct">
		<c:choose>
			<c:when test="${page eq '1'}">
			<a href="/my-account/wishlist" class="ui-link">«&nbsp;Back</a>
			</c:when>
			<c:otherwise>
			<a href="${productUrl}" class="ui-link">«&nbsp;Back</a>
			</c:otherwise>
		</c:choose>
		</div>
		<div id="globalMessages">
			<common:globalMessages />
		</div>
		<div class="mailheading innerContent">
			<h1>
				<spring:theme code="mobile.product.notify.me"/>
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
				
					
				<c:url value="${product.url}/notifymail?page=${page}" var="notifyCustomer" />
                <form:form method="post" action="${notifyCustomer}" commandName="v2NotifyCustomerForm" id="notifyMe">
                    <div class="form_field-elements">
                        <br> <input type="hidden" value="NOTIFY_ME" name="type" /> <input type="hidden" value="${product.code}"
                            name="productCode" />
                        <form:hidden path="name" idKey="customerNotification.name" />
                        <form:hidden path="currentUserName" idKey="customerNotification.currentUserName" />
                        <div class="control-group">
                            <formElement:formInputBox labelKey="Email ID" idKey="currentUserEmailId" path="currentUserEmailId" mandatory="true"
                                inputCSS="clearcontent1" />
                        </div>
                        <br/>
                        <div id="notifybutton" >
                            <button class="positive margin0" data-theme="b" type="submit">Send</button>
                        </div>
                    </div>
                </form:form>
			</ul>
		</div>
		
	</div>
</template:page>
