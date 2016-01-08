<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav"%>
<c:choose>
    <c:when test="${currentTab eq 'accessibleTab_recentOrders'}">
        <c:set var="searchUrl" value="/my-account/orders/recentOrders?sort=${searchPageData.pagination.sort}" />
    </c:when>
    <c:when test="${currentTab eq 'accessibleTab_allOrders'}">
        <c:set var="searchUrl" value="/my-account/orders/allOrders?sort=${searchPageData.pagination.sort}" />
    </c:when>
</c:choose>
<div class="orderHistoryPage">
    <div class="heading">
        <h1>
            <spring:theme code="text.account.orderHistory.mobile.page" text="My Orders" />
        </h1>
        <p class="fontsmall">
            <spring:theme code="text.account.orderHistory.viewOrders" text="View your order history" />
        </p>
    </div>
    <div data-theme="c" class="innerContent">
        <%-- <h1 class="descriptionHeadline"><spring:theme code="text.headline.orders" text="View your orders"/></h1>--%>
        <nav:pagination top="true" supportShowPaged="false" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}"
            searchUrl="${searchUrl}" msgKey="text.account.orderHistory.mobile.page" numberPagesShown="${numberPagesShown}" />
        <ul class="orderType clearfix">
            <li id="accessibletabsnavigation0-6" class="orderHistoryTab"><a href="<c:url value="/my-account/orders/recentOrders" />"
                class="${currentTab eq 'accessibletabsnavigation0-6' ? 'current' :''}">Recent Orders</a></li>
            <li id="accessibletabsnavigation0-7" class="orderHistoryTab"><a href="<c:url value="/my-account/orders/allOrders" />"
                class="${currentTab eq 'accessibletabsnavigation0-7' ? 'current' :''}">All Orders</a></li>
        </ul>
        <c:if test="${not empty searchPageData.results}">
            <table class="orderListTable">
                <thead>
                    <tr class="table_header">
                        <th id="header1"><spring:theme code="text.account.orderHistory.mobile.page.orderRef" text="Order Ref." /></th>
                        <th id="header2"><spring:theme code="text.account.orderHistory.mobile.page.orderStatus" text="Status" /></th>
                        <th id="header3" colspan="2"><spring:theme code="text.account.orderHistory.mobile.page.orderTrackingID"
                                text="Tracking ID" /></th>
                    </tr>
                </thead>
                <c:forEach items="${searchPageData.results}" var="order">
                    <!-- For consigned entries -->
                    <c:choose>
                        <c:when test="${fn:length(order.orderData.consignments) > 0}">
                            <c:forEach items="${order.orderData.consignments}" var="consignment">
                                <tr class="item">
                                    <td>${order.code}<br> <fmt:formatDate value="${order.placed}" pattern="dd/MM/yy" type="date" />
                                    </td>
                                    <td>
                                    	<spring:theme code="text.account.order.status.display.${order.statusDisplay}" />
                                    	<br>
                                    	<a href="javascript:void(0)" class="order_history_view" 
	                                    	data-heading="Order Details"
	                            			data-content= '<div class="innerContent orderpopup">
	                            			<c:if test="${consignment.trackingID == '0000'}">
                                            <label>
                                            <span class="gestOrder">*</span>
                                            <span><spring:theme code="text.gestOrder"/> </span>
                                            </label>
                                             </c:if>
                                            <div> <spring:theme code="text.Location" text="Location" /> : <span class="orderpopupvalue">${consignment.currentLocation}</span> </div>
                                            <div> <spring:theme code="text.lspProvider" text="LSP Provider" />: <span class="orderpopupvalue">${consignment.lspProvider}</span></div>
                                            <div> <spring:theme code="text.trackinID" text="Tracking ID" /> : <span class="orderpopupvalue">${consignment.trackingID}</span></div>
                                            <div> <spring:theme code="text.status" text="Status" /> : <span class="orderpopupvalue">${consignment.status.code}</span></div>
                                            </div>'>
	                            			View more...</a>
                                    </td>
                                    <td class="item_trackingID"><c:choose>
                                            <c:when test="${consignment.lspProvider eq 'DTDC' }">
                                                <a target="_blank" href="${consignment.trackingUrl}">${consignment.trackingID}</a>
                                            </c:when>
                                            <c:otherwise>
                                        ${consignment.trackingID}
                                    </c:otherwise>
                                        </c:choose></td>
                                    <td class="order-arrow"><a href="/my-account/order/${order.code}"><span id="order-arrow-r">&nbsp;</span></a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <!-- For unconsigned entries -->
                            <tr class="item">
                                <td>${order.code}<br> <fmt:formatDate value="${order.placed}" pattern="dd/MM/yy" type="date" /> <!-- dateStyle="short" timeStyle="short" -->
                                </td>
                                <td><spring:theme code="text.account.order.status.display.${order.statusDisplay}" /></td>
                                <td class="item_trackingID"></td>
                                <td class="order-arrow"><a href="/my-account/order/${order.code}"><span id="order-arrow-r">&nbsp;</span></a>
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </table>
            <div class="clear"></div>
            <%-- <ul data-role="listview" data-inset="true" data-theme="c" data-dividertheme="d">
				<c:forEach items="${searchPageData.results}" var="order">
					<li class="orderListItem"><c:url value="/my-account/order/${order.code}" var="myAccountOrderDetailsUrl" />
					<a class="orderLink" href="${myAccountOrderDetailsUrl}">
							<div class='ui-grid-a'>
								<div class='ui-block-a width40'>${order.code}</div>
								<div class='ui-block-b width60'>
									<c:if test="${not empty consignment.trackingID}">
										<div class="fontsmall">
											<spring:theme code="text.trackinID" text="Tracking ID" />
											:&nbsp; ${consignment.trackingID}
										</div>
									</c:if>
								</div>
							</div>
							<div class='ui-grid-a'>
								<div class='ui-block-a width60'>
									<p class="fontsmall">
										<fmt:formatDate value="${order.placed}" dateStyle="long" timeStyle="short" type="both" />
									</p>
								</div>
								<div class='ui-block-b width40'>
									<p class="fontsmall">
										<spring:theme code="order" />
										&nbsp;
										<spring:theme code="text.account.order.status.display.${order.statusDisplay}" />
									</p>
								</div>
							</div>
					</a></li>
				</c:forEach>
			</ul>
			<nav:pagination top="true" supportShowPaged="false" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" 
			searchUrl="${searchUrl}" msgKey="text.account.orderHistory.mobile.page" numberPagesShown="${numberPagesShown}" />--%>
        </c:if>
        <c:if test="${empty searchPageData.results}">
            <p class="emptyMessage">
                <spring:theme code="text.account.orderHistory.noOrders" text="You have no orders" />
            </p>
        </c:if>
    </div>
</div>
