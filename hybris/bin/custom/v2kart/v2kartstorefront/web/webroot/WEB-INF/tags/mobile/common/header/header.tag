<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="hideHeaderLinks" required="false"%>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/mobile/cart"%>
<%@ taglib prefix="header" tagdir="/WEB-INF/tags/mobile/common/header"%>


<h6 class="descriptionHeadline">
    <spring:theme code="text.headline.navigationbar" text="Browse through the navigation bar" />
</h6>

<div id="topHeader">
    <cms:pageSlot position="TopHeaderSlot" var="component">
        <cms:component component="${component}" />
    </cms:pageSlot>
</div>

<div id="navSlot" class="ui-bar-b" data-theme="b">
    <cms:pageSlot position="NavigationSlot" var="component">
        <cms:component component="${component}" />
    </cms:pageSlot>
</div>

<div id="bottomHeader" data-role="content" class="ui-bar-b">
    <cms:pageSlot position="BottomHeaderSlot" var="component">
        <cms:component component="${component}" />
    </cms:pageSlot>
</div>
<div>
<sec:authorize ifNotGranted="ROLE_ANONYMOUS">
    <c:set var="userlogin" value="true" />
</sec:authorize>

<sec:authorize ifAnyGranted="ROLE_ANONYMOUS">
    <c:set var="userlogin" value="false" />
</sec:authorize>

<input type="hidden" id="mobile_check_user_login" value="${userlogin}">
</div>
