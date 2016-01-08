<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<c:if test="${not empty searchPageData.results}">
	<div class="sortingBar item_container_holder">
		<nav:searchTermAndSortingBar pageData="${searchPageData}" top="true" showSearchTerm="false"/>
	</div>
</c:if>

<div class="innerContent">
    <div class="productResultsList">
        <c:if test="${not empty searchPageData.results}">
        <input id="sort_form1" type="hidden" value="${searchPageData.currentQuery.query.value}">
            <ul id="resultsList" data-role="listview" data-inset="true" data-theme="e" data-content-theme="e" class="mainNavigation">
                <c:forEach items="${searchPageData.results}" var="product" varStatus="status">
                    <product:productListerItem product="${product}" />
                </c:forEach>
            </ul>
        </c:if>
        <c:if test="${searchPageData.pagination.totalNumberOfResults == 0}">
               <div class="marginTop"><spring:theme code=" " text="0 Products found" /></div>
        </c:if>
    </div>
</div>
<common:infiniteScroll />

<%-- <nav:pagination searchPageData="${searchPageData}" searchUrl="${searchPageData.currentQuery.url}"/> --%>
