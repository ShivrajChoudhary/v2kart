<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="store" tagdir="/WEB-INF/tags/mobile/store"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<template:page pageTitle="${store.name} | ${siteName}">
	<jsp:attribute name="pageScriptsBeforeJspBody">
		<script type="text/javascript" src="${commonResourcePath}/js/jquery.ui.map.full.min.3.0.rc1.js"></script>
	</jsp:attribute>
	<jsp:attribute name="pageScriptsAfterJspBody">
		<%-- Google maps API --%>
		<c:if test="${not empty googleApiVersion}">
			<script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?v=${googleApiVersion}&key=${googleApiKey}&sensor=false"></script>
		</c:if>
		<script type="text/javascript" src="${siteResourcePath}/js/custom/accmob.storefinder.js"></script>
	</jsp:attribute>
	<jsp:body>
		<div class="item_container_holder storeDetailPage">
			<div class="storeFinderDetailsBox">
                <div class="ui-bar ui-bar-b">
                    <h1>
                        <spring:theme code="mobile.storelocator.title" />
                    </h1>
                </div>
				<div class="innerContent" style="padding-top: 10px;">
                <div data-role="content" data-theme="b">
				<store:storeDetails store="${store}" />
				<store:storeMap store="${store}" />
				<store:navigateToButton store="${store}" />
				<store:storeImage store="${store}" format="store" />
				<store:storeMiscDetails store="${store}" />
                </div>
                </div>
			</div>
			<div class="store_paragraph_content">
				${store.storeContent}
			</div>
			<br />

			<cms:pageSlot position="TopContent" var="feature" element="div">
				<cms:component component="${feature}" />
			</cms:pageSlot>

			<div>
				<store:storeSearch/>
			</div>
		</div>
	</jsp:body>
</template:page>
