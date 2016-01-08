<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="pageData" required="true" type="de.hybris.platform.commerceservices.search.facetdata.FacetSearchPageData" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>


<%-- <div class="headline"><spring:theme code="search.nav.refinements"/></div> --%>
	<c:forEach items="${pageData.facets}" var="facet">
		<c:choose>
			<c:when test="${facet.code eq 'availableInStores'}">
				<nav:facetNavRefinementStoresFacet facetData="${facet}" userLocation="${userLocation}"/>
			</c:when>
			<c:otherwise>
				<nav:facetNavRefinementFacet facetData="${facet}"/>
			</c:otherwise>
		</c:choose>
	</c:forEach>
    <c:choose>
    <c:when test="${searchMode}">
        <c:set var="unfilteredQuery" value="${pageData.freeTextSearch}:${pageData.pagination.sort}" />
        <c:set var="cxt" value="/search" />
        <%-- <c:set var="simpleUri" value="${cxt}/populateFacetsAndProducts" /> --%>
        <c:set var="simpleUri" value="${cxt}/" />
        <c:set var="simpleUri" value="${cxt}/" />
    </c:when>
    <c:otherwise>
        <c:set var="unfilteredQuery" value="${pageData.freeTextSearch}:${pageData.pagination.sort}" />
        <c:set var="cxt" value="/c" />
        <%-- <c:set var="simpleUri" value="${cxt}/${categoryCode}/populateFacetsAndProducts" /> --%>
        <c:set var="simpleUri" value="${cxt}/${categoryCode}/" />
    </c:otherwise>
</c:choose>
    <form action="${simpleUri}" method="get" id="customForm">
        <input type="hidden" name="min" value="${min}" /> 
        <input type="hidden" name="max" value="${max}" /> 
        <input id="unfilteredQuery" type="hidden" name="q" value="${facetValue.query.query.value}" />
        <input type="hidden" name="text" value="${searchPageData.freeTextSearch}" />
    </form>


