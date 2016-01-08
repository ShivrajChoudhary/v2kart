<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ attribute name="entryNumber" required="false" type="java.lang.Long"%>
<%@ attribute name="cartPage" required="false" type="java.lang.Boolean"%>
<%@ attribute name="searchPageData" required="true" type="de.hybris.platform.commerceservices.search.pagedata.SearchPageData"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<script>
	$(document).ready(function() {

		$('.pickUp-button').click(function() {
			var qty = $("#pickupQty").val();
			$(".hiddenPickupQty").val(qty);

		});

		$(document).on("click", ".pickUp-button", function(e) {
			$.colorbox.close();
		});
	});
</script>

<%-- <c:url var="addToCartToPickupInStoreUrl" value="/store-pickup/cart/add" /> --%>
<%-- <c:url var="updateSelectStoreUrl" value="/store-pickup/cart/update" /> --%>

<!-- <script type="text/javascript"> -->
<%-- // 	var addToCartToPickupInStoreUrl = '${addToCartToPickupInStoreUrl}'; --%>
<%-- // 	var searchLocation = '${locationQuery}'; --%>
<!-- </script> -->

<ul class="searchPOSResultsList clear_fix">
    <%--     <c:set var="count" value="1"></c:set> --%>
    <c:forEach items="${searchPageData.results}" var="pickupStore">

        <div class=" col-md-5">
            <div class="pick-box">
                <h3>${pickupStore.pointOfService.name}</h3>
                <div class="pick-info clearfix">
                    <p>${pickupStore.pointOfService.address.line1}<br>
                    ${pickupStore.pointOfService.address.line2}<br>
                        ${pickupStore.pointOfService.address.town}-${pickupStore.pointOfService.address.postalcode}</p>
                    <%-- <p class="pull-left"> ${pickupStore.openingHours.name}</p> --%>
                    <div class="pull-right">
                        <fmt:formatNumber type="number" maxFractionDigits="2" value="${pickupStore.distanceKm}" />
                        km<span class="stock"> <%--         <c:choose> --%> <%--             <c:when test="${pickupStore.stockData.stockLevelStatus.code eq 'outOfStock'}"> --%>
                            <%--                 <p class="negative"><spring:theme code="pickup.out.of.stock" /></p> --%> <%--             </c:when> --%>
                            <%--             <c:when test="${pickupStore.stockData.stockLevelStatus.code ne 'outOfStock' and empty pickupStore.stockData.stockLevel}"> --%>
                            <%--                 <p class="positive"><spring:theme code="pickup.force.in.stock" /></p> --%> <%--             </c:when> --%>
                            <%--             <c:otherwise> --%> <%--                 <p class="positive"><spring:theme code="pickup.in.stock" arguments="${pickupStore.stockData.stockLevel}" /></p> --%>
                            <%--             </c:otherwise> --%> <%--         </c:choose>  --%>

                        </span>
                    </div>
                    <%--         <c:if test="${pickupStore.stockData.stockLevel gt 0 or empty pickupStore.stockData.stockLevel}"> --%>
                    <%--             <c:choose> --%>
                    <%--                 <c:when test="${cartPage}"> --%>
                    <%--                     <div class="text-center"><form:form id="selectStoreForm" class="select_store_form" --%>
                    <%--                         action="${updateSelectStoreUrl}" method="post"> --%>
                    <%--                         <input type="hidden" name="storeNamePost" value="${pickupStore.name}" /> --%>
                    <%--                         <input type="hidden" name="entryNumber" value="${entryNumber}" /> --%>
                    <!--                         <input type="hidden" name="hiddenPickupQty" value="1" class="hiddenPickupQty" /> -->
                    <%--                         <button type="submit" class="btn btn-red pickUp-button"><spring:theme code="pickup.here.button" /></button> --%>
                    <%--                     </form:form></div> --%>
                    <%--                 </c:when> --%>
                    <%--                 <c:otherwise> --%>
                    <%--                     <div class="text-center"><form:form class="add_to_cart_storepickup_form" action="${addToCartToPickupInStoreUrl}" --%>
                    <%--                         method="post"> --%>
                    <%--                         <input type="hidden" name="storeNamePost" value="${pickupStore.name}" /> --%>
                    <%--                         <input type="hidden" name="productCodePost" value="${searchPageData.product.code}" /> --%>
                    <!--                         <input type="hidden" name="hiddenPickupQty" value="1" class="hiddenPickupQty" /> -->
                    <%--                         <button type="submit" class="btn btn-red pickUp-button"><spring:theme code="text.pickUpFromStore" /></button> --%>
                    <%--                     </form:form></div> --%>
                    <%--                 </c:otherwise> --%>
                    <%--             </c:choose> --%>
                    <%--         </c:if> --%>
                    <div class="text-center">
                        <form:form id="selectStoreForm" class="select_store_form" action="/cart/store-pickup"
                            method="post">
                            <input type="hidden" name="name" value/>
                            <input type="hidden" name="line1" value="${pickupStore.pointOfService.address.line1}" />
                            <input type="hidden" name="line2" value="${pickupStore.pointOfService.address.line2}" />
                            <input type="hidden" name="town" value="${pickupStore.pointOfService.address.town}" />
                            <input type="hidden" name="pickup" value="${true}"/>
                            <input type="hidden" name="country" value="IN"/>
                            <input type="hidden" name="postalcode" value="${pickupStore.pointOfService.address.postalcode}" />
                            <input type="hidden" name="phoneNo" value="${pickupStore.pointOfService.phoneNo}"/>
                            <input type="hidden" name="store" value="${pickupStore.pointOfService.name}"/>
                            <input type="hidden" name="region" value="${pickupStore.pointOfService.address.region.isocode}"/>
                            <%--                         <input type="hidden" name="entryNumber" value="${entryNumber}" /> --%>
                            <!--                         <input type="hidden" name="hiddenPickupQty" value="1" class="hiddenPickupQty" /> -->
                            <button type="submit" class="btn btn-red pickUp-button">
                                <spring:theme code="pickup.here.button" />
                            </button>
                        </form:form>
                    </div>

                </div>
            </div>
        </div>
        <%--         <c:set var="count" value="${count+1}"></c:set> --%>
    </c:forEach>
</ul>
