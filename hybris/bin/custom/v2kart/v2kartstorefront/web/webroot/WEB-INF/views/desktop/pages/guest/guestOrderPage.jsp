


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

<template:page pageTitle="${pageTitle}">
    <div id="globalMessages">
        <common:globalMessages />
    </div>
    <div class="guestOrderContent span-20 last" style="margin-left: 12.5%;">
        <div class="headline">
            <spring:theme code="text.account.order.title.details" />
        </div>
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
                    
                     <c:forEach items="${orderData.consignments}" var="consignment">
                   
                     <c:if test="${consignment.trackingID eq '0000'}">
                     
                      <spring:theme code="text.status" text="Status" /> : ${consignment.status.code }</span><br/>
                     <spring:theme code="text.status.pickup.popupstatus"  />  <br />
                                       
                    <br />
                        </c:if>          

                    </c:forEach>
                    <c:if test="${orderData.isCancelable}">
                        <button type="button" class="btn btn-red" onclick="cancelOrder()">Cancel Order</button>
                    </c:if>
                    <!--popover start-->
                    <div class="popover WL medium-box right" style="display: none;" id="orderCancelResultpopover">
                        <div class="popover-inner">
                            <a class="popover-close orderCancelResultpopoverClose" href="javascript:void(0);">×</a>
                            <div class="popover-content">
                                <span class="green-link bold" id="orderCancelMsg"><spring:theme code="order.cancel.text"
                                        arguments="${orderCancelCustomerCareNumber}" /></span>
                            </div>
                        </div>
                    </div>
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
        <div id="order-pg">
            <div class="order-detailscntr" id="defaultAddress">
                <ul class="order-info">
                    <li><order:deliveryAddressItem order="${orderData}" /></li>
                    <li class="nextAddress"><order:deliveryMethodItem order="${orderData}" /></li>
                    <!-- <div class="orderBox billing"> -->
                    <c:if test="${not empty orderData.paymentMode}">
                        <li><order:billingAddressItem order="${orderData}" /></li>
                        <!-- </div> -->


                        <li class="nextAddress"><order:paymentDetailsItem order="${orderData}" /></li>

                    </c:if>
                </ul>
            </div>
        </div>
        <div id="accountOrderDetailsPage">
            <c:if test="${not empty orderData.unconsignedEntries}">
                <order:orderUnconsignedEntries order="${orderData}" />
            </c:if>

            <c:set var="headingWasShown" value="false" />
            <c:forEach items="${orderData.consignments}" var="consignment">
                <c:if
                    test="${consignment.status.code eq 'WAITING' or consignment.status.code eq 'PICKPACK' or consignment.status.code eq 'READY'}">
                    <c:if test="${not headingWasShown}">
                        <c:set var="headingWasShown" value="true" />
                        <h2>
                            <spring:theme code="text.account.order.title.inProgressItems" />
                        </h2>
                    </c:if>
                    <div class="productItemListHolder fulfilment-states-${consignment.status.code}">
                        <order:accountOrderDetailsItem order="${orderData}" consignment="${consignment}" inProgress="true" />
                    </div>
                </c:if>
            </c:forEach>

            <c:forEach items="${orderData.consignments}" var="consignment">
                <c:if
                    test="${consignment.status.code ne 'WAITING' and consignment.status.code ne 'PICKPACK' and consignment.status.code ne 'READY'}">
                    <div class="productItemListHolder fulfilment-states-${consignment.status.code}">
                        <order:accountOrderDetailsItem order="${orderData}" consignment="${consignment}" />
                    </div>
                </c:if>
            </c:forEach>
        </div>
    </div>


</template:page>
