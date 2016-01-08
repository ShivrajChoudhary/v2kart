<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product"%>
<%@ taglib prefix="component" tagdir="/WEB-INF/tags/shared/component"%>
<c:choose>
    <c:when test="${not empty productData}">
        <div class="margin-bottom10 product-carousel" data-role="content" data-theme="b">
            <div class="heading">
                <h2>${title}</h2>
            </div>
            <div id="${title}">
                <c:forEach items="${productData}" var="product">
                    <c:url value="${product.url}" var="productUrl" />
                    <div class="carousel-item">
                        <a href="${productUrl}" class="scrollerProduct"> <!-- <div class="details"> --> 
                    <%--  <product:productPrimaryImage product="${product}" format="product" zoomable="false" /></a> --%>
                        <product:productPrimaryImage product="${product}" format="mobileProductListing" zoomable="false" /></a>
                        <div class="product-desc">
                            <p class="prod-title">${product.name}</p>
                            <c:choose>
                                <c:when test="${product.discountedPrice eq null}">
    		                      <span id="productPrice"><format:price priceData="${product.price}"/></span>
                                </c:when>
                                <c:otherwise>
                                    <div class="ui-grid-b productPriceDetail">
                                        <p><span id="productPrice" class="faded"><format:price priceData="${product.price}"/></span></p>
                                        <c:if test="${product.discountedPrice ne null}">
                                            <p><span><format:price priceData="${product.discountedPrice}"/></span></p>
                                        </c:if>
                                        <c:if test="${product.percentageDiscount ne null}">
                                            <p><span class="badge badge-red discount">${product.percentageDiscount}%</span></p>
                                        </c:if>
                                    </div>
                                </c:otherwise>
                           </c:choose>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </c:when>

    <c:otherwise>
        <component:emptyComponent />
    </c:otherwise>
</c:choose>
