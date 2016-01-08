<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="cat-banner" data-role="content" data-theme="b">
    <%-- <div class="heading heading-border">${title}</div> --%>
    <c:url value="${urlLink}" var="encodedUrl" />
    <c:choose>
        <c:when test="${empty encodedUrl || encodedUrl eq '#'}">
            <img title="${media.altText}" alt="${media.altText}" src="${media.url}">
        </c:when>
        <c:otherwise>
            <a href="${encodedUrl}"><img title="${media.altText}" alt="${media.altText}" src="${media.url}"></a>
        </c:otherwise>
    </c:choose>
</div>