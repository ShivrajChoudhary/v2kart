<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="entryNumber" required="false" type="java.lang.Long" %>
<%@ attribute name="cartPage" required="false" type="java.lang.Boolean" %>
<%@ attribute name="searchPageData" required="true"
			  type="de.hybris.platform.commerceservices.search.pagedata.SearchPageData" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>



<c:url var="addToCartToPickupInStoreUrl" value="/store-pickup/cart/add"/>
<c:url var="updateSelectStoreUrl" value="/store-pickup/cart/update"/>

<ul data-role="listview" data-theme="d" data-resultsfound="${not empty searchPageData.results}"
		class="pickup_store_results-list" data-inset="true" data-divider-theme="b">
	<c:choose>
		<c:when test="${not empty searchPageData.results}">
			<c:forEach items="${searchPageData.results}" var="pickupStore"  varStatus="loop">
				<li class="ui-header ui-bar-a"><span class="ui-btn-inner">${pickupStore.pointOfService.name}</span></li>
				<li>
					<div class="outer-pickupstore-result-box">
					<div class="ui-grid-60-40 clearfix ${outOfStockStatus}">
						<div class="ui-block-a">
							<span class="pickup_store_results-list_name">${pickupStore.pointOfService.name}</span>
							<span class="pickup_store_results-list_line1">${pickupStore.pointOfService.address.line1}</span>
							<span class="pickup_store_results-list_line2">${pickupStore.pointOfService.address.line2}</span>
							<span class="pickup_store_results-list_town">${pickupStore.pointOfService.address.town}</span>
							<span class="pickup_store_results-list_zip">${pickupStore.pointOfService.address.postalcode}</span>
						</div>
						<div class="ui-block-b">
							<span class="pickup_store_results-list_miles"><fmt:formatNumber type="number" maxFractionDigits="2" value="${pickupStore.distanceKm}" />km</span>
						</div>
						</div>
								<form:form id="selectStoreForm" class="select_store_form" action="/cart/store-pickup" method="post">
								 	<input type="hidden" name="name" value/>
								 	 <input type="hidden" name="line1" value="${pickupStore.pointOfService.address.line1}" />
                            <input type="hidden" name="line2" value="${pickupStore.pointOfService.address.line2}" />
                            <input type="hidden" name="town" value="${pickupStore.pointOfService.address.town}" />
                            <input type="hidden" name="postalcode" value="${pickupStore.pointOfService.address.postalcode}" />
                            <input type="hidden" name="pickup" value="${true}"/>
                            <input type="hidden" name="store" value="${pickupStore.pointOfService.name}"/>
                             <input type="hidden" name="country" value="IN"/>
									<input type="hidden" name="storeNamePost" value="${pickupStore.pointOfService.name}"/>
									<input type="hidden" name="phoneNo" value="${pickupStore.pointOfService.address.phone1}"/>
									<input type="hidden" name="region" value="${pickupStore.pointOfService.address.region.isocode}"/>
									<button type="submit" data-role="button" data-theme="b" class="pickUpInStoreButton positive large pickup_here_instore_button"">
										<spring:theme code="pickup.here.button"/>
									</button>
								</form:form>
						</div>
				</li>
			</c:forEach>
		</c:when>
		<%-- Only show when there are no results --%>
		<c:when test="${searchPageData.pagination.totalNumberOfResults eq 0}">
			<li><p><spring:theme code="text.storefinder.mobile.page.noResults"/></p></li>
		</c:when>
	</c:choose>
</ul>


