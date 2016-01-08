<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="pageData" required="true" type="de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="footer" tagdir="/WEB-INF/tags/mobile/common/footer"%>
<%@ taglib prefix="header" tagdir="/WEB-INF/tags/mobile/common/header"%>


<c:url value="/store-finder" var="searchUserLocationUrl" />
<c:url value="/store-finder/position" var="autoUserLocationUrl" />
<c:set value="5" var="initialLimit" scope="session" />

<script type="text/javascript">
	var searchUserLocationUrl = '${searchUserLocationUrl}';
	var autoUserLocationUrl = '${autoUserLocationUrl}';
	var userLocation = "${userLocation.searchTerm}";
	var longitude = "${userLocation.point.longitude}";
	var latitude = "${userLocation.point.latitude}";
	var showStoreLimit = Number("${initialLimit}");
</script>

<template:mobilePage pageId="facetRefinements-page" dataSearchQuery="${pageData.currentQuery.query.value}">
    <jsp:attribute name="header">
    
    	<div data-role="header" class="ui-bar" data-position="inline">
    		<a onclick="backToFacets();" class="backToFacets" style="display:none;" data-role="button"  data-icon="arrow-l"
				data-theme="c">
				<spring:theme code="search.nav.refine.button"/>
			</a>
			<h3 data-role="heading">
				<spring:theme code="search.nav.refinements" />
			</h3>
			<a href="#" data-role="button" id="applyFilter" data-theme="c" data-icon="false" class="ui-btn-right"><spring:theme
                code="search.nav.done.button" /></a>
                
         </div> 
 	</jsp:attribute>
   
    <jsp:attribute name="footer">
    <footer:simpleFooter />		
	</jsp:attribute>
   
    <jsp:body>
    	
               
		<nav:facetNavRefinements pageData="${pageData}" />
		
		
		
	</jsp:body>
	
</template:mobilePage>
