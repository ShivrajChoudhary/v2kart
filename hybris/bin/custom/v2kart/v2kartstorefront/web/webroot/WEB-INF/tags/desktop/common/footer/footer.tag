<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>

<div id="footer">
    <cms:pageSlot position="Footer" var="feature" element="div" class="footer">
        <cms:component component="${feature}" />
    </cms:pageSlot>
    <!-- Pure Chat Plugin  -->

    <!-- <script type="text/javascript" data-cfasync="false">
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
				</script> -->
			<!-- snow strom script -->
<%-- <script type="text/javascript" src="${siteResourcePath}/js/snowstrom.js"></script> --%>
			
</div>
