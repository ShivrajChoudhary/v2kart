<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="breadcrumbs" required="true" type="java.util.List"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<c:url value="/" var="homeUrl"/>
<c:set var="themeMsgKey" value="${not empty msgKey ?  msgKey : 'search.mobile.page'}"/>
<h6 class="descriptionHeadline"><spring:theme code="text.headline.breadcrumbs" text="Breadcrumbs"/></h6>
<c:choose>
	<c:when test="${fn:length(breadcrumbs) gt 0 }">
		<c:forEach items="${breadcrumbs}" var="breadcrumb" varStatus="status" begin="${fn:length(breadcrumbs)-1}" end="${fn:length(breadcrumbs)}">
			<c:url value="${breadcrumb.url}" var="breadcrumbURL"/>
			<c:if test="${status.count eq 1}">
				<c:if test="${breadcrumb.url ne '#'}">
					<a href="#" style="text-decoration: none"><!--<span class="breadcrumbsArrow">&laquo;</span>&nbsp;-->${breadcrumb.name}&nbsp;
					<c:if test="${searchPageData.pagination.totalNumberOfResults > 0  }">
                		(<spring:theme code="${themeMsgKey}.totalResults" arguments="${searchPageData.pagination.totalNumberOfResults}" />)
					</c:if>
					</a>
				</c:if>
				<c:if test="${breadcrumb.url eq '#'}">
					<a href="${breadcrumb.url}" onclick="return false;" style="text-decoration: none"><!--<span class="breadcrumbsArrow">&laquo;</span>&nbsp;-->${breadcrumb.name}</a>&nbsp;
					<c:if test="${searchPageData.pagination.totalNumberOfResults > 0  }">
                		(<spring:theme code="${themeMsgKey}.totalResults" arguments="${searchPageData.pagination.totalNumberOfResults}" />)
					</c:if>
				</c:if>
			</c:if>
		</c:forEach>
	</c:when>
	<c:otherwise>
		<a href="${homeUrl}" style="text-decoration: none"><span class="breadcrumbsArrow">&laquo;</span>&nbsp;<spring:theme code="breadcrumb.home"/></a>
	</c:otherwise>
</c:choose>
