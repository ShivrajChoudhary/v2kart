<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/desktop/storepickup"%>
<script type="text/javascript" src="${commonResourcePath}/js/acc.refinements.js"></script>
<script type="text/javascript" src="${siteResourcePath}/js/acc.product.js"></script>
<script type="text/javascript" src="${siteResourcePath}/js/acc.productlisting.js"></script>
<script type="text/javascript" src="${siteResourcePath}/js/custom-function.js"></script>
<script type="text/javascript" src="${siteResourcePath}/js/v2kart-custom.js"></script>

<div class="col-md-3">
    <div class="${not empty categoryMin and not empty categoryMax ? 'facetNavigation': ''}">
        <cms:pageSlot position="ProductLeftRefinements" var="feature">
            <cms:component component="${feature}" />
        </cms:pageSlot>
    </div>
</div>
<div class="col-md-9">
    <c:if test="${searchMode}">
        <cms:pageSlot position="SearchResultsListSlot" var="feature">
            <cms:component component="${feature}" />
        </cms:pageSlot>
    </c:if>
    <c:if test="${searchMode eq false}">
        <cms:pageSlot position="ProductListSlot" var="feature">
            <cms:component component="${feature}" />
        </cms:pageSlot>
    </c:if>
</div>
