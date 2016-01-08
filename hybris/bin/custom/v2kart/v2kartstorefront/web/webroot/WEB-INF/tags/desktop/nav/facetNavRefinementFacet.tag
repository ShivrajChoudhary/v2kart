<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="facetData" required="true" type="de.hybris.platform.commerceservices.search.facetdata.FacetData"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:if test="${not empty facetData.values}">
<c:choose>
    <c:when test="${searchMode}">
        <c:set var="unfilteredQuery" value="${pageData.freeTextSearch}:${pageData.pagination.sort}" />
        <c:set var="cxt" value="/search" />
        <%-- <c:set var="simpleUri" value="${cxt}/populateFacetsAndProducts" /> --%>
        <c:set var="simpleUri" value="${cxt}/" />
    </c:when>
    <c:otherwise>
        <c:set var="unfilteredQuery" value="${pageData.freeTextSearch}:${pageData.pagination.sort}" />
        <c:set var="cxt" value="/c" />
        <%-- <c:set var="simpleUri" value="${cxt}/${categoryCode}/populateFacetsAndProducts" /> --%>
        <c:set var="simpleUri" value="${cxt}/${categoryCode}/" />
    </c:otherwise>
</c:choose>
    <%-- <div class="${facetData.code eq 'priceRange' or (categoryMin eq categoryMax and facetData.code eq 'price') ? '': 'facet"> --%>
    <div class="facet">
  <%--       <c:if test="${facetData.code ne 'price' and facetData.code ne 'priceRange' }">
            <div class="facetHead">
                <spring:theme code="text.hideFacet" var="hideFacetText" />
                <spring:theme code="text.showFacet" var="showFacetText" />
                <a class="refinementToggle" href="javascript:void(0);" data-hide-facet-text="${hideFacetText}" data-show-facet-text="${showFacetText}"> <spring:theme
                        code="search.nav.facetTitle" arguments="${facetData.name}" />
                </a>
            </div>
        </c:if>

        <c:if test="${facetData.name eq 'Price'}">
            <c:if test="${not empty categoryMin and not empty categoryMax and categoryMax gt categoryMin}"> --%>
                <div class="facetHead">
                    <spring:theme code="text.hideFacet" var="hideFacetText" />
                    <spring:theme code="text.showFacet" var="showFacetText" />
                    <a class="refinementToggle" href="javascript:void(0);" data-hide-facet-text="${hideFacetText}" data-show-facet-text="${showFacetText}">
                        <spring:theme code="search.nav.facetTitle" arguments="${facetData.name}" />
                    </a>
                </div>
       <%--      </c:if>
        </c:if> --%>

        <ycommerce:testId code="facetNav_facet${facetData.name}_links">
            <div class="facetValues">
                <c:if test="${not empty facetData.topValues}">
                    <div class="topFacetValues" >
                        <ul class="facet_block ${facetData.multiSelect ? '' : 'indent'}">
                           <%--  <c:if test="${facetData.name eq 'Price'}">
                                <li style="padding-left:10px; padding-right:10px"><c:if test="${not empty categoryMin and not empty categoryMax and categoryMax gt categoryMin}">
                                        <form action="${simpleUri}" method="get" class="customPriceForm" autocomplete="off">
                                        <input type="hidden" name="min" value="${min}" /> <input type="hidden" name="max" value="${max}" />
                                        <input type="hidden" name="q" value="${searchPageData.currentQuery.query.value}" /> <input
                                            type="hidden" name="text" value="${searchPageData.freeTextSearch}" />
                                        <input id="query${facetValue.name}" type="hidden" name="q" value="${facetValue.query.query.value}" />
                                                
                                        <div class="slider slider-horizontal" style="width: 222px;">
                                            <div class="tooltip top hide" style="top: -30px; left: 56.501002004008px;">
                                                <div class="tooltip-arrow"></div>
                                                <div class="tooltip-inner">${categoryMin }:${categoryMax }</div>
                                            </div>
                                            <input type="text" class="span2" value="" data-slider-min="${min}" data-slider-max="${max }"
                                                data-slider-step="5" data-slider-value="[${categoryMin},${categoryMax}]" id="ex2"
                                                data-slider-tooltip="hide">
                                        </div>
                                        <p class="slidervalue">
                                            <span class="pull-left">Rs. <span id="left">${categoryMin }</span></span></span>
                                            <span class="pull-right">Rs. <span id="right">${categoryMax }</span></span></span>
                                        </p>

                                        <input type="radio" ${facetValue.selected ? 'checked="checked"' : ''} onchange="javascript:handlePriceRadio(this);" date-priceFacet="${facetValue.code}"/>
                                                
                                    </form>
                                    </c:if></li>
                            </c:if> --%>
                            <c:if test="${facetData.code eq 'Color'}">
                                        <div class="product-preview">
                                            <div class="swatch">
                                            <c:forEach items="${facetData.values}" var="facetValue">
                                              <c:choose>
                                              <c:when test="${facetValue.name eq 'Multicolor'}">
                                                 <a href="javascript:void(0);" class="${facetValue.selected ? 'active' : ''}" title="${facetValue.name}" onclick="submitFacetForm('${facetValue.query.query.value}');"><span style="background-image:url(${siteResourcePath}/images/multicolor.png);">${facetValue.name}</span></a>
                                              </c:when>
                                              <c:otherwise>
                                                <a href="javascript:void(0);" class="${facetValue.selected ? 'active' : ''}" title="${facetValue.name}" onclick="submitFacetForm('${facetValue.query.query.value}');"><span style="background: ${facetValue.name};">${facetValue.name}</span></a> 
                                              </c:otherwise>
                                              </c:choose>
                                                </c:forEach>
                                                </div>
                                        </div>
                                        </c:if>


                            <c:forEach items="${facetData.topValues}" var="facetValue">
                                <li><c:if test="${facetData.multiSelect}">
                                        <c:choose>
                                            <c:when test="${facetData.code eq 'price' or facetData.code eq 'Color'}">
                                            <%-- <c:when test="${facetData.code eq 'price' or facetData.code eq 'priceRange' or facetData.code eq 'Color'}"> --%>

                                            </c:when>
                                            <c:otherwise>
                                                <%-- <form action="/c/${categoryCode }/populateFacetsAndProducts" method="get" id="${facetValue.name}">
                                                    <input type="hidden" name="min" value="${min}" /> <input type="hidden" name="max"
                                                        value="${max}" /> <input type="hidden" name="q"
                                                        value="${facetValue.query.query.value}" /> <input type="hidden" name="text"
                                                        value="${searchPageData.freeTextSearch}" /> --%> 
                                                        <input id="query${facetValue.name}" type="hidden" name="q" value="${facetValue.query.query.value}" />
                                                
                                                        <label class="facet_block-label">
                                                        <input type="checkbox" ${facetValue.selected ? 'checked="checked"' : ''}
                                                        onchange="submitFacetForm('${facetValue.query.query.value}');" /><span class="truncate" title="${facetValue.name}">${facetValue.name}</span>&nbsp; <span
                                                        class="facetValueCount"><spring:theme code="search.nav.facetValueCount"
                                                                arguments="${facetValue.count}" /></span>
                                                    </label>
                                               <!--  </form> -->
                                            </c:otherwise>
                                        </c:choose>

                                        <%-- <label class="facet_block-label">
                                                <input type="checkbox" ${facetValue.selected ? 'checked="checked"' : ''}
                                                onchange="$(this).closest('form').submit()" /> ${facetValue.name} <span
                                                class="facetValueCount"><spring:theme code="search.nav.facetValueCount"
                                                        arguments="${facetValue.count}" /></span>
                                            </label> --%>
                                    </c:if> <c:if test="${not facetData.multiSelect}">
                                    <c:set var="query" value="${facetValue.query.url}"></c:set>
                                    <c:set var="index" value="${fn:split(query, '?')}"></c:set>
                                    <%-- <c:set var="finalQuery" value="${fn:join(index, '/populateFacetsAndProducts?')}"></c:set> --%>
                                    <c:set var="finalQuery" value="${fn:join(index, '/?')}"></c:set>
                                        <c:url value="${finalQuery}&amp;text=${searchPageData.freeTextSearch}&min=${min }&max=${max }" var="facetValueQueryUrl" />
                                        <%-- <a href="javascript:void(0);" onclick="facetClick('${facetValueQueryUrl}')" title="${facetValue.name}">${facetValue.name}</a>&nbsp; --%>
                                        <a href="${facetValueQueryUrl}" title="${facetValue.name}">${facetValue.name}</a>&nbsp;
										<span class="facetValueCount"><spring:theme code="search.nav.facetValueCount" arguments="${facetValue.count}" /></span>
                                    </c:if></li>
                            </c:forEach>
                        </ul>
                        <c:choose>
                            <c:when test="${facetData.code eq 'price' or facetData.code eq 'Color'}">

                            </c:when>
                            <c:otherwise>
                                <span class="more"> <a href="javascript:void(0);" class="moreFacetValues"><spring:theme
                                            code="search.nav.facetShowMore" /></a>
                                </span>
                            </c:otherwise>
                        </c:choose>
                        
                    </div>
                </c:if>
                <div class="allFacetValues" style="${not empty facetData.topValues ? 'display:none' : ''}">
                    <ul class="facet_block ${facetData.multiSelect ? '' : 'indent'}">
                       <%--  <c:if test="${facetData.name eq 'Price'}">
                            <li style="padding-left:10px; padding-right:10px"><c:if test="${not empty categoryMin and not empty categoryMax and categoryMax gt categoryMin}">
                                    <form action="${simpleUri}" method="get" class="customPriceForm" autocomplete="off">
                                        <input type="hidden" name="min" value="${min}" /> <input type="hidden" name="max" value="${max}" />
                                        <input type="hidden" name="q" value="${searchPageData.currentQuery.query.value}" /> <input
                                            type="hidden" name="text" value="${searchPageData.freeTextSearch}" />
                                            <input id="query${facetValue.name}" type="hidden" name="q" value="${facetValue.query.query.value}" />
                                                
                                        <div class="slider slider-horizontal" style="width: 222px;">
                                            <div class="tooltip top hide" style="top: -30px; left: 56.501002004008px;">
                                                <div class="tooltip-arrow"></div>
                                                <div class="tooltip-inner">${categoryMin }:${categoryMax }</div>
                                            </div>
                                            <input type="text" class="span2" value="" data-slider-min="${min}" data-slider-max="${max }"
                                                data-slider-step="5" data-slider-value="[${categoryMin},${categoryMax}]" id="ex2"
                                                data-slider-tooltip="hide">
                                        </div>
                                        <p class="slidervalue">
                                            <span class="pull-left">Rs. <span id="left">${categoryMin }</span></span></span>
                                            <span class="pull-right">Rs. <span id="right">${categoryMax }</span></span></span>
                                        </p>

                                        <input type="radio" ${facetValue.selected ? 'checked="checked"' : ''} onchange="javascript:handlePriceRadio(this);" date-priceFacet="${facetValue.code}"/>
                                                
                                    </form>
                                </c:if></li>
                        </c:if> --%>
                        <c:if test="${facetData.code eq 'Color'}">
                                        <div class="product-preview">
                                            <div class="swatch">
                                            <c:forEach items="${facetData.values}" var="facetValue">
                                            <c:choose>
                                              <c:when test="${facetValue.name eq 'Multicolor'}">
                                                 <a href="javascript:void(0);" class="${facetValue.selected ? 'active' : ''}" title="${facetValue.name}" onclick="submitFacetForm('${facetValue.query.query.value}');"><span style="background-image:url(${siteResourcePath}/images/multicolor.png);">${facetValue.name}</span></a>
                                              </c:when>
                                              <c:otherwise>
                                                <a href="javascript:void(0);" class="${facetValue.selected ? 'active' : ''}" title="${facetValue.name}" onclick="submitFacetForm('${facetValue.query.query.value}');"><span style="background: ${facetValue.name};">${facetValue.name}</span></a> 
                                              </c:otherwise>
                                              </c:choose>
                                               </c:forEach>
                                                </div>
                                        </div>
                                        </c:if>

                        <c:forEach items="${facetData.values}" var="facetValue">
                            <li><c:if test="${facetData.multiSelect}">
                                    <c:choose>
                                       <%--  <c:when test="${facetData.code eq 'price' or facetData.code eq 'priceRange' or facetData.code eq 'Color'}"> --%>
                                        <c:when test="${facetData.code eq 'price' or facetData.code eq 'Color'}">

                                        </c:when>
                                        <c:otherwise>
                                            <%-- <form action="/c/${categoryCode }/populateFacetsAndProducts" method="get" id="${facetValue.name}">
                                                <input type="hidden" name="min" value="${min}" /> <input type="hidden" name="max"
                                                    value="${max}" /> <input type="hidden" name="q" value="${facetValue.query.query.value}" />
                                                <input type="hidden" name="text" value="${searchPageData.freeTextSearch}" /> --%> 
                                                <input id="query${facetValue.name}" type="hidden" name="q" value="${facetValue.query.query.value}" />
                                                <label
                                                    class="facet_block-label"> <input type="checkbox" 
                                                    ${facetValue.selected ? 'checked="checked"' : ''}
                                                    onchange="submitFacetForm('${facetValue.query.query.value}');" /><span class="truncate" title="${facetValue.name}">${facetValue.name}</span><span
                                                    class="facetValueCount"><spring:theme code="search.nav.facetValueCount"
                                                            arguments="${facetValue.count}" /></span>
                                                </label>
                                            <!-- </form> -->
                                        </c:otherwise>
                                    </c:choose>

                                    <%-- <label class="facet_block-label">
                                            <input type="checkbox" ${facetValue.selected ? 'checked="checked"' : ''}
                                            onchange="$(this).closest('form').submit()" /> ${facetValue.name}&nbsp; <span
                                            class="facetValueCount"><spring:theme code="search.nav.facetValueCount"
                                                    arguments="${facetValue.count}" /></span>
                                        </label> --%>

                                </c:if> 
                                <c:if test="${not facetData.multiSelect}">
                                <c:set var="query" value="${facetValue.query.url}"></c:set>
                                <c:set var="index" value="${fn:split(query, '?')}"></c:set>
                                <%-- <c:set var="finalQuery" value="${fn:join(index, '/populateFacetsAndProducts?')}"></c:set> --%>
                                <c:set var="finalQuery" value="${fn:join(index, '/?')}"></c:set>
                                    <c:url value="${finalQuery}&min=${min }&max=${max }" var="facetValueQueryUrl" />
                                    <%-- <a href="javascript:void(0);" onclick="facetClick('${facetValueQueryUrl}')" title="${facetValue.name}">${facetValue.name}</a> --%>
                                    
                                    <a href="${facetValueQueryUrl}" title="${facetValue.name}">${facetValue.name}</a>
                                    <span class="facetValueCount"><spring:theme code="search.nav.facetValueCount"
                                            arguments="${facetValue.count}" /></span>
                                </c:if></li>
                        </c:forEach>
                    </ul>
                    <c:if test="${not empty facetData.topValues}">
                        <span class="more"> <a href="#" class="lessFacetValues"><spring:theme
                                    code="search.nav.facetShowLess" /></a>
                        </span>
                    </c:if>
                </div>
            </div>
        </ycommerce:testId>
    </div>
</c:if>
