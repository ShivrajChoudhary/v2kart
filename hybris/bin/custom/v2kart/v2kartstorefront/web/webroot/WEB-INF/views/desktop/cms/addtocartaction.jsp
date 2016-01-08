<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/desktop/storepickup" %>

<c:url value="${url}" var="addToCartUrl" />
<c:url value="/showcart" var="openCartUrl" />
<product:productNotifyMe product="${product}" />
<form:form method="post" id="addToCartForm" class="add_to_cart_form" action="/cart/check">
    <c:if test="${product.purchasable}">
        <input type="hidden" maxlength="3" size="1" id="qty" name="qty" class="qty" value="1">
    </c:if>
    <input type="hidden" name="productCodePost" value="${product.code}" id="productCodePost" />
    <c:set var="buttonType">button</c:set>

    <c:if test="${product.purchasable and product.stock.stockLevelStatus.code ne 'outOfStock' }">
        <c:set var="buttonType">submit</c:set>
    </c:if>

    <c:choose>
        <c:when test="${fn:contains(buttonType, 'button')}">
           <c:if test="${ product.stock.stockLevelStatus.code ne 'outOfStock' or not product.purchasable}">
            <%-- <p>
                <spring:theme code="product.select.size.first" text="Choose a size to add this to your Shopping bag" />
            </p> --%>
            </c:if>
            <c:if test="${product.purchasable and product.stock.stockLevelStatus.code eq 'outOfStock' }">
                <button type="button" class="btn btn-red notifyMe" onclick="showNotifyMe()" title="Notify Me">Notify Me</button>
            </c:if>
        </c:when>

        <c:otherwise>

        </c:otherwise>
    </c:choose>

    <c:if test="${ product.stock.stockLevelStatus.code ne 'outOfStock' or not product.purchasable}">
        <div class="add-to-cart1">
        	<div class="addToCartButton">
	            <button class="btn btn-red" title="Add To Bag" type="${buttonType}" id="addToCartButton"
	                <c:if test="${not product.purchasable}"> disabled   
					data-content="<spring:theme code="product.select.size.first" text="Choose a size to add this to your Shopping bag" />"
					data-placement="bottom" data-toggle="popover" </c:if> >
	                <spring:theme code="basket.add.to.bag" />
	            </button>
            </div>
			<div class="addToCartButton">
				<button class="btn btn-red" title="Buy Now" type="button" id="addToBagButton"  onclick="buyNowFormSubmit()"
	                <c:if test="${not product.purchasable}"> disabled data-toggle="popover" data-trigger="focus"  
					data-content="<spring:theme code="product.select.size.first" text="Choose a size to add this to your Shopping bag" />"
					data-placement="bottom"  </c:if> >
	                <spring:theme code="basket.buy.now" />
	                &nbsp; <span class="glyphicon glyphicon-shopping-cart"></span>
                    
	            </button>
               
            </div>
        </div>
    </c:if>
    

</form:form>
<%--   <storepickup:clickPickupInStore product="${product}" cartPage="false"/> --%>
<%--             <storepickup:pickupStorePopup/> --%>
           
<div class="modal fade" id="confirmAddPopup">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title"><spring:theme code="basket.add.to.bag" /></h4>
      </div>
      <div class="modal-body">
        <p> <spring:theme code="basket.contains.product" /></p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:theme code="text.popup.no"/></button>
        <button type="button" class="btn btn-red" data-dismiss="modal" onclick="addToCartFormSubmit()"><spring:theme code="text.popup.yes"/></button>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<div class="modal fade" id="confirmBuyNowPopup">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title"><spring:theme code="basket.buy.now" /></h4>
      </div>
      <div class="modal-body">
        <p><spring:theme code="basket.contains.product"/></p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:theme code="text.popup.no"/></button>
        <button type="button" class="btn btn-red" onclick="openCartPage1()"><spring:theme code="text.popup.yes"/></button>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

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

<form method="POST" id="addToCartPopupForm" class="add_to_cart_form" action="/cart/add" name="BuyNowForm">
    <c:if test="${product.purchasable}">
        <input type="hidden" maxlength="3" size="1" id="qty" name="qty" class="qty" value="1">
    </c:if>
   <input type="hidden" name="productCodePost" value="${product.code}" id="productCodePost" />
</form>