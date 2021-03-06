<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/mobile/order" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav" %>

<div class="innerContent orderItemdetails">
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
	
	<c:if test="${not empty orderData.cancelledOrderEntries }">
        <order:orderCancelledEntries order="${orderData }"/>
    </c:if>
    <c:if test="${not empty orderData.refundOrderEntries }">
        <order:orderReturnedEntries order="${orderData }"/>
    </c:if>
</div>

