<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.OrderData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<div class="clearfix marT20">
    <div class="orderList order-rc-pg">
        <div class="headline">
            <spring:theme code="order.cancelledItems.headline" text="Your Cancelled Items" />
        </div>
        <div class="clear table-responsive">
            <table class="orderListTable orderRCtable">
                <thead>
                    <tr>
                        <th id="header1" class="product_detail"><spring:theme code="text.product" text="Product" /></th>
                        <th id="header2"><spring:theme code="text.orderQuantity" text="Order Qty" /></th>
                        <th id="header3"><spring:theme code="text.reason" text="Reason" /></th>
                        <th id="header4"><spring:theme code="text.status" text="Status" /></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${order.cancelledOrderEntries}" var="entry">
                        <tr class="item">
                            <td headers="header1" class="details"><ycommerce:testId code="orderDetails_cancelledProductInfo_label">${entry.productInfo}</ycommerce:testId>
                            </td>
                            <td headers="header2" class="quantity"><ycommerce:testId code="orderDetails_cancelledProductQuantity_label">${entry.quantity}</ycommerce:testId>
                            </td>
                            <td headers="header3"><ycommerce:testId code="orderDetails_cancelReason_label">${entry.reason}</ycommerce:testId>
                            </td>
                            <td headers="header4"><ycommerce:testId code="orderDetails_cancellationStatus_label">
                                    <c:choose>
                                        <c:when test="${entry.status eq 'SUCCESSFULL'}">
                                            <spring:theme code="text.order.entry.cancelled" text="Cancelled" />
                                        </c:when>
                                        <c:otherwise>
                                        ${entry.status }
                                    </c:otherwise>
                                    </c:choose>
                                </ycommerce:testId></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>