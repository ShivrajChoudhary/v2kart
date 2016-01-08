<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/mobile/order" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav" %>

<%-- <div data-theme="d">
	<h3 class="summaryHeadline">
		<spring:theme code="text.account.order.summary" text="A summary of your order is below:"/>
	</h3>
</div> --%>
<div class = "innerContent">
<order:deliveryAddressItem order="${orderData}"/>
<order:deliveryMethodItem order="${orderData}"/>
<c:if test="${not empty orderData.paymentMode}">
<order:billingAddressItem order="${orderData}"/>
<order:paymentDetailsItem order="${orderData}"/>
</c:if>
</div>