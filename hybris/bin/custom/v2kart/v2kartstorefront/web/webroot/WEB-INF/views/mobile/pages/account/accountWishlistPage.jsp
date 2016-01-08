<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/mobile/formElement"%>

<c:url value="/cart/check" var="addToCartCheckUrl"/>
<div class="form_field-elements" id="wishListPage">
    <div class="control-group">
        <div class="controls">
            <div class="ui-bar ui-bar-b">
                <h1>
                    <spring:theme code="text.account.wishlist" text="My wishlist" />
                </h1>
	            <c:set var="wishlistEmpty" value="true"></c:set>
                <c:if test="${not empty wishlists}">
                	<c:forEach items="${wishlists}" var="wishlist">
			                <c:if test="${not empty wishlist.entries }">
	                            <c:set var="wishlistEmpty" value="false"/>
			                </c:if>
	                </c:forEach>
	                <c:if test="${not wishlistEmpty}">
			        	<p><spring:theme code="manage.your.wishlist"/></p>
			        </c:if>
                </c:if>
            </div>
            <div class="innerContent">
            <c:if test="${not empty wishlists}">
                <c:forEach items="${wishlists}" var="wishlist">
                        <c:set var="buttonType">button</c:set>
                        <c:if test="${not empty wishlist.entries }">
	                       <c:forEach items="${wishlist.entries}" var="entry">
	                        <div class="cartLi sidePadding10Px" data-theme="b" data-role="content">
	                            <div class="ui-grid-a productItemHolder">
	                                <div class="ui-block-a">
	                                    <a href="<c:url value="${entry.product.url}"/>"><product:productPrimaryImage
	                                    	product="${entry.product}"  format="thumbnail" zoomable="false"/>
	                                    </a>
	                                </div>
	                                <div class="ui-block-b productItemListDetailsHolder">
	                                    <div class="ui-grid-a">
	                                        ${entry.product.name}
	                                    </div>
                                        <!-- get size of the product -->
                                        <div>
                                            <c:set value="${entry.product.baseOptions[0]}" var="baseOptionData"/>
                                            <c:if test="${baseOptionData ne null}">
                                                <c:set value="${baseOptionData.selected}" var="selectedBaseOptionData"/>
                                                <c:if test="${selectedBaseOptionData ne null and selectedBaseOptionData.variantOptionQualifiers[1] ne null}">
                                                    <spring:theme code="wishlist.size.text">${selectedBaseOptionData.variantOptionQualifiers[1].value}</spring:theme>
                                                </c:if>
                                            </c:if>
                                        </div>
	                                    <div class="ui-grid-a">
	                                        
	                                        <span class="priceItemHolder textColorRed">
	                                            ${entry.basePrice.formattedValue}
	                                        </span>
	                                    </div>
	                                    <c:choose>
                                            <c:when test="${entry.product.stock.stockLevelStatus.code eq 'outOfStock' }">
                                            	<a href="${entry.product.url}/notifymail?page=1" id="notifyMeButton" data-role="button" data-theme="b" class="wishlistNotifyme"><spring:theme code="mobile.product.notify.me" /></a>
				                            </c:when>
                                            <c:otherwise>
                                            	<c:url value="/my-account/add-cart-wishlist/${entry.product.code}" var="addToCart" />
	                                    		<%-- <a class="btn btn-red wishlistAddToBagBtn" title="Add To Bag" href="${addToCart}" data-role="button" data-theme="b"><spring:theme code="text.addToBag"/></a> --%>
	                                    		<form:form id="addToCartWishlistForm" class="addToCartWishlistForm wishlistAddToBagBtn" action="${addToCartCheckUrl}" method="post">
													<input type="hidden" maxlength="3" size="1" id="qty" name="qty" class="qty" value="0">
													<input type="hidden" name="productCodePost" value="${entry.product.code}"/>
													<c:set var="buttonType">submit</c:set>
													<spring:theme code="mobile.basket.add.to.bag" var="addToCartText"/>
													<button type="${buttonType}"
															data-rel="dialog"
															data-transition="pop"
															data-theme="b">
														<spring:theme code="mobile.basket.add.to.bag" var="addToCartText"/>
														<spring:theme code="mobile.basket.add.to.bag"/>
													</button>
												</form:form>
												<form method="get" id="addCartWishlistForm_${entry.product.code}" action="${addToCart}">
                                                    <input type="hidden" name="productCodePost" value="${entry.product.code}" id="productCodePost" />
                                                </form>
	                                    	</c:otherwise>
                                        </c:choose>
	                                    <c:url value="/my-account/remove-wishlist/${entry.product.code}" var="removeFromWishlist" />
	                                    <a class="rlink margin0 right submitRemoveProduct " title="Remove from Wishlist"
	                                        href="${removeFromWishlist}"><i class="remove-item"></i></a>
	                                    
	                                </div>
	                            </div>
	                        </div>
	                       </c:forEach>
                         </c:if>
                </c:forEach>
            </c:if>
            <c:if test="${wishlistEmpty}">
                <p>
                    <spring:theme code="text.account.wishlist.empty" text="Your Wishlist is Empty" />
                </p>
            </c:if>
        </div>
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
			<span class='popup-option-y'>
				<a class="wishlistAddToBagBtn" title="Add To Bag" href="${addToCart}"  data-theme='b' data-role="button">
					<spring:theme code="text.popup.mobile.yes"></spring:theme>
				</a>
			</span>
			<span class='popup-option-n ui-easydialog-altClose' data-theme='d' data-role="button">
				<spring:theme code="text.popup.mobile.no"></spring:theme>
			</span>
		</div>
	</div>
</div>