<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%-- <%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData"%> --%>
<%@ attribute name="cartPage" required="true" type="java.lang.Boolean"%>
<%@ attribute name="entryNumber" required="false" type="java.lang.Long"%>
<%@ attribute name="deliveryPointOfService" required="false" type="java.lang.String"%>
<%@ attribute name="quantity" required="false" type="java.lang.Integer"%>
<%@ attribute name="searchResultsPage" required="false" type="java.lang.Boolean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%-- <%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%> --%>
<%@ taglib prefix="store" tagdir="/WEB-INF/tags/desktop/store"%>
<%@ taglib prefix="input" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/desktop/storepickup"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ attribute name="entry" required="false" type="de.hybris.platform.commercefacades.order.data.OrderEntryData"%>
<%@ taglib prefix="entry" tagdir="/WEB-INF/tags/desktop/entry"%>

<c:set var="defaultUrl" value="/store-pickup/pointOfServices" />
<c:url var="pickUpInStoreFormAction" value="${empty actionUrl ? defaultUrl : actionUrl}" />

<c:choose>
    <c:when test="${product.discountedPrice.value > 0}">
        <c:set var="price" value="${product.discountedPrice.formattedValue}"></c:set>
    </c:when>
    <c:otherwise>
        <c:set var="price" value="${product.price.formattedValue}"></c:set>
    </c:otherwise>
</c:choose>


<button class="pickupInStoreButton btn btn-red btn-xs" data-actionurl="${pickUpInStoreFormAction}" id="product">
    <c:choose>
    <c:when test="${not empty deliveryPointOfService}">
        <spring:theme code="basket.page.shipping.change.store" />
    </c:when>
    <c:otherwise>
        <spring:theme code="basket.page.shipping.find.store" text="Find A Store" />
    </c:otherwise>
</c:choose>
</button>
