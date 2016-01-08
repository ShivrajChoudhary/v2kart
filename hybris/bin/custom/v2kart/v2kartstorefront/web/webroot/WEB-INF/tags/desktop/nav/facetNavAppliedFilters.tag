<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="pageData" required="true" type="de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:if test="false">
<c:if test="${not empty pageData.breadcrumbs}">
    <input type="hidden" name="min" value="${min}" />
    <input type="hidden" name="max" value="${max}" />

    <%-- <div class="headline"><spring:theme code="search.nav.appliedFilters"/>	</div> --%>

    <c:forEach items="${pageData.breadcrumbs}" var="breadcrumb">
        <c:if test="${breadcrumb.facetName ne 'Price'}">
            <div class="facetApplied">
                <div class="facetValues">
                    <ul class="facet_block">
                    <c:set var="query" value="${breadcrumb.removeQuery.url}"></c:set>
                    <c:set var="index" value="${fn:split(query, '?')}"></c:set>
                  <%--   <c:set var="finalQuery" value="${fn:join(index, '/populateFacetsAndProducts?')}"></c:set> --%>
                    <c:set var="finalQuery" value="${fn:join(index, '/?')}"></c:set>
                        <li class="remove_item_left"><c:url value="${finalQuery}&min=${min }&max=${max }"
                                var="removeQueryUrl" /> <span class="remove_item_left_name">${breadcrumb.facetValueName}</span> <span
                            class="remove">
                           <%--  <a href="javascript:void(0);" onclick="facetClick('${removeQueryUrl}')"></a> --%>
                            <a href="${removeQueryUrl}"></a></span>
                           </span>
                            <div class="clear"></div></li>
                    </ul>
                </div>
            </div>
        </c:if>
    </c:forEach>


</c:if>
</c:if>