<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<ycommerce:testId code="product_overview_details">
	<c:if test="${not empty product.description or not empty product.classifications}">
	<div data-role="collapsible" data-theme="g" data-content-theme="c" data-collapsed="true" data-icon="arrow-u" id="details_tab">
		<h3><spring:theme code="product.overview"/></h3>
		<div class="Content innerContent">
            <div class="featureClass">
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
    			<c:if test="${not empty product.classifications}">
    				<c:forEach items="${product.classifications}" var="classification">
    					<div class="featureClass">
    						<h4>${classification.name}</h4>
    						<table>
    							<tbody>
    							<c:forEach items="${classification.features}" var="feature">
    								<tr>
    									<td class="attrib">${feature.name}</td>
    									<td>
    										<c:forEach items="${feature.featureValues}" var="value" varStatus="status">
    											${value.value}
    											<c:choose>
    												<c:when test="${feature.range}">${not status.last ? '-' : feature.featureUnit.symbol}</c:when>
    												<c:otherwise>${feature.featureUnit.symbol} ${not status.last ? '<br/>' : ''}</c:otherwise>
    											</c:choose>
    										</c:forEach>
    									</td>
    								</tr>
    							</c:forEach>
    							</tbody>
    						</table>
    					</div>
    				</c:forEach>
    			</c:if>
                <c:if test="${not empty product.description}">
                    <h4 class="productdetailtitle"><spring:theme code="product.description.text" /></h4>
                    <table>
                        <tbody>
                            <tr class="rowstyle">
                                <td class="propertylist_2">${product.description}</td>
                            </tr>
                        </tbody>
                    </table>
                </c:if>
		    </div>
	   </div>
    </div>
	</c:if>
</ycommerce:testId>
