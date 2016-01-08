<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="searchUrl" required="true"%>
<%@ attribute name="searchPageData" required="true" type="de.hybris.platform.commerceservices.search.pagedata.SearchPageData"%>
<%@ attribute name="msgKey" required="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav"%>
<%@ attribute name="top" required="false" type="java.lang.Boolean" %>
<%@ attribute name="supportShowAll" required="false" type="java.lang.Boolean" %>
<%@ attribute name="supportShowPaged" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="pagination" tagdir="/WEB-INF/tags/mobile/nav/pagination" %>
<%@ attribute name="numberPagesShown" required="false" type="java.lang.Integer" %>

<c:set var="themeMsgKey" value="${not empty msgKey ?  msgKey : 'search.mobile.page'}"/>
<div class="pagination">
<c:if test="${top and not empty searchPageData.sorts}">
        <form id="sort_form${top ? '1' : '2'}" name="sort_form${top ? '1' : '2'}" method="get" action="#" class="sortForm clearfix">
            <div class="ui-block-a">
                <label for="sortOptions${top ? '1' : '2'}"><spring:theme code="${themeMsgKey}.sortTitle"/></label>
            </div>
            <div class="ui-block-b">
            <select id="sortOptions${top ? '1' : '2'}" name="sort" class="sortOptions" data-theme="c">
                <c:forEach items="${searchPageData.sorts}" var="sort">
                    <option value="${sort.code}" ${sort.selected ? 'selected="selected"' : ''}>
                        <c:choose>
                            <c:when test="${not empty sort.name}">
                                ${sort.name}
                            </c:when>
                            <c:otherwise>
                                <spring:theme code="${themeMsgKey}.sort.${sort.code}"/>
                            </c:otherwise>
                        </c:choose>
                    </option>
                </c:forEach>
            </select>
            </div>
            <c:catch var="errorException">
                <spring:eval expression="searchPageData.currentQuery.query" var="dummyVar"/><%-- This will throw an exception is it is not supported --%>
                <input type="hidden" name="q" value="${searchPageData.currentQuery.query.value}"/>
            </c:catch>

            <c:if test="${supportShowAll}">
                <ycommerce:testId code="searchResults_showAll_link">
                    <input type="hidden" name="show" value="Page"/>
                </ycommerce:testId>
            </c:if>
            <c:if test="${supportShowPaged}">
                <ycommerce:testId code="searchResults_showPage_link">
                    <input type="hidden" name="show" value="All"/>
                </ycommerce:testId>
            </c:if>
            <input type="hidden" name="currentTab" id="currentTab" value="${currentTab}" />
        </form>
    </c:if>
	<div class="ui-grid-a">
		<div class="ui-block-a">
			<nav:paginationPageCounter searchPageData="${searchPageData}" msgKey="${msgKey}"/>
		</div>
		<div data-position="inline" class="ui-block-b">
			<div data-role="controlgroup" data-type="horizontal">
				<c:set var="hasPreviousPage" value="${searchPageData.pagination.currentPage > 0}"/>
				<c:if test="${hasPreviousPage}">
					<spring:url value="${searchUrl}" var="previousPageUrl">
						<spring:param name="page" value="${searchPageData.pagination.currentPage - 1}"/>
					</spring:url>
					<ycommerce:testId code="searchResults_previousPage_link">
						<a href="${previousPageUrl}" data-role="button" data-theme="b">
							<spring:theme code="${themeMsgKey}.linkPreviousPage"/>
						</a>
					</ycommerce:testId>
				</c:if>
				<c:if test="${not hasPreviousPage}">
					<button data-role="button" data-theme="d" disabled>
						<spring:theme code="${themeMsgKey}.linkPreviousPage"/>
					</button>
				</c:if>
				<c:set var="hasNextPage"
					   value="${(searchPageData.pagination.currentPage + 1) < searchPageData.pagination.numberOfPages}"/>
				<c:if test="${hasNextPage}">
					<spring:url value="${searchUrl}" var="nextPageUrl">
						<spring:param name="page" value="${searchPageData.pagination.currentPage + 1}"/>
					</spring:url>
					<ycommerce:testId code="searchResults_nextPage_link">
						<a href="${nextPageUrl}" data-role="button" data-theme="b">
							<spring:theme code="${themeMsgKey}.linkNextPage"/>
						</a>
					</ycommerce:testId>
				</c:if>
				<c:if test="${not hasNextPage}">
					<button data-role="button" data-theme="d" disabled>
						<spring:theme code="${themeMsgKey}.linkNextPage"/>
					</button>
				</c:if>
			</div>
		</div>
	</div>
</div>
