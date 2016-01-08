<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="action" tagdir="/WEB-INF/tags/desktop/action"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<link rel="stylesheet" type="text/css" media="all" href="${siteResourcePath}/css/productDetails.css" />

<spring:theme code="text.addToCart" var="addToCartText" />
<c:url value="${product.url}" var="productUrl" />
<div id="quickViewSpinner" class="quickViewSpinner" style="display:none;">
    <img src="${siteResourcePath}/images/quickViewSpinner.gif" />
</div>

<div id="quickView" class="quickview">
    <div class="container">
        <div class="row">
            <div class="col-xs-12 col-sm-12 col-md-7">
                <%-- <product:productImagePanel product="${product}" galleryImages="${galleryImages}" />
 --%>
                <div class="productImage">
                 <div class="productImageGallery" id="quickViewPopCarouselContainer">
                    <ul class="jcarousel-skin">
                        <c:forEach items="${galleryImages}" var="container" varStatus="varStatus">
                            <li><span class="thumb ${(varStatus.index==0)? "active":""}"> <img src="${container.thumbnail.url}"
                                    data-primaryimagesrc="${container.zoom.url}" data-galleryposition="${varStatus.index}"
                                    alt="${container.thumbnail.altText}" title="${container.thumbnail.altText}" />
                            </span></li>
                        </c:forEach>
                    </ul>
                </div>
                <div class="productImagePrimary" id="primary_image">
                    <a class="productImagePrimaryLink" href="${productUrl}"> <product:productPrimaryImage product="${product}"
                            format="zoom" />
                    </a>
                </div>
                <%-- <c:url value="${product.url}/zoomImages" var="productZoomImagesUrl" />
                    <a class="productImageZoomLink" href="${productZoomImagesUrl}"></a> --%>
            </div>
            </div>
            <c:url value="/cart/add" var="addToCartUrl" />
            <c:url value="/showcart" var="openCartUrl" />


            <div class="col-xs-12 col-sm-12 col-md-5">
                <div class="heading">
                    <a href="${productUrl}"> ${product.name} </a>
                </div>
                <div class="productDescription">
                    <div class="pricedesc">
                        <c:choose>
                            <c:when test="${product.percentageDiscount ne null}">
                                <span class="small-price">${product.price.formattedValue} </span>
                            </c:when>
                            <c:otherwise>
                                <span class="big-price">${product.price.formattedValue} </span>
                            </c:otherwise>
                        </c:choose>
                        <c:if test="${product.percentageDiscount ne null}">
                            <span class="big-price">${product.discountedPrice.formattedValue}</span>
                        </c:if>
                    </div>

                    <div class="yCmsContentSlot">
                        <product:productVariantSelector product="${product}" />
                        <product:productAddToCartPanel product="${product}" allowAddToCart="${true}" isMain="false" />
                    </div>


                    <span id="addToCartErrorMessage" class="addToCartErrorMessage" style="display: none;"></span>
                    <c:if test="${cartContainsProduct }">
                        <span id="productInCartMessage" class="addToCartErrorMessage red" > 
                           <spring:theme code="text.product.inBag" />
                        </span>
                    </c:if>
                    <div class="add-to-cart">
                        <form:form method="post" id="addToCartForm" class="add_to_cart_form" action="${addToCartUrl}">

                            <c:if test="${product.purchasable}">
                                <input type="hidden" maxlength="3" size="1" name="qty" class="qty" value="1">
                            </c:if>
                            <input type="hidden" name="productCodePost" value="${product.code}" id="productCodePost" class="productCodePost" />
                            <%--    <c:if test="${empty showAddToCart ? true : showAddToCart}"> --%>
                            <c:set var="buttonType">button</c:set>

                            <c:choose>
                                <c:when test="${product.purchasable and product.stock.stockLevelStatus.code ne 'outOfStock' }">
                                    <c:set var="buttonType">submit</c:set>
                                </c:when>
                                <c:otherwise>
                                    <c:if test="${ product.stock.stockLevelStatus.code ne 'outOfStock' or not product.purchasable}">
                                        <div class="red">
                                            <spring:theme code="product.select.size.first" text="Choose a size to add this to your Shopping bag"/>
                                        </div>
                                    </c:if>
                                </c:otherwise>
                            </c:choose>

                            <div class="add-to-cart">
                                <button class="btn btn-red" title="Add To Bag" type="${buttonType}" id="addToCartButton"
                                    <c:if test="${not product.purchasable or product.stock.stockLevelStatus.code eq 'outOfStock'}"> disabled  </c:if> style="margin-bottom: 15px;">
                                    <spring:theme code="basket.add.to.bag" />
                                </button>
                                <button class="btn btn-red" title="Buy Now" type="button"
                                    <c:if test="${not product.purchasable or product.stock.stockLevelStatus.code eq 'outOfStock'}"> disabled  </c:if> onclick="openCartPage1()">
                                    <spring:theme code="basket.buy.now" />
                                    &nbsp; <span class="glyphicon glyphicon-shopping-cart"></span>
                                </button>
                            </div>

                        </form:form>

                        <form method="POST" id="showcartform" action="${openCartUrl}" name="ShowCartForm">
                            <c:if test="${product.purchasable}">
                                <input type="hidden" maxlength="3" size="1" id="qty" name="qty" class="qty" value="1">
                            </c:if>
                            <input type="hidden" name="code" value="${product.code}" id="productCodePost" class="productCodePost" /> <input
                                type="hidden" name="CSRFToken" value="${CSRFToken}">
                        </form>
                    </div>

                </div>

                <div class="clear offer">
                    <c:if test="${not empty product.potentialPromotions}">
                        <div class="offer">
                            <div class="offer-heading">Offer</div>
                            <ul>
                                <c:forEach items="${product.potentialPromotions}" var="promotion">
                                    <c:set var="flag" value="${flag + 1}" />
                                    <c:if test="${flag lt 6 }">
                                        <li>${promotion.description}</li>
                                    </c:if>
                                </c:forEach>

                            </ul>
                        </div>
                    </c:if>
                </div>

                <p class="viewProductDetails">
                    <a class="red" href="${productUrl}"><spring:theme code="product.product.full.details" />&#xBB;</a>
                </p>


            </div>

        </div>
    </div>

</div>
<script type="text/javascript">
	$(document).ready(function() {
		ACC.product.bindToQuickViewAddToCartForm();
		ACC.product.bindToStyleDropDown();
		ACC.product.bindToSizeDropDown();

		$('#addToCartButton').live('click', function() {
			var quantityField = $('[name=qty]', formElement).val();
			if (quantityField > 0) {
				$(window).colorbox.close();
			}
		});

		/* $("#Size").change(function() {
			var url = "";
			var selectedIndex = 0;
			var productCode = 0;
			$("#Size option:selected").each(function() {
				url = $(this).attr('value');
				selectedIndex = $(this).attr("index");
				var splitUrl = new Array();
				splitUrl = url.split("/");
				productCode = splitUrl.pop();
			});
			$('.productCodePost').val(productCode);

		}); */
	});
</script>
<%-- <script type="text/javascript" src="${commonResourcePath}/js/jquery-1.7.2.min.js"></script> --%>
<script type="text/javascript" src="${commonResourcePath}/js/jqueryjcarousel.min.js"></script>
<%-- <script type="text/javascript" src="${commonResourcePath}/js/jquery.colorbox.custom-1.3.16.js"></script> --%>
<script type="text/javascript" src="${commonResourcePath}/js/jquery.easing.1.3.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/acc.productDetail.js"></script>
<%-- <script type="text/javascript" src="${siteResourcePath}/js/custom-functions.js"></script> --%>
<%-- <script type="text/javascript" src="${siteResourcePath}/js/v2kart-custom.js"></script> --%>