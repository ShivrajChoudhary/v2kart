<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>

<c:if test="${navigationNode.visible}">
	<div class="accountNav">
		<div class="headline">
			${navigationNode.title}
            
		</div>
		<ul>
			<c:forEach items="${navigationNode.links}" var="link">
				<c:set value="${ (((requestScope['javax.servlet.forward.servlet_path'] == link.url)||(link.url == '/my-account/update-password' && requestScope['javax.servlet.forward.servlet_path'] == '/my-account/update-password')||(link.url == '/my-account/profile' && requestScope['javax.servlet.forward.servlet_path'] == '/my-account/update-profile')) || ( link.url == '/my-account/orders' && fn:contains(requestScope['javax.servlet.forward.servlet_path'], '/my-account/order/')) || ( link.url == '/my-account/orders' && fn:contains(requestScope['javax.servlet.forward.servlet_path'], '/my-account/orders/all')) ||( link.url == '/my-account/orders' && fn:contains(requestScope['javax.servlet.forward.servlet_path'], '/my-account/orders/recentOrders')) || ( link.url == '/my-account/address-book' && fn:contains(requestScope['javax.servlet.forward.servlet_path'], '/my-account/edit-address/')) || ( link.url == '/my-account/address-book' && fn:contains(requestScope['javax.servlet.forward.servlet_path'], '/my-account/add-address')) ? 'active':'') }" var="selected"/>
				<cms:component component="${link}" evaluateRestriction="true" element="li" class=" ${selected}"/>
			</c:forEach>
		</ul>
	</div>
</c:if>
