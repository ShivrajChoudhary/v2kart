<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/desktop/order" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<div id="order-pg">
    <div class="order-detailscntr" id="defaultAddress">
        <ul class="order-info">
    	   <li><order:deliveryAddressItem order="${orderData}"/></li>
    	   <li class="nextAddress"><order:deliveryMethodItem order="${orderData}"/></li>
    	<!-- <div class="orderBox billing"> -->
        <c:if test="${not empty orderData.paymentMode}">
    		<li><order:billingAddressItem order="${orderData}"/></li>
    	<!-- </div> -->
    	
    		
    		<li class="nextAddress"><order:paymentDetailsItem order="${orderData}"/></li>
    		
    	</c:if>
        </ul>
    </div>
</div>
