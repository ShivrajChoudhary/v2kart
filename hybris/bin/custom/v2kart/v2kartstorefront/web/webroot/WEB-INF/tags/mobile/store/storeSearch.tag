<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="isStoreDetailsPage" required="false" type="java.lang.Boolean"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement"%>

<div class="item_container_holder storeFinderSearch">
    <h6 class="descriptionHeadline">
        <spring:theme code="text.headline.findstore" text="Find your store" />
    </h6>
    <div class="storeSearch" data-theme="d">
        <c:if test="${not isStoreDetailsPage}">
            <div class="ui-bar ui-bar-b">
                <h1>
                    <spring:theme code="storeFinder.store.locator" />
                </h1>
            </div>
        </c:if>
        <div data-role="fieldcontain" class="innerContent accmob-storeSearch">
            <c:url value="/store-finder" var="storeFinderFormAction" />
            <form:form action="${storeFinderFormAction}" method="get" commandName="storeFinderForm">
                <common:errors />
                <ycommerce:testId code="storeFinder_search_box">
                    <div class="accmob-storeFinderFieldHolder">
                        <label for="storelocator-query" class="skip"> <%-- <spring:theme code="storeFinder.stores.nearby"/> --%>
                        </label> <input id="storelocator-query" name="q" class="storeSearchBox" type="search"
                            placeholder="<spring:theme code="storelocator.query"/>" data-theme="b">
                        <div class="accmob-storeSearch-trigger">
                            <button class="form search" data-role="button" data-theme="b">
                                <span class="search-icon"> <spring:theme code="storeFinder.search" />
                                </span>
                            </button>
                        </div>
                    </div>
                </ycommerce:testId>
            </form:form>
            <c:url value="/store-finder/position" var="nearMeStorefinderFormAction" />
            <form:form id="nearMeStorefinderForm" name="near_me_storefinder_form" method="POST" action="${nearMeStorefinderFormAction}">
                <input type="hidden" id="latitude" name="latitude" />
                <input type="hidden" id="longitude" name="longitude" />
                <a href="#" id="findStoresNearMe" class="form search" data-theme="b" data-role="button"> <span class="search-icon"><spring:theme
                            code="storeFinder.findStoresNearMe" /></span>
                </a>
            </form:form>
        </div>
    </div>
</div>
