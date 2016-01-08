<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.OrderData" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>




	<h3><spring:theme code="text.paymentDetails" text="Payment Method"/></h3>
    <div class="del-info">
    <ul>
        <c:if test="${not empty order.paymentMode }">
            <li>${fn:escapeXml(order.paymentMode)}</li>           
        </c:if>
        <li>${fn:escapeXml(order.paymentInfo.cardNumber)}</li>
        
        <c:if test="${not empty order.partialWalletMode }">
        <c:if test="${order.partialWalletMode ne order.paymentMode }">
        <li>${fn:escapeXml(order.partialWalletMode)}</li> 
        </c:if>
        </c:if>
        <%-- <li>${fn:escapeXml(order.paymentInfo.cardTypeData.name)}</li>
        <li><spring:theme code="paymentMethod.paymentDetails.expires" arguments="${fn:escapeXml(order.paymentInfo.expiryMonth)},${fn:escapeXml(order.paymentInfo.expiryYear)}"/></li> --%>
    </ul>
    </div>
