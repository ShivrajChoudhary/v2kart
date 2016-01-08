<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<ycommerce:testId code="productDetails_content_label">
	<%-- <div class="productDescriptionText">
		${product.description}
	</div> --%>
</ycommerce:testId>

<div class="productFeatureClasses">
    <br>
    <c:if test="${not empty product.code}">
        <table>
            <tbody>
                <tr>
                    <td class="attrib"><spring:theme code="product.productcode" /></td>
                    <td>${product.code}</td>
                </tr>
            </tbody>
        </table>
    </c:if>
    
	<product:productDetailsClassifications product="${product}"/>
    
    <c:if test="${not empty product.description}">
        <div class="headline"><spring:theme code="product.description.text" /></div>
        <table>
            <tbody>
                <tr class="rowstyle">
                    <td class="propertylist_2">${product.description}</td>
                </tr>
            </tbody>
        </table>
    </c:if>
</div>