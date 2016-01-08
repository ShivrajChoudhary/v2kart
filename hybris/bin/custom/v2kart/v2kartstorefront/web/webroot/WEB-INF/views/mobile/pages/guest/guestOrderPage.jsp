<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/mobile/order" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/mobile/user" %>

<template:page pageTitle="${pageTitle}">
	<div data-role="content" class="myAccountPage">
		<div id="globalMessages" data-theme="b">
			<common:globalMessages/>
		</div>

		<div class="ui-bar ui-bar-b">
    <h1>
        <spring:theme code="text.account.order.title.details" text="Order details" />
    </h1>
</div>
<ul class="mFormList sidePadding15Px">
 <c:forEach items="${orderData.consignments}" var="orderConsignments">
    <c:if test="${orderConsignments.trackingID == '0000'}">
     <label>
     <span class="gestOrder">*</span>
     <span><spring:theme code="text.gestOrder"/> </span>
    </label>
    </c:if>
    </c:forEach>
    <li>
        <spring:theme code="text.account.order.orderNumber" text="Order number is {0}" arguments="${orderData.code}"/>
    </li>
	<li>
		<spring:theme code="text.account.order.orderPlaced" text="Placed on {0}" arguments="${orderData.created}"/>
	</li>
	<li>
		<spring:theme code="text.account.order.status.display.${orderData.statusDisplay}" var="orderStatus"/>
		<spring:theme code="text.account.order.orderStatus" arguments="${orderStatus}"/>
	</li>
</ul>
<div class = "innerContent">
<div data-theme="b">
	<order:receivedPromotions order="${orderData}"/>
</div>

<div data-theme="b">
	<order:orderTotalsItem order="${orderData}" hideHeading="false"/>
</div>
<c:set var="hasShippedItems" value="${orderData.deliveryItemsQuantity > 0}" />
<c:set value="${not empty orderData.paymentInfo}" var="paymentInfoOk" />
<c:set value="${not empty orderData.paymentInfo and not empty paymentInfo.billingAddress}" var="billingAddressOk" />
<div data-theme="b" data-role="content">
	<div data-theme="d">
		<h4 class="subItemHeader">
			<spring:theme code="text.deliveryAddress" text="Delivery Address"/>
		</h4>
	</div>
    <c:if test="${not hasShippedItems}">
    <div data-theme="d">
        <ul class="mFormList sidePadding15Px">
            <li><spring:theme code="checkout.pickup.no.delivery.required"/></li>
        </ul>
    </div>
    </c:if>
    <c:if test="${hasShippedItems}">
	<div data-theme="d">
		<ul class="mFormList sidePadding15Px">
		    <li>${fn:escapeXml(orderData.deliveryAddress.firstName)}&nbsp;${fn:escapeXml(orderData.deliveryAddress.lastName)}</li>
            <li>${fn:escapeXml(orderData.deliveryAddress.line1)}</li>
            <li>${fn:escapeXml(orderData.deliveryAddress.line2)}</li>
            <li>${fn:escapeXml(orderData.deliveryAddress.town)}</li>
            <li>${fn:escapeXml(orderData.deliveryAddress.postalCode)}</li>
            <li>${fn:escapeXml(orderData.deliveryAddress.region.name)}</li>
            <li>${fn:escapeXml(orderData.deliveryAddress.country.name)}</li>
            <li>${fn:escapeXml(orderData.deliveryAddress.phone)}</li>
		</ul>
	</div>
    </c:if>
</div>
<div data-theme="b" data-role="content">
	<h4 class="subItemHeader"><spring:theme code="text.deliveryMethod" text="Delivery Mode"/></h4>
	<div class="del-info">
	<ul class="mFormList sidePadding15Px">
		<li>${orderData.deliveryMode.name}</li>
		<li>${orderData.deliveryMode.description}</li>
	</ul>
	
	<c:if test="${not empty orderData.pickupOrderGroups}">
		<ul>
			<li><spring:theme code="checkout.pickup.items.to.pickup" arguments="${orderData.pickupItemsQuantity}"/></li>
			<li><spring:theme code="checkout.pickup.store.destinations" arguments="${fn:length(orderData.pickupOrderGroups)}"/></li>
		</ul>
	</c:if>
	</div>
	</div>
	<div data-theme="b" data-role="content">
	<div data-theme="d">
		<h4 class="subItemHeader">
			<spring:theme code="paymentMethod.billingAddress.header"  text="Billing Address"/>
		</h4>
	</div>
    <div data-theme="d">
		<ul class="mFormList sidePadding15Px">
		    <li>${fn:escapeXml(orderData.paymentInfo.billingAddress.firstName)}&nbsp;${fn:escapeXml(orderData.paymentInfo.billingAddress.lastName)}</li>
            <li>${fn:escapeXml(orderData.paymentInfo.billingAddress.line1)}</li>
            <li>${fn:escapeXml(orderData.paymentInfo.billingAddress.line2)}</li>
            <li>${fn:escapeXml(orderData.paymentInfo.billingAddress.town)}</li>
            <li>${fn:escapeXml(orderData.paymentInfo.billingAddress.postalCode)}</li>
             <li>${fn:escapeXml(orderData.paymentInfo.billingAddress.region.name)}</li>
            <li>${fn:escapeXml(orderData.paymentInfo.billingAddress.country.name)}</li>
             <li>${fn:escapeXml(orderData.paymentInfo.billingAddress.phone)}</li>
		</ul>
	</div>
</div>
	<div data-theme="b" data-role="content">
	<div data-theme="b">
		<h4 class="subItemHeader">
			<spring:theme code="text.paymentMethod" text="Payment Method" />
		</h4>
	</div>
	<div data-theme="b">
		<ul class="mFormList sidePadding15Px">
        <c:if test="${not empty orderData.paymentMode }">
            <li>${fn:escapeXml(orderData.paymentMode)}</li>           
        </c:if>
			<li>${fn:escapeXml(orderData.paymentInfo.cardNumber)}</li>
			<%-- <li>${order.paymentInfo.cardTypeData.name}</li> --%>
			<%-- <li>
				<spring:theme code="paymentMethod.paymentDetails.expires" arguments="${order.paymentInfo.expiryMonth},${order.paymentInfo.expiryYear}" />
			</li> --%>
            <c:if test="${not empty orderData.partialWalletMode }">
            <c:if test="${orderData.partialWalletMode ne orderData.paymentMode }">
            <li>${fn:escapeXml(orderData.partialWalletMode)}</li> 
            </c:if>
            </c:if>
		</ul>
	</div>
</div>
<div class="innerContent orderItemdetails guest">
    <h1 class="headlineOrderItem"><spring:theme code="order.orderItems" text="Order Items"/></h1>
    <c:if test="${not empty orderData.unconsignedEntries}">
        <order:orderUnconsignedEntries order="${orderData}"/>
    </c:if>
<%-- <c:set var="headingWasShown" value="false"/> --%>
	<c:forEach items="${orderData.consignments}" var="consignment">
		<c:if test="${consignment.status.code eq 'WAITING' or consignment.status.code eq 'PICKPACK' or consignment.status.code eq 'READY'}">
			<%-- <c:if test="${not headingWasShown}">
				<c:set var="headingWasShown" value="true"/>
                <h4 class="subItemHeader">
                    <spring:theme code="text.account.order.title.inProgressItems"/>
                </h4>
                
			</c:if> --%>
			<div class="productItemListHolder fulfilment-states-${consignment.status.code}">
				<order:accountOrderDetailsItem order="${orderData}" consignment="${consignment}" inProgress="true"/>
			</div>
		</c:if>
	</c:forEach>

	<c:forEach items="${orderData.consignments}" var="consignment">
		<c:if test="${consignment.status.code ne 'WAITING' and consignment.status.code ne 'PICKPACK' and consignment.status.code ne 'READY'}">
			<div class="productItemListHolder fulfilment-states-${consignment.status.code}">
				<order:accountOrderDetailsItem order="${orderData}" consignment="${consignment}"/>
			</div>
		</c:if>
	</c:forEach>
	
</div>
</div>
</template:page>
