<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>

<template:page pageTitle="${pageTitle}">
	<jsp:body>
		<div id="globalMessages">
			<common:globalMessages />
		</div>
		<cms:pageSlot position="TopRotatingBanner-mobile" var="feature">
	        <cms:component component="${feature}" element="div"
				class="col-md-12" />
	    </cms:pageSlot>
	    <div class="innerContent categoryHomeScetions">
			<cms:pageSlot position="BannerSection1" var="feature">
              <cms:component component="${feature}" element="div"
					class="col-md-4" />
            </cms:pageSlot>
            <cms:pageSlot position="BannerSection2" var="feature">
                <cms:component component="${feature}" element="div"
					class="col-md-4" />
            </cms:pageSlot>
            <cms:pageSlot position="BannerSection3" var="feature">
                <cms:component component="${feature}" element="div"
					class="col-md-8" />
            </cms:pageSlot>
            <cms:pageSlot position="CarouselSection" var="feature">
                <cms:component component="${feature}" element="div"
					class="home-disp-img homepage" />
            </cms:pageSlot>
        </div>
	</jsp:body>
</template:page>
<nav:popupMenu />
