<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="supportedCountries" required="true" type="java.util.List"%>
<%@ attribute name="regions" required="true" type="java.util.List"%>
<%@ attribute name="country" required="false" type="java.lang.String"%>
<%@ attribute name="cancelUrl" required="false" type="java.lang.String"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/desktop/address"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>


<c:if test="${not empty deliveryAddresses}">
	<button type="button" class="positive clear view-address-book" id="viewAddressBook" >
		<spring:theme code="checkout.checkout.multi.deliveryAddress.viewAddressBook" text="View Address Book"/>
	</button>
</c:if>

<c:set var="country" value="IN" />
<form:form method="post" commandName="v2AddressForm">
	<form:hidden path="addressId" class="add_edit_delivery_address_id" status="${not empty suggestedAddresses ? 'hasSuggestedAddresses' : ''}"/>
	<input type="hidden" name="bill_state" id="address.billstate"/>
<%-- 	<div id="countrySelector" data-address-code="${addressData.id}" data-country-iso-code="${addressData.country.isocode}" class="clearfix"> --%>
<%-- 		<formElement:formSelectBox idKey="address.country" --%>
<%-- 		                           labelKey="address.country" --%>
<%-- 		                           path="countryIso" --%>
<%-- 		                           mandatory="true" --%>
<%-- 		                           skipBlank="false" --%>
<%-- 		                           skipBlankMessageKey="address.selectCountry" --%>
<%-- 		                           items="${supportedCountries}" --%>
<%-- 		                           itemValue="isocode" --%>
<%-- 		                           selectedValue="${addressForm.countryIso}"/> --%>
<!-- 	</div> -->
	<div id="i18nAddressForm" class="i18nAddressForm">
		<c:if test="${not empty country}">
			<address:addressFormElements regions="${regions}"
			                             country="IN"/>
		</c:if>
		
		
		
		
	</div>
	
	<sec:authorize ifNotGranted="ROLE_ANONYMOUS">
	<div class="form-additionals">
		<c:choose>
			<c:when test="${showSaveToAddressBook}">
				<formElement:formCheckbox idKey="saveAddressInMyAddressBook" labelKey="checkout.summary.deliveryAddress.saveAddressInMyAddressBook" path="saveInAddressBook" inputCSS="add-address-left-input" labelCSS="add-address-left-label" mandatory="false"/>
			</c:when>
			<c:when test="${not addressBookEmpty && not isDefaultAddress}">
				<formElement:formCheckbox idKey="defaultAddress" labelKey="address.default" path="defaultAddress"
				                          inputCSS="add-address-left-input" labelCSS="add-address-left-label" mandatory="false"/>
			</c:when>
		</c:choose>
		
	</div>
	</sec:authorize>

	<div id="bottom-btn-panel" class="form-actions">
    <table>
    <tr><td>
    <c:choose>
                    <c:when test="${edit eq true}">
                        <ycommerce:testId code="multicheckout_saveAddress_button">
                            <button class="btn btn-red" type="submit">
                                <spring:theme code="checkout.multi.saveAddress" text="Save"/>
                            </button>
                        </ycommerce:testId>
                    </c:when>
                    <c:otherwise>
                        <ycommerce:testId code="multicheckout_saveAddress_button">
                            <button class="btn btn-red" type="submit">
                                <spring:theme code="checkout.multi.save" text="Save"/>
                            </button>
                        </ycommerce:testId>
                    </c:otherwise>
                </c:choose>
                </td><td style="margin-top: 20px; ">
				<c:if test="${not noAddress}">
					<ycommerce:testId code="multicheckout_cancel_button">
						<c:url value="${cancelUrl}" var="cancel"/>
							<a href="${cancel}" class="btn btn-secondary"><spring:theme code="checkout.multi.cancel" text="Cancel"/></a>
					</ycommerce:testId>
				</c:if>
                </td>
</table>	
			
	</div>
</form:form>
