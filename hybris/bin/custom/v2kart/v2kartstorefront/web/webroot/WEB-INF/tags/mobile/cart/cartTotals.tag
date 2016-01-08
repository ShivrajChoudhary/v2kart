<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ attribute name="showTaxEstimate" required="false" type="java.lang.Boolean" %>
<%@ attribute name="showTax" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>

<div id='cartTotals' class="margin-LR-10">
	<div data-theme="d" data-role="content">
		<table cellpadding="0" cellspacing="0" class="order_totals">
			<tr>
				<td><span><spring:theme code="basket.page.totals.subtotal" /></span></td>
				<td class="order_totals_Sum"><span><format:price priceData="${cartData.subTotal}" /></span></td>
			</tr>
            
			<c:if test="${not empty cartData.deliveryCost}">
                <tr class="shippingChargesInfo">
                    <td><spring:theme code="basket.page.totals.delivery" />
                        <c:if test="${empty cartData.deliveryMode}">
                            <span class="clearfix">
                                <span style="font-size: 10px; font-style: italic""><spring:theme 
                                    code="basket.page.shipping.amountVariation" text="(Actual amount may vary based on your location)" /></span>
                            </span>
                        </c:if>
                    </td>
                    <td class="order_totals_Sum" style="vertical-align:top;"><format:price priceData="${cartData.deliveryCost}" displayFreeForZero="TRUE" /></td>
                </tr>
			</c:if>
            
			<c:if test="${cartData.net && cartData.totalTax.value > 0 && showTax}">
				<tr>
					<td><span><spring:theme code="basket.page.totals.netTax" /></span></td>
					<td class="order_totals_Sum"><span><format:price priceData="${cartData.totalTax}" /></span></td>
				</tr>
			</c:if>
            <c:if test="${cartData.totalGiftWrapPrice.value ne null }">
            <tr >
                <td><spring:theme code="" /><spring:theme code="basket.page.totals.giftwrap" /></td>
                <td class="giftwrapChargesInfo">${cartData.totalGiftWrapPrice.formattedValue}</td>
            </tr>
            </c:if>
			<%-- <cart:taxExtimate cartData="${cartData}" showTaxEstimate="true"/> --%>
            <c:if test="${cartData.productDiscounts.value > 0}">
                <tr>
                    <td class="savings"><span><spring:theme code="basket.page.totals.product.savings" /></span></td>
                    <td class="savings order_totals_Sum"><span class="text-uppercase"><spring:theme code="wallet.subtract"></spring:theme><format:price priceData="${cartData.productDiscounts}" /></span></td>
                </tr>
            </c:if>
            
            <c:if test="${cartData.orderDiscounts.value > 0}">
                <tr>
                    <td class="savings"><span><spring:theme code="basket.page.totals.order.savings" /></span></td>
                    <td class="savings order_totals_Sum"><span class="text-uppercase"><spring:theme code="wallet.subtract"></spring:theme><format:price priceData="${cartData.orderDiscounts}" /></span></td>
                </tr>
            </c:if>
			<tr>
				<td class="completePrice"><span><spring:theme code="mobile.basket.page.totals.total" /></span></td>
				<td class="completePrice order_totals_Sum">
					<span>
						<ycommerce:testId code="cart_totalPrice_label">
							<c:choose>
								<c:when test="${showTax}">
									<format:price priceData="${cartData.totalPriceWithTax}" />
								</c:when>
								<c:otherwise>
									<format:price priceData="${cartData.totalPrice}" />
								</c:otherwise>
							</c:choose>
						</ycommerce:testId>
					</span>
				</td>
			</tr>
			<!-- commenting total discounts and showing product discounts and order discounts seperately. -->
            <%-- <c:if test="${cartData.totalDiscounts.value > 0}">
				<tr>
					<td class="savings"><span><spring:theme code="basket.page.totals.savings" /></span></td>
					<td class="savings order_totals_Sum"><span class="text-uppercase"><spring:theme code="wallet.subtract"></spring:theme><format:price priceData="${cartData.totalDiscounts}" /></span></td>
				</tr>
			</c:if> --%>
            
		</table>
	</div>
	
</div>
