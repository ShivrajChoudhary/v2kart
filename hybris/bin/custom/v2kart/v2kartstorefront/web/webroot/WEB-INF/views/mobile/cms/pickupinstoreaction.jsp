<%-- <%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<c:if test="${empty showAddToCart ? true : showAddToCart and product.availableForPickup}">
	<c:set var="actionUrl" value="${fn:replace(url,
	                                '{productCode}', product.code)}" scope="request"/>
	<sec:authorize access="isAnonymous()">
        <c:url value="/my-account/login-add-to-wishlist?productCode=${product.code}" var="addToWishListUrl" />
        <c:set var="addToWishListProduct" value="" />
    </sec:authorize>
    <sec:authorize access="isAuthenticated()">
        <c:url value="/p/add-to-wishlist" var="addToWishListUrl" />
        <c:set var="addToWishListProduct" value="${product.code}" />
    </sec:authorize>	                                
		<div id='pickUpInStore'>
			Buy Reserve Online and Collect in Store
				<a href="pickupInStorePage" class="pdpPickUpInStoreButton" data-theme="b" data-productCode="${product.code}" data-productavailable="${product.availableForPickup}" data-rel="dialog" data-transition="pop" data-role="button" data-theme="c">
					<spring:theme code="pickup.in.store"/>
				</a>
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
</c:if>
 --%>