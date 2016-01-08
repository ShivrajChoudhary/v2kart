<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.OrderData" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>

<div data-theme="b" data-role="content">
	<div data-theme="d">
		<h4 class="subItemHeader">
			<spring:theme code="text.deliveryMethod" text="Delivery Mode"/>
		</h4>
	</div>
	<div data-theme="d">
		<ul class="mFormList sidePadding15Px">
			<li>${order.deliveryMode.name}</li>
			<li>${order.deliveryMode.description}</li>
		</ul>
	</div>
	<c:if test="${not empty order.pickupOrderGroups}">
	<div data-theme="d">
		<ul class="mFormList sidePadding15Px">
			<li><spring:theme code="checkout.pickup.items.to.pickup" arguments="${order.pickupItemsQuantity}"/></li>
			<li><spring:theme code="checkout.pickup.store.destinations" arguments="${fn:length(order.pickupOrderGroups)}"/></li>
		</ul>
	</div>
	</c:if>
</div>
