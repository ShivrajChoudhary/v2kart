<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/desktop/storepickup"%>

<template:page pageTitle="${pageTitle}">

    <jsp:attribute name="pageScripts">
              <script type="text/javascript" src="${siteResourcePath}/js/acc.productlisting.js"></script>
       </jsp:attribute>
    <jsp:body>
        
    <div id="globalMessages">
        <common:globalMessages />
    </div>
    <div id="plpSpinner" class="" style="display:none;">
        <img src="${siteResourcePath}/images/spinner.gif" />
    </div>
    <div class="row">
    <div class="col-xs-12 col-sm-12 col-md-12">
            <div class="plpbanner">
            <cms:pageSlot position="TopCategoryBanner" var="feature">
                                <cms:component component="${feature}" />
                                <%-- <img src="${categoryPictureUrl}" alt="Fashion banner" class="img-responsive"> --%>
                            </cms:pageSlot>
                </div>
          </div>
    <cms:pageSlot position="Section1" var="feature">
        <cms:component component="${feature}" element="div" class="span-24 section1 cms_disp-img_slot" />
    </cms:pageSlot>
    <div id="categoryPage">
    <div class="col-md-3">
<%--         <div class="facetNavigation  ${searchPageData.pagination.totalNumberOfResults eq 0 ? 'noFacet' : ''} "> --%>
        <div class="${not empty categoryMin and not empty categoryMax ? 'facetNavigation': ''}">
            <cms:pageSlot position="ProductLeftRefinements" var="feature">
                <cms:component component="${feature}" />
            </cms:pageSlot>
        </div>
    </div>
    <div class="col-md-9">
    <cms:pageSlot position="ProductListSlot" var="feature">
                <cms:component component="${feature}" />
            </cms:pageSlot>
    </div>
    </div>
    <storepickup:pickupStorePopup />
    
    </div>
    
    </jsp:body>

</template:page>
