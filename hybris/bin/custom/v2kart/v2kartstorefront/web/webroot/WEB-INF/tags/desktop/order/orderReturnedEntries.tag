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
            <spring:theme code="order.returnedItems.headline" text="Your Returned Items" />
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
                    <c:forEach items="${order.refundOrderEntries}" var="entry">
                        <tr class="item">
                            <td headers="header1" class="details"><ycommerce:testId code="orderDetails_returnProductInfo_label">${entry.productInfo}</ycommerce:testId>
                            </td>
                            <td headers="header2" class="quantity"><ycommerce:testId code="orderDetails_returnProductQuantity_label">${entry.quantity}</ycommerce:testId>
                            </td>
                            <td headers="header3"><ycommerce:testId code="orderDetails_returnReason_label">${entry.reason}</ycommerce:testId>
                            </td>
                            <td headers="header4"><ycommerce:testId code="orderDetails_returnStatus_label">
                                    ${entry.status }
                                </ycommerce:testId></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>