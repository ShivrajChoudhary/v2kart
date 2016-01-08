<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.OrderData"%>
<%@ attribute name="consignment" required="true" type="de.hybris.platform.commercefacades.order.data.ConsignmentData"%>
<%@ attribute name="inProgress" required="false" type="java.lang.Boolean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>

<c:choose>
    <c:when test="${not inProgress}">
        <c:if test="${consignment.status.code eq 'READY_FOR_PICKUP'}">
            <h2>
                <spring:theme code="text.account.order.warning.storePickUpItems" text="Reminder - Please pick up your items(s) soon." />
            </h2>
        </c:if>
    </c:when>
</c:choose>


<div class="orderList">
    <c:choose>
        <c:when test="${consignment.deliveryPointOfService ne null}">
            <div class="headline">
                <spring:theme code="text.account.order.title.storePickUpItems" />
            </div>
        </c:when>
        <c:otherwise>
            <div class="headline">
                <spring:theme code="text.account.order.title.details" />
            </div>
        </c:otherwise>
    </c:choose>

<!--     <div class="description"> -->
<%--         <c:choose> --%>
<%--             <c:when test="${not inProgress}"> --%>
<%--                 <strong><spring:theme code="text.account.order.consignment.status.${consignment.statusDisplay}" />:</strong> --%>
<%--                 <fmt:formatDate value="${consignment.statusDate}" pattern="MM/dd/yy" /> --%>
<%--                 <c:choose> --%>
<%--                     <c:when test="${consignment.status.code eq 'SHIPPED'}"> --%>
<%--                         <strong> <spring:theme code="text.account.order.tracking" text="Tracking #:" /> --%>
<!--                         </strong> -->
<%--                         <c:choose> --%>
<%--                             <c:when test="${not empty consignment.trackingID}">${consignment.trackingID}</c:when> --%>
<%--                             <c:otherwise> --%>
<%--                                 <spring:theme code="text.account.order.consignment.trackingID.notavailable" text="Not available." /> --%>
<%--                             </c:otherwise> --%>
<%--                         </c:choose> --%>

<%--                     </c:when> --%>
<%--                 </c:choose> --%>

<%--             </c:when> --%>
<%--             <c:otherwise> --%>

<%--                 <c:choose> --%>
<%--                     <c:when test="${consignment.deliveryPointOfService ne null}"> --%>
<!--                         <h3> -->
<%--                             <spring:theme code="text.account.order.title.storePickUpItems" /> --%>
<!--                         </h3> -->
<%--                     </c:when> --%>
<%--                     <c:otherwise> --%>
<!--                         <h3> -->
<%--                             <spring:theme code="text.account.order.title.deliveryItems" /> --%>
<!--                             dddd -->
<!--                         </h3> -->
<%--                     </c:otherwise> --%>
<%--                 </c:choose> --%>
<%--             </c:otherwise> --%>
<%--         </c:choose> --%>


<%--         <c:choose> --%>
<%--             <c:when test="${consignment.status.code eq 'READY_FOR_PICKUP'}"> --%>
<%--                 <c:set var="pos" value="${consignment.entries[0].orderEntry.deliveryPointOfService}" /> --%>
<%--                 <c:url value="/store/${pos.name}" var="posUrl" /> --%>
<%--                 <strong> <spring:theme code="text.account.order.pickup.location" text="Pick Up Location:" /> --%>
<!--                 </strong> -->
<%--                 <a href="${posUrl}">${pos.name}</a> --%>
<%--             </c:when> --%>
<%--         </c:choose> --%>


<!--     </div> -->





    <table class="orderListTable">
        <thead>
            <tr>
                <th id="header2" colspan="2"><spring:theme code="text.productDetails" text="Product Details"/></th>
                <th id="header4"><spring:theme code="text.quantity" text="Quantity" /></th>
                <th id="header5"><spring:theme code="text.itemPrice" text="Item Price" /></th>
                <th id="header6"><spring:theme code="text.total" text="Total" /></th>
                <th id="header7"><spring:theme code="text.status" text="Status" /></th>
                <th id="header9"><spring:theme code="text.trackingID" text="Tracking ID" /> / <spring:theme code="text.lspProvider" text="LSP Provider" /></th>
            </tr>
        </thead>

        <c:forEach items="${consignment.entries}" var="entry">
            <c:url value="${entry.orderEntry.product.url}" var="productUrl" />
            <tr class="item">
                <td headers="header1" class="product_image thumb" rowspan="2"><a href="${productUrl}"> <product:productPrimaryImage
                            product="${entry.orderEntry.product}" format="thumbnail" />
                </a></td>
                <td headers="header2" class="product_details"><ycommerce:testId code="orderDetails_productName_link">
                        <a class="capitalize" href="${entry.orderEntry.product.purchasable ? productUrl : ''}">${entry.orderEntry.product.name}</a>
                    </ycommerce:testId> <c:forEach items="${entry.orderEntry.product.baseOptions}" var="option">
                        <c:if test="${not empty option.selected and option.selected.url eq entry.orderEntry.product.url}">
                            <c:forEach items="${option.selected.variantOptionQualifiers}" var="selectedOption">
                                    <c:if test="${not empty selectedOption.value && selectedOption.value ne 'NA' && selectedOption.value ne 'N/A' && selectedOption.value ne 'A'}">
                                        <div>
                                        <strong>${selectedOption.name}:</strong> <span>${selectedOption.value}</span>
                                    </div>
                                </c:if>
                            </c:forEach>
                        </c:if>
                    </c:forEach> <%-- <c:if test="${not empty order.appliedProductPromotions}">
                        <ul>
                            <c:forEach items="${order.appliedProductPromotions}" var="promotion">
                                <c:set var="displayed" value="false" />
                                <c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
                                    <c:if test="${not displayed && consumedEntry.orderEntryNumber == entry.orderEntry.entryNumber}">
                                        <c:set var="displayed" value="true" />
                                        <li><span>${promotion.description}</span></li>
                                    </c:if>
                                </c:forEach>
                            </c:forEach>
                        </ul>
                    </c:if> --%></td>
                <td headers="header4" class="quantity"><ycommerce:testId code="orderDetails_productQuantity_label">${entry.quantity}</ycommerce:testId>
                </td>
                <td headers="header5"><ycommerce:testId code="orderDetails_productItemPrice_label">
                        <format:price priceData="${entry.orderEntry.basePrice}" displayFreeForZero="true" />
                    </ycommerce:testId></td>
                <td headers="header6" class="total">
                    <ycommerce:testId code="orderDetails_productItemPrice_label">
                        <format:price priceData="${entry.orderEntry.totalPrice}" displayFreeForZero="false" />
                    </ycommerce:testId>
                
                
                </td>
                <td headers="header7">
                <ycommerce:testId code="orderDetails_productItemPrice_label">
                       ${consignment.statusDisplay}
                    </ycommerce:testId>
                
                </td>
                <td headers="header9">
                 <ycommerce:testId code="orderDetails_productItemPrice_label">
                        ${consignment.trackingID} / ${consignment.lspProvider}
                    </ycommerce:testId>
                
                </td>
<!--                 <td headers="header9"> -->
<%--                  <ycommerce:testId code="orderDetails_productItemPrice_label"> --%>
<%--                         ${consignment.lspProvider} --%>
<%--                     </ycommerce:testId> --%>
                
<!--                 </td> -->
            </tr>
            <tr id="promotions">
                    <c:if test="${not empty order.appliedProductPromotions }">
                        <td headers="header2" class="details" colspan="8">
                           <c:set var="promotionApplied" value="false" />
                           <c:forEach items="${order.appliedProductPromotions}" var="promotion">
                                    <c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
                                        <c:if test="${not promotionApplied && consumedEntry.orderEntryNumber == entry.orderEntry.entryNumber}">
                                            <c:set var="promotionApplied" value="true" />
                                        </c:if>
                                    </c:forEach>
                           </c:forEach>
                           <div>
                                <c:if test="${promotionApplied == 'true'}">
                                    <strong><spring:theme code="text.account.order.appliedOffers" text="Applied Offers:" /></strong>
                                </c:if>
                            </div>
                            <ul>
                                <c:forEach items="${order.appliedProductPromotions}" var="promotion">
                                    <c:set var="displayed" value="false" />
                                    <c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
                                        <c:if test="${not displayed && consumedEntry.orderEntryNumber == entry.orderEntry.entryNumber}">
                                            <c:set var="displayed" value="true" />
                                            <li><span>${promotion.description}</span></li>
                                        </c:if>
                                    </c:forEach>
                                </c:forEach>
                            </ul>
                        
                        </td>
                    </c:if>
            </tr>
        </c:forEach>

        </tbody>
    </table>
</div>
