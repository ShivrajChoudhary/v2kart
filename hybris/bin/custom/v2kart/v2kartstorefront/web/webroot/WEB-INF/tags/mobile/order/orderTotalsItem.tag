<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.OrderData" %>
<%@ attribute name="hideHeading" required="false" type="java.lang.Boolean" %>
<%@ attribute name="containerCSS" required="false" type="java.lang.String" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>


<div class="orderTotalFullPusher" id="cartTotals" data-theme="b" data-role="content">
	<c:if test="${not hideHeading}">
		<h4 class="subItemHeader">
			<spring:theme code="text.account.order.orderTotals" text="Order Details" />
		</h4>
	</c:if>
<div class = "sidePadding15Px orderTotal-display">

	<div class="ui-grid-a" data-theme="b">
		<div class="ui-block-a">
			<spring:theme code="text.account.order.subtotal" text="Subtotal:" />
		</div>
		<div class="ui-block-b">
			<format:price priceData="${order.subTotal}" />
		</div>
	</div>
	
	<div class="ui-grid-a" data-theme="b">
		<div class="ui-block-a">
			<spring:theme code="text.account.order.delivery" text="Shipping charges:" />
		</div>
		<div class="ui-block-b">
			<format:price priceData="${order.deliveryCost}" displayFreeForZero="true" />
		</div>
	</div>
    <c:if test="${order.totalGiftWrapPrice.value ne null }">
        <div class="ui-grid-a" data-theme="b">
            <div class="ui-block-a">
                <spring:theme code="text.account.order.giftwrap" text="Gift Wrap:" />
            </div>
            <div class="ui-block-b">
                <format:price priceData="${order.totalGiftWrapPrice}" displayFreeForZero="true" />
            </div>
        </div>
    </c:if>
	
	<c:set var="codCharges" value="${order.codCharges}"/>
		
        <c:if test="${codCharges ne null && codCharges.value ne null && codCharges.value ne '0.0'}">
    		<div class="ui-grid-a" data-theme="b">
				<div class="ui-block-a">
					<spring:theme code="text.account.order.cod.charges"/>
				</div>
				<div class="ui-block-b">
					<format:price priceData="${codCharges}"/>
				</div>
			</div>
        </c:if>
        
	<c:if test="${order.net && order.totalTax.value > 0}" >
		<div class="ui-grid-a" data-theme="b">
			<div class="ui-block-a">
				<spring:theme code="text.account.order.netTax" text="Tax:" />
			</div>
			<div class="ui-block-b">
				<format:price priceData="${order.totalTax}" />
			</div>
		</div>
	</c:if>				
    
		<div class="ui-grid-a" data-theme="b">
			<div class="ui-block-a">
				<span style="font-weight: 600"><spring:theme code="text.account.order.total" text="Total:" /></span>
			</div>
			<div class="ui-block-b">
        		<c:choose>
                	<c:when test="${order.net}">
                    	<span style="font-weight: 600"><format:price priceData="${order.totalPriceWithTax}" /></span>
                	</c:when>
                	<c:otherwise>
                    	<span style="font-weight: 600"><format:price priceData="${order.totalPrice}" /></span>
                	</c:otherwise>
        		</c:choose>
        	</div>
        </div>
	
	 <c:if test="${order.totalDiscounts.value > 0}">
        <div class="ui-grid-a" data-theme="b">
            <div class="ui-block-a">
               <span style="font-weight: 600"> <spring:theme code="text.account.order.savings" text="Savings:"/></span>
            </div>
            <div class="ui-block-b">
              <span style="font-weight: 600">  <format:price priceData="${order.totalDiscounts}"/></span>
            </div>
        </div>
    </c:if>
</div>
</div>
