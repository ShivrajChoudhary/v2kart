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
<%@ taglib prefix="multi-checkout-custom" tagdir="/WEB-INF/tags/addons/v2kartcheckoutaddon/mobile/checkout/multi"%>
<c:set var="hideBreadcrumb" value="true" scope="request" />
<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">
    <jsp:body>
		<div id="globalMessages" data-theme="b">
			<common:globalMessages />
		</div>
		<multi-checkout:checkoutProgressBar steps="${checkoutSteps}" progressBarId="${progressBarId}" />
		<div data-theme="e" class="innerContent">
				<div class="checkout-delivery">
					<form:form id="selectDeliveryMethodForm" action="${request.contextPath}/checkout/multi/delivery-method/select" method="get">
                        <div data-theme="b" data-role="content" role="main" class="checkoutDeliveryOptionContainer">
                            <h4 class="subItemHeader">
                                <spring:theme code="checkout.multi.deliveryMethod.stepHeader" text="Select Delivery Mode" />
                            </h4>
                            <div class="sidePadding10Px">
                            <multi-checkout-custom:deliveryMethodSelector deliveryMethods="${deliveryMethods}"
                                selectedDeliveryMethodId="${cartData.deliveryMode.code}" />
                            </div>
                         </div>
                        <c:if test="${not empty cartData.deliveryMode.code}">
                            <button id="chooseDeliveryMethod_continue_button" data-theme="b">
                                <spring:theme code="checkout.multi.deliveryMethod.proceed.payment" text="Continue" />
                                  <!--   Proceed To Pay -->
                            </button>
                        </c:if>
                    </form:form>
                     <input type="hidden" name="currentStepUrl" value="${currentStepUrl}">
				</div>
			</div>
	</jsp:body>
</template:page>