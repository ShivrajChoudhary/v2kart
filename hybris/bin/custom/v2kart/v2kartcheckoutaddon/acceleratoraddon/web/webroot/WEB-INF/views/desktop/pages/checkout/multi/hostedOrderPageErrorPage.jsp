<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/addons/b2ccheckoutaddon/desktop/checkout/multi" %>
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/desktop/address" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<c:url value="${currentStepUrl}" var="choosePaymentMethodUrl"/>
<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">

    <div id="globalMessages">
        <common:globalMessages/>
    </div>

    <multi-checkout:checkoutProgressBar steps="${checkoutSteps}" progressBarId="${progressBarId}"/>

        <div class="span-14 append-1">
            <div id="checkoutContentPanel" class="clearfix">
                <ycommerce:testId code="paymentDetailsForm">

                <form:form id="silentOrderPostForm" name="silentOrderPostForm" commandName="v2PaymentInfoForm" class="create_update_payment_form" action="send" method="POST">

                    <div class="headline clear"><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.billingAddress"/></div>
                    <c:if test="${cartData.deliveryItemsQuantity > 0}">
                        <form:checkbox path="isUsingShippingAddress" id="useDeliveryAddress"
                                       data-firstname="${cartData.deliveryAddress.firstName}"
                                       data-lastname="${cartData.deliveryAddress.lastName}"
                                       data-line1="${cartData.deliveryAddress.line1}"
                                       data-line2="${cartData.deliveryAddress.line2}"
                                       data-town="${cartData.deliveryAddress.town}"
                                       data-postalcode="${cartData.deliveryAddress.postalCode}"
                                       data-countryisocode="${cartData.deliveryAddress.country.isocode}"
                                       data-regionisocode="${cartData.deliveryAddress.region.isocodeShort}"
                                       data-address-id="${cartData.deliveryAddress.id}"
                                       tabindex="11"/>
                        <spring:theme code="checkout.multi.sop.useMyDeliveryAddress"/>
                    </c:if>    
                    <address:billAddressFormSelector supportedCountries="${countries}" regions="${regions}" tabindex="12"/>
                    <div class="form-additionals">
                    </div>


                    <div class="form-actions">
                        <c:url value="/checkout/multi/delivery-method/choose" var="chooseDeliveryMethodUrl"/>
                        <a class="button" href="${chooseDeliveryMethodUrl}"><spring:theme code="checkout.multi.cancel" text="Cancel"/></a>
                        <button class="positive right submit_silentOrderPostForm" tabindex="20">
                            <spring:theme code="checkout.multi.paymentMethod.continue" text="Continue"/>
                        </button>
                    </div>
                </form:form>
                
                </ycommerce:testId>
            </div>
            
        </div>

</template:page>
