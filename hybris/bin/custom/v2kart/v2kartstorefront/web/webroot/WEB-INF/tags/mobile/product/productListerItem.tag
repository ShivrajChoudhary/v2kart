<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>

<spring:theme code="text.addToCart" var="addToCartText" />
<c:url value="${product.url}" var="productUrl" />
<li class="mlist-listItem" data-theme="e"><a href="${productUrl}">
        <div class="ui-grid-a">
            <div class="ui-block-a plp-prod-img">
                <c:if test="${not empty product.potentialPromotions}">
                    <span class="offer-badge"></span>
                </c:if>
                    <product:productPrimaryImage product="${product}" format="thumbnail" zoomable="false" />
            </div>
            <div class="ui-block-b">
                <h1>${product.name}</h1>
                 <p class="prod-price">
                        <c:if test="${product.discountedPrice.value eq product.price.value}">
                            <span class="price"><format:price priceData="${product.price}" /></span>
                        </c:if>
                        <c:if test="${product.discountedPrice.value ne product.price.value}">
                            <span class="price"><format:price priceData="${product.discountedPrice}" /></span>
                        </c:if>
                 </p>
                 <p class="prod-price discount">
                    <c:if test="${product.discountedPrice.value ne product.price.value}">
                        <span class="old-price"><format:price priceData="${product.price}" /></span>
                         <span class="prod-discount">(${product.percentageDiscount}% OFF)</span>
                     </c:if>
                 </p>
                
               
                <%-- <span class="mlist-price"><format:price priceData="${product.price}" /></span> --%>
                <%-- <c:choose>
                    <c:when test="${product.stock.stockLevelStatus.code eq 'outOfStock' }">
                        <span class='listProductOutOfStock mlist-stock'><spring:theme code="product.variants.out.of.stock" /></span>
                    </c:when>
                    <c:when test="${product.stock.stockLevelStatus.code eq 'lowStock' }">
                        <span class='listProductLowStock mlist-stock'><spring:theme code="product.variants.only.left"
                                arguments="${product.stock.stockLevel}" /></span>
                    </c:when>
                </c:choose> --%>
                <%-- <span class="mlist-rating"><product:productAverageRatingListerItem product="${product}" /></span> --%>
            </div>
        </div>
</a></li>
