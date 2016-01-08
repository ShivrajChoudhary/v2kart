<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.OrderData"%>
<%@ attribute name="orderGroup" required="true" type="de.hybris.platform.commercefacades.order.data.OrderEntryGroupData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>


	<c:forEach items="${orderGroup.entries}" var="entry">
    <c:if test="${entry.quantity ne 0}">
	<c:url value="${entry.product.url}" var="productUrl"/>
		<div class="cartLi" style="width: 100%">
			<div class="ui-grid-a cartItemproductImage">
				<div class="ui-block-a width40">
				     <a href="${productUrl}">
						<product:productPrimaryImage product="${entry.product}" format="thumbnail" zoomable="false" />
					</a>
				</div>
				<div class="ui-block-b width60">
					<ycommerce:testId code="cart_product_name">
						<div class="ui-grid-a cart_product_name">
							<ycommerce:testId code="orderDetails_productName_link">
								<a href="${entry.product.purchasable ? productUrl : ''}">${entry.product.name}</a>
							</ycommerce:testId>
						</div>
					</ycommerce:testId>
					<c:forEach items="${entry.product.baseOptions}" var="option">
						<c:if test="${not empty option.selected and option.selected.url eq entry.product.url}">
							<c:forEach items="${option.selected.variantOptionQualifiers}" var="selectedOption">
								<c:if test="${not empty selectedOption.name and  not empty selectedOption.value}">
									<div class="ui-grid-a">
										<div class="ui-block-a width40">${selectedOption.name}:</div>
										<div class="ui-block-b width60">${selectedOption.value}</div>
									</div>
								</c:if>
							</c:forEach>
						</c:if>
					</c:forEach>
					<div class="ui-grid-a">
						<div class="ui-block-a width40">
							<spring:theme code="basket.page.quantity" />:
						</div>
						<div class="ui-block-b width60">${entry.quantity}</div>
					</div>
					<div class="ui-grid-a">
						<div class="ui-block-a width40">
							<spring:theme code="basket.page.itemPrice" />:
						</div>
						<div class="ui-block-b width60">
							<format:price priceData="${entry.basePrice}" displayFreeForZero="true" />
						</div>
					</div>
					<div class="ui-grid-a">
						<div class="ui-block-a width40">
							<spring:theme code="basket.page.total" />:
						</div>
						<div class="ui-block-b width60">
							<format:price priceData="${entry.totalPrice}" displayFreeForZero="true" />
						</div>
					</div>
					<c:set var="promotionApplied" value="false" />
                    <c:forEach items="${order.appliedProductPromotions}" var="promotion">
                        <c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
                            <c:if test="${not promotionApplied && consumedEntry.orderEntryNumber == entry.entryNumber}">
                                <c:set var="promotionApplied" value="true" />
                            </c:if>
                        </c:forEach>
                    </c:forEach>
                    <c:if test="${not empty order.appliedProductPromotions}">
					<div class="ui-grid-a">
						<div class="ui-block-a width40">
							<c:if test="${ promotionApplied == 'true'}">
								<spring:theme code="text.account.order.appliedOffers"
									text="Applied Offers:" />
							</c:if>
						</div>
						<div class="ui-block-b width60">
							<c:if test="${not empty order.appliedProductPromotions}">
								<c:forEach items="${order.appliedProductPromotions}"
									var="promotion">
									<c:set var="displayed" value="false" />
									<c:forEach items="${promotion.consumedEntries}"
										var="consumedEntry">
										<c:if
											test="${not displayed && consumedEntry.orderEntryNumber == entry.entryNumber}">
											<c:set var="displayed" value="true" />
											<span>${promotion.description}</span>
										</c:if>
									</c:forEach>
								</c:forEach>
							</c:if>
						</div>
					</div>
				</c:if>
				</div>
			</div>
		</div>
        </c:if>
	</c:forEach>
