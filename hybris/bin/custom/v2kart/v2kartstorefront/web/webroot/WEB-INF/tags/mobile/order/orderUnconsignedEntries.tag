<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.OrderData" %>
<%@ attribute name="inProgress" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/mobile/order" %>


<div>
<%--     <c:forEach items="${order.unconsignedEntries}" var="entry">
        <c:url value="${entry.product.url}" var="productUrl"/>
        <div class="cartLi sidePadding15Px" data-theme="b" data-role="content">
            <div class="ui-grid-a productItemHolder">
                <div class="ui-block-a">
                    <a href="${entryProductUrl}" data-transition="slide">
                        <p><product:productPrimaryImage product="${entry.product}" format="thumbnail" zoomable="false"/></p>
                    </a>
                </div>
                <div class="ui-block-b productItemListDetailsHolder">
                    <div class="ui-grid-a">
                       <a href="${entry.product.purchasable ? entryProductUrl : ''}">${entry.product.name}</a>
                    </div>
                    <c:forEach items="${entry.product.baseOptions}" var="option">
                        <c:if test="${not empty option.selected and option.selected.url eq entry.product.url}">
                            <c:forEach items="${option.selected.variantOptionQualifiers}" var="selectedOption">
                                 <c:if test="${not empty selectedOption.value}">
	                                <div class="ui-grid-a">
	                                    ${selectedOption.name}:
	                                    &nbsp;<span class="quantityItemHolder">${selectedOption.value}</span>
	                                </div>
                                </c:if>
                            </c:forEach>
                        </c:if>
                    </c:forEach>
                   
                    <div class="ui-grid-a">
                        <spring:theme code="text.quantity" text="Quantity"/>:
                        <span class="quantityItemHolder">
                            ${entry.quantity}
                        </span>
                    </div>
                    <div class="ui-grid-a">
                        <spring:theme code="text.itemPrice" text="Item Price"/>:
                        <span class="priceItemHolder">
                            <format:price priceData="${entry.basePrice}" displayFreeForZero="true"/>
                        </span>
                    </div>
                    <div class="ui-grid-a">
                        <spring:theme code="text.total" text="Total"/>:
                        <span class="priceItemHolder">
                            <format:price priceData="${entry.totalPrice}" displayFreeForZero="true"/>
                        </span>
                    </div>
                </div>
            </div>
            <div class="ui-grid-a potential-product-promotions">
                <c:if test="${not empty order.appliedProductPromotions}">
                    <ul class="itemPromotionBox">
                        <c:forEach items="${order.appliedProductPromotions}" var="promotion">
                            <c:set var="displayed" value="false"/>
                            <c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
                                <c:if test="${not displayed && consumedEntry.orderEntryNumber == entry.entryNumber}">
                                    <c:set var="displayed" value="true"/>
                                    <li class="cart-promotions-applied indian-rupee-price"><span>${promotion.description}</span></li>
                                </c:if>
                            </c:forEach>
                        </c:forEach>
                    </ul>
                </c:if>
            </div>
        </div>
    </c:forEach> --%>
    	<c:if test="${not empty orderData.deliveryOrderGroups}">
        <div class="th-pg-deliveryDetail">
    		<div data-theme="b" data-role="content">
            <c:forEach items="${orderData.deliveryOrderGroups}" var="orderGroup">
             <h3 class="subItemHeader">
					<spring:theme code="basket.page.title.yourDeliveryItems" text="Your Delivery Items"/>
				</h3>
					<order:orderDetailsItem order="${orderData}" orderGroup="${orderGroup}" />
            </c:forEach>
			</div>		
    		</div>
    		</c:if>
    		<c:if test="${not empty orderData.pickupOrderGroups}">
    		 <div class="th-pg-deliveryDetail">
    		<div data-theme="b" data-role="content">
            <c:forEach items="${orderData.pickupOrderGroups}" var="orderGroup">
                			<c:set var="pos" value="${orderGroup.entries[0].deliveryPointOfService}"/>
							<h3 class="subItemHeader"><spring:theme code="basket.page.title.pickupFrom" text="Pick Up from\:" /></h4>
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
</div>