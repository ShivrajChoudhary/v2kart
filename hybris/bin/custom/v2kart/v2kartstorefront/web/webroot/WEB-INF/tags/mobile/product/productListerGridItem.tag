<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ taglib prefix="mproduct" tagdir="/WEB-INF/tags/mobile/product"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>

<spring:theme code="text.addToCart" var="addToCartText"/>
<c:url value="${product.url}" var="productUrl"/>
<ycommerce:testId code="product_wholeProduct">
    <div id="grid">
    	<a href="${productUrl}">
    	<%-- 	<mproduct:productPrimaryImage product="${product}" format="thumbnail" zoomable='false'/> --%>
    		<mproduct:productPrimaryImage product="${product}" format="mobileProductListing" zoomable='false'/>
                <div class="productTitle">${product.name}</div>
                
            <c:choose>
                <c:when test="${product.discountedPrice.value eq product.price.value}">
    		      <span id="productPrice" class="mlist-price"><format:price priceData="${product.price}"/></span>
                </c:when>
                <c:otherwise>
                    <div class="ui-grid-b productPriceDetail">
                        <p><span id="productPrice" class="faded"><format:price priceData="${product.price}"/></span></p>
                        <p> <span><format:price priceData="${product.discountedPrice}"/></span></p>
                        <p><span class="badge badge-red discount">${product.percentageDiscount}%</span></p>
                    </div>
                </c:otherwise>
            </c:choose>

        <%-- <c:choose>
            <c:when test="${product.stock.stockLevelStatus.code eq 'outOfStock' }">
                <span class='listProductOutOfStock mlist-stock'><spring:theme code="product.variants.out.of.stock"/></span>
            </c:when>
            <c:when test="${product.stock.stockLevelStatus.code eq 'lowStock' }">
                <span class='listProductLowStock mlist-stock'><spring:theme code="product.variants.only.left" arguments="${product.stock.stockLevel}"/></span>
            </c:when>
        </c:choose>  --%><%-- <span class="mlist-rating"><mproduct:productAverageRatingListerItem product="${product}"/></span> --%>
        </a>
    </div>
</ycommerce:testId>
