<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@ attribute name="product" required="true"
	type="de.hybris.platform.commercefacades.product.data.ProductData"%>


<c:set var="showAddToCart" value="" scope="session" />
<div class="variant_options">
	<%-- Determine if product is one of V2kart style or size variant --%>
	<c:if test="${product.variantType eq 'V2kartStyleVariantProduct'}">
		<c:set var="variantStyles" value="${product.variantOptions}" />
	</c:if>
	<c:if
		test="${(not empty product.baseOptions[0].options) and (product.baseOptions[0].variantType eq 'V2kartStyleVariantProduct')}">
		<c:set var="variantStyles" value="${product.baseOptions[0].options}" />
		<c:set var="variantSizes" value="${product.variantOptions}" />
		<c:set var="currentStyleUrl" value="${product.url}" />
	</c:if>
	<c:if
		test="${(not empty product.baseOptions[1].options) and (product.baseOptions[0].variantType eq 'V2kartSizeVariantProduct')}">
		<c:set var="variantStyles" value="${product.baseOptions[1].options}" />
		<c:set var="variantSizes" value="${product.baseOptions[0].options}" />
		<c:set var="currentStyleUrl"
			value="${product.baseOptions[1].selected.url}" />
	</c:if>
	<c:url value="${currentStyleUrl}" var="currentStyledProductUrl" />
	<%-- Determine if product is other variant --%>
	<c:if test="${empty variantStyles}">
		<c:if test="${not empty product.variantOptions}">
			<c:set var="variantOptions" value="${product.variantOptions}" />
		</c:if>
		<c:if test="${not empty product.baseOptions[0].options}">
			<c:set var="variantOptions" value="${product.baseOptions[0].options}" />
		</c:if>
	</c:if>

	<c:if test="${ not empty variantSizes}">
		<c:choose>
			<c:when
				test="${product.purchasable and product.stock.stockLevelStatus.code ne 'outOfStock' }">
				<c:set var="showAddToCart" value="${true}" scope="session" />
			</c:when>
			<c:otherwise>
				<c:set var="showAddToCart" value="${false}" scope="session" />
			</c:otherwise>
		</c:choose>
		<div class="variant_options">

			<c:if test="${not empty variantSizes}">
				<div class="size clearfix">
					<form>
						<!-- disabled="disabled" -->
						<c:if test="${not empty variantSizes}">
							<c:set var="isSizeAvailable" value="false" />
							<c:forEach items="${variantSizes}" var="variantSize">
								<c:set var="optionsString" value="" />
								<c:forEach items="${variantSize.variantOptionQualifiers}"
									var="variantOptionQualifier">
									<c:if test="${variantOptionQualifier.qualifier eq 'size'}">
										<%--                                             <c:set var="optionsString">${optionsString}&nbsp;${variantOptionQualifier.name}&nbsp;${variantOptionQualifier.value}, </c:set> --%>
										<c:set var="optionsString">${variantOptionQualifier.value} </c:set>
										<c:if
											test="${(fn:toUpperCase(optionsString) ne 'NA') && (fn:toUpperCase(optionsString) ne 'N/A') && (fn:toUpperCase(optionsString) ne 'A')}">
											<c:set var="isSizeAvailable" value="true" />
										</c:if>
									</c:if>
								</c:forEach>
							</c:forEach>
						</c:if>
						<c:if test="${isSizeAvailable}">
							<label for="Size"><spring:theme
									code="product.variants.size" /></label>
						</c:if>
						<!--                                                 <select id="Size" class="variant-select"> -->
						<c:if test="${empty variantSizes}">
							<%--                                 <option selected="selected"><spring:theme code="product.variants.select.style" /></option> --%>
						</c:if>
						<c:if test="${not empty variantSizes}">
							<%--                                 <option value="${currentStyledProductUrl}" --%>
							<%--                                     <c:if test="${empty variantParams['size']}">selected="selected"</c:if>> --%>
							<%--                                     <spring:theme code="product.variants.select.size" /> --%>
							<!--                                 </option> -->
							<c:forEach items="${variantSizes}" var="variantSize">
								<c:set var="optionsString" value="" />
								<c:forEach items="${variantSize.variantOptionQualifiers}"
									var="variantOptionQualifier">
									<c:if test="${variantOptionQualifier.qualifier eq 'size'}">
										<%--                                             <c:set var="optionsString">${optionsString}&nbsp;${variantOptionQualifier.name}&nbsp;${variantOptionQualifier.value}, </c:set> --%>
										<c:set var="optionsString">${variantOptionQualifier.value} </c:set>
									</c:if>
								</c:forEach>

								<c:if
									test="${(variantSize.stock.stockLevel gt 0) and (variantSize.stock.stockLevelStatus ne 'outOfStock')}">
									<c:set var="stockLevel">${variantSize.stock.stockLevel}&nbsp;<spring:theme
											code="product.variants.in.stock" />
									</c:set>
								</c:if>
								<c:if
									test="${(variantSize.stock.stockLevel le 0) and (variantSize.stock.stockLevelStatus eq 'inStock')}">
									<c:set var="stockLevel">
										<spring:theme code="product.variants.available" />
									</c:set>
								</c:if>
								<c:if
									test="${(variantSize.stock.stockLevel le 0) and (variantSize.stock.stockLevelStatus ne 'inStock')}">
									<c:set var="stockLevel">
										<spring:theme code="product.variants.out.of.stock" />
									</c:set>
								</c:if>

								<c:if test="${(variantSize.url eq product.url)}">
									<c:set var="hoverclass" value="pdp-selected-size" />
									<c:set var="showAddToCart" value="${true}" scope="session" />
								</c:if>
								<c:if test="${(variantSize.url != product.url)}">
									<c:set var="hoverclass" value="pdp-size-btn" />
								</c:if>
								<c:url value="${variantSize.url}" var="variantOptionUrl" />
								<%--                                     <option value="${variantOptionUrl}" ${(variantSize.url eq product.url) ? 'selected="selected"' : ''}> --%>
								<%--                                         ${optionsString}</option> --%>
								<c:if
									test="${(fn:toUpperCase(optionsString) ne 'NA') && (fn:toUpperCase(optionsString) ne 'N/A')&& (fn:toUpperCase(optionsString) ne 'A')}">
									<button id="Size${variantOptionUrl}"
										class="${hoverclass} variant-select" type="button"
										value="${variantOptionUrl}"
										onclick="sizeClick('${variantOptionUrl }');"
										title="Product Size">${optionsString}</button>
								</c:if>
							</c:forEach>
						</c:if>
						<!--    </select> -->
						<div
							style="margin-left: 110px; margin-top: -30px; text-decoration: none">
							<c:if test="${not empty showSizeguide}">
								<a href="#" id="sizeGuideId" style="text-decoration: none">&nbsp;
									<!-- <span class="glyphicon glyphicon-barcode-detail"> --> 
									<img src="${siteResourcePath}/images/barcode.png" />
								</a>
							</c:if>
						</div>
					</form>
				</div>
			</c:if>
		</div>
	</c:if>
	<c:if test="${not empty variantOptions}">
		<div class="size">
			<form>
				<label for="Size">Size</label> <select id="variant"
					class="variant-select">
					<%--                     <option selected="selected" disabled="disabled"><spring:theme code="product.variants.select.size" /></option> --%>
					<c:forEach items="${variantOptions}" var="variantOption">
						<c:set var="optionsString" value="" />
						<c:forEach items="${variantOption.variantOptionQualifiers}"
							var="variantOptionQualifier">
							<%--                                <c:set var="optionsString">${optionsString}&nbsp;${variantOptionQualifier.name}&nbsp;${variantOptionQualifier.value}, </c:set> --%>
							<c:set var="optionsString">${variantOptionQualifier.value} </c:set>
						</c:forEach>

						<c:if
							test="${(variantOption.stock.stockLevel gt 0) and (variantSize.stock.stockLevelStatus ne 'outOfStock')}">
							<c:set var="stockLevel">${variantOption.stock.stockLevel} <spring:theme
									code="product.variants.in.stock" />
							</c:set>
						</c:if>
						<c:if
							test="${(variantOption.stock.stockLevel le 0) and (variantSize.stock.stockLevelStatus eq 'inStock')}">
							<c:set var="stockLevel">
								<spring:theme code="product.variants.available" />
							</c:set>
						</c:if>
						<c:if
							test="${(variantOption.stock.stockLevel le 0) and (variantSize.stock.stockLevelStatus ne 'inStock')}">
							<c:set var="stockLevel">
								<spring:theme code="product.variants.out.of.stock" />
							</c:set>
						</c:if>

						<c:choose>
							<c:when
								test="${product.purchasable and product.stock.stockLevelStatus.code ne 'outOfStock' }">
								<c:set var="showAddToCart" value="${true}" scope="session" />
							</c:when>
							<c:otherwise>
								<c:set var="showAddToCart" value="${false}" scope="session" />
							</c:otherwise>
						</c:choose>

						<c:url value="${variantOption.url}" var="variantOptionUrl" />
						<%-- <option value="${variantOptionUrl}" ${(variantOption.url eq product.url) ? 'selected="selected"' : ''}>
                                ${optionsString}&nbsp;<format:price priceData="${variantOption.priceData}"/>&nbsp;&nbsp;${variantOption.stock.stockLevel}
                            </option> --%>
						<option value="${variantOptionUrl}"
							${(variantOption.url eq product.url) ? 'selected="selected"' : ''}>
							${optionsString}</option>
					</c:forEach>
				</select>
		</div>
	</c:if>
</div>