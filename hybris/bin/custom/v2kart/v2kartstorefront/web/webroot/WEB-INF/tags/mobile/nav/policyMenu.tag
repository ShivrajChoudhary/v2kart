<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>

<spring:theme code="text.header.connect" text="Menu" var="menu"/>
<div id="policy-popup"
	data-role="content"
	class="top-nav-bar-layer accmob-hotlineBox menu-container"
	style="display:none;"
	data-theme="f"
	data-content-theme="f"
	data-options="{&quot;mode&quot;:&quot;blank&quot;,&quot;headerText&quot;:&quot;${menu}&quot;,&quot;headerClose&quot;:true,&quot;blankContent&quot;:true,&quot;themeDialog&quot;:&quot;f&quot;}">
	<div class="policyPopup">
	<cms:pageSlot position="PolicyContent" var="feature" element="ul" data-role="listview" data-inset="true" data-theme="d">
		<cms:component component="${feature}" />
	</cms:pageSlot>
	</div>
</div>
