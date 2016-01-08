<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<c:url var="trackGuestOrdersUrl" value="/trackYourOrder/trackGuestUserOrders" />
<template:page pageTitle="${pageTitle}">

  <input type="hidden" value="<c:url value="/trackYourOrder/trackGuestOrder" />" id="mobile_trackGuestOrderUrl" />
    <div data-content-theme="d" data-theme="e" class="trackYourOrderPage">
        <!-- <div class="fakeHR"></div> -->
        <div class="description" data-role="header" data-theme="a">
            <h1 class="heading">
                <spring:theme code="guest.track.using.order.id" text="Track using Order ID" />
            </h1>
        </div>
        <div class="innerContent">
            <form:form method="post" id="mobile_trackOrderFormId" action="${trackGuestOrdersUrl}" commandName="trackOrderForm">

                <div class="form_field-elements">
                    <div class="control-group">
                        <label class="control-label "> Email Address <span class="mandatory"> <spring:theme
                                    code="login.required" var="loginrequiredText" /> <img width="5" height="6" alt="${loginrequiredText}"
                                title="${loginrequiredText}" src="${commonResourcePath}/images/mandatory.png">
                        </span><span class="skip" id="mobile_emailErrorId"></span> <span id="mobile_emailIdNotFound" class="skip">${emailMsg}</span>
                        </label>
                        <div class="controls">
                            <formElement:formInputBox idKey="guest.user.email" labelKey="" path="emailId"
                                inputCSS="mobile_emailIdForOrderHistory" />
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label "> Order ID <span class="mandatory"> <spring:theme code="login.required"
                                    var="loginrequiredText" /> <img width="5" height="6" alt="${loginrequiredText}"
                                title="${loginrequiredText}" src="${commonResourcePath}/images/mandatory.png">
                        </span> <span class="skip" id="mobile_orderNoErrorId"> </span> <span id="mobile_orderIdNotFound" class="skip">${orderMsg}</span>
                        </label>
                        <div class="controls">
                            <formElement:formInputBox idKey="guest.user.order" labelKey="" path="orderNumber"
                                inputCSS="mobile_orderIdForOrderHistory" />
                        </div>
                    </div>
                    <input type="hidden" name="CSRFToken" value="${CSRFToken}">
                </div>
                <div>
                    <button data-theme="b" type="button" id="mobile_OrderHistoryForGuest">
                        <spring:theme code="guest.view.order.status" />
                    </button>

                </div>
            </form:form>
        </div>
        <div class="description" data-role="header" data-theme="a">
            <h1 class="heading">
                <spring:theme code="guest.login.and.do.more.text" text="Login and do more!" />
            </h1>
        </div>
        <div class="innerContent">
            <ul class="trackinfo">
                <li><spring:theme code="guest.track.individual.order.text" text="Track individual Orders" /></li>
                <li><spring:theme code="guest.track.view.entire.order.text" text="View your entire Order history" /></li>
                <li><spring:theme code="guest.track.cancel.order.text" text="Cancel individual Orders" /></li>
                <li><spring:theme code="guest.track.conveniently.review.text" text="Conveniently review products and sellers." /></li>
            </ul>
            <div class="form-actions clearfix">
                <a href="<c:url value="/login"/>" data-theme="b" data-role="button" title="Login"><spring:theme code="guest.track.login.text" text="Login"/></a>
            </div>
        </div>
    </div>

</template:page>