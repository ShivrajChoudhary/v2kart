<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="hideBreadcrumb" value="true" scope="request" />
<template:page pageTitle="${pageTitle}">
    <div class="homepage">
        <div id="globalMessages">
            <common:globalMessages />
        </div>
        <div class="">
            <cms:pageSlot position="CategoryButtonSlot1" var="feature" element="div" id="categoryImage-Disp">
                <cms:component component="${feature}" element="div" class="homepage-button-container" />
            </cms:pageSlot>
            <cms:pageSlot position="CategoryButtonSlot2" var="feature" element="div" id="categoryImage-Disp">
                <cms:component component="${feature}" element="div" class="homepage-button-container" />
            </cms:pageSlot>
            <cms:pageSlot position="CategoryButtonSlot3" var="feature" element="div" id="categoryImage-Disp">
                <cms:component component="${feature}" element="div" class="homepage-button-container" />
            </cms:pageSlot>
            <div class="homepage-button-container-clear"></div>
        </div>

        <div id="top-disp-img" class="home-disp-img">
            <cms:pageSlot position="Section1" var="feature" element="div" id="categoryImage-Disp">
                <cms:component component="${feature}" element="div" class="span-24 section1 cms_disp-img_slot" />
            </cms:pageSlot>
        </div>

        <div class="innerContent">
            <cms:pageSlot position="Section2" var="feature" element="div" class="home-disp-img">
                <cms:component component="${feature}" element="div" class="span-24 section2 cms_disp-img_slot" />
            </cms:pageSlot>
            <cms:pageSlot position="Section3" var="feature" element="div" class="home-disp-img">
                <cms:component component="${feature}" element="div" class="span-24 section2 cms_disp-img_slot" />
            </cms:pageSlot>
            <cms:pageSlot position="Section4" var="feature" element="div" class="home-disp-img">
                <cms:component component="${feature}" element="div" class="span-24 section2 cms_disp-img_slot" />
            </cms:pageSlot>
            <cms:pageSlot position="Section5" var="feature" element="div" class="home-disp-img">
                <cms:component component="${feature}" element="div" class="span-24 section3 cms_disp-img_slot" />
            </cms:pageSlot>
        </div>
    </div>
    <script type="text/javascript" data-cfasync="false">
					(function() {
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
					})();
				</script>
</template:page>