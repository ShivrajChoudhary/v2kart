<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<c:set value="${component.styleClass} ${dropDownLayout}" var="bannerClasses" />

<li class="La ${bannerClasses} auto <c:if test="${not empty component.navigationNode.children}"> parent</c:if>"><cms:component
        component="${component.link}" evaluateRestriction="true" /> <c:if test="${not empty component.navigationNode.children}">
        <ul class="Lb">
            <div class="leftmenu left">
                <c:forEach items="${component.navigationNode.children}" var="childNode">
                    <c:if test="${childNode.visible}">
                        <c:if test="${not empty childNode.children}">
                            <li class="Lb"><c:forEach items="${childNode.children}" var="child">
                                <span class="nav-submenu-title">${child.title}</span> <c:forEach items="${child.links}"
                                    step="${fn:length(child.links)}" varStatus="i">
                                        <ul class="Lc ${i.count < 2 ? 'left_col' : 'right_col'}">
                                            <c:forEach items="${child.links}" var="childlink" begin="${i.index}"
                                                end="${fn:length(child.links)}">
                                                <cms:component component="${childlink}" evaluateRestriction="true" element="li"
                                                    class="Lc ${i.count < 2 ? 'left_col' : 'right_col'}" />
                                            </c:forEach>
                                        </ul>
                                    </c:forEach>
                            </c:forEach></li>
                        </c:if>
                    </c:if>
                </c:forEach>
            </div>
            <div class="right">
                <c:if test="${not empty component.banner}">
                    <ul class="Lc left_col">
                        <li class="yCmsComponent Lc left_col"><c:url value="${banner.urlLink}" var="encodedUrl" /> <a
                            href="${encodedUrl}"><cms:component component="${component.banner}" evaluateRestriction="true" /></a></li>
                    </ul>
                </c:if>
            </div>
        </ul>
    </c:if></li>
