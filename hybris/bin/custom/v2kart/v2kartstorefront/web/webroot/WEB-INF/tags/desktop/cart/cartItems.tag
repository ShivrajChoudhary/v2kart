<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/desktop/storepickup"%>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart"%>
<script>
// $(document).ready(function() {
// 		alert("${deliveryPointOfService}"+" hello");
		
// 			if($(" input[name='shipModePickUp']:checked"))
// 				{
// 				$(".changeStoreLink").show();
// 				}
// 			if($(" input[name='shipMode']:checked"))
// 				{
// 			   $(".changeStoreLink").hide();
// 				}
		
// 		});
	
</script>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>


<%-- <form:form action="/cart/checkout" id="checkoutForm" method="get"> --%>
<!--     <input type="hidden" name="entryNumbers" id="giftWrapEntries" /> -->
<%--     <input type="hidden" name="deliveryPointOfService" id="deliveryPointOfService" value="${deliveryPointOfService}"/> --%>
<%-- </form:form> --%>

<div id="cartItems" class="clear">
    <c:url value="/my-account/wishlist" var="addFromWishlistUrl" />
    <div class="cartPageButtons"></div>
    <div class="headline">
        <spring:theme code="basket.page.title.yourItems" />
        <button id="checkoutButtonTop" class="checkout-btn btn btn-red submitCheckoutForm" type="submit">
            <spring:theme code="checkout.checkout" />
        </button>


        <sec:authorize access="isAnonymous()">
            <%-- <a id="addFromWishlist" class="checkout-btn btn btn-red " type="button" href="/login"> <span
                class="glyphicon glyphicon-heart"></span> </span>
            <spring:theme code="cart.add.from.wishlist" text="Add from Wishlist" />
            </a> --%>
        </sec:authorize>
        <sec:authorize access="isAuthenticated()">
            <c:if test="${enableAddFromWishlistBtn eq true}">
                <a id="addFromWishlist" class="checkout-btn btn btn-red " type="button" href="${addFromWishlistUrl }"> <span
                    class="glyphicon glyphicon-heart"></span> </span> <spring:theme code="cart.add.from.wishlist" text="Add from Wishlist" />
                </a>
            </c:if>
            <c:if test="${enableAddFromWishlistBtn eq false}">
                <a id="addFromWishlist" class="checkout-btn btn btn-red " type="button" href="${addFromWishlistUrl }" disabled="disabled">
                    <span class="glyphicon glyphicon-heart"></span> </span> <spring:theme code="cart.add.from.wishlist" text="Add from Wishlist" />
                </a>
            </c:if>
        </sec:authorize>


        <%-- <span class="cartId">
			<spring:theme code="basket.page.cartId"/>&nbsp;<span class="cartIdNr">${cartData.code}</span>
		</span> --%>
    </div>

    <div class="delievery-option">
    <form:form action="/cart/checkout" id="checkoutForm" method="get">
    <input type="hidden" name="entryNumbers" id="giftWrapEntries" />
    <input type="hidden" name="deliveryPointOfService" id="deliveryPointOfService" value="${deliveryPointOfService}"/>

        <c:url value="/store-pickup/cart/update/delivery" var="cartEntryShippingModeAction" />
<%--         <form:form id="" class="cartForm cartEntryShippingModeForm clear_fix" action="" method="post"> --%>


                <label for="shipMode" class="listing"> <input type="radio" name="shipMode" value="ship" id="shipMode"
                    class="updateToShippingSelection" <c:if test="${empty deliveryPointOfService}">checked="checked"</c:if>/> <spring:theme code="basket.page.shipping.ship" />
                </label>
                

            <label for="shipModePickUp" class="listing"> <input type="radio" name="shipMode" value="pickUp" id="shipModePickUp"
                class="basket-page-shipping-ship pickupstoreSelection"<c:if test="${not empty deliveryPointOfService}">checked="checked"</c:if>/> <spring:theme code="basket.page.shipping.pickup" />
            </label>
            
<%--             <div class="basket-page-shipping-pickup pointOfServiceName">${entry.deliveryPointOfService.name}</div> --%>

            <%--                             <c:set var="canBePickedUp" --%>
            <%--                                 value="${entry.product.availableForPickup and not empty entry.deliveryPointOfService.name}" /> --%>
            <%--                             <c:set var="hideChangeStoreLink" value="${not canBePickedUp ? 'style=display:none' : ''}" /> --%>
              </form:form>
            <div class="changeStoreLink">
        
                <storepickup:clickPickupInStore cartPage="false" deliveryPointOfService="${deliveryPointOfService }" />
                <storepickup:pickupStorePopup />
            </div>
            <c:choose>
   				 <c:when test="${not empty deliveryPointOfService}">
       			 		<div class="storename"><spring:theme code="">${cartData.deliveryAddress.companyName}</spring:theme><div>COD and shipping charges are waived off, if you select delivery of your order at our Dealer Network.</div></div>
    			</c:when>
   				 <c:otherwise>
   				<div class="storealign"><div>COD and shipping charges are waived off, if you select delivery of your order at our Dealer Network.</div></div>
    			</c:otherwise>
		</c:choose>
</div>

<%--         </form:form> --%>
    

    <table class="cart">
        <thead>
            <tr>
                <th id="header2" colspan="2"><spring:theme code="basket.page.title" /></th>

                <%-- <th id="header5"><spring:theme code="basket.page.shipping" /></th> --%>

                <th></th>
                <th id="header3"><spring:theme code="basket.page.itemPrice" /></th>
                <th id="header4"><spring:theme code="basket.page.quantity" /></th>
                <%-- <c:if test="${ycommerce:checkIfPickupEnabledForStore() eq true}">
					<th id="header5"><spring:theme code="basket.page.shipping"/></th>
				</c:if> --%>
                <th id="header6"><spring:theme code="basket.page.total" /></th>
                <th id="header7">&nbsp;</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${cartData.entries}" var="entry">
                <c:url value="${entry.product.url}" var="productUrl" />
                <tr class="cartItem">

                    <td headers="header2" class="thumb productCartImage"><a href="${productUrl}"><product:productPrimaryImage
                                product="${entry.product}" format="thumbnail" /></a></td>

                    <td headers="header2" class="details"><ycommerce:testId code="cart_product_name">
                            <div class="itemName">
                                <a href="${productUrl}" class="capitalize">${entry.product.name}</a>
                            </div>
                        </ycommerce:testId> <c:set var="entryStock" value="${entry.product.stock.stockLevelStatus.code}" /> <c:forEach
                            items="${entry.product.baseOptions}" var="option">
                            <c:if test="${not empty option.selected and option.selected.url eq entry.product.url}">
                                <c:forEach items="${option.selected.variantOptionQualifiers}" var="selectedOption">
                                    <c:if test="${not empty selectedOption.value && selectedOption.value ne 'NA' && selectedOption.value ne 'A'}">
                                        <div>
                                            <strong>${selectedOption.name}:</strong> <span>${selectedOption.value}</span>
                                        </div>
                                    </c:if>
                                    <c:set var="entryStock" value="${option.selected.stock.stockLevelStatus.code}" />
                                    <div class="clear"></div>
                                </c:forEach>
                            </c:if>
                        </c:forEach> <c:if test="${ycommerce:doesPotentialPromotionExistForOrderEntry(cartData, entry.entryNumber)}">
                            <ul class="potentialPromotions">
                                <c:forEach items="${cartData.potentialProductPromotions}" var="promotion">
                                    <c:set var="displayed" value="false" />
                                    <c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
                                        <c:if
                                            test="${not displayed && consumedEntry.orderEntryNumber == entry.entryNumber && not empty promotion.description}">
                                            <c:set var="displayed" value="true" />
                                            <li><ycommerce:testId code="cart_potentialPromotion_label">
													${promotion.description}
												</ycommerce:testId></li>
                                        </c:if>
                                    </c:forEach>
                                </c:forEach>
                            </ul>
                        </c:if> <c:if test="${ycommerce:doesAppliedPromotionExistForOrderEntry(cartData, entry.entryNumber)}">
                            <ul class="appliedPromotions">
                                <c:forEach items="${cartData.appliedProductPromotions}" var="promotion">
                                    <c:set var="displayed" value="false" />
                                    <c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
                                        <c:if test="${not displayed && consumedEntry.orderEntryNumber == entry.entryNumber}">
                                            <c:set var="displayed" value="true" />
                                            <li><ycommerce:testId code="cart_appliedPromotion_label">
													${promotion.description}
												</ycommerce:testId></li>
                                        </c:if>
                                    </c:forEach>
                                </c:forEach>
                            </ul>
                        </c:if>
                     <%--    <input type="checkbox" name="entryNumber" <c:if test="${entry.giftWrap}">checked</c:if> class="giftWrap" value="${entry.entryNumber}">
                        <label class="smallTxt">
                            <div class="giftIconSmall"></div>
                            <spring:theme code="cart.giftWrap.item" text="Gift Wrap this item"/>
                        </label>
                      --%>   </td>
                        
                       <%-- <c:if test="${ycommerce:checkIfPickupEnabledForStore() eq true}">
                         <td headers="header5" class="shipping">
                            <c:url value="/store-pickup/cart/update/delivery" var="cartEntryShippingModeAction" />
                            <form:form id="cartEntryShippingModeForm_${entry.product.code}${entry.entryNumber}" class="cartForm cartEntryShippingModeForm clear_fix"  action="${cartEntryShippingModeAction}" method="post">
                                <input type="hidden" name="entryNumber" value="${entry.entryNumber}"/>
                                <input type="hidden" name="hiddenPickupQty" value="${entry.quantity}"/>

                        </c:if> <input type="checkbox" name="entryNumber" <c:if test="${entry.giftWrap}">checked</c:if> class="giftWrap"
                        value="${entry.entryNumber}"> <label class="smallTxt">
                            <div class="giftIconSmall"></div> <spring:theme code="cart.giftWrap.item" text="Gift Wrap this item" />
                    </label></td>

                    <c:if test="1 eq 0">
                        <td headers="header5" class="shipping"><c:url value="/store-pickup/cart/update/delivery"
                                var="cartEntryShippingModeAction" /> <form:form
                                id="cartEntryShippingModeForm_${entry.product.code}${entry.entryNumber}"
                                class="cartForm cartEntryShippingModeForm clear_fix" action="${cartEntryShippingModeAction}" method="post">
                                <input type="hidden" name="entryNumber" value="${entry.entryNumber}" />
                                <input type="hidden" name="hiddenPickupQty" value="${entry.quantity}" />

                                <c:choose>
                                    <c:when test="${entry.product.purchasable}">
                                        <c:if test="${not empty entryStock and entryStock ne 'outOfStock'}">
                                            <label for="shipMode${entry.entryNumber}" class="nostyle"> <input type="radio"
                                                name="shipMode" value="ship" id="shipMode${entry.entryNumber}"
                                                class="updateToShippingSelection"
                                                <c:choose>
                                                       <c:when test="${entry.deliveryPointOfService eq null or not entry.product.availableForPickup}">checked="checked"</c:when>
                                                        <c:when test="${entry.product.purchasable}"></c:when>
                                                        <c:otherwise>disabled="disabled"</c:otherwise>
                                                    </c:choose> />
                                                <spring:theme code="basket.page.shipping.ship" />
                                            </label>
                                            <br>
                                        </c:if>
                                        <c:choose>
                                            <c:when test="${entry.product.availableForPickup}">
                                                <label for="shipModePickUp${entry.entryNumber}" class="nostyle"> <input type="radio"
                                                    name="shipMode" value="pickUp" id="shipModePickUp${entry.entryNumber}"
                                                    class="basket-page-shipping-ship pickupstoreSelection"
                                                    <c:choose>
                                                            <c:when test="${not empty entry.deliveryPointOfService.name}">checked="checked"</c:when>
                                                            <c:when test="${not empty entry.product.availableForPickup}"></c:when>
                                                            <c:otherwise>disabled="disabled"</c:otherwise>
                                                        </c:choose> />
                                                    <spring:theme code="basket.page.shipping.pickup" />
                                                </label>
                                                <div class="basket-page-shipping-pickup pointOfServiceName">${entry.deliveryPointOfService.name}</div>

                                                <c:set var="canBePickedUp"
                                                    value="${entry.product.availableForPickup and not empty entry.deliveryPointOfService.name}" />
                                                <c:set var="hideChangeStoreLink" value="${not canBePickedUp ? 'style=display:none' : ''}" />
                                                <div ${hideChangeStoreLink} class="changeStoreLink">
                                                    <%--                                                 <storepickup:clickPickupInStore product="${entry.product}" cartPage="true"  entryNumber="${entry.entryNumber}" --%>
                                                    <%--                                                         deliveryPointOfService="${entry.deliveryPointOfService.name}" quantity="${entry.quantity}" entry="${entry}"/> --%>
<!--                                                 </div> -->
<%--                                             </c:when> --%>
<%--                                             <c:otherwise> --%>
<%--                                             </c:otherwise> --%>
<%--                                         </c:choose> --%>
<%--                                     </c:when> --%>
<%--                                 </c:choose> --%>

<%--                             </form:form> --%>
<!--                          </td>  -->
<%--                     </c:if>--%> 
                      
                       

                        
                        <td>
                        
                        <!--        Add product in wishlist -->
                                        
                                <sec:authorize access="isAnonymous()">
                                    <c:url value="/my-account/login-add-to-wishlist?productCode=${entry.product.code}&wishlist=true&entryNumber=${entry.entryNumber}" var="addToWishListUrl" />
                                    <c:set var="addToWishListProduct" value="" />
                                </sec:authorize> <sec:authorize access="isAuthenticated()">
                                    <c:url value="/p/add-to-wishlist" var="addToWishListUrl" />
                                    <c:set var="addToWishListProduct" value="${entry.product.code}" />
                                </sec:authorize>
                                
                                <input type="hidden" value='<c:url value="/cart?wishlist=true"/>' name="cartpath" id="cartWishlist">
                                
                                <!-- don't show add to wishlist button for FREE product -->
                                <c:if test="${entry.basePrice.value ne '0.0'}">
                                    <a href="javascript:void(0);" id="RemoveProductfromcart_${entry.entryNumber}"  class="addToWishListLinkfromCart"
                                        title="Move to Wishlist"
                                        data-options='{"addToWishListUrl" : "${addToWishListUrl}","addToWishListProduct" : "${addToWishListProduct}","entryNumber":"${entry.entryNumber}"}'>
                                       <p class="wishlist"> <img alt="Move to Wishlist" src="${siteResourcePath }/images/wishlist.png">
                                       <spring:theme code="cart.wishlist.move" text="Move to Wishlist"/>
                                       </p></a> 
                                </c:if>

                                            
                       <!--              product added in wishlist -->
                        </td>
            




                   


                    <td headers="header3" class="itemPrice"><format:price priceData="${entry.basePrice}" displayFreeForZero="true" /></td>

                    <td headers="header4" class="quantity"><c:url value="/cart/update" var="cartUpdateFormAction" /> <form:form
                            id="updateCartForm${entry.entryNumber}" action="${cartUpdateFormAction}" method="post"
                            cssClass="updateQuantityClass" commandName="updateQuantityForm${entry.entryNumber}"
                            data-cart='{"cartCode" : "${cartData.code}","productPostPrice":"${entry.basePrice.value}","productName":"${entry.product.name}"}'>
                            <input type="hidden" name="entryNumber" value="${entry.entryNumber}" />
                            <input type="hidden" name="productCode" value="${entry.product.code}" />
                            <input type="hidden" name="initialQuantity" value="${entry.quantity}" />
                            <c:choose>
                                <c:when test="${entry.updateable}">
                                    <ycommerce:testId code="cart_product_quantity">

                                        <input title="<spring:theme code="cart.quantity.subtract" />" type="button"
                                            id="dec${entry.entryNumber}" onclick="subtract('quantity${entry.entryNumber}')" value="-"
                                            autocomplete="off" />

                                        <form:input title="Quantity of the product" readonly="true" disabled="${not entry.updateable}"
                                            type="text" size="1" maxlength="3" id="quantity${entry.entryNumber}" path="quantity" />



                                        <input title="<spring:theme code="cart.quantity.add" />" type="button" id="inc${entry.entryNumber}"
                                            class="clickUpdate incQty" onclick="add('quantity${entry.entryNumber}','${entry.entryNumber}')"
                                            value="+" autocomplete="off"
                                            <c:if test="${entry.quantity == productMaxCount}">disabled='disabled'</c:if>>


                                    </ycommerce:testId>
                                </c:when>
                                <c:otherwise>
                                    <ycommerce:testId code="cart_product_quantity">
                                        <form:input title="Cart product quantity" disabled="${not entry.updateable}" type="text" size="1"
                                            id="quantity${entry.entryNumber}" class="qty" path="quantity" />
                                    </ycommerce:testId>
                                </c:otherwise>
                            </c:choose>

                        </form:form> <%-- <c:if test="${entry.updateable}" >
							<ycommerce:testId code="cart_product_removeProduct">
								<spring:theme code="text.iconCartRemove" var="iconCartRemove"/>
								<a href="#" id="RemoveProduct_${entry.entryNumber}" class="submitRemoveProduct">${iconCartRemove}</a>
							</ycommerce:testId>
						</c:if> --%></td>



                    <%-- <c:if test="${ycommerce:checkIfPickupEnabledForStore() eq true}">
						<td headers="header5" class="shipping">
							<c:url value="/store-pickup/cart/update/delivery" var="cartEntryShippingModeAction" />
							<form:form id="cartEntryShippingModeForm_${entry.product.code}${entry.entryNumber}" class="cartForm cartEntryShippingModeForm clear_fix"  action="${cartEntryShippingModeAction}" method="post">
								<input type="hidden" name="entryNumber" value="${entry.entryNumber}"/>
								<input type="hidden" name="hiddenPickupQty" value="${entry.quantity}"/>
								<c:choose>
									<c:when test="${entry.product.purchasable}">
										<c:if test="${not empty entryStock and entryStock ne 'outOfStock'}">
											<label for="shipMode${entry.entryNumber}" class="nostyle">
												<input type="radio" name="shipMode" value="ship" id="shipMode${entry.entryNumber}" class="updateToShippingSelection"
												    <c:choose>
												       <c:when test="${entry.deliveryPointOfService eq null or not entry.product.availableForPickup}">checked="checked"</c:when>
														<c:when test="${entry.product.purchasable}"></c:when>
														<c:otherwise>disabled="disabled"</c:otherwise>
												    </c:choose>
												/>
												<spring:theme code="basket.page.shipping.ship"/>
											</label>
											<br>
										</c:if>
										<c:choose>
											<c:when test="${entry.product.availableForPickup}">
												<label for="shipModePickUp${entry.entryNumber}" class="nostyle">
													<input type="radio" name="shipMode" value="pickUp" id="shipModePickUp${entry.entryNumber}" class="basket-page-shipping-ship pickupstoreSelection"
														<c:choose>
															<c:when test="${not empty entry.deliveryPointOfService.name}">checked="checked"</c:when>
															<c:when test="${not empty entry.product.availableForPickup}"></c:when>
															<c:otherwise>disabled="disabled"</c:otherwise>
														</c:choose>
													/>
													<spring:theme code="basket.page.shipping.pickup"/>
												</label>
												<div class="basket-page-shipping-pickup pointOfServiceName" >${entry.deliveryPointOfService.name}</div>
											
											<c:set var="canBePickedUp" value="${entry.product.availableForPickup and not empty entry.deliveryPointOfService.name}" />
											<c:set var="hideChangeStoreLink" value="${not canBePickedUp ? 'style=display:none' : ''}" />
											<div ${hideChangeStoreLink} class="changeStoreLink">
												<storepickup:clickPickupInStore product="${entry.product}" cartPage="true"  entryNumber="${entry.entryNumber}"
														deliveryPointOfService="${entry.deliveryPointOfService.name}" quantity="${entry.quantity}"/>
											</div>
											</c:when>
											<c:otherwise>
											</c:otherwise>
										</c:choose>
									</c:when>
								</c:choose>
							</form:form>
						 </td>
					</c:if> --%>

                    <td headers="header6" class="total"><ycommerce:testId code="cart_totalProductPrice_label">
                            <format:price priceData="${entry.totalPrice}" displayFreeForZero="true" />
                        </ycommerce:testId></td>
                    <td><c:if test="${entry.updateable}">
                            <ycommerce:testId code="cart_product_removeProduct">
                                <spring:theme code="text.iconCartRemove" var="iconCartRemove" />
                                <a href="#" data-toggle="modal" class="submitRemoveProduct"
                                    data-target="#itemDelconfirmationPopup_${entry.entryNumber}"><i class="remove-item"></i>${iconCartRemove}</a>
                                <div class="modal fade cartPagePopup" id="itemDelconfirmationPopup_${entry.entryNumber}" role="dialog"
                                    aria-labelledby="myModalLabel" aria-hidden="true">
                                    <div class="modal-dialog">
                                        <div class="modal-content">
                                            <div class="modal-header">
                                                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                                    <span aria-hidden="true">×</span>
                                                </button>
                                                <h4 class="modal-title">Confirmation</h4>
                                            </div>
                                            <div class="modal-body">
                                                <div class="description">Are you sure you would like to delete this item?</div>
                                            </div>
                                            <div class="modal-footer">
                                                <div class="popupFooterButtons">
                                                    <button type="button" id="RemoveProduct_${entry.entryNumber}"
                                                        class="btn btn-red submitRemoveProduct" data-dismiss="modal" aria-label="Close">Yes</button>
                                                    <button type="button" class="btn btn-secondary" data-dismiss="modal" aria-label="Close">No</button>
                                                </div>
                                            </div>
                                            <!-- /.modal-content -->
                                        </div>
                                        <!-- /.modal-dialog -->
                                    </div>
                                </div>
                            </ycommerce:testId>

                        </c:if></td>

                </tr>

            </c:forEach>
        </tbody>
    </table>



</div>

<storepickup:pickupStorePopup />
