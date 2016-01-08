<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.OrderData"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>

<%@ attribute name="containerCSS" required="false" type="java.lang.String"%>





<table id="orderTotals" class="${containerCSS}">
    <thead>
        <tr>
            <th><spring:theme code="text.account.order.orderTotals" text="Order Details" /></th>
            <th></th>
        </tr>
    </thead>
    <tfoot>
        <tr>
            <td><spring:theme code="text.account.order.total" text="Total:" /></td>
            <c:choose>
                <c:when test="${order.net}">
                    <td><format:price priceData="${order.totalPriceWithTax}" /></td>
                </c:when>
                <c:otherwise>
                    <td><format:price priceData="${order.totalPrice}" /></td>
                </c:otherwise>
            </c:choose>
        </tr>
        <c:if test="${order.totalDiscounts.value > 0}">
            <tr class="savings">
                <td><spring:theme code="text.account.order.savings" text="Savings:" /></td>
                <td><format:price priceData="${order.totalDiscounts}" /></td>
            </tr>
        </c:if>
    </tfoot>
    <tbody>
        <tr>
            <td><spring:theme code="text.account.order.subtotal" text="Subtotal:" /></td>
            <td><format:price priceData="${order.subTotal}" /></td>
        </tr>



        <tr>
            <td><spring:theme code="text.account.order.delivery" text="Shipping charges:" /></td>
            <td><format:price priceData="${order.deliveryCost}" displayFreeForZero="true" /></td>
        </tr>
        
         <c:if test="${order.totalGiftWrapPrice.value ne null }">
        
        <tr class="shippingChargesInfo">
            <td><spring:theme code="" /><spring:theme code="basket.page.totals.giftwrap" /></td>
            <td>${order.totalGiftWrapPrice.formattedValue}</td>
        </tr>
        
        
        </c:if>
        <c:set var="codCharges" value="${order.codCharges}" />

        <c:if test="${codCharges ne null && codCharges.value ne null && codCharges.value ne '0.0'}">
            <tr>
                <td><spring:theme code="text.account.order.cod.charges" /></td>
                <td><format:price priceData="${codCharges}" /></td>
            </tr>
        </c:if>

        <c:if test="${order.net && order.totalTax.value > 0}">
            <tr>
                <td><spring:theme code="text.account.order.netTax" text="Tax:" /></td>
                <td><format:price priceData="${order.totalTax}" /></td>
            </tr>
        </c:if>
        

    </tbody>

</table>
