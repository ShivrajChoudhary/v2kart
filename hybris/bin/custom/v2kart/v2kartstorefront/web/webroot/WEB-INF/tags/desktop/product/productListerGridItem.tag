<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="product" required="true"
	type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="storepickup"
	tagdir="/WEB-INF/tags/desktop/storepickup"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="action" tagdir="/WEB-INF/tags/desktop/action"%>

<spring:theme code="text.addToCart" var="addToCartText" />
<c:url value="${product.url}" var="productUrl" />

<c:set value="${not empty product.potentialPromotions}"
	var="hasPromotion" />

<ycommerce:testId code="product_wholeProduct">
	<div
		class="productGridItem ${hasPromotion ? 'productGridItemPromotion' : ''}">

		<c:if test="${not empty product.potentialPromotions}">
			<span class="offer-badge"> <%-- <img class="promo" src="${product.potentialPromotions[0].productBanner.url}"
                    alt="${product.potentialPromotions[0].description}" title="${product.potentialPromotions[0].description}" /> --%>
			</span>
		</c:if>
		<a href="${productUrl}" title="${product.name}"
			class="productMainLink">
			<div class="thumb">
				<%--            <product:productPrimaryImage product="${product}" format="productListing" /> --%>
				<product:productPrimaryImage product="${product}"
					format="newProductListing" />
			</div>
			<div class="product-desc">
				<p class="prod-title">
					<ycommerce:testId code="product_productName">${product.name}</ycommerce:testId>
				</p>
				<p class="prod-price">
					<c:if
						test="${product.discountedPrice.value ne product.price.value}">
						<span class="old-price">${product.price.formattedValue}</span>
					</c:if>
					<c:set var="buttonType">submit</c:set>
					<ycommerce:testId code="product_productPrice">
						<c:if
							test="${product.discountedPrice.value eq product.price.value}">
							<span class="mrp">${product.price.formattedValue}</span>
						</c:if>
						<c:if
							test="${product.discountedPrice.value ne product.price.value}">
							<span class="mrp">${product.discountedPrice.formattedValue}</span>
						</c:if>
					</ycommerce:testId>
					<c:if
						test="${product.discountedPrice.value ne product.price.value}">
						<span class="badge badge-red discount">${product.percentageDiscount}%
							</span>
					</c:if>
				</p>
			</div> <%-- <c:choose>
                <c:when test="${product.stock.stockLevelStatus.code eq 'outOfStock' }">
                    <c:set var="buttonType">button</c:set>
                    <spring:theme code="text.addToCart.outOfStock" var="addToCartText" />
                    <span class='listProductLowStock listProductOutOfStock mlist-stock'>${addToCartText}</span>
                </c:when>
            </c:choose> --%>
		</a>

		<div class="action-btn">
			<c:set value="false" var="sizeStripVisibility" />
			<c:forEach var="sizeValue" items="${product.size}">
				<c:if test="${sizeValue ne 'A'}">
					<c:set value="true" var="sizeStripVisibility" />
				</c:if>
			</c:forEach>

			<c:if test="${sizeStripVisibility}">
				<div class="size-strip">
					<span class="sizeinfo">Sizes:</span>
					<c:forEach var="sizeValue" items="${product.size}">
						<c:if test="${sizeValue ne 'A'}">
							<span class="sizeinfo"> <c:out value="${sizeValue}" /></span>
						</c:if>
					</c:forEach>
				</div>
			</c:if>

			<a class="buy-btn" href="${productUrl}" title="${product.name}"><span
				class="glyphicon glyphicon-lock"></span> Buy now</a>
			<c:url value="${productUrl}/quickView" var="quickviewAction" />
			<a class="quickbtn" href="${quickviewAction}" title="Quick View"><span
				class="glyphicon glyphicon-eye-open"></span> Quick View</a>
		</div>

		<%-- <div class="cart clearfix">
            <c:if test="${not empty product.averageRating}">
                <product:productStars rating="${product.averageRating}" />
            </c:if>

            <c:set var="product" value="${product}" scope="request" />
            <c:set var="addToCartText" value="${addToCartText}" scope="request" />
            <c:set var="addToCartUrl" value="${addToCartUrl}" scope="request" />
            <div id="actions-container-for-${component.uid}" class="listAddPickupContainer clearfix">
                 <action:actions element="div" parentComponent="${component}" />
            </div>
        </div> --%>

	</div>
</ycommerce:testId>
