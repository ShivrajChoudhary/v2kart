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


<h1 class="headlineOrderItem"><spring:theme code="order.cancelledItems.headline" text="Your Cancelled Items" /></h1>
<div>
    <div>
        <h4 class="subItemHeader">
            <spring:theme code="text.account.order.title.cancelItems" text="Cancel Items"/>
        </h4>
    </div>
    <c:forEach items="${order.cancelledOrderEntries}" var="entry">
        <div class="cartLi sidePadding15Px" data-theme="b" data-role="content">
            <div class="ui-grid-a productItemHolder">
                <div class="ui-block-a productItemListDetailsHolder cancelledItem">
                    <div class="ui-grid-a">
                       ${entry.productInfo}
                    </div>
                    <div class="ui-grid-a">
                        <spring:theme code="text.orderQuantity" text="Order Qty" />:
                        <span class="quantityItemHolder">
                            ${entry.quantity}
                        </span>
                    </div>
                    <div class="ui-grid-a">
                        <spring:theme code="text.reason" text="Reason" />:
                        <span class="quantityItemHolder">
                            ${entry.reason}
                        </span>
                    </div>
                    <div class="ui-grid-a">
                        <spring:theme code="text.status" text="Status" />:
                        <span class="priceItemHolder">
                            <c:choose>
	                            <c:when test="${entry.status eq 'SUCCESSFULL'}">
	                                <spring:theme code="text.order.entry.cancelled" text="Cancelled" />
	                            </c:when>
	                            <c:otherwise>
	                            ${entry.status }
	                        </c:otherwise>
	                        </c:choose>
                        </span>
                    </div>
                </div>
            </div>
        </div>
    </c:forEach>
</div>