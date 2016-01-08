<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>

<c:if test="${navigationNode.visible}">

	<div class="item_container" data-theme="e">

		<div>
			<h1 class="heading">
				${navigationNode.title}
			</h1>
		</div>
        <div class="innerContent">
		<ul class="accountNavigation" data-role="listview" data-inset="true" data-theme="b">
			<c:forEach items="${navigationNode.links}" var="link">
				<cms:component component="${link}" evaluateRestriction="true" element="li"/>
			</c:forEach>
		</ul>
        </div>
	</div>
</c:if>