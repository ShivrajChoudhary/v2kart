<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="store" required="true" type="de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div class="innerContent">
<button id="navigateTo"
		class="form search"
		data-role="button"
		data-icon="arrow-r"
		data-iconpos="right"
		data-theme="b"
		data-latitude="${store.geoPoint.latitude}"
		data-longitude="${store.geoPoint.longitude}">
	<span class="search-icon">
		<spring:theme code="storeFinder.navigateTo"/>
	</span>
</button>
</div>