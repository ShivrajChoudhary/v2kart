<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="jspcache" uri="/WEB-INF/common/tld/jspfragmentcache.tld"%>

<template:page pageTitle="${pageTitle}">
    <div class="row  category-section">
        <cms:pageSlot position="topRotatingBanner" var="feature">
            <cms:component component="${feature}" element="div" class="col-md-12" />
        </cms:pageSlot>

    <c:set scope="page" var="cacheKey" value="custom-category-pages-${cmsPage.uid}"/>
           
            <jspcache:cache key="${cacheKey}">
                <cms:pageSlot position="BannerSection1" var="feature">
                    <cms:component component="${feature}" element="div" class="col-md-4" />
                </cms:pageSlot>

                <cms:pageSlot position="BannerSection2" var="feature">
                    <cms:component component="${feature}" element="div" class="col-md-4" />
                </cms:pageSlot>
                <cms:pageSlot position="BannerSection3" var="feature">
                    <cms:component component="${feature}" element="div" class="col-md-8" />
                </cms:pageSlot>
            </jspcache:cache>
        

        <cms:pageSlot position="CarouselSection" var="feature">
            <cms:component component="${feature}" element="div" class="" />
        </cms:pageSlot>


    </div>
</template:page>
