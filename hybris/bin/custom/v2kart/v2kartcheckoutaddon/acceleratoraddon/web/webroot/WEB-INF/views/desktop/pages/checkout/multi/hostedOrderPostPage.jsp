<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav"%>
<%@ taglib prefix="formElement"
	tagdir="/WEB-INF/tags/desktop/formElement"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="multi-checkout"
	tagdir="/WEB-INF/tags/addons/b2ccheckoutaddon/desktop/checkout/multi"%>
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/desktop/address"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="v2-multi-checkout"
	tagdir="/WEB-INF/tags/addons/v2kartcheckoutaddon/desktop/checkout/multi"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<c:url value="${currentStepUrl}" var="choosePaymentMethodUrl" />
<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">

	<div id="globalMessages">
		<common:globalMessages />
	</div>

	<common:shopMoreBanner
		shopMoreAmount="${cartData.shopMoreForFreeDelivery}" />

	<c:choose>
		<c:when test="${v2PaymentInfoForm.isUsingWallet eq false }">
			<c:url var="walletCheckBoxActionUrl"
				value="/checkout/multi/payment-method/add/wallet" />
		</c:when>
		<c:otherwise>
			<c:url var="walletCheckBoxActionUrl"
				value="/checkout/multi/payment-method/add/notUseWallet" />
		</c:otherwise>
	</c:choose>

	<form action="${walletCheckBoxActionUrl }" method="get"
		id="isUsingWalletCheckBoxForm"></form>

	<div class="col-md-12">
		<multi-checkout:checkoutProgressBar steps="${checkoutSteps}"
			progressBarId="${progressBarId}" />
		<input type="hidden" id="pickup" value="${isPickup}" />
	</div>

	<div class="col-md-7" id="paymentStepCheckout">
		<!-- <div id="checkoutContentPanel" class="clearfix"> -->
		<div class="yCmsContentSlot last check-content-sec">

			<ycommerce:testId code="paymentDetailsForm">

				<form:form commandName="v2PaymentInfoForm" action="send"
					method="POST" id="v2PaymentInfoForm">
					<c:set var="count" value="1" />
					<input type="hidden" class="hiddenCountSubmit" value="${count}">
					<div class="marB30">
						<div class="pay-title">
							<div class="headline">
								<spring:theme
									code="checkout.multi.paymentMethod.addPaymentDetails.billingAddress" />
							</div>
						</div>

						<div class="address-fields ">
							<c:choose>
								<c:when test="${isPickUpAllFromStore eq true}">
									<form:hidden path="isUsingShippingAddress"
										id="isUsingShippingAddress" value="false" />
									<input type="hidden" name="isPickUpAllFromStore" value="true">
									<div id="billingAdress" class="form_field-elements"></div>
								</c:when>
								<c:otherwise>
									<c:if test="${cartData.deliveryItemsQuantity > 0}">
										<form:checkbox path="isUsingShippingAddress"
											id="isUsingShippingAddress"
											data-firstname="${cartData.deliveryAddress.firstName}"
											data-lastname="${cartData.deliveryAddress.lastName}"
											data-line1="${cartData.deliveryAddress.line1}"
											data-line2="${cartData.deliveryAddress.line2}"
											data-town="${cartData.deliveryAddress.town}"
											data-postalcode="${cartData.deliveryAddress.postalCode}"
											data-countryisocode="${cartData.deliveryAddress.country.isocode}"
											data-regionisocode="${cartData.deliveryAddress.region.isocodeShort}"
											data-address-id="${cartData.deliveryAddress.id}"
											tabindex="11" />
										<spring:theme code="checkout.multi.sop.useMyDeliveryAddress" />
									</c:if>
								</c:otherwise>
							</c:choose>
						</div>

						<div class="addressContentPane " id="bill-address-content">
							<%-- <formElement:formSelectBox idKey="address.title" labelKey="address.title" path="titleCode"
                                selectCSSClass="inputbox width70" mandatory="true" skipBlank="false"
                                skipBlankMessageKey="address.title.pleaseSelect" items="${titles}" selectedValue="${addressForm.titleCode}" /> --%>
							<div class="clear"></div>
							<div id="billingAddressForm" class="addressContentPane clearfix ">
								<address:billAddressFormSelector tabindex="12" />

								<formElement:formSelectBox idKey="address.region"
									labelKey="address.label.state" path="regionIso"
									mandatory="true" skipBlank="false"
									skipBlankMessageKey="address.selectState" items="${regions}"
									itemValue="${useShortRegionIso ? 'isocodeShort' : 'isocode'}" />

								<formElement:formInputBox idKey="address.countryName"
									labelKey="address.countryName" path="countryName"
									inputCSS="text" readOnly="true" />
								<form:hidden path="countryIso" value="IN" />

								<template:errorSpanField path="mobileno">
									<ycommerce:testId code="LoginPage_Item_address.mobileno">
										<label class="control-label ${labelCSS}"
											for="address.mobileno"> <spring:theme
												code="address.mobileno" /> <span class="mandatory">
												<spring:theme code="login.required" var="loginrequiredText" />
												<img width="5" height="6" alt="${loginrequiredText}"
												title="${loginrequiredText}"
												src="${commonResourcePath}/images/mandatory.png">
										</span> <span class="skip"><form:errors path="mobileno" /></span>
										</label>
										<div class="controls">
											<form:input id="" path="" cssClass="text small-input"
												readOnly="true" value="+91" />
											<form:input cssClass="text checkp-input"
												id="address.mobileno" path="mobileno" />
										</div>
									</ycommerce:testId>
								</template:errorSpanField>
							</div>

						</div>
						<div class="form-additionals"></div>
					</div>

					<div class="smsAlert #phoneNumberError">
						<template:errorSpanField path="smsmobileno">
							<ycommerce:testId code="LoginPage_Item_address.smsmobileno">
								<label class="control-label ${labelCSS}"
									for="address.smsmobileno"> <span class="skip"><form:errors
											path="smsmobileno" /></span>
								</label>
							</ycommerce:testId>
						</template:errorSpanField>

						<div id="phoneNumberError" style="display: none;">
							<label class="control-label ${labelCSS}"
								for="address.smsmobileno"> <span class="skip"><spring:theme
										code="checkout.phonenumber.incorrect" /></span>
							</label>
						</div>

						<div class="clear"></div>
						<label><span class="glyphicon glyphicon-envelope alertMsg"></span>
							<spring:theme code="order.confirmation.sms.text"
								text="Send Order Confirmation SMS alert to +91" /></label>
						<form:input id="address.smsmobileno" path="smsmobileno"
							cssClass="smsInput phone-no-sms" />
					</div>
					<div class="marB30">
						<div class="pay-title">
							<div class="headline" style="clear: both;">
								<spring:theme
									code="checkout.multi.paymentMethod.addPaymentDetails.wallet.headline" />
							</div>
						</div>
						<div class="wallet-fields ">
							<fmt:parseNumber integerOnly="true" type="number" var="balance"
								value="${user.walletCreditBalance }" />
							<c:choose>
								<c:when test="${user.walletCreditBalance > 0 }">
									<form:checkbox path="isUsingWallet" id="isUsingWallet"
										tabindex="13" />

									<spring:theme
										code="checkout.multi.paymentMethod.addPaymentDetails.wallet.walletToBeUsed"
										arguments="${balance }" />
								</c:when>
								<c:otherwise>
									<form:checkbox path="isUsingWallet" id="isUsingWallet"
										tabindex="13" disabled="true" />
                                        &nbsp;
									<spring:theme
										code="checkout.multi.paymentMethod.addPaymentDetails.wallet.walletToBeUsed"
										arguments="${balance }" />
									</br>
									</br>
									<spring:theme
										code="checkout.multi.paymentMethod.addPaymentDetails.wallet.walletToBeUsed.empty"
										text="Empty Wallet" />
								</c:otherwise>
							</c:choose>
							<c:if test="${v2PaymentInfoForm.isUsingWallet}">
							<div class="walletbalance">
								<spring:theme code="wallet.balance" />
								<fmt:formatNumber type="number" maxFractionDigits="0"
									value="${balance-((cartData.totalPrice.value)-(cartData.totalPayableBalance.value))}" />
								<spring:theme code="wallet.points" />
							</div>
							</c:if>
						</div>
					</div>
					<c:choose>
						<c:when
							test="${v2PaymentInfoForm.isUsingWallet && (not (cartData.totalPayableBalance.value > 0)) }">
<!-- 							<div class="walletbalance"> -->
<%-- 								<spring:theme code="wallet.balance" /> --%>
<%-- 								<fmt:formatNumber type="number" maxFractionDigits="0" --%>
<%-- 									value="${balance-((cartData.totalPrice.value)-(cartData.totalPayableBalance.value))}" /> --%>
<%-- 								<spring:theme code="wallet.points" /> --%>
<!-- 							</div> -->
							<button type="submit" class="btn btn-red" formmethod="post"
								formnovalidate="formnovalidate"
								formaction="<c:url value="/checkout/multi/payment-method/add/wallet?cod=false&pg=false"/>">Place
								Order</button>
						</c:when>
						<c:otherwise>
							<div class="pay-title">
								<div class="headline">Select Payment Method</div>
							</div>
							<div class="tabbable tabs-left">
								<ul class="nav nav-tabs" id="left-nav-tab"
									style="height: 222px;">
									<li class="active"><a href="#tab1" data-toggle="tab"><i
											class="flaticon-creditcard5 on-pay-icon"></i>Online Payment</a></li>
									<!-- Cod Threshold check to hide the COD Menu Option  -->
									<c:if test="${isCODFacilityAvailable eq true}">
										<li class=""><a href="#tab2" data-toggle="tab"><i
												class="flaticon-rupee1" on-pay-cash-icon=""
												class="cashOnDeliveryOtp"></i>Cash On Delivery</a></li>

									</c:if>
									<li><a href="#tab3" data-toggle="tab"><i
											class="glyphicon glyphicon-credit-card cash-card-icon"
											on-pay-cash-icon></i>Cash Card</a></li>
								</ul>
								<div class="tab-content" id="all-tab-content">
									<div class="tab-pane active" id="tab1"
										style="padding-bottom: 20px;">
										<div class="pay-mode-sec">
											<form:radiobutton path="enforcedPaymentMethod"
												value="${netbanking}" checked="checked" />
											<label> Net Banking</label>
										</div>
										<div class="pay-mode-sec">
											<form:radiobutton path="enforcedPaymentMethod"
												value="${debitcard}" />
											<label> Debit Card</label>
										</div>
										<div class="pay-mode-sec">
											<form:radiobutton path="enforcedPaymentMethod"
												value="${creditcard}" />
											<label> Credit Card</label>
										</div>
										<div class="pay-mode-sec">
											<ul class="online-paymentcard" style="padding-bottom: 20px;">
												<li><a href="#"> <img
														src="${siteResourcePath}/images/visa.jpg" alt="">
												</a></li>
												<li><a href="#"> <img
														src="${siteResourcePath}/images/mastercard.jpg" alt="">
												</a></li>
												<li><a href="#"> <img
														src="${siteResourcePath}/images/maestroCC.png" alt="">
												</a></li>
												<li><a href="#"> <img
														src="${siteResourcePath}/images/americanCC.png" alt="">
												</a></li>
											</ul>
										</div>

										<input type="checkbox" id="termsAndConditionsAgree" /> <label
											for="termsAndConditionsAgree"><spring:theme
												code="text.termsAndConditions.agree" text="I agree to" /></label> <a
											href="/termsAndCondition" target="blank" class="terms"><spring:theme
												code="text.termsAndConditions" text="Terms and Conditions" /></a>

										<div class="bottom-btn-panel right" id="payNowDiv">
											<c:choose>
												<c:when test="${v2PaymentInfoForm.isUsingWallet }">
													<button id="payNowBtn" type="submit" class="btn btn-red"
														formmethod="post" formnovalidate="formnovalidate"
														formaction="<c:url value="/checkout/multi/payment-method/add/wallet?cod=false&pg=true"/>"
														disabled="disabled" data-toggle="popover"
																data-content="Please check Terms and Conditions." title="Pay Now"
																data-placement="bottom">Pay Now</button>
												</c:when>
												<c:otherwise>
													<button id="payNowBtn" type="submit" class="btn btn-red"
														disabled="disabled" data-toggle="popover"
														data-content="Please check Terms and Conditions." title="Pay Now"
														data-placement="bottom">Pay Now</button>
												</c:otherwise>
											</c:choose>
										</div>
									</div>
									<!--  tab2 start-->
									<!-- Cod Threshold check to hide the COD Pay Now Button  -->

									<c:if test="${isCODFacilityAvailable eq true}">
										<div class="tab-pane" id="tab2">
											<div class="pay-mode-sec">
												<strong><spring:theme
														code="checkout.multi.paymentMethod.cod.shipping.charges"></spring:theme></strong>
												<c:choose>
													<c:when test="${cartData.isCODChargesApplicable}">
														<br>
														<input type="checkbox" id="codCheckboxChecked">
														<c:choose>
															<c:when test="${cartData.isPickup}">
																<span class="textcod"><spring:theme
																		code="cod.charges.bopis.text" /></span>
															</c:when>
															<c:otherwise>
																<span class="textcod"><spring:theme
																		code="cod.charges.applicable.text"
																		arguments="${cartData.codCharges.formattedValue}" /></span>
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														<span class="freeCodCharges"> <spring:theme
																code="cod.charges.free.text" />
														</span>
													</c:otherwise>
												</c:choose>
												<div class="codOtpTextBox">
													<p>
														<spring:theme
															code="checkout.multi.paymentMethod.cod.otpmessage"></spring:theme>
													</p>
													<p>
														<spring:theme
															code="checkout.multi.paymentMethod.cod.otpvalid"></spring:theme>
													</p>
													<div class="smsAlert clearfix">
														<label>OTP</label> <input type="text" class="otpSmsInput"
															maxlength="6"> <a class="resentOtp"
															title="Resend OTP" href="javascript:void(0)"><spring:theme
															code="resend.opt"></spring:theme></a> <br> <span class="otpMessage "></span> <span
															class="not-valid-otp"></span>
													</div>
												</div>


												<c:choose>
													<c:when test="${v2PaymentInfoForm.isUsingWallet }">
														<c:choose>
															<c:when test="${cartData.isCODChargesApplicable}">
																<button type="submit" disabled="disabled"
																	class="btn btn-red placeOrderForOtp"
																	id="codChargesApplicableOrderButton" formmethod="post"
																	formnovalidate="formnovalidate"
																	formaction="<c:url value="/checkout/multi/payment-method/add/wallet?cod=true&pg=false"/>">Place
																	Order</button>
															</c:when>
															<c:otherwise>
																<button type="submit"
																	class="btn btn-red placeOrderForOtp" formmethod="post"
																	formnovalidate="formnovalidate"
																	formaction="<c:url value="/checkout/multi/payment-method/add/wallet?cod=true&pg=false"/>">Place
																	Order</button>
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:when test="${cartData.isCODChargesApplicable}">
														<button type="submit" disabled="disabled"
															class="btn btn-red placeOrderForOtp"
															id="codChargesApplicableOrderButton" formmethod="post"
															formnovalidate="formnovalidate"
															formaction="<c:url value="/checkout/multi/payment-method/cod"/>">Place
															Order</button>
													</c:when>
													<c:otherwise>
														<button type="submit" class="btn btn-red placeOrderForOtp"
															formmethod="post" formnovalidate="formnovalidate"
															formaction="<c:url value="/checkout/multi/payment-method/cod"/>">Places
															Order</button>
													</c:otherwise>
												</c:choose>
											</div>
										</div>
									</c:if>
									<!--  tab2 end-->


									<!-- tab3 start -->
									<div class="tab-pane" id="tab3">
										<%-- <div class="pay-mode-sec" style="padding-bottom: 30px;">
											<spring:theme code="checkout.multi.cash.card"
											text="Please select your preferred CashCard option from the list below" /> 
										</div> --%>

										<div class="pay-mode-sec">
											<form:radiobutton style="visibility:hidden;"
                                                path="enforcedPaymentMethod"
												value="${cashcard}" id="cashCard"
                                                 /> 
											<label style="visibility:hidden;">Cash Card</label>
										</div>


										<div class="bottom-btn-panel right">
											<c:choose>
												<c:when test="${v2PaymentInfoForm.isUsingWallet }">
													<button type="submit" class="btn btn-red" formmethod="post"
														formnovalidate="formnovalidate"
														formaction="<c:url value="/checkout/multi/payment-method/add/wallet?cod=false&pg=true"/>">Pay
														Now</button>
												</c:when>
												<c:otherwise>
													<button type="submit" onclick="javascript:payThroughCashCard();" class="btn btn-red">Pay
														Through Cash Card</button>
												</c:otherwise>
											</c:choose>
										</div>
									</div>
									<!-- tab3 end -->
								</div>
							</div>
						</c:otherwise>
					</c:choose>
				</form:form>

			</ycommerce:testId>

		</div>

	</div>
	<div class="col-md-5">
		<v2-multi-checkout:checkoutOrderDetails cartData="${cartData}"
			showShipDeliveryEntries="true" showPickupDeliveryEntries="false"
			showTax="false" />
	</div>

	<common:shopMoreBanner
		shopMoreAmount="${cartData.shopMoreForFreeDelivery}" />
</template:page>
