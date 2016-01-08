<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="checkout" tagdir="/WEB-INF/tags/addons/b2ccheckoutaddon/desktop/checkout"%>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart"%>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/addons/b2ccheckoutaddon/desktop/checkout/multi"%>
<%@ taglib prefix="multi-checkout-custom" tagdir="/WEB-INF/tags/addons/v2kartcheckoutaddon/desktop/checkout/multi"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="v2-multi-checkout" tagdir="/WEB-INF/tags/addons/v2kartcheckoutaddon/desktop/checkout/multi"%>

<c:url value="${nextStepUrl}" var="continueSelectDeliveryMethodUrl" />
<c:url value="${previousStepUrl}" var="addDeliveryAddressUrl" />
<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">

    <div id="globalMessages">
        <common:globalMessages />
    </div>
    <multi-checkout:checkoutProgressBar steps="${checkoutSteps}" progressBarId="${progressBarId}" />
    <input type="hidden" id="pickup" value="${cartData.deliveryMode.isPickUp}"/>
    <div class=row>
        <div class="col-md-7">
            <div class="yCmsContentSlot last check-content-sec chooseDeliveryMethod">
                <div class="checkout-pg">
                    <div class="headline">
                        <spring:theme code="checkout.multi.deliveryMethod.stepHeader" />
                    </div>
                </div>
                <div class="clearfix" id="checkoutContentPanel">

                    <form:form id="selectDeliveryMethodForm" action="${request.contextPath}/checkout/multi/delivery-method/select"
                        method="get">
                        <multi-checkout-custom:deliveryMethodSelector deliveryMethods="${deliveryMethods}"
                            selectedDeliveryMethodId="${cartData.deliveryMode.code}" />
                        <div class="form-actions">
                            <div class="bottom-btn-panel left">

                                <c:if test="${not empty cartData.deliveryMode.code}">
                                    <button id="chooseDeliveryMethod_continue_button" class="btn btn-red" style="margin-right: 0;">
                                        <spring:theme code="checkout.multi.deliveryMethod.proceed.payment" text="Continue" />
                                        <!--   Proceed To Pay -->
                                    </button>
                                </c:if>
                            </div>
                        </div>
                    </form:form>
                    <input type="hidden" name="currentStepUrl" value="${currentStepUrl}">
                </div>
            </div>
        </div>
        <!--col-md-6 end-->
        <div class="col-md-5">
            <v2-multi-checkout:checkoutOrderDetails cartData="${cartData}" showShipDeliveryEntries="true" showPickupDeliveryEntries="false"
                showTax="false" />
        </div>
        <cms:pageSlot position="SideContent" var="feature" element="div" class="span-24 side-content-slot cms_disp-img_slot">
            <cms:component component="${feature}" />
        </cms:pageSlot>
    </div>
</template:page>
