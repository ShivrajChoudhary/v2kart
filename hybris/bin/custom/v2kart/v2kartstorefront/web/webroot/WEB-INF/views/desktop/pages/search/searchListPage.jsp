<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/desktop/storepickup" %>

<template:page pageTitle="${pageTitle}">
<jsp:attribute name="pageScripts">
              <script type="text/javascript" src="${siteResourcePath}/js/acc.productlisting.js"></script>
       </jsp:attribute>
    <jsp:body>

	<div id="globalMessages">
		<common:globalMessages/>
	</div>
    <div id="plpSpinner" class="" style="display:none;">
        <img src="${siteResourcePath}/images/spinner.gif" />
    </div>
    <div id="categoryPage">
    <div class="col-md-3">
    	<div class="${not empty categoryMin and not empty categoryMax ? 'facetNavigation': ''}">
    		<cms:pageSlot position="ProductLeftRefinements" var="feature">
    			<cms:component component="${feature}"/>
    		</cms:pageSlot>
    	</div>
    </div>

    <div class="col-md-9">
    	<cms:pageSlot position="SearchResultsListSlot" var="feature">
    		<cms:component component="${feature}"/>
    	</cms:pageSlot>
    </div>
    </div>

	<storepickup:pickupStorePopup />
    </jsp:body>
</template:page>
