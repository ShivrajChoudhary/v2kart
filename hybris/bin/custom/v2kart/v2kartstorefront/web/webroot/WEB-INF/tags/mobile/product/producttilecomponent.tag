<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ attribute name="pageData" required="true" type="de.hybris.platform.commerceservices.search.pagedata.SearchPageData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div class="innerContent">
    <div id="resultsTile" class="productResultsTile">
        <c:if test="${not empty searchPageData.results}">
        <input id="sort_form1" type="hidden" value="${searchPageData.currentQuery.query.value}">
            <ul id="resultsTile" data-role="listview" data-inset="true" data-theme="e" data-content-theme="e" class="mainNavigation">
                <c:forEach items="${searchPageData.results}" var="product" varStatus="status">
                   <center> <product:productListerTileItem product="${product}" /></center>
                    <div class="lineSeperator"></div>
                </c:forEach>
            </ul>
        </c:if>
        <c:if test="${searchPageData.pagination.totalNumberOfResults == 0}">
               <div class="marginTop"><spring:theme code=" " text="0 Products found" /></div>
        </c:if>
    </div>
</div>
<%-- <nav:pagination searchPageData="${searchPageData}" searchUrl="${searchPageData.currentQuery.url}"/> --%>
