<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/theme"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="mproduct" tagdir="/WEB-INF/tags/mobile/product"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="action" tagdir="/WEB-INF/tags/mobile/action" %>


<c:url value="${product.url}" var="productUrl" />
<c:set var="count" value="1" />
<%-- Determine if product is one of V2kart style or size variant --%>
    <c:if test="${product.variantType eq 'V2kartStyleVariantProduct'}">
        <c:set var="variantStyles" value="${product.variantOptions}" />
    </c:if>
    <c:if test="${(not empty product.baseOptions[0].options) and (product.baseOptions[0].variantType eq 'V2kartStyleVariantProduct')}">
        <c:set var="variantStyles" value="${product.baseOptions[0].options}" />
        <c:set var="variantSizes" value="${product.variantOptions}" />
        <c:set var="currentStyleUrl" value="${product.url}" />
    </c:if>
    <c:if test="${(not empty product.baseOptions[1].options) and (product.baseOptions[0].variantType eq 'V2kartSizeVariantProduct')}">
        <c:set var="variantStyles" value="${product.baseOptions[1].options}" />
        <c:set var="variantSizes" value="${product.baseOptions[0].options}" />
        <c:set var="currentStyleUrl" value="${product.baseOptions[1].selected.url}" />
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
<div class="productpricing">
		<div class="productPriceDetailPdp">
            <c:choose>
                <c:when test="${product.percentageDiscount ne null}">
                    <span class="old-price">${product.price.formattedValue} </span>
                </c:when>
                <c:otherwise>
                    <span class="mrp">${product.price.formattedValue} </span>
                </c:otherwise>
            </c:choose>
            <c:if test="${product.percentageDiscount ne null}">
                <span class="mrp">${product.discountedPrice.formattedValue}</span>
                <span class="badge badge-red discount">${product.percentageDiscount}% OFF</span>
            </c:if>
	</div>    
</div>
<%-- promotions --%>
<%-- <ycommerce:testId code="productDetails_promotion_label">
	<c:if test="${not empty product.potentialPromotions}">
		<div class="itemPromotionBox" data-theme="b">
			<c:choose>
				<c:when test="${not empty product.potentialPromotions[0].couldFireMessages}">
					<p>${product.potentialPromotions[0].couldFireMessages[0]}</p>
				</c:when>
				<c:otherwise>
					<p>${product.potentialPromotions[0].description}</p>
				</c:otherwise>
			</c:choose>
		</div>
	</c:if>
</ycommerce:testId> --%>
<c:if test="${not empty product.summary}">
			<div class="item_container_holder continuous-text" id="productDescription">${product.summary}</div>
</c:if>
<div class="prod_add_to_cart" data-theme="b">
	<c:if test="${not empty variantStyles}">
		<div class="colorSelect checkout-delivery clear" id="colorselect">
			<div class="subItemHeader">
				<spring:theme code="mobile.product.variants.style.options" />
			</div>
			<ul class="colorlist clearfix">
				<c:forEach items="${variantStyles}" var="variantStyle"
					varStatus="status">
					<c:forEach items="${variantStyle.variantOptionQualifiers}"
						var="variantOptionQualifier">
						<c:if test="${variantOptionQualifier.qualifier eq 'style'}">
							<c:set var="styleValue" value="${variantOptionQualifier.value}" />
							<c:set var="imageData" value="${variantOptionQualifier.image}" />
						</c:if>
					</c:forEach>

					<li
						<c:if test="${variantStyle.url eq currentStyleUrl}">class="selected"</c:if>>
						<c:url value="${variantStyle.url}" var="productStyleUrl" />
						<p class="stylevalue">${styleValue}</p> <a
						href="${productStyleUrl}" class="colorVariant"
						name="${variantStyle.url}">
							<center>
								<c:if test="${not empty imageData}">
									<img class="styleImage" src="${imageData.url}"
										title="${styleValue}" alt="${styleValue}" />
								</c:if>
								<c:if test="${empty imageData}">
									<img class="styleImage"
										src="${siteResourcePath}/images/missing-product-40x40.png"
										title="${styleValue}" alt="${styleValue}" />
								</c:if>
							</center>

					</a>
					</li>
				</c:forEach>
			</ul>
		</div>
	</c:if>
	<c:if test="${not empty variantSizes}">
            <div class="ui-grid-a variant">
       
                <%-- Size Selector --%>
                <c:if test="${not empty variantSizes}">
                    <c:set var="isSizeAvailable" value="false" />
                	<c:forEach items="${variantSizes}" var="variantSize">
                        <c:set var="optionsString" value="" />
                        <c:forEach items="${variantSize.variantOptionQualifiers}" var="variantOptionQualifier">
                            <c:if test="${variantOptionQualifier.qualifier eq 'size'}">
                                <c:set var="optionsString">${variantOptionQualifier.value} </c:set>
                                <c:if test="${optionsString ne 'NA' && optionsString ne 'A' }">
                                	<c:set var="isSizeAvailable" value="true" />
                                </c:if>
                            </c:if>
                        </c:forEach>
                    </c:forEach>
                </c:if>
                <c:if test="${isSizeAvailable}">
                <div class="ui-block-a clearfix">
                <select id="Size" class='variantSelector' data-theme="c">
                  	   <c:if test="${empty variantSizes}">
                           <option selected="selected"><spring:theme code="product.variants.select.style" /></option>
                       </c:if>
                       <c:if test="${not empty variantSizes}">
                           <option value="${currentStyledProductUrl}"
                               <c:if test="${empty variantParams['size']}">selected="selected"</c:if>>
                               <spring:theme code="product.variants.select.size" />
                           </option>
	                       <c:forEach items="${variantSizes}" var="variantSize">
	                        	<c:set var="sizeVariantSuffix" value="" />
	                    		<c:set var="stockClass" value="" />
	                           
	                            <c:if test="${(variantSize.stock.stockLevel gt 0) and (variantSize.stock.stockLevelStatus ne 'outOfStock')}">
	                        		<c:set var="stockLevel">${variantSize.stock.stockLevel}&nbsp;<spring:theme code="product.variants.in.stock" />
	                        		</c:set>
	                    		</c:if>
	                    		<c:if test="${(variantSize.stock.stockLevel le 0) and (variantSize.stock.stockLevelStatus eq 'inStock')}">
	                        		<c:set var="stockLevel"><spring:theme code="product.variants.available" />
	                        		</c:set>
	                    		</c:if>
	                    		<c:if test="${(variantSize.stock.stockLevel le 0) and (variantSize.stock.stockLevelStatus ne 'inStock')}">
	                        		<c:set var="stockLevel"><spring:theme code="product.variants.out.of.stock" />
	                        		</c:set>
	                        		<c:set var="sizeVariantSuffix"> - <spring:theme code="product.product.out.of.stock" /></c:set>
	                        		<c:set var="stockClass">
	                            		<spring:theme code="sizeVariantOutOfStock" />
	                        		</c:set>
	                    		</c:if>
	                    		 <c:set var="optionsString" value="" />
	                            <c:forEach items="${variantSize.variantOptionQualifiers}" var="variantOptionQualifier">
	                                <c:if test="${variantOptionQualifier.qualifier eq 'size'}">
	                                            <c:set var="optionsString">${variantOptionQualifier.value} </c:set>
	                                        </c:if>
	                                    </c:forEach>
	                           
	                            <c:url value="${variantSize.url}" var="variantOptionUrl" />
	                                    <option value="${variantOptionUrl}" ${(variantSize.url eq product.url) ? 'selected="selected"' : ''}>
	                                        ${optionsString}</option>
	                        </c:forEach>
                        </c:if>
                        </select>
                        </div>
                        </c:if>
                <div class="ui-block-b ${isSizeAvailable? '' : 'fullWidthImp'}">
				<div class="prod_add_to_cart_quantity">
					<c:choose>
						<c:when test="${product.purchasable and product.stock.stockLevelStatus.code ne 'outOfStock'}">
						 	<select id="qty" class='qty qtySelector' name="qty" data-theme="c">
								<%-- <option value="1">
									<spring:theme code="basket.page.quantity"/>
								</option> --%>
								<formElement:formProductQuantitySelectOption stockLevel="${product.stock.stockLevel}" startSelectBoxCounter="1"/>
							</select>
						</c:when>
						<c:otherwise>
							<select class="noSelectMenu" disabled='true'>
								<option value="1">
									<spring:theme code="basket.page.quantity"/>
								</option>
							</select>
						</c:otherwise>
					</c:choose>
				</div>
				<c:if test="${product.stock.stockLevelStatus.code eq 'lowStock'}">
			       	<c:set var="productStockLevel">
			           <spring:theme code="product.variants.only.left" arguments="${product.stock.stockLevel}"/>
			       	</c:set>
			   	  </c:if>
				  <ycommerce:testId code="productDetails_productInStock_label">
				      <p class="stock_message">${productStockLevel}</p>
				  </ycommerce:testId>
			</div>
            
                      </div>
                 
        </c:if>
                   
   		<c:if test="${not empty variantOptions}">
                    <c:forEach items="${variantOptions}" var="variantOption">
                        <c:set var="optionsString" value="" />
                        <c:forEach items="${variantOption.variantOptionQualifiers}" var="variantOptionQualifier">
                            <c:set var="optionsString">${optionsString}&nbsp;${variantOptionQualifier.name}&nbsp;${variantOptionQualifier.value}, </c:set>
                        </c:forEach>
                        <c:if test="${(variantOption.stock.stockLevel gt 0) and (variantSize.stock.stockLevelStatus ne 'outOfStock')}">
                            <c:set var="stockLevel">${variantOption.stock.stockLevel} <spring:theme code="product.variants.in.stock" />
                            </c:set>
                        </c:if>
                        <c:if test="${(variantOption.stock.stockLevel le 0) and (variantSize.stock.stockLevelStatus eq 'inStock')}">
                            <c:set var="stockLevel">
                                <spring:theme code="product.variants.available" />
                            </c:set>
                        </c:if>
                        <c:if test="${((variantOption.stock.stockLevel le 0) or (variantOption.stock.meteredStockLevel lt 1.0)) and (variantSize.stock.stockLevelStatus ne 'inStock')}">
                            <c:set var="stockLevel">
                                <spring:theme code="product.variants.out.of.stock" />
                            </c:set>
                        </c:if>
                    </c:forEach>
        </c:if>
        <c:if test="${not empty showSizeguide}">
        	<div id="sizeDetails" class="sizeDetails"><div class="sizeimage"></div><spring:theme code="mobile.product.size.details" /></div>
        </c:if>
        
        <div class="checkout-delivery">
           <h4 class="subItemHeader">
               <spring:theme code="text.delivery.options" />
           </h4>
           <div class="divBoxPinCOD pinSearchInputbox clearfix" data-theme="b" data-role="content" role="main" >
                 <input type="hidden" value="${product.code}" name="productCode"
                                id="productCodeServiceability" />
                          <div id = "checkForm">
                            <c:choose>
                                <c:when test="${pinCode != null }">
                                    <input type="text" id="searchServiceability" data-theme="c"
                                        class="pinSearchInput" name="text" value="${pinCode}"
                                        maxlength="100" autocomplete="off" placeholder=" Enter Pincode">
                                </c:when>

                                <c:otherwise>
                                    <input type="text" id="searchServiceability" data-theme="c"
                                        class="pinSearchInput" name="text" value="" maxlength="100"
                                        autocomplete="off" placeholder=" Enter Pincode">
                                </c:otherwise>
                            </c:choose>
                            <button class="btn-red checkPin" 
                                type="button"
                                data-theme="b"
                                onClick="checkForServiceability(); return false;"><spring:theme code="text.delivery.check" /></button>
                        </div>
                        <div style="display: none;" id="validPin" class="pinvalid clear">
                            <div class="panel-static fk-inline-block vmiddle headLabel">
                            	<spring:theme code="text.delivery.pinCodeText" />: <span class="enteredPinCode"></span>
                            	<a href="javascript:void();" class="pincodeChangeBtn blue-link"><spring:theme code="text.delivery.pinCode.change" /></a>
                            	<br/>
                                <span class="icon icon-map"></span> <div class="pincode-text"></div> 
                            </div>
                        </div>
                    </div>
                    <div class="divBoxPinCOD" data-theme="d" data-role="content" role="main" >
                    	<p class="colorRed"><spring:theme code="text.delivery.cod" /></p>
                    	<div class="codServiceability-text"><spring:theme code="text.delivery.cod.available" /></div>
                    </div>
        </div>
        

</div>