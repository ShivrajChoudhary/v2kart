<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<div class="row">
    <div class="col-md-12">
        <div id="productTabs">

            <ul class="clearfix tabs-list tabamount3">
                <li id="accessibletabsnavigation0-0" class="first"><a href="#accessibletabscontent0-0"><span class="current-info">current
                            tab: </span> <spring:theme code="product.product.details" /></a></li>
                            
                <li id="tab-reviews" class=""><a href="#accessibletabscontent0-1" id="tab-reviews"><spring:theme
                            code="review.reviews" /></a></li>
                            
<!--                 <li id="accessibletabsnavigation0-2" class="last"><a href="#accessibletabscontent0-2">Shipping</a></li> -->
            </ul>

            <div class="tabBody productTabDescription">
                <product:productDetailsTab product="${product}" />
            </div>

            <div id="review-tab" class="tabBody">
                <product:productPageReviewsTab product="${product}" />
            </div>
            
            <cms:pageSlot position="Tabs" var="tabs">
                <cms:component component="${tabs}" />
            </cms:pageSlot>

            
        </div>
    </div>
</div>