<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div id="popup_store_pickup_form" class="modal-content" style="display:none">
	<div class="modal-header">
<!--         <button type="button" class="close" data-dismiss="modal" aria-label="Close" href="javascript:void(0);" ><span aria-hidden="true">×</span></button> -->
     
      <spring:theme code="pickup.product.availability" />
      </div>
	<div class="modal-body">
<div class="row">

<!-- 	<div class="productInfo"> -->
    
<!-- 		<span class="thumb"></span> -->
<!--         <div class="product-desc"> -->
<!-- 		<p class="prod-title details"></p> -->
<!-- 		<p class="prod-price price"></p> -->
<!-- 		<div class="qty"> -->
<%-- 			<label data-for="pickupQty"><spring:theme code="basket.page.quantity" /></label> --%>
<!--             <br> -->
<!-- 			<input type="text" size="1" maxlength="3"  data-id="pickupQty" name="qty" class="text width50" /> -->
<!-- 		</div> -->
<!--         </div> -->
<!-- 	</div> -->

    <div class="col-md-9">
    <div class="row">
	<div class="col-md-12">
    <div class="pickup-form">
   
		            <form:form name="pickupInStoreForm" action="${actionUrl}" method="get" class="searchPOSForm clearfix form-inline">
							 <div class="form-group">  
								   <input type="text" name="locationQuery" data-id="locationForSearch" class="form-control" placeholder="<spring:theme code="pickup.search.message" />" />
							   </div>
<%-- 							<input type="hidden" name="cartPage" data-id="atCartPage" value="${cartPage}" /> --%>
<%-- 							<input type="hidden" name="entryNumber" value="${entryNumber}" class="entryNumber" /> --%>
					
					<button type="submit" class="btn btn-red" data-id="pickupstore_search_button"><spring:theme code="pickup.search.button" /></button>
                   <%--  <td><button type="submit" class="btn btn-red" data-id="find_pickupStoresNearMe_button"><spring:theme code="storeFinder.findStoresNearMe"/></button></td> --%>
			</form:form>
            </div>
            </div>
			<div data-id="pickup_store_results"></div>
		
        </div>
        </div>
        </div>
		</div>
</div>
