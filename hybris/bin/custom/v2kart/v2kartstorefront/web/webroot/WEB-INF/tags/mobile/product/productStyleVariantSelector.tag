<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>

<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData"%>

<c:set var="showAddToCart" value="" scope="session" />
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
