<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="pageData" required="true" type="de.hybris.platform.commerceservices.search.facetdata.FacetSearchPageData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<script>
	function showSecondLevel(facetButton) {
		$('.secondLevelFacetsDiv').css('display', 'none');
		$('.firstLevel').css('display', 'none');
		$('#' + facetButton).css('display', 'block');
		$('.backToFacets').css('display', 'block');
		$('.ui-loader').css('display', 'none');
	}

	function backToFacets() {
		$('.secondLevelFacetsDiv').css('display', 'none');
		$('.firstLevel').css('display', 'block');
		$('.backToFacets').css('display', 'none');
		$('.ui-loader').css('display', 'none');
		

	}
</script>
<div class="firstLevel">
    <ul data-role="listview" data-inset="true" data-content-theme="a">
        <c:forEach items="${pageData.facets}" var="facet">  
          <c:choose>
               <c:when test="${facet.code eq 'price'}">
                </c:when>
               <c:otherwise>          
                    <li data-corners="false" data-shadow="false" data-iconshadow="true" data-inline="false" data-wrapperels="div"
                        data-icon="arrow-r" data-iconpos="right" data-theme="f"><a onclick="showSecondLevel('${facet.name}-button');"
                        id="f${facet.name}-button" class="refinementFacetPageLink"> <span class="refinementSetName">${facet.name}</span>
                        <span class="refinementSetFilter">
                        <c:set var="firstelement" value="true"/>
                        <c:forEach items="${facet.values}" var="facetValue" varStatus="theCount">
                        <c:if test="${facetValue.selected}">
                        <c:if test="${not firstelement}">
                        <span class='refinementFilterDelimiter'>|</span>
                        </c:if>${facetValue.name}
                        <c:set var="firstelement" value="false"/>
                        </c:if>
                        </c:forEach>
                        </span>
                    </a></li>
     </c:otherwise>
        </c:choose>
 
        </c:forEach>
    </ul>
</div>

<div class="secondLevel">
    <c:forEach items="${pageData.facets}" var="facet">
      <c:choose>
            <c:when test="${facet.code eq 'price'}">
            </c:when>
            <c:otherwise>
              
                <div id="${facet.name}-button" style="display: none;" class="secondLevelFacetsDiv">
                    <ul data-role="listview" data-inset="true" data-content-theme="a">
                        <fieldset data-role="controlgroup" class="facetValueList" data-facet="${facet.name}">
                            <c:forEach items="${facet.values}" var="facetValue" varStatus="theCount">
                                <c:set var="shortFacetName" value="${fn:substring(facetValue.name, 0, 22)}" />
                                <c:if test="${facet.multiSelect}">
                                    <li><c:choose>
                                            <c:when test="${facet.code eq 'htmlColors'}">
                                                <input type="checkbox" data-theme="f" data-query="${facet.code}:${facetValue.code}"
                                                    id="s${facetValue.name}" ${facetValue.selected ? 'checked="checked" ' : ''} />

                                                <label for="s${facetValue.name}"> <c:set var="color"
                                                        value="${fn:split(facetValue.code,'_')}" /> <c:set var="colorval"
                                                        value="${not empty color and fn:length(color) gt 1 ? color[1] : '' }" /> <c:choose>
                                                        <c:when test="${fn:startsWith(colorval,'/medias')}">
                                             ${shortFacetName}&nbsp;<span class="style_colorBox tooltipItem"
                                                                style="background:url(${colorval})"> </span>
                                                            <span class="ui-li-count"> <spring:theme
                                                                    code="mobile.search.nav.facetValueCount" arguments="${facetValue.count}" />
                                                            </span>
                                                        </c:when>
                                                        <c:otherwise>
                                              ${shortFacetName}&nbsp; <span class="style_colorBox tooltipItem"
                                                                style="background:${colorval}"> </span>
                                                            <span class="ui-li-count"> <spring:theme
                                                                    code="mobile.search.nav.facetValueCount" arguments="${facetValue.count}" />
                                                            </span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </label>

                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" data-theme="a" data-query="${facet.code}:${facetValue.code}"
                                                    id="s${facetValue.name}" ${facetValue.selected ? 'checked="checked" ' : ''} />
                                                <label for="s${facetValue.name}">
                                                <c:if test="${facet.name eq 'Color'}">
                                                	<div class="swatch" style="background: ${facetValue.name};">&nbsp;</div>&nbsp;
                                                </c:if>
                                                ${shortFacetName}&nbsp; <span class="ui-li-count">
                                                        <spring:theme code="mobile.search.nav.facetValueCount"
                                                            arguments="${facetValue.count}" />
                                                </span>
                                                </label>
                                            </c:otherwise>
                                        </c:choose></li>
                                </c:if>
                                <c:if test="${not facet.multiSelect}">
                                    <li class="notMultiSelect" data-theme="a">
                                        <div class="ui-btn-corner-all ui-fullsize ui-btn-icon-right ui-first-child ui-btn-up-a"
                                            data-query="${facet.code}:${facetValue.code}" id="s${facetValue.name}">
                                            <a href="<%= request.getContextPath() %>${facetValue.query.url}" class="ui-link-inherit"> <span
                                                class="ui-btn-inner"> <span class="ui-btn-text"> ${facetValue.name} <span
                                                        class="ui-li-count ui-btn-up-a ui-btn-corner-all"> <spring:theme
                                                                code="mobile.search.nav.facetValueCount" arguments="${facetValue.count}" />
                                                    </span>
                                                </span>
                                            </span>
                                            </a>
                                        </div>
                                    </li>
                                </c:if>
                            </c:forEach>
                        </fieldset>
                    </ul>
                </div>
       </c:otherwise>
    </c:choose>


        <%-- <div id="${facet.name}-button" style="display: none;" class="secondLevelFacetsDiv">
            <ul data-role="listview" data-inset="true" data-content-theme="a">
                <fieldset data-role="controlgroup" class="facetValueList" data-facet="${facet.name}">
                    <c:forEach items="${facet.values}" var="facetValue" varStatus="theCount">

                        <c:if test="${facet.multiSelect}">
                            <li><c:choose>
                                    <c:when test="${facet.code eq 'htmlColors'}">

                                        <input type="checkbox" data-theme="f" data-query="${facet.code}:${facetValue.code}"
                                            id="s${facetValue.name}" ${facetValue.selected ? 'checked="checked" ' : ''} />

                                        <label for="s${facetValue.name}"> <c:set var="color"
                                                value="${fn:split(facetValue.code,'_')}" /> <c:set var="colorval"
                                                value="${not empty color and fn:length(color) gt 1 ? color[1] : '' }" /> <c:choose>
                                                <c:when test="${fn:startsWith(colorval,'/medias')}">
                                              ${facetValue.name}&nbsp;<span class="style_colorBox tooltipItem"
                                                        style="background:url(${colorval})"> </span>
                                                    <span class="ui-li-count"> <spring:theme code="mobile.search.nav.facetValueCount"
                                                            arguments="${facetValue.count}" />
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                              ${facetValue.name}&nbsp; <span class="style_colorBox tooltipItem"
                                                        style="background:${colorval}"> </span>
                                                    <span class="ui-li-count"> <spring:theme code="mobile.search.nav.facetValueCount"
                                                            arguments="${facetValue.count}" />
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </label>

                                    </c:when>
                                    <c:otherwise>
                                        <input type="checkbox" data-theme="a" data-query="${facet.code}:${facetValue.code}"
                                            id="s${facetValue.name}" ${facetValue.selected ? 'checked="checked" ' : ''} />
                                        <label for="s${facetValue.name}"> ${facetValue.name}&nbsp; <span class="ui-li-count">
                                                <spring:theme code="mobile.search.nav.facetValueCount" arguments="${facetValue.count}" />
                                        </span>
                                        </label>
                                    </c:otherwise>
                                </c:choose></li>
                        </c:if>
                        <c:if test="${not facet.multiSelect}">
                            <li class="notMultiSelect" data-theme="a">
                                <div class="ui-btn-corner-all ui-fullsize ui-btn-icon-right ui-first-child ui-btn-up-a"
                                    data-query="${facet.code}:${facetValue.code}" id="s${facetValue.name}">
                                    <a href="<%= request.getContextPath() %>${facetValue.query.url}" class="ui-link-inherit"> <span
                                        class="ui-btn-inner"> <span class="ui-btn-text"> ${facetValue.name} <span
                                                class="ui-li-count ui-btn-up-a ui-btn-corner-all"> <spring:theme
                                                        code="mobile.search.nav.facetValueCount" arguments="${facetValue.count}" />
                                            </span>
                                        </span>
                                    </span>
                                    </a>
                                </div>
                            </li>
                        </c:if>
                    </c:forEach>
                </fieldset>
            </ul>
        </div> --%>
    </c:forEach>


</div>
