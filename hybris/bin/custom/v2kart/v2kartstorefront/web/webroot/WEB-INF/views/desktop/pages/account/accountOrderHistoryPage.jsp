<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/desktop/order"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb"%>
<%@ taglib prefix="action" tagdir="/WEB-INF/tags/desktop/action"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>

<div class="headline"><spring:theme code="text.account.orderHistory" text="Order History" /></div>
<c:choose>
    <c:when test="${currentTab eq 'accessibletabsnavigation0-6'}">
        <c:set var="searchUrl" value="/my-account/orders/recentOrders?sort=${searchPageData.pagination.sort}" />
    </c:when>
    <c:when test="${currentTab eq 'accessibletabsnavigation0-7'}">
        <c:set var="searchUrl" value="/my-account/orders/allOrders?sort=${searchPageData.pagination.sort}" />
    </c:when>
</c:choose>

<nav:pagination top="true" supportShowPaged="false" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}"
    searchUrl="${searchUrl}" msgKey="text.account.orderHistory.page" numberPagesShown="${numberPagesShown}" currentTab="${currentTab}" />

<div id="comTabs">
<ul class="clearfix tabs-list tabamount4">
    <li id="accessibletabsnavigation0-6" class="orderHistoryTab"><a id="accessibleanchor0-6"
        href="<c:url value="/my-account/orders/recentOrders"/>" class="${currentTab eq 'accessibletabsnavigation0-6' ? 'current' :''}">Recent
    Orders</a></li>
    <li id="accessibletabsnavigation0-7" class="orderHistoryTab"><a id="accessibleanchor0-7"
        href="<c:url value="/my-account/orders/allOrders"/>" id="tab-reviews"
        class="${currentTab eq 'accessibletabsnavigation0-7' ? 'current' :''}">All Orders</a></li>
</ul>
<c:set var="flag" value="0" />
<c:if test="${not empty searchPageData.results}">
    <div id="accountOrderHistory"><%--  <div class="description">
                <spring:theme code="text.account.orderHistory.viewOrders" text="View your orders" />
            </div> --%>
    <div class="orderList table-responsive">
    <table class="orderListTable" border="1">
        <c:forEach items="${searchPageData.results}" var="order">
            <thead>
                <tr style="width: 100%">
                    <th colspan="6">
                    <table style="width: 100%">
                        <tr>
                            <th id="header1"><spring:theme code="text.account.orderHistory.orderNumber" text="Order Number" />:
                            ${order.code}</th>
                            <th id="header2" style="text-align: right !important"><c:set var="orderCode" value="${order.code}"
                                scope="request" /> <action:actions element="div" parentComponent="${component}" /></th>
                        </tr>
                    </table>
                    </th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td class="orderhistoryheading"><spring:theme code="text.productDetails" text="Product Details" /></td>
                    <td class="orderhistoryheading"><span class="hidden"><spring:theme code="text.product" text="Product" /></span></td>

                    <td class="orderhistoryheading"><spring:theme code="text.itemPrice" text="Item Price" /></td>
                    <td class="orderhistoryheading"><spring:theme code="text.quantity" text="Quantity" /></td>

                    <td class="orderhistoryheading"><spring:theme code="text.total" text="Total" /></td>
                    <c:choose>
                        <c:when test="${fn:length(order.orderData.consignments) > 0}">
                            <td class="orderhistoryheading"><spring:theme code="text.trackinID" text="Tracking ID" /></td>
                        </c:when>
                        <c:otherwise>
                            <td class="orderhistoryheading"></td>
                        </c:otherwise>
                    </c:choose>
                </tr>
                <!-- For consigned entries -->
                <c:forEach items="${order.orderData.consignments}" var="consignment">

                    <c:forEach items="${consignment.entries}" var="entry">
                        <c:url value="${entry.orderEntry.product.url}" var="productUrl" />
                        <tr class="item">
                            <td class="product_image thumb"><a href="${productUrl}"> <product:productPrimaryImage
                                product="${entry.orderEntry.product}" format="thumbnail" /> </a></td>
                            <td headers="header2" class="product_details"><ycommerce:testId code="orderDetails_productName_link">
                                <a href="${entry.orderEntry.product.purchasable ? productUrl : ''}">${entry.orderEntry.product.name}</a>
                            </ycommerce:testId> <c:forEach items="${entry.orderEntry.product.baseOptions}" var="option">
                                <c:if test="${not empty option.selected and option.selected.url eq entry.orderEntry.product.url}">
                                    <c:forEach items="${option.selected.variantOptionQualifiers}" var="selectedOption">
                               <c:if test="${not empty selectedOption.value && selectedOption.value ne 'NA' && selectedOption.value ne 'N/A' && selectedOption.value ne 'A'}">
                               <div><strong>${selectedOption.name}:</strong> <span>${selectedOption.value}</span></div>
                                        </c:if>
                                    </c:forEach>
                                </c:if>
                            </c:forEach></td>

                            <td class="price"><ycommerce:testId code="orderDetails_productItemPrice_label">
                                <format:price priceData="${entry.orderEntry.basePrice}" displayFreeForZero="true" />
                            </ycommerce:testId></td>
                            <td class="quantity"><ycommerce:testId code="orderDetails_productQuantity_label">${entry.quantity}</ycommerce:testId>
                            </td>
                            <td class="total"><ycommerce:testId code="orderDetails_productItemPrice_label">
                                <format:price priceData="${entry.orderEntry.totalPrice}" displayFreeForZero="false" />
                            </ycommerce:testId></td>

                            <td><ycommerce:testId code="orderDetails_productItemPrice_label">
                                <c:choose>
                                    <c:when test="${consignment.lspProvider eq 'DTDC' }">
                                        <a target="_blank" href="${consignment.trackingUrl}">${consignment.trackingID}</a>
                                    </c:when>
                                    <c:when test="${consignment.trackingID eq '0000'}">
                                        ${consignment.trackingID}<br>
                                        <c:set var="flag" value="${flag + 1 }" />
                                        <a href="#" data-toggle="modal" class="order_history_view" data-target="#tracking_id_${flag}">View More</a>
                                        <div class="modal fade order_history_popup small-modal" id="tracking_id_${flag}" role="dialog"
                                            aria-labelledby="myModalLabel" aria-hidden="true">
                                        <div class="modal-dialog">
                                        <div class="modal-content">
                                        <div class="modal-header">
                                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                                            aria-hidden="true">×</span></button>
                                        <h4 class="modal-title">Order Details</h4>
                                        </div>
                                        <div class="modal-body">
                                        
                                            <p style="color:#000!important;line-height: 20px;  font-size: 13px;"><spring:theme code="text.status.pickup.popupstatus"  /></p>
                                        </div>
                                        <div class="modal-footer">
                                        <span class="pull-left"><spring:theme code="text.status" text="Status" /> : ${consignment.status.code }</span>
                                        </div>
                                        
                                        <!-- /.modal-content --></div>
                                        <!-- /.modal-dialog --></div>
                                        </div>

                                    </c:when>
                                    
                                    <c:otherwise>
                                        ${consignment.trackingID}<br>
                                        <c:set var="flag" value="${flag + 1 }" />
                                        <a href="#" data-toggle="modal" class="order_history_view" data-target="#tracking_id_${flag}">View More</a>
                                        <div class="modal fade order_history_popup small-modal" id="tracking_id_${flag}" role="dialog"
                                            aria-labelledby="myModalLabel" aria-hidden="true">
                                        <div class="modal-dialog">
                                        <div class="modal-content">
                                        <div class="modal-header">
                                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                                            aria-hidden="true">×</span></button>
                                        <h4 class="modal-title">Order Details</h4>
                                        </div>
                                        <div class="modal-body">
                                        <table class="tracking_details">
                                            <tr>
                                                <th><spring:theme code="text.trackinID" text="Tracking ID" /></th>
                                                <td>${consignment.trackingID}</td>
                                            </tr>
                                            <tr>
                                                <th><spring:theme code="text.status" text="Status" /></th>
                                                <td>${consignment.status.code }</td>
                                            </tr>
                                            <tr>
                                                <th><spring:theme code="text.Location" text="Location" /></th>
                                                <td>${consignment.currentLocation}</td>
                                            </tr>
                                            <tr>
                                                <th><spring:theme code="text.lspProvider" text="LSP Provider" /></th>
                                                <td>${consignment.lspProvider}</td>
                                       <c:choose>
                                                 <c:when test="${consignment.lspProvider.equalsIgnoreCase('Aramex') }">
              									<tr><td ><a class="button"  href="http://www.aramex.com/track-results-multiple.aspx?ShipmentNumber=${consignment.trackingID}" target="_blank"">Track More</a></td></tr>
            									 </c:when>
                                                <c:when test="${consignment.lspProvider.equalsIgnoreCase('Delhivery')}">
                                                   <tr><td><a class="button" href="https://track.delhivery.com/p/${consignment.trackingID}" target="_blank"">Track More</a></td></tr>
                                                </c:when>
                                                <c:when test="${consignment.lspProvider.equalsIgnoreCase('Dotzot')}">
                                                          <tr><td><a class="button" href="http://dotzot.azurewebsites.net/GUI/Tracking_New/WebSite/TrackConsignment_new.Aspx?track_flag=A&CONSIGNMENT=${consignment.trackingID}"  target="_blank"">Track More</a></td></tr>
             									</c:when>
                                                <c:when test="${consignment.lspProvider.equalsIgnoreCase('DTDC')}">
                                                      <tr><td><a class="button" href="http://dtdc.in/tracking/tracking.asp"   target="_blank"">Track More</a></td></tr>
             									</c:when>
                                                <c:when test="${consignment.lspProvider.equalsIgnoreCase('Ecom express')}">
                                                     <tr><td><a class="button" href="https://billing.ecomexpress.in/track_me/multipleawb_open/?awb=${consignment.trackingID}&order=&news_go=track+now" ${consignment.lspProvider} target="_blank"">Track More</a></td></tr>
                                                </c:when>
                                                <c:otherwise>
                                               </c:otherwise>
                                                </c:choose>
                                            </tr>
                                        </table>
                                        </div>
                                        
                                        <!-- /.modal-content --></div>
                                        <!-- /.modal-dialog --></div>
                                        </div>

                                    </c:otherwise>
                                </c:choose>

                            </ycommerce:testId></td>
                        </tr>

                    </c:forEach>
                </c:forEach>

                <!-- For unconsigned entries -->
                <c:forEach items="${order.orderData.unconsignedEntries}" var="entry">
                    <c:url value="${entry.product.url}" var="productUrl" />
                    <tr class="item">
                        <td headers="header2" class="thumb productImage"><a href="${productUrl}"> <product:productPrimaryImage
                            product="${entry.product}" format="thumbnail" /> </a></td>
                        <td headers="header2" class="product_details"><ycommerce:testId code="orderDetails_productName_link">
                            <div class="itemName"><a href="${entry.product.purchasable ? productUrl : ''}">${entry.product.name}</a></div>
                        </ycommerce:testId> <c:forEach items="${entry.product.baseOptions}" var="option">
                            <c:if test="${not empty option.selected and option.selected.url eq entry.product.url}">
                                <c:forEach items="${option.selected.variantOptionQualifiers}" var="selectedOption">
                                            <c:if test="${not empty selectedOption.value && selectedOption.value ne 'NA' && selectedOption.value ne 'N/A' && selectedOption.value ne 'A'}">
                                        <div><strong>${selectedOption.name}:</strong> <span>${selectedOption.value}</span></div>
                                    </c:if>
                                </c:forEach>
                            </c:if>
                        </c:forEach></td>
                        <td headers="header5" class="price"><ycommerce:testId code="orderDetails_productItemPrice_label">
                            <format:price priceData="${entry.basePrice}" displayFreeForZero="true" />
                        </ycommerce:testId></td>
                        <td headers="header4" class="quantity"><ycommerce:testId code="orderDetails_productQuantity_label">${entry.quantity}</ycommerce:testId></td>

                        <td headers="header6" class="total" ><ycommerce:testId code="orderDetails_productTotalPrice_label">
                            <format:price priceData="${entry.totalPrice}" displayFreeForZero="false" />
                        </ycommerce:testId></td>
                        <td></td>
                    </tr>

                </c:forEach>
                <!--                         <tr> -->
                <%--                             <td headers="header1"><ycommerce:testId code="orderHistory_orderNumber_link"> --%>
                <%--                            ${order.code} --%>
                <%--                        </ycommerce:testId>;</td> --%>
                <%--                             <td headers="header2"><ycommerce:testId code="orderHistory_orderStatus_label"> --%>
                <!--                                     <p> -->
                <%--                                         <spring:theme code="text.account.order.status.display.${order.statusDisplay}" /> --%>
                <!--                                     </p> -->
                <%--                                 </ycommerce:testId></td> --%>
                <%--                             <td headers="header3"><ycommerce:testId code="orderHistory_orderDate_label"> --%>
                <!--                                     <p> -->
                <%--                                         <fmt:formatDate value="${order.placed}" dateStyle="long" timeStyle="short" type="both" /> --%>
                <!--                                     </p> -->
                <%--                                 </ycommerce:testId></td> --%>
                <%--                             <td headers="header4" class="orderHistoryTotal"><ycommerce:testId code="orderHistory_Total_links"> --%>
                <%--                                     <p>${order.total.formattedValue}</p> --%>
                <%--                                 </ycommerce:testId></td> --%>

                <%--                             <td headers="header5"><c:set var="orderCode" value="${order.code}" scope="request" /> <action:actions --%>
                <%--                                     element="div" parentComponent="${component}" /></td> --%>
                <!-- <!--                             <td headers="header6"> -->
                <!-- <!--                                 <table> -->
                <!-- <!--                                     <tbody> -->
                <%-- <%--                                         <c:forEach items="${order.orderData.consignments}" var="consignment"> --%>
                <%-- <%--                                             <order:v2OrderConsignmentEntries order="${order}" consignment="${consignment}" />  --%>

                <%-- <%--                                        <tr> <td>${consignment.trackingID}</td></tr> --%>
                <%-- <%--                                         </c:forEach> --%>
                <!-- <!--                                     </tbody> -->
                <!-- <!--                                 </table> -->
                <!-- <!--                             </td> -->
                <!--                         </tr> -->




            </tbody>
        </c:forEach>
    </table>
    </div>
    </div></div>
</c:if>
<c:if test="${empty searchPageData.results}">
    <br />
    <p><spring:theme code="text.account.orderHistory.noOrders" text="You have no orders" /></p>
</c:if>
</div>