<%@ page trimDirectiveWhitespaces="true" contentType="application/json"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product"%>
<%@ taglib prefix="json" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="grid" tagdir="/WEB-INF/tags/mobile/grid"%>
<%-- <%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav" %> --%>

<c:set var="skuIndex" scope="session" value="${skuIndex}" />

{
	"pagination" : {
		"currentPage":	"${searchPageData.pagination.currentPage}",
		"numberOfPages": "${searchPageData.pagination.numberOfPages}",
		"totalNumberOfResults" : "${searchPageData.pagination.totalNumberOfResults}",
		"searchResultsType" : "${searchResultType}"
	},
	
	"results":  "<spring:escapeBody javaScriptEscape="true">
   
        <c:forEach items="${searchPageData.results}" var="product" varStatus="status">
				<product:productListerItem product="${product}"/>
        </c:forEach>
   
</spring:escapeBody>",
	"skuIndex" : "${sessionScope.skuIndex}"
}




