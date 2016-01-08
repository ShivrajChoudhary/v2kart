<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="component" tagdir="/WEB-INF/tags/shared/component"%>

<c:choose>
    <c:when test="${not empty productReferences and component.maximumNumberProducts > 0}">
        <div class="col-md-12">
            <div class="heading uppercaseText">${component.title}</div>
            <div class="product-slider pdp-slider">
                <div class="bestSeller">
                    <c:forEach items="${productReferences}" var="productReference">
                        <div class="slide">
                            <c:url value="${productReference.target.url}" var="productQuickViewUrl" />
                            <a href="${productQuickViewUrl}" class="popup scrollerProduct"> <product:productPrimaryImage
                                    product="${productReference.target}" format="product" />
                            </a>
                            <div class="product-desc">
                                <p class="prod-title">${productReference.target.name}</p>

                                <p class="prod-price">
                                    <c:choose>
                                        <c:when test="${productReference.target.percentageDiscount ne null}">
                                            <span class="old-price">${productReference.target.price.formattedValue}</span>
                                            <span  class="mrp">${productReference.target.discountedPrice.formattedValue}</span>
                                            <span class="badge badge-red discount">${productReference.target.percentageDiscount}% OFF</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span  class="mrp">${productReference.target.price.formattedValue}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </p>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </c:when>

    <c:otherwise>
        <component:emptyComponent />
    </c:otherwise>
</c:choose>
