<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="hideHeaderLinks" required="false"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="header" tagdir="/WEB-INF/tags/desktop/common/header"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>

<div class="top-scroller">
	<div class="container">
		<div class="topparagraph">
			<cms:pageSlot position="HeaderStripeParagraph" var="feature">
				<cms:component component="${feature}"/>
			</cms:pageSlot>
		</div>
		<ul class="topnav clearfix">
	         <cms:pageSlot position="HeaderStripeLinks" var="link">
	             <cms:component component="${link}" element="li" />
	         </cms:pageSlot>
	     </ul>
	</div>
</div>

<%-- Test if the UiExperience is currently overriden and we should show the UiExperience prompt --%>
<c:if test="${uiExperienceOverride and not sessionScope.hideUiExperienceLevelOverridePrompt}">
    <c:url value="/_s/ui-experience?level=" var="clearUiExperienceLevelOverrideUrl" />
    <c:url value="/_s/ui-experience-level-prompt?hide=true" var="stayOnDesktopStoreUrl" />
    <div class="backToMobileStore">
        <a href="${clearUiExperienceLevelOverrideUrl}"><span class="greyDot">&lt;</span> <spring:theme code="text.swithToMobileStore" /></a>
        <span class="greyDot closeDot"><a href="${stayOnDesktopStoreUrl}">x</a></span>
    </div>
</c:if>
<c:set var="userlogin" value="false"/>

<div class="container">
    <div class="row">
        <div class="col-xs-12 col-sm-5 col-md-3 logo">
            <cms:pageSlot position="SiteLogo" var="logo" limit="1">
                <cms:component component="${logo}" class="sitelogo" />
            </cms:pageSlot>
        </div>

        <div id="header">
            <div class="col-xs-12 col-sm-7 col-md-5" id="search">
                <%-- <div class="headerContent ">
                    <ul class="topnav clearfix">
                        <cms:pageSlot position="HeaderLinks" var="link">
                            <cms:component component="${link}" element="li" />
                        </cms:pageSlot>
                    </ul>
                </div>
                <div class="clear"></div> --%>

                <cms:pageSlot position="SearchBox" var="component">
                    <cms:component component="${component}" />
                </cms:pageSlot>
            </div>
            <div class="col-xs-12 col-sm-12 col-md-4 text-right pLeft0">
                <div class="account-link">
                    <sec:authorize ifNotGranted="ROLE_ANONYMOUS">
                        <div class="btn-group" role="group" aria-label="Large button group">
                            <ycommerce:testId code="header_LoggedUser">
                                <a href="<c:url value="/my-account"/>" class="btn btn-red"> <spring:theme code="header.welcome.user"
                                        arguments="${user.firstName},${user.lastName}" htmlEscape="true" /></a>
                            </ycommerce:testId>



                            <a class="btn btn-red dropdown-toggle" data-toggle="dropdown" aria-expanded="false">Account <span
                                class="caret"></span></a>
                            <ul class="dropdown-menu" role="menu">
                                <li><a title="My Profile" href="<c:url value="/my-account/profile"/>"><spring:theme
                                            code="header.link.myAccount.dropdown.myProfile" text="My Profile" /></a></li>
                                <li><a title="My Address" href="<c:url value="/my-account/address-book"/>"><spring:theme
                                            code="header.link.myAccount.dropdown.addressBook" text="My Address" /></a></li>
                                <li><a title="My orders" href="<c:url value="/my-account/orders"/>"><spring:theme
                                            code="header.link.myAccount.dropdown.myOrders" text="My Orders" /></a></li>
                                <li><a title="My wishlist" href="<c:url value="/my-account/wishlist"/>"><spring:theme
                                            code="header.link.myAccount.dropdown.myWishlist" text="My Wishlist" /></a></li>
                                <li><a title="My wallet" href="<c:url value="/my-account/wallet"/>"><spring:theme
                                            code="header.link.myAccount.dropdown.myWallet" text="My Wallet" /></a></li>
                                <li><a title="My Password" href="<c:url value="/my-account/update-password"/>"><spring:theme
                                            code="header.link.myAccount.dropdown.myPassword" text="My Password" /></a></li>
                                <li class="divider"></li>
                                <li><ycommerce:testId code="header_signOut">
                                        <a href="<c:url value='/logout'/>"><spring:theme code="header.link.logout" /></a>
                                    </ycommerce:testId></li>
                            </ul>
                        </div>
                        <c:set var="userlogin" value="true"/>
                    </sec:authorize>

                    <sec:authorize ifAnyGranted="ROLE_ANONYMOUS">
                        <div class="btn-group" role="group" aria-label="Large button group">
                            <ycommerce:testId code="header_Login_link">
                                <a class="btn btn-red" href="<c:url value="/login"/>"><span class="glyphicon glyphicon-lock"></span>
                                    LOGIN</a>
                                <a class="btn btn-red" href="<c:url value="/login"/>">SIGN UP</a>
                            </ycommerce:testId>
                        </div>
                        <c:set var="userlogin" value="false"/>
                    </sec:authorize>
                </div>
                <cms:pageSlot position="MiniCart" var="cart" limit="1">
                    <cms:component component="${cart}" element="li" class="myCartBx miniCart" />
                </cms:pageSlot>
                <input type="hidden" id="check_user_login" value="${userlogin}">
            </div>
        </div>
    </div>
</div>
