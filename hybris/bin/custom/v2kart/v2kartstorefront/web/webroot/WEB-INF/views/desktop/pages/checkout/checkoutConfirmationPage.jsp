<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/desktop/order" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<template:page pageTitle="${pageTitle}">
	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	
	

		<%-- <cms:pageSlot position="TopContent" var="feature" element="div" class="span-24 top-content-slot cms_disp-img_slot">
			<cms:component component="${feature}"/>
		</cms:pageSlot> --%>


	<div class="row" id="orderConfirmationPage">
		<%-- <div>
			<a href="${request.contextPath}" class="button positive right"><spring:theme code="checkout.orderConfirmation.continueShopping" /></a>
		</div> --%>
        <div class="col-xs-12 col-sm-12 col-md-12">
    		<div class="thank-pg-cont">
    			<ycommerce:testId code="orderConfirmation_yourOrderResults_text">
    				<div ><spring:theme code="text.orderConfirmation.thankYouForOrder" /></div>
                    <div class="detail-pro"><spring:theme code="text.orderConfirmation.orderPlaced" /></div>
                    <div class="detail-pro"><spring:theme code="text.account.order.orderNumber" text="Order number : {0}" arguments="${orderData.code}"/></div>
    				
    			</ycommerce:testId>
    			<%-- <div><spring:theme code="text.account.order.orderNumber" text="Order number : {0}" arguments="${orderData.code}"/></div>
    			<div><spring:theme code="text.account.order.orderPlaced" text="Placed on {0}" arguments="${orderData.created}"/></div> --%>
    			<%-- <c:if test="${not empty orderData.statusDisplay}">
    				<spring:theme code="text.account.order.status.display.${orderData.statusDisplay}" var="orderStatus"/>
    				<div><spring:theme code="text.account.order.orderStatus" text="The order is {0}" arguments="${orderStatus}"/></div>
    			</c:if> --%>
             </div>
             
             <div class="th-pg-content">
                 <spring:theme code="text.orderConfirmation.copySentTo" /><br>
                 <p class="printInvoice"> <spring:theme code="text.orderConfirmation.viewPrint" />&nbsp;&nbsp;<button type="button" class="btn btn-red" onClick="window.print()"><span class="glyphicon glyphicon-print"></span></button><br></p>
                 <spring:theme code="text.orderConfirmation.trackOrderHistory" /><br>
                 <spring:theme code="text.orderConfirmation.myAccoountClick" />
         
            </div>
    		
		
		

			<sec:authorize ifAnyGranted="ROLE_ANONYMOUS">
				<div class="span-24 delivery_stages-guest last">
					<user:guestRegister actionNameKey="guest.register.submit"/>
				</div>
			</sec:authorize>
		
		
    		<div id="th-pg-main">
    		     <div class="order-detailscntr" id="defaultAddress">
                    <ul class="order-info">    		          
    				    <li><order:deliveryAddressItem order="${orderData}"/></li>
    				    <li class="nextAddress"><order:deliveryMethodItem order="${orderData}"/></li>
                         <li class="nextAddress"><order:billingAddressItem order="${orderData}"/></li>
    				    <li class="nextAddress"><order:paymentDetailsItem order="${orderData}"/></li>
                       
                    </ul>
                 </div>
    	
    		</div>

	
			<c:forEach items="${orderData.deliveryOrderGroups}" var="orderGroup">
				<order:orderDetailsItem order="${orderData}" orderGroup="${orderGroup}" />
			</c:forEach>		

			<c:forEach items="${orderData.pickupOrderGroups}" var="orderGroup">
				<order:orderPickupDetailsItem order="${orderData}" orderGroup="${orderGroup}" />
			</c:forEach>	
			
			
						

			<div class="span-16">
				<order:receivedPromotions order="${orderData}"/>
			</div>
			<div class="span-8 right last">
				<order:orderTotalsItem order="${orderData}" containerCSS="positive"/>
			</div>
		
        </div>
	</div>
	
	
	<cms:pageSlot position="SideContent" var="feature" element="div" class="span-24 side-content-slot cms_disp-img_slot">
		<cms:component component="${feature}"/>
	</cms:pageSlot>
	
</template:page>
