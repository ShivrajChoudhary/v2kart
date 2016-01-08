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
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>

<template:page pageTitle="${pageTitle}">
	<jsp:body>
		<div id="globalMessages" data-theme="b">
			<common:globalMessages/>
		</div>

		<cms:pageSlot position="TopContent" var="feature">
			<cms:component component="${feature}" element="div" class="span-24 cms_disp-img_slot"/>
		</cms:pageSlot>
		<div class="innerContent" id="orderConfirmationPage">
			<div>
				<h1><spring:theme code="text.orderConfirmation.thankYouForOrder" text="Thank you for Shopping with us!"/></h1>
				<div class="detail-pro"><spring:theme code="text.orderConfirmation.orderPlaced" /></div>
                <div class="detail-pro"><spring:theme code="text.orderConfirmation.copySentTo" /></div>
                <div class="detail-pro"><spring:theme code="text.account.order.orderNumber" text="Order number : {0}" arguments="${orderData.code}"/></div>
				<%-- <ul class="mFormList">
					<li><spring:theme code="checkout.orderConfirmation.orderNumberShort" text="Order # {0}" arguments="${orderData.code}"/></li>
					<c:if test="${not empty orderData.statusDisplay}">
						<li><spring:theme code="checkout.orderConfirmation.orderStatus" text="Order Status: {0}" arguments="${orderData.statusDisplay}"/></li>
					</c:if>
					<li><spring:theme code="text.account.order.orderPlaced" text="Placed on {0}" arguments="${orderData.created}"/></li>
				</ul> --%>
			</div>
            
            <div data-theme="b" class="thank-pg-cont">
                <div class="detail-pro"><spring:theme code="text.orderConfirmation.thanksMsg1" /></div>
                <div class="detail-pro"><spring:theme code="text.orderConfirmation.thanksMsg2" /></div>
            </div>
            
			<sec:authorize ifAnyGranted="ROLE_ANONYMOUS">
				<div class="span-24 delivery_stages-guest last">
					<user:guestRegister actionNameKey="guest.register.submit"/>
				</div>
			</sec:authorize>
		
		
    		<div id="th-pg-main" class="innerContent">
                 <h3>
                    <spring:theme code="text.account.order.orderSuccessful" text="Your Order was successful!" />
                 </h3>
                 <div>
                    <div class="detail-pro"><spring:theme code="text.account.order.orderNumberShort" text="Order ID # {0}" arguments="${orderData.code}"/></div>
                    <div class="detail-pro"><b><spring:theme code="text.account.order.orderDate"/></b><spring:theme text="{0}" arguments="${orderData.created}"/></div>
                    <div class="detail-pro" style="text-transform: capitalize;"><b><spring:theme code="text.account.order.orderStatusShort"/></b><spring:theme text="{0}" arguments="${orderData.statusDisplay}"/></div>
                    <div class="detail-pro"><b><spring:theme code="text.account.order.orderGrandTotal"/></b><span><format:price priceData="${orderData.totalPrice}" /></span></div>
                    <br>
                 </div>
    		     <h3 class="order-summary-heading">
    		     	<spring:theme code="text.account.order.summary" text="A summary of your order is below:" />
    		     </h3>
    		     <div class="order-detailscntr" id="defaultAddress">
                    <ul class="order-info">    		          
    				    <li><order:deliveryAddressItem order="${orderData}"/></li>
    				    <li class="nextAddress"><order:deliveryMethodItem order="${orderData}"/></li>
                         <li class="nextAddress"><order:billingAddressItem order="${orderData}"/></li>
    				    <li class="nextAddress"><order:paymentDetailsItem order="${orderData}"/></li>
                       
                    </ul>
                 </div>
    	
    		</div>
    	
    		<div class="th-pg-deliveryDetail">
    		<div data-theme="b" data-role="content">
				<c:forEach items="${orderData.deliveryOrderGroups}" var="orderGroup">
				 <h3 class="orderDeatails-heading">
					<spring:theme code="basket.page.title.yourDeliveryItems" text="Your Delivery Items"/>
				</h3>
					<order:orderDetailsItem order="${orderData}" orderGroup="${orderGroup}" />
				</c:forEach>
				</div>		
    		</div>
    		<c:if test="${not empty orderData.pickupOrderGroups}">
    		<div class="th-pg-deliveryDetail">
			<div data-theme="b" data-role="content">
			<c:forEach items="${orderData.pickupOrderGroups}" var="orderGroup">
			<c:set var="pos" value="${orderGroup.entries[0].deliveryPointOfService}"/>
							<h3 class="orderDeatails-heading"><spring:theme code="basket.page.title.pickupFrom" text="Pick Up from\:" /></h4>
							<ul style="margin: 15px">
								<li>${pos.name}</li>
								<li>${pos.address.line1}</li>
								<li>${pos.address.line2}</li>
								<li>${pos.address.town}</li>
							</ul>
				<order:orderDetailsItem order="${orderData}" orderGroup="${orderGroup}" />
			</c:forEach>							
			</div>
			</div>
			</c:if>
			<c:if test="${not empty orderData.appliedOrderPromotions}">
			<div class="th-pg-orderTotal">
				<order:receivedPromotions order="${orderData}"/>
			</div>
			</c:if>
			<div class="th-pg-orderTotal">
				<order:orderTotalsItem order="${orderData}" containerCSS="positive"/>
			</div>
			<cms:pageSlot position="BottomContent" var="feature" element="div" id="bottom-disp-img" class="home-disp-img">
				<cms:component component="${feature}" element="div" class="span-24 cms_disp-img_slot"/>
			</cms:pageSlot>
		</div>
	</jsp:body>
</template:page>
