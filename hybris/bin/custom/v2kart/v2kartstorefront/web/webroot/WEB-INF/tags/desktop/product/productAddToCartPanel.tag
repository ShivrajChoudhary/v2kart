<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ attribute name="allowAddToCart" required="true" type="java.lang.Boolean"%>
<%@ attribute name="isMain" required="true" type="java.lang.Boolean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/desktop/storepickup"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="action" tagdir="/WEB-INF/tags/desktop/action"%>


<div class="qty" style="width: initial;">
    <c:if test="${product.stock.stockLevelStatus.code eq 'lowStock'}">
        <c:set var="productStockLevel">
            <spring:theme code="product.variants.only.left" arguments="${product.stock.stockLevel}" />
        </c:set>
    </c:if>
    <label for="qtyInput">Quantity</label>
    <select id="qtyInput" name="qtyInput" class="text width50" style="float: left;" ${product.purchasable and product.stock.stockLevelStatus.code ne 'outOfStock' ? "" : "disabled" } >
    	<c:forEach var="i" begin="1" end="${product.stock.stockLevel == 0 ? 1 : product.stock.stockLevel < 10 ? product.stock.stockLevel : 10}">
			<option value="${i}">${i}</option>
		</c:forEach>
    </select>
    <ycommerce:testId code="productDetails_productInStock_label">
    	<c:if test="${productStockLevel}">
        	<p class="stock_message">&nbsp; ${productStockLevel}</p>
        </c:if>
    </ycommerce:testId>
</div>

<div id="actions-container-for-${component.uid}" class="productAddToCartPanelContainer clearfix">
    <ul class="productAddToCartPanel clearfix">
        <action:actions element="li" styleClass="productAddToCartPanelItem span-5" parentComponent="${component}" />
    </ul>
</div>

<%-- <c:if test="${product.purchasable}"> --%>
<%--     <c:if test="${product.stock.stockLevelStatus.code eq 'outOfStock'}"> --%>
<!--         <div class="outOfStock"> -->
<%--             <spring:theme code="product.variants.out.of.stock" /> --%>
<!--         </div> -->
<%--     </c:if> --%>
<%-- </c:if> --%>

<c:if test="${product.purchasable}">
         
		<c:if test="${product.stock.stockLevelStatus.code eq 'lowStock'}">
            <c:set var="productStockLevel">
                <spring:theme code="product.variants.only.left" arguments="${product.stock.stockLevel}" />
            </c:set>
        </c:if>
        <c:if test="${product.stock.stockLevelStatus.code ne 'outOfStock'}">
              <ycommerce:testId code="productDetails_productInStock_label">
                    <p class="stock_message"> ${productStockLevel}</p>
                </ycommerce:testId>
        </c:if>
        <c:if test="${product.stock.stockLevelStatus.code eq 'outOfStock'}">
            <div class="outOfStock">
                <spring:theme code="product.variants.out.of.stock" />
            </div>
        </c:if>
    </c:if>

   
<%-- <c:if test="${product.stock.stockLevel gt 0}">
		<c:set var="productStockLevel">${product.stock.stockLevel}&nbsp;
			<spring:theme code="product.variants.in.stock"/>
		</c:set>
	</c:if> --%>

<%-- <c:if test="${product.stock.stockLevelStatus.code eq 'inStock' and empty product.stock.stockLevel}">
		<c:set var="productStockLevel">
			<spring:theme code="product.variants.available"/>
		</c:set>
	</c:if> --%>






