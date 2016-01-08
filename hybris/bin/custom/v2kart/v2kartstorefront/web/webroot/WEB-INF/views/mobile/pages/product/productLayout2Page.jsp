<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav"%>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/mobile/storepickup"%>


<template:page pageTitle="${pageTitle}">
	<jsp:body>
	<div id="productdetailspage">
		<div id="globalMessages">
			<common:globalMessages />
		</div>

		<cms:pageSlot position="Section1" var="feature" element="div" class="span-4 section1 cms_disp-img_slot">
			<cms:component component="${feature}" />
		</cms:pageSlot>

		<div class="productHeader">
			<h1 class="productHeadline innerContent" >
				<div class="productTitle">${product.name}</div>
			</h1>
			<div class="productRatingHolder innerContent" >
				<product:productAverageReviewDetailPage product="${product}" />
			</div>
		</div>
		<div class="innerContent">
		<product:productImage product="${product}" format="product" />
		<product:productAddToCartPanel product="${product}" />
		
		<cms:pageSlot position="AddToCart" var="comp" element="div">
			<cms:component component="${comp}" />
		</cms:pageSlot>
		<product:productStyleVariantSelector product="${product}" />
		<c:set var="flag" value="0" />
		 <c:if test="${not empty product.potentialPromotions}">
                <div class="offer">
                    <div class="offer-heading heading-white"><span class="offericon"></span>Offer</div>
                    <div class="offerContent">
                    <ul >
                        <c:forEach items="${product.potentialPromotions}" var="promotion">
                            
                                <li>&emsp;&#8226; ${promotion.description}</li>
                           
                        </c:forEach>

                    </ul>
                    </div>
                </div>
         </c:if>
            
		<div class="productAccordeon productInfos">
			<product:productOverview product="${product}" />
			<product:productReviews product="${product}" />
			<cms:pageSlot position="Tabs" var="tabs">
				<cms:component component="${tabs}" />
			</cms:pageSlot>
		</div>

		<cms:pageSlot position="UpSelling" var="comp" element="div" class="productAccordeon">
			<cms:component component="${comp}" />
		</cms:pageSlot>

		<cms:pageSlot position="CrossSelling" var="comp" element="div" class="productAccordeon">
			<cms:component component="${comp}" />
		</cms:pageSlot>
		</div>
		</div>
	</jsp:body>
</template:page>
<div data-role="page" id="imageContainerPage">
    <div data-role="main" class="ui-content">
        <div class="imageBox">
        </div>
        <div class="innerContent">
            <a href="#body" data-role="button" data-icon="delete" data-iconpos="notext">Close</a>
        </div>
    </div>
</div>
<nav:popupMenu />
