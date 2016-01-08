<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ attribute name="galleryImages" required="true" type="java.util.List"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/formElement"%>

<spring:theme code="text.addToCart" var="addToCartText" />

<div class="productDetailsPanel">
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
    <div class="row">
        <div class="col-md-7">
            <product:productImagePanel product="${product}" galleryImages="${galleryImages}" />
        </div>
        <div class="col-md-5">

            <div class="span-10 productDescription last">
                <div class="pdp-heading capitalize">
                    <ycommerce:testId code="productDetails_productNamePrice_label_${product.code}">
                     ${product.name}
                    </ycommerce:testId>
                </div>
                <div class="prodReview clearfix">
                    <product:productReviewSummary product="${product}" />

                    &nbsp;|&nbsp;
                    <product:productEmailAFriend />
                    &nbsp;|&nbsp;
                    <!-- AddToAny BEGIN -->
                    <div class="a2a_kit a2a_default_style" style="display: inline-block;">
                        <a class="a2a_button_facebook" title="Facebook"></a> <a class="a2a_button_twitter" title="Twitter"></a> <a
                            class="a2a_button_google_plus" title="Google Plus"></a> <a class="a2a_button_pinterest_pin"
                            data-media="${pinitMediaUrl}" data-description="${product.name}" data-pin-config="none" data-pin-shape="round"></a>
                    </div>
                    <!-- AddToAny END -->
                </div>

                <div class="pricedesc">
                    <ycommerce:testId code="productDetails_productNamePrice_label_${product.code}">

                        <div class="pricedesc">
                            <c:choose>
                                <c:when test="${product.percentageDiscount ne null}">
                                    <span class="old-price">${product.price.formattedValue} </span>
                                </c:when>
                                <c:otherwise>
                                    <span class="mrp">${product.price.formattedValue} </span>
                                </c:otherwise>
                            </c:choose>
                            <c:if test="${product.percentageDiscount ne null}">
                                <span class="mrp">${product.discountedPrice.formattedValue}</span>
                                <span class="badge badge-red discount">${product.percentageDiscount}% OFF</span>
                            </c:if>
                        </div>

                    </ycommerce:testId>
                </div>


                <%--                                 <product:productPromotionSection product="${product}" /> --%>

                <input type="hidden" value="${isAddedToWishlist}" name="isAddedToWishlist" id="isAddedToWishlist">

                <c:set var="flag" value="0" />

                <div class="clearfix">

                    <%-- <cms:pageSlot position="VariantSelector" var="component" element="div">
                        <cms:component component="${component}" />
                    </cms:pageSlot> --%>
                    <product:productVariantSelector product="${product}" />
                    
                    <product:productStyleVariantSelector product="${product}" />

                    <cms:pageSlot position="AddToCart" var="component" element="div" class="span-10 last add-to-cart">
                        <cms:component component="${component}" />
                    </cms:pageSlot>
                    <span id="addToCartErrorMessage" class="addToCartErrorMessage" style="display: none;"></span> <span style="color: red;"><spring:theme
                            code="${errorMsg}" /></span>
                </div>
                <div class="coditional">
          	<p># Actual product may differ from the images shown</p>
            <p># Conditions Apply</p>
          </div>
                <div id="pincode">

                    <%--                    <span class="delivery"><spring:theme code="text.delivery.options" /></span> --%>
                    <div class="control-group pincheck mleft0 ">
                        <div id="checkForm" class="controls" style="display: block;">

                            <input type="hidden" value="${product.code}" name="productCode" id="productCodeServiceability" />
                            <div class="form-group">

                                <table>
                                    <tr>
                                        <td><span class="delivery"><spring:theme code="text.delivery.options" /></span></td>
                                        <td><c:choose>
                                                <c:when test="${pinCode != null }">
                                                    <input type="text" id="searchServiceability" class="pinSearchInput form-control "
                                                        name="text" value="${pinCode}" maxlength="6" autocomplete="off"
                                                        placeholder="Enter Pincode">
                                                </c:when>
                                                <c:otherwise>
                                                    <input type="text" id="searchServiceability"
                                                        class="pinSearchInput form-control margin-top-8 " name="text" value=""
                                                        maxlength="6" autocomplete="off" placeholder="Enter Pincode">
                                                </c:otherwise>
                                            </c:choose></td>
                                        <td class="status_button">
                                            <button class="btn-cod checkPin margin-top-8" disabled="disabled" type="submit"
                                                onClick="checkForServiceability(); return false;" title="check pincode">Check</button>
                                        </td>
                                    </tr>
                                </table>
                            </div>
                        </div>

                        <div style="display: none;" id="validPin" class="pinvalid clear">
                            <div class="panel-static fk-inline-block vmiddle headLabel">
                                <span class="icon icon-map"></span> <span class="serviceability-text"></span> <a href="javascript:void(0);"
                                    class="pincodeChangeBtn blue-link"><spring:theme code="text.delivery.pinCode.change" /></a>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="cod">
                    <p class="codhead">
                        <spring:theme code="text.delivery.cod" />
                        <span class="glyphicon glyphicon-question-sign" style ="margin-left: 9px;"></span>
                    </p>
                    <p class="cod-msg">
                        <spring:theme code="text.delivery.cod.available" />
                    </p>
                    <span id="validCod" class="codServiceability-text"></span>
                </div>
            </div>



            <c:if test="${product.purchasable}">
                <div class="bottomlink">

                    <div class="pdplinks">
                        <a href="javascript:void(0);" tabindex="0" title="Add To Wishlist" class="wishlist addToWishListLink"
                            data-toggle="popover" data-trigger="focus" title="Add to Wishlist"
                            data-content="Product has been successfully added to your wishlist" data-placement="bottom"
                            data-options='{"addToWishListUrl" : "${addToWishListUrl}","addToWishListProduct" : "${addToWishListProduct}"}'>
                            <span class="glyphicon glyphicon-heart"></span> Add to Wishlist
                        </a>
                    </div>
                </div>
            </c:if>
            <div class="span-10 productDescription last">

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
            <div class="summary">
       
                <p>${product.summary}</p>
            </div>
        </div>
    </div>
</div>



<cms:pageSlot position="Section2" var="feature" element="div" class="span-8 section2 cms_disp-img_slot last">
    <cms:component component="${feature}" />
</cms:pageSlot>
<cms:pageSlot position="Section2" var="feature" element="div" class="span-8 section2 cms_disp-img_slot last">
    <cms:component component="${feature}" />
</cms:pageSlot>

