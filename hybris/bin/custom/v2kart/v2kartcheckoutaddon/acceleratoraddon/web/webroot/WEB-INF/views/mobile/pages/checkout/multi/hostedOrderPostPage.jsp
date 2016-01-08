<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/mobile/cart"%>
<%@ taglib prefix="checkout-cart" tagdir="/WEB-INF/tags/addons/b2ccheckoutaddon/mobile/checkout"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/addons/v2kartcheckoutaddon/mobile/checkout/multi"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<c:url value="${currentStepUrl}" var="choosePaymentMethodUrl" />
<c:set var="hideBreadcrumb" value="true" scope="request" />
<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">

    <common:shopMoreBanner shopMoreAmount="${cartData.shopMoreForFreeDelivery}"/>
    <multi-checkout:checkoutProgressBar steps="${checkoutSteps}" progressBarId="${progressBarId}" />
    <div id="globalMessages">
        <common:globalMessages />
    </div>
    
    <c:choose>
        <c:when test="${v2PaymentInfoForm.isUsingWallet eq false }">
            <c:url var="walletCheckBoxActionUrl" value="/checkout/multi/payment-method/add/wallet" />
        </c:when>
        <c:otherwise>
            <c:url var="walletCheckBoxActionUrl" value="/checkout/multi/payment-method/add" />
        </c:otherwise>
    </c:choose>
                
    <form action="${walletCheckBoxActionUrl }" method="get" id="isUsingWalletCheckBoxForm">
    </form>
    
    <div class="innerContent" id="paymentInfo">
        <form:form commandName="v2PaymentInfoForm" action="send" method="POST">
            <div data-role="content" data-theme="b" class="marginTop">
                <div class="heading-black">
                    <spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.billingAddress" />
                </div>
                 <c:choose>
                                <c:when test="${isPickUpAllFromStore eq true}">
                                    <form:hidden path="isUsingShippingAddress" id="isUsingShippingAddress" value="false" />
                                    <input type="hidden" name="isPickUpAllFromStore" value="true">
                                    <div id="billingAdress" class="form_field-elements"></div>
                                </c:when>
                                <c:otherwise>
                                    <c:if test="${cartData.deliveryItemsQuantity > 0}">
                                        <form:checkbox path="isUsingShippingAddress" id="differentAddress" data-theme="c"
                                            data-firstname="${cartData.deliveryAddress.firstName}"
                                            data-lastname="${cartData.deliveryAddress.lastName}"
                                            data-line1="${cartData.deliveryAddress.line1}" data-line2="${cartData.deliveryAddress.line2}"
                                            data-town="${cartData.deliveryAddress.town}"
                                            data-postalcode="${cartData.deliveryAddress.postalCode}"
                                            data-countryisocode="${cartData.deliveryAddress.country.isocode}"
                                            data-regionisocode="${cartData.deliveryAddress.region.isocodeShort}"
                                            data-address-id="${cartData.deliveryAddress.id}" tabindex="11"/>
                                        <label for="differentAddress"><spring:theme code="v2kartcheckout.multi.sop.useMyDeliveryAddress" /></label>
                                    </c:if>
                                </c:otherwise>
                            </c:choose>
<%--   <c:choose>
            <c:when test="${isPickUpAllFromStore eq true}">
            <form:hidden path="isUsingShippingAddress" id="isUsingShippingAddress" value="false"/>
            <input type="hidden" name="isPickUpAllFromStore" value="true">
                <div id="billingAdress" class="form_field-elements"></div>
            </c:when>
            <c:otherwise>
             <c:if test="${cartData.deliveryItemsQuantity > 0}">
                <form:checkbox id="differentAddress" path="isUsingShippingAddress" data-role="checkbox" data-theme="c" />
                <label for="differentAddress"><spring:theme code="v2kartcheckout.multi.sop.useMyDeliveryAddress" /></label>
                </c:if>
                </c:otherwise>
                </c:choose> --%>
            </div>
            <div data-content-theme="c" data-theme="b" id="addBillingAddressForm" style="display: none" data-role="content"
                class="innerContent">
                <h3>
                    <spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.billingAddress" />
                </h3>
                <spring:theme code="mobile.review.required1" />
                <span class="mandatory"><spring:theme code="mobile.review.required.mark" /></span>
                <spring:theme code="mobile.review.required2" />
                <ul class="billingAddress">
                    <%-- <form:hidden path="billingAddress.addressId" class="create_update_address_id" /> --%>
                    <%-- <li><formElement:formSelectBox idKey="address.title" labelKey="address.title" path="billingAddress.titleCode"
                        mandatory="true" skipBlank="false" skipBlankMessageKey="address.title.pleaseSelect" items="${titles}" tabindex="10" />
                </li> --%>
                    <li><formElement:formInputBox idKey="address.firstName" labelKey="address.firstName" path="firstName"
                            inputCSS="text" mandatory="true" tabindex="11" /></li>
                    <li><formElement:formInputBox idKey="address.surname" labelKey="address.surname" path="lastName" inputCSS="text"
                            mandatory="true" tabindex="12" /></li>
                    <li><formElement:formInputBox idKey="address.line1" labelKey="address.line1" path="line1" inputCSS="text"
                            mandatory="true" tabindex="14" /></li>
                    <li><formElement:formInputBox idKey="address.line2" labelKey="address.line2" path="line2" inputCSS="text"
                            mandatory="false" tabindex="15" /></li>
                    <li><formElement:formInputBox idKey="address.townCity" labelKey="address.townCity" path="townCity" inputCSS="text"
                            mandatory="true" tabindex="16" /></li>
                    <li><formElement:formInputBox idKey="address.postcode" labelKey="address.postcode" path="postcode" inputCSS="text"
                            mandatory="true" tabindex="17" /></li>
                    <li><formElement:formSelectBox idKey="address.region" labelKey="address.label.state" path="regionIso"
                            mandatory="true" skipBlank="false" skipBlankMessageKey="address.selectState" items="${regions}"
                            itemValue="${useShortRegionIso ? 'isocodeShort' : 'isocode'}" /><span class="skip form_field_error"><form:errors
                                path="regionIso" /></span></li>
                    <li><formElement:formInputBox idKey="address.countryName" labelKey="address.countryName" path="countryName"
                            inputCSS="text" mandatory="true" tabindex="17" readOnly="true" className="readOnly_inputBox"/> <form:hidden path="countryIso" value="IN" /></li>

                    <li><template:errorSpanField path="mobileno">
                            <ycommerce:testId code="LoginPage_Item_address.mobileno">
                            <div data-role="fieldcontain" data-theme="c" data-content-theme="c" class="ui-field-contain ui-body ui-br">
                                <label for="address.mobileno"> <spring:theme code="address.mobileno" /> <span class="mandatory">
                                        <spring:theme code="login.required" var="loginrequiredText" /> *
                                </span>
                                </label>
                                <div class="ui-grid-a">
                                    <div class="isoCode readOnly_inputBox">
                                        <form:input id="" path="" cssClass="text" readOnly="true" value="+91" data-theme="c"/>
                                    </div>
                                    <div class="mobileNo">
                                        <form:input cssClass="text" id="address.mobileno" data-theme="c" type="text" path="mobileno" />
                                    </div>
                                    <span class="skip"><form:errors path="mobileno" /></span>
                                </div>
                                </div>
                            </ycommerce:testId>
                        </template:errorSpanField></li>
                     
                </ul>
            </div>
            <div data-role="content" data-theme="b">
                <template:errorSpanField path="smsmobileno">
                    <ycommerce:testId code="LoginPage_Item_address.smsmobileno">
                        <label class="control-label ${labelCSS}" for="address.smsmobileno"> <span class="skip"><form:errors
                                    path="smsmobileno" /></span>
                        </label>
                    </ycommerce:testId>
                </template:errorSpanField>
            
                <div class="clear"></div>
                <label style="font-weight: 600;;padding-left: 9px;"><span style="font-weight: normal;" class="emailicon">&#9993;</span>
                <spring:theme code="order.confirmation.sms.alternate.text" text="Send Order Confirmation SMS alert to" /></label>
                <div class="innerContent">
	                <div data-role="fieldcontain" data-theme="c" data-content-theme="c" class="ui-field-contain ui-body ui-br">
		                 <div class="ui-grid-a">
		                   <div class="isoCode readOnly_inputBox">
		                       <form:input id="" path="" cssClass="text" readOnly="true" value="+91" data-theme="c"/>
		                   </div>
		                   <div class="mobileNo">
		                       <form:input cssClass="text" id="address.smsmobileno" data-theme="c" type="text" path="smsmobileno" />
		                   </div>
		               	</div>
	                </div>
                </div>
            </div>
            <div data-role="content" data-theme="b" class="marginTop">
                <div class="heading-black">
                    <spring:theme code="text.account.paymentDetails" />
                </div>
                <h3 class="margin05">Select Payment Method</h3>
                <div data-role="collapsible-set" data-theme="b" data-collapsed-icon="arrow-d" data-iconpos="right" data-expanded-icon="arrow-u" class="margin05" >
                                    <div data-role="collapsible" id="walletPaymentAccordion" data-collapsed="false">
                        <h2><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.wallet.headline" /></h2>
                        <ul data-role="controlgroup" data-theme="a">
                            <li>
                            <fmt:parseNumber  integerOnly="true" type="number" var="balance"  value="${user.walletCreditBalance }" />
                           
                             
                                <c:choose>
                                    <c:when test="${user.walletCreditBalance > 0 }">
                           
                                    <form:checkbox id="isUsingWallet" path="isUsingWallet" data-theme="c" data-role="checkbox"/>
                                        <span class="useMyWallet"><label for="isUsingWallet"><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.wallet" /></label></span>
                                        <label><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.wallet.walletToBeUsed" arguments="${balance}"/></label>
                                        <br>
                                        <c:if test="${v2PaymentInfoForm.isUsingWallet}">
                                        <label><spring:theme code="wallet.consume.balance" />
                                        <spring:theme code="wallet.points" />
								<fmt:formatNumber type="number" maxFractionDigits="0"
									value="${(cartData.totalPrice.value)}" />
								
                                        </label>
							<div class="walletbalance">
								<spring:theme code="wallet.balance" />
								<spring:theme code="wallet.points" />
								<fmt:formatNumber type="number" maxFractionDigits="0"
									value="${user.walletCreditBalance-((cartData.totalPrice.value)-(cartData.totalPayableBalance.value))}" />
								
							</div>
							</c:if>
                                    </c:when>
                                    <c:otherwise>
                                        <spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.wallet.walletToBeUsed.empty" text="Empty Wallet"/>
                                    </c:otherwise>
                                </c:choose>
                                
                            </li>                            
                        </ul>                        
                    </div>
                    </div>
                <div data-role="collapsible-set" data-theme="b" data-collapsed-icon="arrow-d" data-iconpos="right" data-expanded-icon="arrow-u" class="margin05">
                    <c:if test="${not (v2PaymentInfoForm.isUsingWallet && (not (cartData.totalPayableBalance.value > 0))) }">
                    <div data-role="collapsible" id="onlinePaymentAccordion" data-collapsed="true">
                        <h2>Online Payment</h2>
                        <ul data-role="controlgroup" data-theme="a">
              
										
							<li><input type="radio" name="enforcedPaymentMethod" id="enforcedPaymentMethod1" value="${netbanking}" checked="checked"
                                data-theme="c" data-role="radio" /> <label for="enforcedPaymentMethod1"> Net Banking</label></li>
                            <li><input type="radio" name="enforcedPaymentMethod" id="enforcedPaymentMethod2" value="${debitcard}"
                                data-theme="c" data-role="radio" /> <label for="enforcedPaymentMethod2"> Debit Card</label></li>
                            <li><input type="radio" name="enforcedPaymentMethod" id="enforcedPaymentMethod3" value="${creditcard}"
                                data-theme="c" data-role="radio" /> <label for="enforcedPaymentMethod3"> Credit Card</label></li>
                            <li><input type="radio" name="enforcedPaymentMethod" id="enforcedPaymentMethod4" value="${cashcard}"
                                data-theme="c" data-role="radio" /> <label for="enforcedPaymentMethod4">Cash Card</label></li>	
                                
                        </ul>
                        <div class="paymentcard">
                            <a href="#"> <img src="${siteResourcePath}/images/visa.jpg" alt="">
                            </a> <a href="#"> <img src="${siteResourcePath}/images/mastercard.jpg" alt="">
                            </a> <a href="#"> <img src="${siteResourcePath}/images/maestroCC.png" alt="">
                            </a> <a href="#"> <img src="${siteResourcePath}/images/americanCC.png" alt="">
                            </a>
                        </div>
                        <div class="tAndCchkbox">
                            <input type="checkbox" id="termsAndConditionsAgree"/>
                            <label for="termsAndConditionsAgree"><spring:theme code="text.termsAndConditions.agree" text="I agree to"/></label>
                            <a href="/termsAndCondition" target="blank" class="terms"><spring:theme code="text.termsAndConditions" text="Terms and Conditions"/></a>
                        </div>
                    </div>
                    <c:if test="${isCODFacilityAvailable eq true}">
                        <div data-role="collapsible" id="codAccordion">
                            <h2>Cash on Delivery</h2>
                            <ul>
                                <li><c:choose>
                                        <c:when test="${cartData.isCODChargesApplicable}">
                                            <%-- <span style="font-weight: 600;">
                                                <spring:theme code="shop.more.cod.free.charges.text"
                                                    arguments="${cartData.shopMoreForFreeCOD.formattedValue}" />
                                            </span>
                                            <br><br>
                                            <a class="shopMoreBtn" data-role="button" href="/">Shop More</a> --%>
                                            <div id="codCheckboxCheck">
                                            <label><input type="checkbox" id="codCheckboxChecked" data-role="checkbox"
                                                data-theme="c" />  <span class="textcod"><spring:theme code="cod.charges.applicable.text"
                                                    arguments="${cartData.codCharges.formattedValue}" /></span></label>
                                             </div>
                                        </c:when>
                                        <c:otherwise>
                                            <span style="font-weight: 600;">
                                                <spring:theme code="cod.charges.free.text" />
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                 </li>
                                 
                            </ul>
                      <div class="codOtpTextBox">
                                                    <p><spring:theme code="checkout.multi.paymentMethod.cod.otpmessage"></spring:theme></p>
                                                    <p> <spring:theme code="checkout.multi.paymentMethod.cod.otpvalid"></spring:theme></p>
                                                    <div class="smsAlert clearfix">
                                                   <div class="ui-grid-b">
	<div class="ui-block-a" >  <input type="text" class="otpSmsInput" maxlength="6"  placeholder="One time password"> </div>
	<div class="ui-block-b" >   <a  data-role="button"  class="resentOtp" title="Generate OTP" href="javascript:void(0)" >Generate OTP</a></div>
</div>
                                                        <span class="otpMessage "></span>
                                                         <span class="not-valid-otp"></span>
                                                    </div>
                                                </div>
                        
                        </div>
                              
                        
                    </c:if>
                  
                    <div>
       
    </div>
                   

                    </c:if>
                </div>
                
            </div>
            <div data-role="content" data-theme="b" class="marginTop">
                <div class="heading-black">
                    <spring:theme code="text.account.Summary" text="Summary" />
                </div>
                <div>
                	<table id="orderTotals">
						<%-- <thead>
							<tr>
								<th><spring:theme code="order.order.totals" /></th>
								<th></th>
							</tr>
						</thead> --%>
						<tfoot>
							<tr>
								<td><spring:theme code="basket.page.totals.total" /></td>
								<td><ycommerce:testId code="cart_totalPrice_label">
										<c:choose>
											<c:when test="${showTax}">
												<format:price priceData="${cartData.totalPriceWithTax}" />
											</c:when>
											<c:otherwise>
												<format:price priceData="${cartData.totalPrice}" />
											</c:otherwise>
										</c:choose>
					
									</ycommerce:testId></td>
							</tr>
							<c:if
								test="${(v2PaymentInfoForm.isUsingWallet) && (not (cartData.totalPayableBalance.value eq cartData.totalPrice.value)) }">
								<tr class="savings">
									<td><spring:theme code="basket.page.totals.walletamount" /></td>
									<td><ycommerce:testId code="Order_Totals_Savings">
											<spring:theme code="wallet.points" />
											<spring:theme code="wallet.subtract" />
											<c:choose>
												<c:when test="${showTax}">
													<fmt:formatNumber type="number" maxFractionDigits="0"
														value=" ${cartData.totalPriceWithTax.value-cartData.totalPayableBalance.value}" />
												</c:when>
												<c:otherwise>
													<fmt:formatNumber type="number" maxFractionDigits="0"
														value=" ${cartData.totalPrice.value-cartData.totalPayableBalance.value}" />
												</c:otherwise>
											</c:choose>
										</ycommerce:testId></td>
								</tr>
								<tr class="savings">
									<td><spring:theme code="basket.page.totals.payable" /></td>
									<td><ycommerce:testId code="Order_Totals_Savings">
											<format:price priceData="${cartData.totalPayableBalance}" />
										</ycommerce:testId></td>
								</tr>
							</c:if>
					
						</tfoot>
	<tbody>
		<tr>
			<td><spring:theme code="basket.page.totals.subtotal" /></td>
			<td><ycommerce:testId code="Order_Totals_Subtotal">
					<format:price priceData="${cartData.subTotal}" />
				</ycommerce:testId></td>
		</tr>

		<tr class="shippingChargesInfo">
			<td><spring:theme code="basket.page.totals.delivery" /> <c:if
					test="${empty cartData.deliveryMode}">
					<span class="clearfix" style="line-height: 14px;"> <span
						style="font-size: 12px;"><spring:theme
								code="basket.page.shipping.amountVariation"
								text="(Actual amount may vary<br> based on your location)" /></span>
					</span>
				</c:if></td>
			<td style="vertical-align: top;"><format:price
					priceData="${cartData.deliveryCost}" displayFreeForZero="TRUE" /></td>
		</tr>

		<c:if test="${cartData.totalGiftWrapPrice.value ne null }">
			<tr>
				<td><spring:theme code="" />
					<spring:theme code="basket.page.totals.giftwrap" /></td>
				<td>${cartData.totalGiftWrapPrice.formattedValue}</td>
			</tr>


		</c:if>
		<c:if test="${cartData.net && cartData.totalTax.value > 0 && showTax}">
			<tr>
				<td class="total"><spring:theme
						code="basket.page.totals.netTax" /></td>
				<td class="total"><format:price
						priceData="${cartData.totalTax}" /></td>
			</tr>
		</c:if>

		<c:if test="${cartData.productDiscounts.value > 0}">
			<tr class="savings">
				<td><spring:theme code="basket.page.totals.product.savings" /></td>
				<td><ycommerce:testId code="Order_Totals_Savings">
						<spring:theme code="wallet.subtract"></spring:theme>
						<format:price priceData="${cartData.productDiscounts}" />
					</ycommerce:testId></td>
			</tr>
		</c:if>

		<c:if test="${cartData.orderDiscounts.value > 0}">
			<tr class="savings">
				<td><spring:theme code="basket.page.totals.order.savings" /></td>
				<td><ycommerce:testId code="Order_Totals_Savings">
						<spring:theme code="wallet.subtract"></spring:theme>
						<format:price priceData="${cartData.orderDiscounts}" />
					</ycommerce:testId></td>
			</tr>
		</c:if>

		<cart:taxExtimate cartData="${cartData}"
			showTaxEstimate="${showTaxEstimate}" />
	</tbody>
</table>
                </div>
            </div>
            <c:if test="${cartData.isCODChargesApplicable}">
            	<button type="submit" data-theme="b" formmethod="post" formnovalidate="formnovalidate" class="placeOrderForOtp"
                	formaction="<c:url value="/checkout/multi/payment-method/add/wallet?cod=true&pg=false"/>" id="placeOrderBtn" 
                    disabled="disabled">Place Order</button>
            </c:if>
            
            <c:choose>
                <c:when test="${v2PaymentInfoForm.isUsingWallet && (not (cartData.totalPayableBalance.value > 0)) }">
                    <button type="submit" class="btn btn-red" data-theme="b" formmethod="post" formnovalidate="formnovalidate"
                    	formaction="<c:url value="/checkout/multi/payment-method/add/wallet?cod=false&pg=false"/>" id="payNowBtn">
                        Pay Now</button>
                </c:when>
                
                <c:when test="${v2PaymentInfoForm.isUsingWallet && (cartData.totalPayableBalance.value > 0)}">
                    <button type="submit" class="btn btn-red" data-theme="b" formmethod="post" formnovalidate="formnovalidate"
                           id="payNowBtn" disabled="disabled" formaction="<c:url value="/checkout/multi/payment-method/add/wallet?cod=false&pg=true"/>">Pay Now</button>
                </c:when>
                
                <c:otherwise>
                    <button type="submit" class="btn btn-red" data-theme="b" formaction="<c:url value="/checkout/multi/payment-method/send"/>"
                    id="payNowBtn" disabled="disabled">Pay Now</button>                     
                </c:otherwise>
            
            
            </c:choose>
            </form:form>
        
    </div>
    
    
    <common:shopMoreBanner shopMoreAmount="${cartData.shopMoreForFreeDelivery}"/>
 
</template:page>
