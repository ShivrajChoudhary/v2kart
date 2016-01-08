<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.OrderData"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div data-theme="b" data-role="content">
	<div data-theme="b">
		<h4 class="subItemHeader">
			<spring:theme code="text.paymentDetails" text="Payment Method"/>
		</h4>
	</div>
	<div data-theme="b">
		<ul class="mFormList sidePadding15Px">
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
</div>
