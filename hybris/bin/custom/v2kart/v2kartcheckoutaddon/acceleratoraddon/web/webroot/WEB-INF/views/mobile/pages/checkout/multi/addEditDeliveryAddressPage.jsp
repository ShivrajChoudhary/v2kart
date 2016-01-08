<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/mobile/address"%>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/addons/v2kartcheckoutaddon/mobile/checkout/multi"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<c:set var="hideBreadcrumb" value="true" scope="request" />
<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">
    <jsp:body>
		
		<multi-checkout:checkoutProgressBar steps="${checkoutSteps}" progressBarId="${progressBarId}" />
        <div id="globalMessages" data-theme="b">
			<common:globalMessages />
		</div>
		<div data-theme="e" class="innerContent">
				<div class="checkout-delivery">
					<h1>
						<spring:theme code="checkout.multi.addressDetails" text="Address Details" />
					</h1>
					<ul data-role="controlgroup" data-theme="a">
                            <li><input type="radio" name="deliveryAddressRadio" id="useExistingAddress" data-theme="c" data-role="radio"
                        <c:if test="${(empty accErrorMsgs) and (not noAddress)}">checked</c:if> <c:if test="${noAddress}">disabled</c:if> /> <label
                        for="useExistingAddress"> Address Book</label></li>
                            <li><input type="radio" name="deliveryAddressRadio" id="addNewAddress" data-theme="c" data-role="radio"
                        <c:if test="${(not empty accErrorMsgs) or noAddress}">checked</c:if> /> <label for="addNewAddress"> Add new delivery address</label></li>
                    </ul>
					<c:if test="${not empty deliveryAddresses}">
                    <div class="payment_details_right_col saved-payment-list" style='<c:if test="${(empty accErrorMsgs) and (noAddress)}">display:none</c:if>'>
                    <h2>
                        <spring:theme code="" text="Select saved delivery address" />
                    </h2>
                        <c:forEach items="${deliveryAddresses}" var="deliveryAddress" varStatus="status">
                            <div class="saved-payment-list-entry">
                                <form action="${request.contextPath}/checkout/multi/delivery-address/select" method="GET">
                                    <input type="hidden" name="selectedAddressCode" value="${deliveryAddress.id}" />

                                    <span class="saved-payment-list-item">${fn:escapeXml(deliveryAddress.firstName)}&nbsp; ${fn:escapeXml(deliveryAddress.lastName)}</span>
                                    <span class="saved-payment-list-item">${fn:escapeXml(deliveryAddress.line1)}</span>
                                    <span class="saved-payment-list-item">${fn:escapeXml(deliveryAddress.line2)}</span>
                                    <c:choose>
                                        <c:when test="${not empty deliveryAddress.region.name}">
                                            <span class="saved-payment-list-item">${fn:escapeXml(deliveryAddress.town)}-${fn:escapeXml(deliveryAddress.postalCode)}</span>
                                            <span class="saved-payment-list-item">${fn:escapeXml(deliveryAddress.region.name)},${fn:escapeXml(deliveryAddress.country.name)}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="saved-payment-list-item">${fn:escapeXml(deliveryAddress.town)},${fn:escapeXml(deliveryAddress.postalCode)}</span>
                                            <span class="saved-payment-list-item">${fn:escapeXml(deliveryAddress.country.name)}</span>
                                        </c:otherwise>
                                    </c:choose>
                                    <span class="saved-payment-list-item">${fn:escapeXml(deliveryAddress.phone)}</span>
                                    
                                    <button type="submit" class="form" tabindex="${status.count + 21}" data-theme="b">
                                        <spring:theme code="checkout.multi.deliveryAddress.useThisAddress" text="Use this delivery address" />
                                    </button>
                                </form>
                               <%--  <form:form action="${request.contextPath}/checkout/multi/delivery-address/remove" method="POST">
                                    <input type="hidden" name="addressCode" value="${deliveryAddress.id}" />
                                    <button type="submit" class="text-button remove-payment-item" tabindex="${status.count + 22}" data-theme="g">
                                        <spring:theme code="checkout.multi.deliveryAddress.remove" text="Remove" />
                                    </button>
                            </form:form> --%>
                            </div>
                            
                        </c:forEach>
                    </div>
                    <!-- <div data-role="header" data-theme="b">
                    <h1>OR</h1>
                </div> -->
        			</c:if>
        
					
                	<div class="enter_new_address" style='<c:if test="${(empty accErrorMsgs) and (not noAddress)}">display:none</c:if>'>
                	 <h2>
                        <spring:theme code="" text="Enter new delivery address" />
                    </h2>
                	                
                	<p> <spring:theme code="mobile.review.required1" />
                        <span class="mandatory"><spring:theme code="mobile.review.required.mark" /></span>
                        <spring:theme code="mobile.review.required2" />
                	</p>
                	
					<form:form method="post" commandName="v2addressForm"
                        action="${request.contextPath}/checkout/multi/delivery-address/V2-save-delivery-address">
    					<form:hidden path="addressId" class="add_edit_delivery_address_id"
                            status="${not empty suggestedAddresses ? 'hasSuggestedAddresses' : ''}" />
    					<input type="hidden" name="bill_state" id="address.billstate" />

  						<div id="i18nAddressForm" class="i18nAddressForm">
        				
        				<%-- <formElement:formSelectBox idKey="address.title" labelKey="address.title" path="titleCode" mandatory="true" skipBlank="false"
                                skipBlankMessageKey="address.title.pleaseSelect" items="${titles}" selectedValue="${addressForm.titleCode}" /> --%>
        				<formElement:formInputBox idKey="address.firstName" labelKey="address.firstName" path="firstName" inputCSS="text"
                                mandatory="true" />
        				<formElement:formInputBox idKey="address.surname" labelKey="address.surname" path="lastName" inputCSS="text" mandatory="true" />
     
        				<template:errorSpanField path="mobileno">
                            <ycommerce:testId code="LoginPage_Item_address.mobileno">
                            <div data-role="fieldcontain" data-theme="c" data-content-theme="c" class="ui-field-contain ui-body ui-br">
                                <label for="address.mobileno"> <spring:theme code="address.mobileno" /> <span class="mandatory">
                                        <spring:theme code="login.required" var="loginrequiredText" /> *
                                </span>
                                </label>
                                <div class="ui-grid-a">
                                    <div class="isoCode readOnly_inputBox">
                                        <form:input id="" path="" cssClass="text" readOnly="true" value="+91" />
                                    </div>
                                    <div class="mobileNo">
                                        <form:input cssClass="text" id="address.mobileno" class="text ui-input-text ui-body-c" data-theme="c" type="text" path="mobileno" />
                                    </div>
                                    <span class="skip"><form:errors path="mobileno" /></span>
                                </div>
                                </div>
                            </ycommerce:testId>
                        </template:errorSpanField>      
       					 <formElement:formInputBox idKey="address.line1" labelKey="address.line1" path="line1" inputCSS="text" mandatory="true" maxLength="35" />
        				<formElement:formInputBox idKey="address.line2"  labelKey="address.line2" path="line2" inputCSS="text" mandatory="false" maxLength="40" />
        				<formElement:formInputBox idKey="address.townCity" labelKey="address.townCity" path="townCity" inputCSS="text" mandatory="true" />
        				<formElement:formInputBox idKey="address.postcode" labelKey="address.postcode" path="postcode" inputCSS="text" mandatory="true" />
         				<formElement:formSelectBox idKey="address.region" labelKey="address.label.state" path="regionIso" mandatory="true"
                                skipBlank="false" skipBlankMessageKey="address.selectState" items="${regions}"
                                itemValue="${useShortRegionIso ? 'isocodeShort' : 'isocode'}" />
        				
        				 <formElement:formInputBox idKey="address.countryName" labelKey="address.countryName" path="countryName" inputCSS="text"
                                readOnly="true" className="readOnly_inputBox" />
        				
    					</div>
    				<sec:authorize ifNotGranted="ROLE_ANONYMOUS">
            			<c:choose>
                			<c:when test="${showSaveToAddressBook}">
                    			<formElement:formCheckbox idKey="saveAddressInMyAddressBook"
                                        labelKey="checkout.summary.deliveryAddress.saveAddressInMyAddressBook" path="saveInAddressBook"
                                        inputCSS="add-address-left-input" labelCSS="add-address-left-label" mandatory="false" />
                			</c:when>
                			<c:when test="${not addressBookEmpty && not isDefaultAddress}">
                    			<formElement:formCheckbox idKey="defaultAddress" labelKey="address.default" path="defaultAddress"
                                        inputCSS="add-address-left-input" labelCSS="add-address-left-label" mandatory="false" />
                			</c:when>
            			</c:choose>
        			</sec:authorize>
            <span style="display: block; clear: both;">
                <c:choose>
                    <c:when test="${edit eq true}">
                        <ycommerce:testId code="multicheckout_saveAddress_button">
                            <button class="form change_address_button" data-theme="b">
                                <spring:theme code="checkout.multi.saveAddress" text="Save address" />
                            </button>
                        </ycommerce:testId>
                    </c:when>
                    <c:otherwise>
                        <ycommerce:testId code="multicheckout_saveAddress_button">
                            <button class="form change_address_button" type="submit" data-theme="b">
                                <spring:theme code="checkout.checkout.multi.next" text="Next" />
                            </button>
                        </ycommerce:testId>
                    </c:otherwise>
                </c:choose>

            </span>
         </form:form>
		</div>
					<%-- <address:addressFormSelector supportedCountries="${countries}" regions="${regions}" cancelUrl="${currentStepUrl}" country="${country}" /> --%>
					<%-- <address:suggestedAddresses selectedAddressUrl="/checkout/multi/delivery-address/select" /> --%>
				</div>

				
			</div>
	</jsp:body>
</template:page>
