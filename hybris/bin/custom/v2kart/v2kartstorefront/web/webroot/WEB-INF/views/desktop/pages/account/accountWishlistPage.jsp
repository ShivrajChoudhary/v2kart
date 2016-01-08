<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/formElement"%>

<c:set var="hideBreadcrumb" value="true" scope="request" />
<c:set var="wishlistEmpty" value="true"/>
<div class="form_field-elements" id="wishListPage">
    <div class="control-group">
        <div class="controls">
            <label class="control-label headline"><spring:theme code="text.account.wishlist" text="My wishlist" /></label>
            <c:if test="${not empty wishlists}">
                <c:forEach items="${wishlists}" var="wishlist">
                    <ul>
                        <c:set var="buttonType">button</c:set>
                        <c:if test="${not empty wishlist.entries }">
                            <c:set var="wishlistEmpty" value="false"/>
                            <c:forEach items="${wishlist.entries}" var="entry">
                                <li class="wishItem"><c:url value="/my-account/add-cart-wishlist/${entry.product.code}" var="addToCart" />
                                    <c:url value="/my-account/remove-wishlist/${entry.product.code}" var="removeFromWishlist" />
    
                                    <div class="image-sec left padright20">
                                        <a href="<c:url value="${entry.product.url}"/>"><product:productPrimaryImage
                                                product="${entry.product}" format="cartIcon" /></a>
                                    </div>
    
                                    <div class="add-to-cart-sec left wishListDetail">
                                   <a href="<c:url value="${entry.product.url}"/>">     <p class="wishlistentryname nobtmmargin">${entry.product.name}</p></a>
                                        
                                        <!-- get size of the product -->
                                        <div class="wishlistentrysize">
                                            <c:set value="${entry.product.baseOptions[0]}" var="baseOptionData"/>
                                            <c:if test="${baseOptionData ne null}">
                                                <c:set value="${baseOptionData.selected}" var="selectedBaseOptionData"/>
                                                <c:if test="${selectedBaseOptionData ne null and selectedBaseOptionData.variantOptionQualifiers[1] ne null}">
                                                    <spring:theme code="wishlist.size.text">${selectedBaseOptionData.variantOptionQualifiers[1].value}</spring:theme>
                                                </c:if>
                                            </c:if>
                                        </div>
                                        <c:choose>
                                            <c:when test="${entry.product.stock.stockLevelStatus.code eq 'outOfStock' }">
                                                <div class="popover-link notifymewishlistdiv notifyplpaddtowishlistform">
    
    
                                                    <a href="javascript:void(0)" title="Notify Me" class="btn btn-red clearpopovercontent"">Notify
                                                        Me</a>
                                                    <div style="display: none;" class="popover large-box right"
                                                        id="notifymepopover_${entry.product.code}">
                                                        <div class="popover-inner">
                                                            <a class="popover-close" href="javascript:void(0);">&times;</a>
                                                            <h3 class="popover-title margin0">Notify Me</h3>
                                                            <div class="popover-content">
                                                                <c:url value="/p/notifyCustomer" var="notifyCustomer" />
                                                                <form:form method="post" action="${notifyCustomer}"
                                                                    commandName="v2NotifyCustomerForm" id="notifyMe_${entry.product.code}">
                                                                    <div class="form_field-elements">
                                                                        <input type="hidden" value="NOTIFY_ME" name="type" /> <input
                                                                            type="hidden" value="${entry.product.code}" name="productCode" /> <input
                                                                            type="hidden" value="${entry.product.name}" name="name"
                                                                            id="customerNotification.name" />
                                                                                         
                                                                        <form:hidden path="currentUserName"
                                                                            idKey="customerNotification.currentUserName" />
                                                                        <div class="control-group" style="float: none; height: 60px;">
                                                                            <formUtil:formInputBox labelKey="Email ID"
                                                                                idKey="currentUserEmailId_${entry.product.code}"
                                                                                path="currentUserEmailId" mandatory="true"
                                                                                inputCSS="notifymewishlistpage clearcontent1" />
                                                                        </div>
                                                                        <div class="form-actions clearfix">
                                                                            <button class="positive margin0 notifymesendbutton" type="submit">Send</button>
                                                                        </div>
                                                                    </div>
                                                                </form:form>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <!--popover start-->
                                                    <div class="popover WL medium-box" style="display: none;"
                                                        id="notifymeresultpopover_${entry.product.code}">
                                                        <div class="popover-inner">
                                                            <a class="popover-close" href="javascript:void(0);">x</a>
                                                            <div class="popover-content">
                                                                <span class="green-link bold" id="notifymeresultmsg_${entry.product.code}"></span>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <!--popover end-->
                                                </div>
                                            </c:when>
                                            <c:otherwise>
                                                <%-- <a class="btn btn-red" title="Add To Bag" href="${addToCart}">Add To Bag</a> --%>
                                                <form method="post" id="addToCartWishlistForm" class="addToCartWishlistForm" action="/cart/check">
                                                    <input type="hidden" maxlength="3" size="1" id="qty" name="qty" class="qty" value="0" />
                                                    <input type="hidden" name="productCodePost" value="${entry.product.code}" id="productCodePost" />
                                                    <button class="btn btn-red" title="Add To Bag" type="submit" id="addToCartButton">
                                                        <spring:theme code="basket.add.to.bag" />
                                                    </button>
                                                </form>
                                                <form method="get" id="addCartWishlistForm_${entry.product.code}" action="${addToCart}">
                                                    <input type="hidden" name="productCodePost" value="${entry.product.code}" id="productCodePost" />
                                                </form>
                                                
                                            </c:otherwise>
                                        </c:choose>
    
                                    </div>
                                    <div class="itemPrice left">
                                        <p class="nobtmmargin">${entry.basePrice.formattedValue}</p>
                                    </div>
    
    
                                    <div class="share-wishlistcntr right" style="margin-top: 18px;">
                                        <a class="rlink margin0 right submitRemoveProduct " title="Remove from Wishlist"
                                            href="${removeFromWishlist}"><i class="remove-item"></i>Remove </a>
                                    </div></li>
                                    <div class="modal fade confirmAddPopup" id="confirmAddPopup_${entry.product.code}">
	                                   <div class="modal-dialog">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                                                        <h4 class="modal-title"><spring:theme code="basket.add.to.bag" /></h4>
                                                </div>
                                                <div class="modal-body">
                                                   <p><spring:theme code="basket.contains.product" /></p>
                                                </div>
                                                <div class="modal-footer">
                                                    <a class="btn btn-red" title="Add To Bag" href="${addToCart}"><spring:theme code="text.popup.yes"/></a>
                                                    <button type="button" class="btn btn-default" data-dismiss="modal"><spring:theme code="text.popup.no"/></button>
                                                </div>
                                             </div><!-- /.modal-content -->
                                        </div><!-- /.modal-dialog -->
                                    </div><!-- /.modal -->
                            </c:forEach>
                        </c:if>
                    </ul>
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

