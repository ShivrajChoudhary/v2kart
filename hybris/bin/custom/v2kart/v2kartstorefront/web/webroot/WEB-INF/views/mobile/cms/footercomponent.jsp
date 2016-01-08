<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<div id="navbar" data-role="navbar" data-theme="d" data-iconpos="top">
<ul>
<c:forEach items="${navigationNodes}" var="node">
<c:if test="${(node.uid eq 'MobileFooterNavNode')}">
	<c:if test="${node.visible}">
		<c:forEach items="${node.links}" step="${component.wrapAfter}" varStatus="i">
			<c:forEach items="${node.links}" var="childlink" varStatus="status" begin="${i.index}" end="${i.index + component.wrapAfter - 1}">
			<c:if test="${i.index eq 0}">
				 <c:if test="${not status.last}">
					 <div class="footerfirstrow">
						<cms:component component="${childlink}" evaluateRestriction="true" element="li" />
					 </div>
				 </c:if>
				 <c:if test="${status.last}">
					 <div class="footerfirstrow footerlast">
						<cms:component component="${childlink}" evaluateRestriction="true" element="li" />
					</div>
				 </c:if>
			</c:if>
			</c:forEach>
		</c:forEach>
	</c:if>
	</c:if>
</c:forEach>
</ul>
</div>
<div id="footerContainer" class="footer-nav-bar-layer accmob-topMenu header-popup menu-container" style="left: 11px;display: none;" data-theme="a">
<ul data-role="listview">
<c:forEach items="${navigationNodes}" var="node">
	<c:if test="${(node.uid eq 'MobileFooterNavNode')}">
		<c:if test="${node.visible}">
				<c:forEach items="${node.links}" step="${component.wrapAfter}" varStatus="i">
					<c:forEach items="${node.links}" var="childlink" varStatus="status" begin="${i.index}" end="${i.index + component.wrapAfter - 1}">
						<c:if test="${i.index eq 4 or i.index eq 8}">
					  		<li><a ${childlink.styleAttributes} href="${childlink.url}">${childlink.linkName}</a></li>
					    </c:if>
				  </c:forEach>
			  </c:forEach>
		  </c:if>
	  </c:if>
  </c:forEach>
</ul>
</div>
 <c:forEach items="${navigationNodes}" var="node">
    	 <c:if test="${(node.uid eq 'MobileFollowUsNavNode')}">
    	 <c:if test="${node.visible}">
			<c:forEach items="${node.links}" step="${component.wrapAfter}" varStatus="i">
				<c:forEach items="${node.links}" var="childlink" begin="${i.index}" end="${i.index + component.wrapAfter - 1}">
					<cms:component component="${childlink}" evaluateRestriction="true" element="li" />
				</c:forEach>
			</c:forEach>
	</c:if>
    	 </c:if>
</c:forEach>
<c:forEach items="${navigationNodes}" var="node">
    	 <c:if test="${(node.uid eq 'MobilePolicyNavNode')}">
    	 <c:if test="${node.visible}">
			<c:forEach items="${node.links}" step="${component.wrapAfter}" varStatus="i">
				<c:forEach items="${node.links}" var="childlink" begin="${i.index}" end="${i.index + component.wrapAfter - 1}">
					<cms:component component="${childlink}" evaluateRestriction="true" element="li" />
				</c:forEach>
			</c:forEach>
	</c:if>
    	 </c:if>
</c:forEach>