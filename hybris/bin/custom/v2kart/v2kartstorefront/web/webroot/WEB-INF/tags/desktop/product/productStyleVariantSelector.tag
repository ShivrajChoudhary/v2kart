<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>

<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData"%>

<c:set var="showAddToCart" value="" scope="session" />
<div class="variant_options">
    <%-- Determine if product is one of V2kart style or size variant --%>
    <c:if test="${product.variantType eq 'V2kartStyleVariantProduct'}">
        <c:set var="variantStyles" value="${product.variantOptions}" />
    </c:if>
    <c:if test="${(not empty product.baseOptions[0].options) and (product.baseOptions[0].variantType eq 'V2kartStyleVariantProduct')}">
        <c:set var="variantStyles" value="${product.baseOptions[0].options}" />
        <c:set var="variantSizes" value="${product.variantOptions}" />
        <c:set var="currentStyleUrl" value="${product.url}" />
    </c:if>
    <c:if test="${(not empty product.baseOptions[1].options) and (product.baseOptions[0].variantType eq 'V2kartSizeVariantProduct')}">
        <c:set var="variantStyles" value="${product.baseOptions[1].options}" />
        <c:set var="variantSizes" value="${product.baseOptions[0].options}" />
        <c:set var="currentStyleUrl" value="${product.baseOptions[1].selected.url}" />
    </c:if>
    <c:url value="${currentStyleUrl}" var="currentStyledProductUrl" />
    <%-- Determine if product is other variant --%>
    <c:if test="${empty variantStyles}">
        <c:if test="${not empty product.variantOptions}">
            <c:set var="variantOptions" value="${product.variantOptions}" />
        </c:if>
        <c:if test="${not empty product.baseOptions[0].options}">
            <c:set var="variantOptions" value="${product.baseOptions[0].options}" />
        </c:if>
    </c:if>

    <c:if test="${not empty variantStyles }">
        <c:choose>
            <c:when test="${product.purchasable and product.stock.stockLevelStatus.code ne 'outOfStock' }">
                <c:set var="showAddToCart" value="${true}" scope="session" />
            </c:when>
            <c:otherwise>
                <c:set var="showAddToCart" value="${false}" scope="session" />
            </c:otherwise>
        </c:choose>
        <div class="variant_options">
            <c:if test="${not empty variantStyles}">
                    <div class="colour clearfix">
                        <div class="pdpcolortitle optitle">
                            <spring:theme code="product.variants.style" />
                        </div>
                        <ul class="clear colorlist">
                            <c:forEach items="${variantStyles}" var="variantStyle">
                                <c:forEach items="${variantStyle.variantOptionQualifiers}" var="variantOptionQualifier">
                                    <c:if test="${variantOptionQualifier.qualifier eq 'style'}">
                                        <c:set var="styleValue" value="${variantOptionQualifier.value}" />
                                        <c:set var="imageData" value="${variantOptionQualifier.image}" />
                                    </c:if>
                                </c:forEach>

                                <li <c:if test="${variantStyle.url eq currentStyleUrl}">class="selected"</c:if>><c:url
                                        value="${variantStyle.url}" var="productStyleUrl" /> <c:if test="${not empty imageData}">
                                        <%--    <p>${styleValue}</p> --%>
                                    </c:if> <a href="${productStyleUrl}" class="colorVariant" name="${variantStyle.url}"> <c:if
                                            test="${not empty imageData}">
                                            <img src="${imageData.url}" title="${styleValue}" alt="${styleValue}" />
                                        </c:if> <c:if test="${empty imageData}">
<!--                                            <span class="swatch_colour_a" title="${styleValue}"></span>-->
                                            <img src="${siteResourcePath}/images/missing-product-30x30.png" title="${styleValue}" alt="${styleValue}" />
                                        </c:if>
                                </a></li>
                            </c:forEach>
                        </ul>

                    </div>
            </c:if>
        </div>
    </c:if>


</div>