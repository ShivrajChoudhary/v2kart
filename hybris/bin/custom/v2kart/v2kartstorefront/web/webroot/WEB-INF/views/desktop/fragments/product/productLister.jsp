<%@ page trimDirectiveWhitespaces="true" contentType="application/json"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="json" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="grid" tagdir="/WEB-INF/tags/desktop/grid"%>

<c:set var="skuIndex" scope="session" value="${skuIndex}" />

{
	"pagination" : {
		"currentPage":	"${searchPageData.pagination.currentPage}",
		"numberOfPages": "${searchPageData.pagination.numberOfPages}",
		"totalNumberOfResults" : "${searchPageData.pagination.totalNumberOfResults}",
		"searchResultsType" : "${searchResultType}"
	},
	
	"results":  "<spring:escapeBody javaScriptEscape="true">
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
</spring:escapeBody>",
	"skuIndex" : "${sessionScope.skuIndex}"
}




