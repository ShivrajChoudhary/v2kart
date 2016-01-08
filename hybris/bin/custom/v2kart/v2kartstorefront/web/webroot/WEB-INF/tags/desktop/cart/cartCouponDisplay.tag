<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="cartData" required="false" type="de.hybris.platform.commercefacades.order.data.CartData"%>
<%@ attribute name="showTaxEstimate" required="false" type="java.lang.Boolean"%>
<%@ attribute name="showTax" required="false" type="java.lang.Boolean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart"%>

<c:url value="/cart/redeemVoucherCode" var="redeemVoucherCodeUrl" />
<c:url value="/cart/releaseVoucherCode" var="releaseVoucherCodeUrl" />

<div class="promotion-box">
    <c:choose>
        <c:when test="${not empty cartData.appliedOrderPromotions}">
            <h3>
                <spring:theme code="basket.received.promotions" />
            </h3>
            <div class="item_container">
                <ul class="cart-promotions">
                    <c:forEach items="${cartData.appliedOrderPromotions}" var="promotion">
                        <li class="cart-promotions-applied">
                            <p>${promotion.description}</p>
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </c:when>
    </c:choose>
    <c:choose>
        <c:when test="${not empty cartData.potentialOrderPromotions}">
            <h3>
                <spring:theme code="basket.potential.promotions" />
            </h3>
            <div class="item_container">
                <ul class="cart-promotions">

                    <c:forEach items="${cartData.potentialOrderPromotions}" var="promotion">
                        <li class="cart-promotions-potential" style="border-bottom: none">
                            <p>${promotion.description}</p>
                        </li>
                    </c:forEach>

                </ul>
            </div>
        </c:when>
    </c:choose>
</div>

<div class="coupon-codesection">
    <c:if test="${empty voucherCode }">
        <form:form action="${redeemVoucherCodeUrl}" method="post" class="payment-controlsection ">
        	<label for="voucherCode">Coupon Code</label>
            <input type="text" value="" class="couponinputbox" name="voucherCode" id="voucherCode" placeholder="Enter Coupon Code">
            <button class="btn btn-red coupon-btn" type="submit" value="submit" id="sucess">Apply</button>

            <c:if test="${not empty errorMessageKeys}">
                <c:forEach var="errorMessageKey" items="${errorMessageKeys}">
                    <p class="voucherMessageNegative clear">
                        <spring:theme code="${errorMessageKey}" />
                    </p>
                </c:forEach>
            </c:if>
            <c:if test="${not empty infoMessageKeys}">
                <c:forEach var="infoMessageKey" items="${infoMessageKeys}">
                    <p class="voucherMessagePositive clear">
                        <spring:theme code="${infoMessageKey}" />
                    </p>
                </c:forEach>
            </c:if>
        </form:form>
    </c:if>
    <c:if test="${not empty voucherCode }">
        <form:form action="${releaseVoucherCodeUrl}" method="post" class="payment-controlsection ">
        	<label for="voucherCode">Coupon Code</label>
            <input type="text" value="${voucherCode }" class="couponinputbox" name="voucherCode" id="voucherCode" placeholder="Coupon code"
                readonly="readonly">
            <button class="btn btn-red coupon-btn" type="submit" value="submit">Remove</button>
            <span></span>
            <c:if test="${not empty errorMessageKeys}">
                <c:forEach var="errorMessageKey" items="${errorMessageKeys}">
                    <p class="voucherMessageNegative clear">
                        <spring:theme code="${errorMessageKey}" />
                    </p>
                </c:forEach>
            </c:if>
            <c:if test="${not empty infoMessageKeys}">
                <c:forEach var="infoMessageKey" items="${infoMessageKeys}">
                    <p class="voucherMessagePositive clear">
                        <spring:theme code="${infoMessageKey}" />
                    </p>
                </c:forEach>
            </c:if>
        </form:form>
    </c:if>
</div>
