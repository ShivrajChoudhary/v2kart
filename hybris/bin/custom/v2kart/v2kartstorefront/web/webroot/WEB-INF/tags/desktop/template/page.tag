<%@ tag body-content="scriptless" trimDirectiveWhitespaces="true"%>
<%@ attribute name="pageTitle" required="false" rtexprvalue="true"%>
<%@ attribute name="pageCss" required="false" fragment="true"%>
<%@ attribute name="pageScripts" required="false" fragment="true"%>
<%@ attribute name="hideHeaderLinks" required="false"%>

<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="header" tagdir="/WEB-INF/tags/desktop/common/header"%>
<%@ taglib prefix="footer" tagdir="/WEB-INF/tags/desktop/common/footer"%>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav"%>
<%@ taglib prefix="trackOrder" tagdir="/WEB-INF/tags/desktop/common/header"%>

<template:master pageTitle="${pageTitle}">

    <jsp:attribute name="pageCss">
		<jsp:invoke fragment="pageCss" />
	</jsp:attribute>

    <jsp:attribute name="pageScripts">
		<jsp:invoke fragment="pageScripts" />
	</jsp:attribute>

    <jsp:body>

		<div id="page" data-currency-iso-code="${currentCurrency.isocode}">
			<spring:theme code="text.skipToContent" var="skipToContent" />
			<a href="#skip-to-content" class="skiptocontent" data-role="none">${skipToContent}</a>
			<spring:theme code="text.skipToNavigation" var="skipToNavigation" />
			<a href="#skiptonavigation" class="skiptonavigation" data-role="none">${skipToNavigation}</a>
			<div class="masthead navbar-fixed-top">
            <header:header hideHeaderLinks="${hideHeaderLinks}" />
			<a id="skiptonavigation"></a>
			<nav:topNavigation />
            </div>
			<header:bottomHeader />
			<cart:cartRestoration />
			<div id="content" class="container clearfix">
			<a id="skip-to-content"></a>
				<jsp:doBody />
			</div>
		</div>
			<footer:footer/>
<trackOrder:trackYourOrderPopup />

<script type="text/javascript" data-cfasync="false">
setTimeout(  function downloadJSAtOnload() {
						var done = false;
						var script = document.createElement('script');
						script.async = true;
						script.type = 'text/javascript';
						script.src = 'https://app.purechat.com/VisitorWidget/WidgetScript';
						document.getElementsByTagName('HEAD').item(0)
								.appendChild(script);
						script.onreadystatechange = script.onload = function(e) {
							if (!done
									&& (!this.readyState
											|| this.readyState == 'loaded' || this.readyState == 'complete')) {
								var w = new PCWidget({
									c : 'bccf1419-dd03-4b51-a947-9aa1704650f9',
									f : true
								});
								done = true;
							}
						};
					},15000);
</script>
	</jsp:body>

</template:master>
