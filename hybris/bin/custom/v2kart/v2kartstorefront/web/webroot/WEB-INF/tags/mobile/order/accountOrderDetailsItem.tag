<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.OrderData" %>
<%@ attribute name="consignment" required="true" type="de.hybris.platform.commercefacades.order.data.ConsignmentData" %>
<%@ attribute name="inProgress" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/mobile/order" %>


<c:choose>
    <c:when test="${not inProgress}">
        <c:if test="${consignment.status.code eq 'READY_FOR_PICKUP'}">
            <h2>
                <spring:theme code="text.account.order.warning.storePickUpItems" text="Reminder - Please pick up your items(s) soon." />
            </h2>
        </c:if>
    </c:when>
</c:choose>


    <c:choose>
        <c:when test="${consignment.deliveryPointOfService ne null}">
            <h4 class="subItemHeader">
                <spring:theme code="text.account.order.title.storePickUpItems" />
            </h4>
        </c:when>
        <c:otherwise>
            <h4 class="subItemHeader">
                <spring:theme code="text.account.order.title.details" />
            </h4>
        </c:otherwise>
    </c:choose>
    
<c:forEach items="${consignment.entries}" var="entry">
	<c:url value="${entry.orderEntry.product.url}" var="productUrl"/>
    <div class="cartLi sidePadding15Px" data-theme="b" data-role="content">
        <div class="ui-grid-a productItemHolder">
            <div class="ui-block-a">
                <a href="${entryProductUrl}" data-transition="slide">
                    <p><product:productPrimaryImage product="${entry.orderEntry.product}" format="thumbnail" zoomable="false"/></p>
                </a>
            </div>
            <div class="ui-block-b productItemListDetailsHolder">
                <div class="ui-grid-a">
                    <a href="${entry.orderEntry.product.purchasable ? productUrl : ''}">${entry.orderEntry.product.name}</a>
                </div>
                <c:forEach items="${entry.orderEntry.product.baseOptions}" var="option">
                    <c:if test="${not empty option.selected and option.selected.url eq entry.orderEntry.product.url}">
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
                        <format:price priceData="${entry.orderEntry.basePrice}" displayFreeForZero="true"/>
                    </span>
                </div>
                <div class="ui-grid-a">
                    <spring:theme code="text.total" text="Total"/>:
                    <span class="priceItemHolder">
                        <format:price priceData="${entry.orderEntry.totalPrice}" displayFreeForZero="true"/>
                    </span>
                </div>
                <div class="ui-grid-a">
                    <spring:theme code="text.status" text="Status" />:
                    <span class="priceItemHolder">
                        <ycommerce:testId code="orderDetails_productItemPrice_label">
                           ${consignment.statusDisplay}
                        </ycommerce:testId>
                    </span>
                </div>
                <div class="ui-grid-a">
                    <spring:theme code="text.location" text="Location" />:
                    <span class="priceItemHolder">
                        <ycommerce:testId code="orderDetails_productItemPrice_label">
                           ${consignment.currentLocation }
                        </ycommerce:testId>
                    </span>
                </div>
                <div class="ui-grid-a">
                    <spring:theme code="text.trackingID" text="Tracking ID" />:
                    <span class="priceItemHolder trackingID">
                        <ycommerce:testId code="orderDetails_productItemPrice_label">
                            ${consignment.trackingID}
                        </ycommerce:testId>
                    </span>
                </div>
                <div class="ui-grid-a">
                    <spring:theme code="text.lspProvider" text="LSP Provider" />:
                    <span class="priceItemHolder">
                        <ycommerce:testId code="orderDetails_productItemPrice_label">
                           ${consignment.lspProvider}
                        </ycommerce:testId>
                    </span>
                </div>
            </div>
        </div>
        <div class="potential-product-promotions">
            <c:if test="${not empty order.appliedProductPromotions}">
                <ul class="itemPromotionBox">
                    <c:forEach items="${order.appliedProductPromotions}" var="promotion">
                        <c:set var="displayed" value="false"/>
                        <c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
                            <c:if test="${not displayed && consumedEntry.orderEntryNumber == entry.orderEntry.entryNumber}">
                                <c:set var="displayed" value="true"/>
                                <li class="cart-promotions-applied"><span>${promotion.description}</span></li>
                            </c:if>
                        </c:forEach>
                    </c:forEach>
                </ul>
            </c:if>
        </div>
    </div>
</c:forEach>
