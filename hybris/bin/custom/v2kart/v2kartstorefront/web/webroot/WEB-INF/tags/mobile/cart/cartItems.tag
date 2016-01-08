<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<c:url value="/my-account/wishlist" var="addFromWishlistUrl" />
<!-- <ul data-role="listview" data-split-theme="d" data-theme="e" data-divider-theme="e"> -->
<ul id="cartlist" data-role="listview" data-theme="e">
    <li data-role="list-divider" style="border:none;" >
        <div class="heading">
            <h2>
                <spring:theme code="mobile.basket.page.title.yourItems"/>
                <span> (<spring:theme code="basket.page.number"/> ${cartData.code})</span>
            </h2>
            <%-- <div class="cartItemsHelp">
                <a href="#" data-cartcode='${cartData.code}' id='helpLink'>
                    <spring:theme code="text.help"/>
                </a>
            </div>
            <div id='modalHelpMessage'>
                <spring:theme code="basket.page.cartHelpMessageMobile" text="Help? Call us with cart ID: ${cartData.code}" arguments="${cartData.code}"/>
            </div>--%>
        </div>
    </li>
    <sec:authorize access="isAuthenticated()">
      	<c:if test="${enableAddFromWishlistBtn eq true}">
      		<li class="addMoreFromWishlist" data-icon="false" data-theme="b">
      			<a id="addMoreFromWishlist" href="${addFromWishlistUrl }" data-role="button" data-theme="b" data-icon="heart">
	        			<spring:theme code="mobile.basket.add.more.from.wishlist"/>
	      		</a>
	      	</li>
      	</c:if>
      	<c:if test="${enableAddFromWishlistBtn eq false}">
      		<li class="addMoreFromWishlist ui-disabled" data-icon="false" data-theme="b">
      			<a id="addMoreFromWishlist" href="${addFromWishlistUrl }" data-theme="b" data-icon="heart" data-role="button" class="ui-disabled" >
	        			<spring:theme code="mobile.basket.add.more.from.wishlist"/>
	      		</a>
	      	</li>
      	</c:if>
      </sec:authorize>
              <form:form action="/cart/checkout" id="checkoutForm" method="get">      
    		<input type="hidden" name="entryNumbers" id="giftWrapEntries" />
    		<input type="hidden" name="deliveryPointOfService" id="deliveryPointOfService" value="${deliveryPointOfService}"/>
				<c:url value="/store-pickup/cart/update/delivery" var="cartEntryShippingModeAction" />
				<c:set var="canBePickedUp" value="${entry.product.availableForPickup and not empty entry.deliveryPointOfService.name}" />
				<c:set var="shipChecked" value="${not canBePickedUp ? 'checked=\"checked\"' : ''}" />
				<c:set var="pickupChecked" value="${canBePickedUp ? 'checked=\"checked\"' : ''}" />
				<c:set var="changeStoreMessageCode" value="${not empty deliveryPointOfService ? 'basket.page.shipping.change.store' : 'basket.page.shipping.find.store'}" />
					<input type="hidden" id="entryNumber" name="entryNumber" value="${entry.entryNumber}" />
					<input type="hidden" id="hiddenPickupQty" name="hiddenPickupQty" value="${entry.quantity}" class="qty" />
					<div class="ui-grid-a cartRadioSelector" style="background-color: white;">
					<div class="ui-block-a" style="width: 40%;height: 89px;">
						<input type="radio" name="shipMode" id="shipRadioButton" ${shipChecked} class="updateToShippingSelection" value="ship" <c:if test="${empty deliveryPointOfService}">checked="checked"</c:if>/>
						<label for="shipRadioButton" class="radioButton_entry"><spring:theme code="basket.page.shipping.ship"/></label>
						</div>
						<div class="ui-block-b" style="width: 60%">
						<span class="cart-pickup-container">
							<input type="radio" name="shipMode" id="pickUpRadioButton" ${pickupChecked} class="showStoreFinderLink" value="pickUp"<c:if test="${not empty deliveryPointOfService}">checked="checked"</c:if>/>
							<label for="pickUpRadioButton" class="radioButton_entry">
								<spring:theme code="basket.page.shipping.pickup"/>
							</label>
							<span class="clearfix"></span>
															 <c:choose>
   				 <c:when test="${not empty deliveryPointOfService}">
								<span class="basket-page-shipping-pickup">${cartData.deliveryAddress.companyName}</span>
								</c:when>
								</c:choose>
													<span id="changeStore" class="cart-changeStore changeStoreLink" ${hideChangeStoreLink}>
								<a href="pickupInStorePage" class="ui-link ui-mini" data-productCode="${entry.product.code}" data-entrynumber="${entry.entryNumber}" data-qty="${entry.quantity}" data-role="button" data-mini="true" data-theme="b">
									<spring:theme code="${changeStoreMessageCode}"/>
								</a>
						</span>
						</span>
						</div>
						</div>
</form:form>
    <c:forEach items="${cartData.entries}" var="entry">
    <sec:authorize access="isAnonymous()">
        <c:url value="/my-account/login-add-to-wishlist?productCode=${entry.product.code}&wishlist=true&entryNumber=${entry.entryNumber}" var="addToWishListUrl" />
        <c:set var="addToWishListProduct" value="" />
    </sec:authorize> <sec:authorize access="isAuthenticated()">
        <c:url value="/p/add-to-wishlist" var="addToWishListUrl" />
        <c:set var="addToWishListProduct" value="${entry.product.code}" />
    </sec:authorize>
    <c:if test="${empty acceleratorSecureGUID}">
        <c:set var="addToWishListProduct" value="" />
       <c:url value="/my-account/login-add-to-wishlist?productCode=${entry.product.code}&wishlist=true&entryNumber=${entry.entryNumber}" var="addToWishListUrl" />
    </c:if>
        <c:url value="${entry.product.url}" var="entryProductUrl"/>
        <li class="cartListItem margin-LR-10" width="97%" data-theme="c">
            <div class="ui-grid-a">
              <div class="ui-block-a cartItemproductImage" style="width:34%">
                  <a href="${entryProductUrl}" data-transition="slide">
                      <product:productCartImage product="${entry.product}" format="thumbnail"/>
                  </a>
              </div>
				<div class="ui-block-b" style="width:66%">
				<div class="pinfo">
					 <ycommerce:testId code="cart_product_name">
				                <h3 class="ui-li-heading cartProductTitle">
				                    <a href="${entryProductUrl}" data-transition="slide">${entry.product.name}</a>
				                </h3>
				    </ycommerce:testId>
				            
					<c:forEach items="${entry.product.baseOptions}" var="option">
						<c:if test="${not empty option.selected and option.selected.url eq entry.product.url}">
				           
							<c:forEach items="${option.selected.variantOptionQualifiers}" var="selectedOption">
								<c:if test="${not empty selectedOption.name and not empty selectedOption.value}">
									<div class="ui-block-a" >
										<c:if test="${selectedOption.value ne 'NA' && selectedOption.value ne 'A'}">
											<span class="selectedOptions">${selectedOption.name} : </span>${selectedOption.value}
										</c:if>
									</div>
								</c:if>
							</c:forEach>
						</c:if>
					</c:forEach>
					</div>
					<div class="rIcon">
				<%-- 	<div>
                    	<span class='giftwrapicon ${entry.giftWrap? "gift-select-icon" : "gift-icon"}'></span>
                    	<input type="hidden" name="entryNumber" class="giftWrap" id="${entry.entryNumber}" value='${entry.giftWrap? "true":"false"}'>
                    </div>
				 --%>	<div class="moveToWishlist" >
					<input type="hidden" value='<c:url value="/cart?wishlist=true"/>' name="cartpath" id="cartWishlist">
					<a href="javascript:void(0);" id="RemoveProductfromcart_${entry.entryNumber}" class="addToWishListLinkfromCart" 
                            data-options='{"addToWishListUrl" : "${addToWishListUrl}","addToWishListProduct" : "${addToWishListProduct}","entryNumber":"${entry.entryNumber}"}'>
                    <span class="wishlisticon"></span></a> </div>
                    
                    </div>
				</div>
        </div>
		<%-- <c:if test="${ycommerce:checkIfPickupEnabledForStore() eq true}">
			<fieldset data-role="controlgroup" class="cart-entry-shipping-mode">
				<c:url value="/store-pickup/cart/update/delivery" var="cartEntryShippingModeAction" />
				<c:set var="canBePickedUp" value="${entry.product.availableForPickup and not empty entry.deliveryPointOfService.name}" />
				<c:set var="shipChecked" value="${not canBePickedUp ? 'checked=\"checked\"' : ''}" />
				<c:set var="pickupChecked" value="${canBePickedUp ? 'checked=\"checked\"' : ''}" />
				<c:set var="changeStoreMessageCode" value="${not empty entry.deliveryPointOfService ? 'basket.page.shipping.change.store' : 'basket.page.shipping.find.store'}" />
				<form:form id="cartEntryShippingModeForm_${entry.product.code}${entry.entryNumber}" class="cartForm"  action="${cartEntryShippingModeAction}" method="POST">
					<input type="hidden" id="entryNumber" name="entryNumber" value="${entry.entryNumber}" />
					<input type="hidden" id="hiddenPickupQty" name="hiddenPickupQty" value="${entry.quantity}" class="qty" />
					<c:if test="${entry.product.stock.stockLevelStatus.code ne 'outOfStock'}">
						<input type="radio" name="shippingStatus_entry_${entry.entryNumber}" id="shipRadioButton_entry_${entry.entryNumber}" ${shipChecked} class="updateToShippingSelection" />
						<label for="shipRadioButton_entry_${entry.entryNumber}"><spring:theme code="basket.page.shipping.ship"/></label>
					</c:if>
					<c:if test="${entry.product.availableForPickup}">
						<span class="cart-pickup-container">
							<input type="radio" name="shippingStatus_entry_${entry.entryNumber}" id="pickUpRadioButton_entry_${entry.entryNumber}" ${pickupChecked} class="showStoreFinderLink" />
							<label for="pickUpRadioButton_entry_${entry.entryNumber}">
								<spring:theme code="basket.page.shipping.pickup"/>
								<span class="basket-page-shipping-pickup">${entry.deliveryPointOfService.name}</span>
							</label>
							<c:set var="hideChangeStoreLink" value="${not canBePickedUp ? 'style=display:none' : ''}" />
							<span id="changeStore_entry_${entry.entryNumber}" class="cart-changeStore" ${hideChangeStoreLink}>
								<c:url value="/store-pickup/${entry.product.code}" var="encodedUrl"/>
								<a href="#" class="ui-link" data-productCode="${entry.product.code}" data-rel="dialog" data-transition="pop" data-entrynumber="${entry.entryNumber}">
									<spring:theme code="${changeStoreMessageCode}"/>
								</a>
							</span>
						</span>
					</c:if>
				</form:form>
			</fieldset>
		</c:if> --%>

      <div class="ui-grid-a">
              <div class="ui-block-a update-quantity">
                <div class="clear"></div>
                <div class="qtyForm">
                    <c:url value="/" var="updateCartFormAction"/>
                    <form:form id="updateCartForm${entry.entryNumber}"
                               data-ajax="false"
                               action="${updateCartFormAction}cart/update"
                               method="post"
                               commandName="updateQuantityForm${entry.entryNumber}"
                               data-cart='{"cartCode" : "${cartData.code}","productPostPrice":"${entry.basePrice.value}","productName":"${entry.product.name}"}'>
                        <input type="hidden" name="entryNumber" value="${entry.entryNumber}"/>
                        <input type="hidden" name="productCode" value="${entry.product.code}"/>
                        <input type="hidden" name="initialQuantity" value="${entry.quantity}" />
                        <ycommerce:testId code="cart_product_quantity">
                            <form:select disabled="${not entry.updateable}"
                                         id="quantity${entry.entryNumber}"
                                         class="quantitySelector"
                                         entryNumber="${entry.entryNumber}"
                                         path="quantity"
                                         data-theme="c"
                                         data-ajax="false">
                                 <option value="0" class="hideQuantity"></option>
                                <formElement:formProductQuantitySelectOption stockLevel="${entry.product.stock.stockLevel}" quantity="${entry.quantity}" startSelectBoxCounter="1"/>
                            </form:select>
                        </ycommerce:testId>
	                    <c:if test="${entry.updateable}" >
							<ycommerce:testId code="cart_product_removeProduct">
								<a href="#" id="${entry.entryNumber}" class="submitRemoveProduct">&nbsp;
									<%-- <spring:theme code="text.iconCartRemove" var="iconCartRemove"/> --%>
									${iconCartRemove}
						    </a>
							</ycommerce:testId>
						</c:if>
                    </form:form>
                </div>
                <div class="clear"></div>
            </div>
            <div class="ui-block-b" style="float: right;">
      <div class="ui-grid-a basket-page-item-prices">
      	<div class="ui-block-a">
          <spring:theme code="basket.page.itemPrice"/>
         </div>
         <div class="ui-block-b">
          <span class="itemPrice"><format:price priceData="${entry.basePrice}" displayFreeForZero="true"/></span>
         </div>
         <div class="ui-block-a">
          <span class="itemTotalPrice"> <spring:theme code="basket.page.total"/> </span>
         </div>
         <div class="ui-block-b">
          <span class="itemTotalPrice"> <format:price priceData="${entry.totalPrice}" displayFreeForZero="true"/> </span>
         </div>
        </div>
      </div>
	</div>
        <div class="potential-product-promotions">
          <c:if test="${ycommerce:doesPotentialPromotionExistForOrderEntry(cartData, entry.entryNumber)}">
              <c:forEach items="${cartData.potentialProductPromotions}" var="promotion">
                  <c:set var="displayed" value="false"/>
                  <c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
                      <c:if test="${not displayed && consumedEntry.orderEntryNumber == entry.entryNumber && not empty promotion.description}">
                          <c:set var="displayed" value="true"/>
                          <ul class="cart-promotions itemPromotionBox">
                              <li class="cart-promotions-potential">
                                  <ycommerce:testId code="cart_promotion_label">
                                      <span>${promotion.description}</span>
                                  </ycommerce:testId>
                              </li>
                          </ul>
                      </c:if>
                  </c:forEach>
              </c:forEach>
          </c:if>
         
          <c:if test="${ycommerce:doesAppliedPromotionExistForOrderEntry(cartData, entry.entryNumber)}">
              <ul class="cart-promotions  itemPromotionBox">
                  <c:forEach items="${cartData.appliedProductPromotions}" var="promotion">
                      <c:set var="displayed" value="false"/>
                      <c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
                          <c:if test="${not displayed && consumedEntry.orderEntryNumber == entry.entryNumber}">
                              <c:set var="displayed" value="true"/>
                              <li class="cart-promotions-applied">
                                  <ycommerce:testId code="cart_appliedPromotion_label">
                                      <span>${promotion.description}</span>
                                  </ycommerce:testId>
                              </li>
                          </c:if>
                      </c:forEach>
                  </c:forEach>
              </ul>
          </c:if>
       </div>

      </li>
     <%--        <c:if test="${ycommerce:checkIfPickupEnabledForStore() eq true}">
			<fieldset data-role="controlgroup" class="cart-entry-shipping-mode margin-LR-10">
				<c:url value="/store-pickup/cart/update/delivery" var="cartEntryShippingModeAction" />
				<c:set var="canBePickedUp" value="${entry.product.availableForPickup and not empty entry.deliveryPointOfService.name}" />
				<c:set var="shipChecked" value="${not canBePickedUp ? 'checked=\"checked\"' : ''}" />
				<c:set var="pickupChecked" value="${canBePickedUp ? 'checked=\"checked\"' : ''}" />
				<c:set var="changeStoreMessageCode" value="${not empty entry.deliveryPointOfService ? 'basket.page.shipping.change.store' : 'basket.page.shipping.find.store'}" />
				<form:form id="cartEntryShippingModeForm_${entry.product.code}${entry.entryNumber}" class="cartForm"  action="${cartEntryShippingModeAction}" method="POST">
					<input type="hidden" id="entryNumber" name="entryNumber" value="${entry.entryNumber}" />
					<input type="hidden" id="hiddenPickupQty" name="hiddenPickupQty" value="${entry.quantity}" class="qty" />
					<c:if test="${entry.product.stock.stockLevelStatus.code ne 'outOfStock'}">
						<input type="radio" name="shippingStatus_entry_${entry.entryNumber}" id="shipRadioButton_entry_${entry.entryNumber}" ${shipChecked} class="updateToShippingSelection" />
						<label for="shipRadioButton_entry_${entry.entryNumber}" class="radioButton_entry"><spring:theme code="basket.page.shipping.ship"/></label>
					</c:if>
					<c:if test="${entry.product.availableForPickup}">
						<span class="cart-pickup-container">
							<input type="radio" name="shippingStatus_entry_${entry.entryNumber}" id="pickUpRadioButton_entry_${entry.entryNumber}" ${pickupChecked} class="showStoreFinderLink" />
							<label for="pickUpRadioButton_entry_${entry.entryNumber}" class="radioButton_entry">
								<spring:theme code="basket.page.shipping.pickup"/>
								<span class="basket-page-shipping-pickup">${entry.deliveryPointOfService.name}</span>
							</label>
							<c:set var="hideChangeStoreLink" value="${not canBePickedUp ? 'style=display:none' : ''}" />
							<span id="changeStore_entry_${entry.entryNumber}" class="cart-changeStore" ${hideChangeStoreLink}>
								<c:url value="/store-pickup/${entry.product.code}" var="encodedUrl"/>
								<a href="pdpPickupFromStore" class="ui-link" data-productCode="${entry.product.code}" data-entrynumber="${entry.entryNumber}" data-qty="${entry.quantity}">
									<spring:theme code="${changeStoreMessageCode}"/>
								</a>
							</span>
						</span>
					</c:if>
				</form:form>
			</fieldset>
		</c:if> --%>
  </c:forEach>
</ul>
