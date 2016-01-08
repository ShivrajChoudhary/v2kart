<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<sec:authorize access="isFullyAuthenticated()">
	<cart:cartExpressCheckoutEnabled/>
</sec:authorize>

<div class="ui-grid-a checkoutDisplayPage margin-LR-10">
	<div class="ui-block-a">
		<a href="${continueShoppingUrl}" data-theme="g" data-role="button" >
			<spring:theme text="Continue Shopping" code="cart.page.shop" />
		</a>
	</div>
	<div class="ui-block-b">
		<a href="javascript:void(0);" id="checkoutButton" data-role="button" data-theme="b" class="continueCheckout">
			<spring:theme code="checkout.checkout" />
		</a>
	</div>
	<div class="clear"></div>
</div>
