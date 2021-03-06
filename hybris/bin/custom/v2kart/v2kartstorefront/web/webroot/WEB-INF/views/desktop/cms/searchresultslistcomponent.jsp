<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>

<div class="results">
    <div class="heading">
        <spring:theme code="search.page.searchText" arguments="${searchPageData.freeTextSearch}" />
    </div>
</div>

<nav:searchSpellingSuggestion spellingSuggestion="${searchPageData.spellingSuggestion}" />

<nav:lazyLoadingPagination top="true" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}"
    searchUrl="${searchPageData.currentQuery.url}" numberPagesShown="${numberPagesShown}" />

<div class="productGrid">
    <div class="row">
        <c:url value="/search" var="currentURL" />
        <div id="resultsList" data-current-path="${currentURL}" data-current-query="${searchPageData.currentQuery.query.value}">
            <ul class="product">
                <c:set var="wrap" value="0" />
                <c:forEach items="${searchPageData.results}" var="product" varStatus="status">

                    <c:if test="${wrap eq 0}">
                        <section class="section-separator">
                    </c:if>
                    <div class="col-md-3">
                        <product:productListerGridItem product="${product}" />
                    </div>

                    <c:set var="wrap" value="${wrap+1}" />
                    <c:if test="${wrap eq 4 }">
                        <c:set var="wrap" value="0" />
                        </section>

                    </c:if>

                </c:forEach>
            </ul>
        </div>
    </div>
</div>
<!-- Return to Top -->
<a href="javascript:" id="return-to-top"><span class="glyphicon glyphicon-chevron-up"></span></a>
<common:infiniteScroll />
<%-- <nav:pagination top="false"  supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchPageData.currentQuery.url}"  numberPagesShown="${numberPagesShown}"/>
 --%>