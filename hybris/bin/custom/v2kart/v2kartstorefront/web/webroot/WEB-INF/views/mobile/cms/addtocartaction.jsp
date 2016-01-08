<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product"%>
	<c:url value="/cart/add" var="addToCartUrl"/>
	<c:url value="/cart/check" var="addToCartCheckUrl"/>
	<c:url value="/showcart" var="openCartUrl" />
	    <sec:authorize access="isAnonymous()">
        <c:url value="/my-account/login-add-to-wishlist?productCode=${product.code}" var="addToWishListUrl" />
        <c:set var="addToWishListProduct" value="" />
    </sec:authorize>
    <sec:authorize access="isAuthenticated()">
        <c:url value="/p/add-to-wishlist" var="addToWishListUrl" />
        <c:set var="addToWishListProduct" value="${product.code}" />
    </sec:authorize>
    <c:if test="${empty acceleratorSecureGUID}">
        <c:set var="addToWishListProduct" value="" />
        <c:url value="/my-account/login-add-to-wishlist?productCode=${product.code}" var="addToWishListUrl" />
    </c:if>
	<form:form id="addToCartForm" class="add_to_cart_form" action="${addToCartCheckUrl}" method="post">
		 <input type="hidden" maxlength="3" size="1" id="qty" name="qty" class="qty" value="1">
		<input type="hidden" name="productCodePost" value="${product.code}"/>
		<input type="hidden" value="${isAddedToWishlist}" name="isAddedToWishlist" id="isAddedToWishlist">
		<div id='addToBasket'>
			<c:set var="buttonType">button</c:set>
			<c:choose>
				<c:when test="${product.purchasable and product.stock.stockLevelStatus.code ne 'outOfStock'}">
					<c:set var="buttonType">submit</c:set>
					<spring:theme code="mobile.basket.add.to.bag" var="addToCartText"/>
					<button type="${buttonType}"
							data-rel="dialog"
							data-transition="pop"
							data-theme="b"
							class="positive large <c:if test="${fn:contains(buttonType, 'button')}">out-of-stock</c:if>">
						<spring:theme code="mobile.basket.add.to.bag" var="addToCartText"/>
						<spring:theme code="mobile.basket.add.to.bag"/>
					</button>
					<button id="buyNowButton" type="button" data-theme="b"
                            class="positive large"><spring:theme code="mobile.basket.buy.now" /></button>      
				</c:when>
				<c:when test="${product.purchasable and product.stock.stockLevelStatus.code eq 'outOfStock'}">
				<a href="${product.url}/notifymail?page=2" id="notifyMeButton" data-role="button" data-theme="b"><spring:theme code="mobile.product.notify.me" /></a>
				<button id="wishlistButton" type="button" data-theme="b" 
                    		data-heading="Added To WishList"
                            data-content="Product has been successfully added to your wishlist" 
                            data-options='{"addToWishListUrl" : "${addToWishListUrl}","addToWishListProduct" : "${addToWishListProduct}"}'>
                    <spring:theme code="mobile.basket.add.to.wishlist"/>
                    </button> 
				</c:when>
				<c:when test="${ product.stock.stockLevelStatus.code ne 'outOfStock' or not product.purchasable}">
		            <p class="red">
		                <spring:theme code="product.select.size.first" />
		            </p>
		            <button type="button" data-theme="b" disabled="disabled"
                            class="positive large"><spring:theme code="mobile.basket.add.to.bag" /></button>
                            <button id="buyNowButton" type="button" data-theme="b" disabled="disabled"
                            class="positive large"><spring:theme code="mobile.basket.buy.now" /></button>
	            </c:when>
				
				<c:otherwise>
					
				</c:otherwise>
			</c:choose>
		</div>
	</form:form>
	<form method="POST" id="showcartform" action="${openCartUrl}" name="ShowCartForm">
    <c:if test="${product.purchasable}">
        <input type="hidden" maxlength="3" size="1" id="qty" name="qty" class="qty" value="1">
    </c:if>
    <input type="hidden" name="code" value="${product.code}" id="productCodePost" /> <input type="hidden" name="CSRFToken"
        value="${CSRFToken}">

</form>

<form:form method="POST" id="buyNowForm" action="/cart/check" name="BuyNowForm">
    <c:if test="${product.purchasable}">
        <input type="hidden" maxlength="3" size="1" name="qty" value="0">
    </c:if>
   <input type="hidden" name="productCodePost" value="${product.code}" id="productCodePost" />
</form:form>

<form method="POST" id="addToCartPopupForm" class="add_to_cart_form" action="${addToCartUrl}" name="BuyNowForm">
    <c:if test="${product.purchasable}">
        <input type="hidden" maxlength="3" size="1" id="qty" name="qty" class="qty" value="1">
    </c:if>
   <input type="hidden" name="productCodePost" value="${product.code}" id="productCodePost" />
</form>

<div id="buyNowDialog" style="display: none;">
	<div class="header">
		<spring:theme code="mobile.basket.buy.now"/>
	</div>
	<div class="content">
		<div class='popup-content'>
			<spring:theme code="mobile.basket.contains.product"/>
		</div>
		<div class='popup-option'>
			<span class='popup-option-y' data-theme='b' onclick='openCartPage()' data-role="button">
				<spring:theme code="text.popup.mobile.yes"></spring:theme>
			</span>
			<span class='popup-option-n ui-easydialog-altClose' data-theme='b' data-role="button">
				<spring:theme code="text.popup.mobile.no"></spring:theme>
			</span>
		</div>
	</div>
</div>

<div id="addToBagDialog" style="display: none;">
	<div class="header">
		<spring:theme code="mobile.basket.add.to.bag"/>
	</div>
	<div class="content">
		<div class='popup-content'>
			<spring:theme code="mobile.basket.contains.product"/>
		</div>
		<div class='popup-option'>
			<span class='popup-option-y' data-theme='b' onclick='addToCartFormSubmit()' data-role="button">
				<spring:theme code="text.popup.mobile.yes"></spring:theme>
			</span>
			<span class='popup-option-n ui-easydialog-altClose' data-theme='b' data-role="button">
				<spring:theme code="text.popup.mobile.no"></spring:theme>
			</span>
		</div>
	</div>
</div>
<a id="wishlistButton" data-theme="b" 
                    		data-heading="Added To WishList"
                            data-content="Product has been successfully added to your wishlist" 
                            data-options='{"addToWishListUrl" : "${addToWishListUrl}","addToWishListProduct" : "${addToWishListProduct}"}' class = "wishlistButtonPickup" style="margin-top: 10px;margin-bottom: 10px;display: block;">
                    <spring:theme code="mobile.basket.add.to.wishlist"/></a>
	<c:remove var="actionUrl"/>
	<div class="wishlistSocialIcons addthis_toolbox addthis_default_style">
		<div class="custom_images">
			<a class="addthis_button_facebook"><img src="${commonResourcePath}/images/facebook.png" width="25" height="25" border="0" alt="Share to Facebook" /></a>
			<a class="addthis_button_twitter"><img src="${commonResourcePath}/images/twitter.png" width="25" height="25" border="0" alt="Share to Twitter" /></a>
			<a class="addthis_button_google_plusone_share"><img src="${commonResourcePath}/images/googleplus.png" width="25" height="25" border="0" alt="Share to Google+" /></a>
			<a class="addthis_button_pinterest_share"><img src="${commonResourcePath}/images/pintrest.png" width="25" height="25" border="0" alt="Share to Pinterest" /></a>
		</div>
	</div>
	<script type="text/javascript" src="http://s7.addthis.com/js/250/addthis_widget.js#pubid=xa-4f28754e346e1aeb"></script>