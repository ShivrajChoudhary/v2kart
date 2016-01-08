<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/addons/b2ccheckoutaddon/desktop/checkout/multi"%>
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/desktop/address"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/addons/b2ccheckoutaddon/desktop/checkout/multi"%>
<%@ taglib prefix="v2-multi-checkout" tagdir="/WEB-INF/tags/addons/v2kartcheckoutaddon/desktop/checkout/multi"%>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/desktop/storepickup" %>
<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">
    <div id="globalMessages">
        <common:globalMessages />
    </div>
    <multi-checkout:checkoutProgressBar steps="${checkoutSteps}" progressBarId="${progressBarId}" />
<%--       <storepickup:clickPickupInStore cartPage="false"/> --%>
<%--             <storepickup:pickupStorePopup/> --%>
    <div class="span-14 append-1 v2kartDelivaryAddress">
        <div class="yCmsContentSlot last check-content-sec">
            <div class="checkout-pg">
                <div class="headline">
                    <spring:theme code="checkout.multi.addressDetails" text="Address Details" />
                </div>
                <div class="address-fields">
                    <div>
                        <input type="radio" data-id="newAddress" name="checkR"
                            <c:if test="${(empty accErrorMsgs) and (not noAddress)}">checked</c:if>
                            <c:if test="${noAddress}">disabled</c:if>> <label><spring:theme
                                code="checkout.multi.deliveryAddress.addressBook" text="Address Book" /></label>
                    </div>
                    <div>
                        <input type="radio" data-id="defaultAddress" name="checkR"
                            <c:if test="${(not empty accErrorMsgs) or noAddress}">checked</c:if>> <label>Add new delivery
                            address</label>
                    </div>
                </div>
                <c:choose>
                    <c:when test="${fn:length(deliveryAddresses)>2}">
                        <div class="order-detailscntr" id="defaultAddress" style="overflow: scroll; max-height: 267px; overflow-x: hidden;">
                    </c:when>
                    <c:otherwise>
                        <div class="order-detailscntr" id="defaultAddress">
                    </c:otherwise>
                </c:choose>
                <!-- <div class="order-detailscntr" id="defaultAddress"> -->
                <ul class="order-info">
                    <c:if test="${not empty deliveryAddresses}">
                        <c:set var="counter" value="0" />
                        <c:forEach items="${deliveryAddresses}" var="deliveryAddress" varStatus="status">
                            <c:set var="counter" value="${counter + 1}" />
                            <li <c:if test="${counter%2 == 0}">class="nextAddress"</c:if>>
                                <h3>ADDRESS ${counter}</h3>
                                <form action="${request.contextPath}/checkout/multi/delivery-address/select" method="GET">
                                    <input type="hidden" name="selectedAddressCode" value="${deliveryAddress.id}" />
                                    <div class="addressItem del-info add-delAddress">
                                        <ul class="addressformat">
                                            <li>${fn:escapeXml(deliveryAddress.firstName)}&nbsp;${fn:escapeXml(deliveryAddress.lastName)}</li>
                                            <li class="addresslistformat">${fn:escapeXml(deliveryAddress.line1)}</li>
                                            <li class="addresslistformat">${fn:escapeXml(deliveryAddress.line2)}</li>
                                            <li>${fn:escapeXml(deliveryAddress.town)}&nbsp;-${fn:escapeXml(deliveryAddress.postalCode)}</li>
                                            <%-- <li>${fn:escapeXml(deliveryAddress.postalCode)}</li> --%>
                                            <li>${fn:escapeXml(deliveryAddress.region.name)}&nbsp;,${fn:escapeXml(deliveryAddress.country.name)}</li>
                                            <%-- <li>${fn:escapeXml(deliveryAddress.country.name)}</li> --%>
                                            <li>${fn:escapeXml(deliveryAddress.phone)}</li>
                                        </ul>
                                    </div>
                                    <button class="btn btn-red right " tabindex="22" type="submit"
                                        style="position: absolute; bottom: 8px; margin-left: -227px;">Use This Delivery Address</button>
                                </form>
                            </li>
                        </c:forEach>
                    </c:if>
                </ul>
            </div>
            
            <div class="addressContentPane none" id="newAddress">
                <div class="yCmsContentSlot accountBodyContentSlot">
                    <div class="yCmsComponent clearfix">
                        <div class="addressContentPane clearfix">
                            <form:form method="post" commandName="v2addressForm"
                                action="${request.contextPath}/checkout/multi/delivery-address/V2-save-delivery-address">
                                <form:hidden path="addressId" class="add_edit_delivery_address_id"
                                    status="${not empty suggestedAddresses ? 'hasSuggestedAddresses' : ''}" />
                                <input type="hidden" name="bill_state" id="address.billstate" />
                                <div id="i18nAddressForm" class="i18nAddressForm">
                                    <%-- <formElement:formSelectBox idKey="address.title" labelKey="address.title" path="titleCode"
                                            mandatory="true" skipBlank="false" skipBlankMessageKey="address.title.pleaseSelect"
                                            items="${titles}" selectedValue="${addressForm.titleCode}" /> --%>
                                    <formElement:formInputBox idKey="address.firstName" labelKey="address.firstName" path="firstName"
                                        inputCSS="text" mandatory="true"  />
                                    <formElement:formInputBox idKey="address.surname" labelKey="address.surname" path="lastName"
                                        inputCSS="text" mandatory="true" />
                                    <formElement:formInputBox idKey="address.line1" labelKey="address.line1" path="line1" inputCSS="text"
                                        mandatory="true" maxLength="35" />
                                    <formElement:formInputBox idKey="address.line2" labelKey="address.line2" path="line2" inputCSS="text"
                                        mandatory="false" maxLength="40" />
                                    <formElement:formInputBox idKey="address.townCity" labelKey="address.townCity" path="townCity"
                                        inputCSS="text" mandatory="true" />
                                    <formElement:formInputBox idKey="address.postcode" labelKey="address.postcode" path="postcode"
                                        inputCSS="text" mandatory="true" />
                                    <formElement:formSelectBox idKey="address.region" labelKey="address.label.state" path="regionIso"
                                        mandatory="true" skipBlank="false" skipBlankMessageKey="address.selectState" items="${regions}"
                                        itemValue="${useShortRegionIso ? 'isocodeShort' : 'isocode'}" />

                                    <formElement:formInputBox idKey="address.countryName" labelKey="address.countryName" path="countryName"
                                        inputCSS="text" readOnly="true" />


                                    <template:errorSpanField path="mobileno">
                                        <ycommerce:testId code="LoginPage_Item_address.mobileno">
                                            <label class="control-label ${labelCSS}" for="address.mobileno"> <spring:theme
                                                    code="address.mobileno" /> <span class="mandatory"> <spring:theme
                                                        code="login.required" var="loginrequiredText" /> <img width="5" height="6"
                                                    alt="${loginrequiredText}" title="${loginrequiredText}"
                                                    src="${commonResourcePath}/images/mandatory.png">
                                            </span> <span class="skip"><form:errors path="mobileno" /></span>
                                            </label>
                                            <div class="controls">
                                                <input type="text" value="+91" disabled="true" class="text small-input">
                                                <form:input cssClass="text checkp-input" id="address.mobileno" path="mobileno" />
                                            </div>
                                        </ycommerce:testId>
                                    </template:errorSpanField>
                                </div>
                                <sec:authorize ifNotGranted="ROLE_ANONYMOUS">
                                    <div class="form-additionals">
                                        <c:choose>
                                            <c:when test="${showSaveToAddressBook}">
                                                <formElement:formCheckbox idKey="saveAddressInMyAddressBook"
                                                    labelKey="checkout.summary.deliveryAddress.saveAddressInMyAddressBook"
                                                    path="saveInAddressBook" inputCSS="add-address-left-input"
                                                    labelCSS="add-address-left-label" mandatory="false" />
                                            </c:when>
                                            <c:when test="${not addressBookEmpty && not isDefaultAddress}">
                                                <formElement:formCheckbox idKey="defaultAddress" labelKey="address.default"
                                                    path="defaultAddress" inputCSS="add-address-left-input"
                                                    labelCSS="add-address-left-label" mandatory="false" />
                                            </c:when>
                                        </c:choose>
                                    </div>
                                </sec:authorize>
                                <div id="addressform_button_panel" class="form-actions">
                                    <c:choose>
                                        <c:when test="${edit eq true}">
                                            <ycommerce:testId code="multicheckout_saveAddress_button">
                                                <button class="positive right change_address_button show_processing_message" type="submit">
                                                    <spring:theme code="checkout.multi.saveAddress" text="Save address" />
                                                </button>
                                            </ycommerce:testId>
                                        </c:when>
                                        <c:otherwise>
                                            <ycommerce:testId code="multicheckout_saveAddress_button">
                                                <button class="btn btn-red change_address_button show_processing_message" type="submit">
                                                    <spring:theme code="checkout.multi.deliveryAddress.proceed"
                                                        text="Proceed to Delivery Mode" />
                                                </button>
                                            </ycommerce:testId>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </form:form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    </div>
    <v2-multi-checkout:checkoutOrderDetails cartData="${cartData}" showShipDeliveryEntries="true" showPickupDeliveryEntries="true"
        showTax="false" />
    <%--     <cms:pageSlot position="SideContent" var="feature" element="div" class="span-24 side-content-slot cms_disp-img_slot"> --%>
    <%--         <cms:component component="${feature}" /> --%>
    <%--     </cms:pageSlot> --%>
</template:page>