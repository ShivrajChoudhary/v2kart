<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>
<%@ attribute name="showShipDeliveryEntries" required="false" type="java.lang.Boolean" %>
<%@ attribute name="showPickupDeliveryEntries" required="false" type="java.lang.Boolean" %>
<%@ attribute name="showTax" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="checkout" tagdir="/WEB-INF/tags/addons/b2ccheckoutaddon/desktop/checkout" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/addons/b2ccheckoutaddon/desktop/checkout/multi" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="v2-multi-checkout" tagdir="/WEB-INF/tags/addons/v2kartcheckoutaddon/desktop/checkout/multi" %>



<c:if test="${not empty cartData}">
	<div id="checkoutOrderDetails" class="span-9 last">
			
		 <c:if test="${cartData.deliveryItemsQuantity>0}">
			<v2-multi-checkout:deliveryCartItems cartData="${cartData}"/>
            </c:if>
			<c:forEach items="${cartData.pickupOrderGroups}" var="groupData" varStatus="status">
					<multi-checkout:pickupCartItems cartData="${cartData}" groupData="${groupData}" index="${status.index}" showHead="true" />
			</c:forEach>	
            <cart:cartTotals cartData="${cartData}" showTaxEstimate="false" showTax="${showTax}"/>
            <cart:cartPromotions cartData="${cartData}"/>
	</div>
</c:if>
