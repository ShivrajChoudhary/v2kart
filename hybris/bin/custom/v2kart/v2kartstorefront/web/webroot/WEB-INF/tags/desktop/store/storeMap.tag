<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="store" required="false" type="de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="comma" value="," scope="page"/>
<c:set var="warehouseName" scope="page">
    ${store.name}${comma}${store.address.line1}${comma}${store.address.line2}${comma}${store.address.town}${comma}${store.address.postalCode}${comma}${store.address.country.name}
</c:set>
<div class="storeMap">	
	<c:if test="${store ne null and store.geoPoint.latitude ne null and store.geoPoint.longitude ne null}">
		<div class="store_map_details" id="map_canvas"
			data-latitude = '${store.geoPoint.latitude}'
			data-longitude = '${store.geoPoint.longitude}'
			data-stores= '{"id":"0","latitude":"${store.geoPoint.latitude}","longitude":"${store.geoPoint.longitude}","name":"${warehouseName}"}'
			></div>
	</c:if>
</div>
