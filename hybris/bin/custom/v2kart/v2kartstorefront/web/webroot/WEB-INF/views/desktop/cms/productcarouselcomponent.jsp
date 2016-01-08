<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="component" tagdir="/WEB-INF/tags/shared/component"%>

<c:choose>
    <c:when test="${not empty productData}">
        <div class="col-md-12">
            <div class="heading">${title}</div>

            <c:choose>
                <c:when test="${component.popup}">
                    <ul class="carousel jcarousel-skin popup">
                        <c:forEach items="${productData}" var="product">

                            <c:url value="${product.url}/quickView" var="productQuickViewUrl" />
                            <li><a href="${productQuickViewUrl}" class="popup scrollerProduct">
                                    <div class="thumb">
                                        <product:productPrimaryImage product="${product}" format="product" />
                                    </div>

                                    <div class="priceContainer">
                                        <format:fromPrice priceData="${product.price}" />
                                    </div>
                                    <div class="details">${product.name}</div>
                            </a></li>
                        </c:forEach>
                    </ul>
                </c:when>
                <c:otherwise>
                    <div class="product-slider">
                        <!-- <ul class="carousel jcarousel-skin"> -->
                        <div id="${title}">
                            <c:forEach items="${productData}" var="product">
                                <div class="slide">
                                    <c:url value="${product.url}" var="productUrl" />
                                    <!-- <li> -->
                                    <a href="${productUrl}" class=""> <!-- <div class="details"> --> 
                            		  <product:productPrimaryImage product="${product}" format="newProductListing" />
                      	        <div class="product-desc">
                                        <p class="prod-title">${product.name}</p>
                                        <!-- </div> -->
                                        <p class="prod-price">
                                            <c:choose>
                                                <c:when test="${product.percentageDiscount ne null}">
                                                    <span class="old-price"><format:fromPrice priceData="${product.price}" /></span>
                                                    <span class="mrp"><format:fromPrice priceData="${product.discountedPrice}" /></span>
                                                    <c:if test="${product.percentageDiscount ne null}">
                                                    <span class="badge badge-red discount">${product.percentageDiscount}%</span>
                                                    </c:if>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="mrp"><format:fromPrice priceData="${product.price}" /></span>
                                                </c:otherwise>
                                            </c:choose>
                                        </p>                                        
                                        <!-- <div class="thumb"> -->
                                        <!-- </div> -->
                                        <!-- <div class="priceContainer"></div> -->
                                    </div></a>
                                    <!-- </li> -->
                                </div>
                            </c:forEach>
                        </div>
                        <!-- </ul> -->
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </c:when>

    <c:otherwise>
        <component:emptyComponent />
    </c:otherwise>
</c:choose>
