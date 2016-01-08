<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav"%>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/desktop/order"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>


<div class="accountOrderDetailOrderTotals clearfix">
    <div class="row">
        <div class="col-md-7">
            <spring:theme code="text.account.order.orderNumber" arguments="${orderData.code}" />
            <br />
            <spring:theme code="text.account.order.orderPlaced" arguments="${orderData.created}" />
            <br />
            <c:if test="${not empty orderData.statusDisplay}">
                <spring:theme code="text.account.order.status.display.${orderData.statusDisplay}" var="orderStatus" />
                <spring:theme code="text.account.order.orderStatus" arguments="${orderStatus}" />
                <br />
            </c:if>
            <c:url value="/my-account/order/${orderData.code }/cancel" var="orderCancelUrl"/>
            
            <c:if test="${orderData.isCancelable}">
          
                <div class="marT20">
				 <a href="javascript:void(0)" data-url="${orderCancelUrl }" class="btn btn-red cancel-order" >
					Cancel Order
				</a> 
			</div>
            </c:if>
            <c:url value="/my-account/order/${orderData.code }/return" var="orderReturnUrl"/>
            <c:if test="${orderData.isReturnable}">
          
                <div class="marT20">
				 <a href="javascript:void(0)" data-url="${orderReturnUrl }" class="btn btn-red return-order" >
					Return Order
				</a> 
			</div>
            </c:if>
            
            
            
            
            <!--popover start-->
            <%-- <div class="popover WL medium-box right" style="display: none;" id="orderCancelResultpopover">
                <div class="popover-inner">
                    <a class="popover-close orderCancelResultpopoverClose" href="javascript:void(0);">×</a>
                    <div class="popover-content">
                        <span class="green-link bold" id="orderCancelMsg"><spring:theme code="order.cancel.text" arguments="${orderCancelCustomerCareNumber}"/></span>
                    </div>
                </div>
            </div> --%>
            <!--popover end-->
        </div>
        <div class="col-sm-5 last order-totals">
            <div class="value-order">
                <c:if test="${not empty orderData.appliedOrderPromotions}">
                    <order:receivedPromotions order="${orderData}" />
                  &nbsp;
              </c:if>
                <order:orderTotalsItem order="${orderData}" />
            </div>
        </div>
    </div>
</div>




