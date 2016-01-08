<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:if test="${not empty searchPageData.results}">
    <div class="sortingBar item_container_holder">
        <nav:searchTermAndSortingBar pageData="${searchPageData}" top="true" showSearchTerm="false" />
    </div>
</c:if>

<c:if test="${ empty sessionScope.isTileView }">
    <c:set var="isTileView" value="false" scope="session"></c:set>
</c:if>
<c:if test="${not empty param.isTileView }">
    <c:set var="isTileView" value="${param.isTileView}" scope="session"></c:set>
</c:if>

<div class="gridCss">
<c:choose>
    <c:when test="${ fn:contains(sessionScope.isTileView,'true') }">
        <product:producttilecomponent   pageData="${searchPageData}" />
        <div id="gridviewbutton">
            <form action="">
                <input type="hidden" value="false" name="isTileView" />
                <button type="submit" ><img src="${siteResourcePath}/images/grid-icon.png"/></button>
            </form>
        </div>
    </c:when>
    <c:otherwise>
        <div id="resultsGrid" class="span-24 productResultsGrid">
            <c:forEach items="${searchPageData.results}" var="product" varStatus="status">
                <c:choose>
                    <c:when test="${status.first}">
                        <div class="ui-grid-a">
                            <div class='ui-block-a left'>
                                <product:productListerGridItem product="${product}" />
                            </div>
                            <c:if test="${status.last}">
                        </div>
                            </c:if>
                    </c:when>
                    <c:otherwise>
                        <c:choose>
                            <c:when test="${(status.count % 2) == 0}">
                                <div class='ui-block-b right'>
                                    <product:productListerGridItem product="${product}" />
                                </div>
                        </div>
                            </c:when>
                            <c:otherwise>
                                <div class="ui-grid-a">
                                    <div class='ui-block-a left'>
                                        <product:productListerGridItem product="${product}" />
                                    </div>
                                    <c:if test="${status.last}">
                                </div>
                                    </c:if>
                            </c:otherwise>
                        </c:choose>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </div>
        <div  id="tileviewbutton" >
            <form action="">
                <input type="hidden" value="true" name="isTileView" />
                <button type="submit" ><img src="${siteResourcePath}/images/tile-icon.png"/></button>
            </form>
        </div>
    </c:otherwise>
</c:choose>
</div>

<a href="javascript:void(0)" id="return-to-top"><span class="glyphicon glyphicon-chevron-up"></span></a>
