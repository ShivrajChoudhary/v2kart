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
<div class="ui-bar ui-bar-b">
    <h1>
        <spring:theme code="text.account.order.title.details" text="Order details" />
    </h1>
</div>
<ul class="mFormList sidePadding15Px">
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
	<c:if test="${orderData.isCancelable}">
		<li class="cancelOrder">
			<a href="/my-account/order/${orderData.code }/cancelOrder" data-theme="b" data-role="button">Cancel Order</a>
			<%-- <button id="cancelorderButton" type="button"  onclick="cancelOrder()" data-theme="b" data-content='<spring:theme code="order.cancel.text"/>'>Cancel Order</button> --%>
		</li>
    </c:if>
    <c:if test="${orderData.isReturnable}">
        <li class="returnOrder">
            <a href="/my-account/order/${orderData.code }/returnOrder" data-theme="b" data-role="button">Return Order</a>
        </li>
    </c:if>
</ul>
<div class = "innerContent">
<div data-theme="b">
	<order:receivedPromotions order="${orderData}"/>
</div>

<div data-theme="b">
	<order:orderTotalsItem order="${orderData}" hideHeading="false"/>
</div>
</div>