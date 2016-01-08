<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="jspcache" uri="/WEB-INF/common/tld/jspfragmentcache.tld"%>

<template:page>
    <div>
        <div id="globalMessages">
            <common:globalMessages />
        </div>
        <div class="row" id="mainSliderSlot">
            <div class="col-md-9">
                <cms:pageSlot position="Section1A" var="feature">
                    <cms:component component="${feature}" element="div" class="cms_disp-img_slot" />
                </cms:pageSlot>
            </div>
            <div class="col-md-3">
                <div class="side-banner">
                    <div class="heading">Super Saver</div>
                    <div class="row">
                        <cms:pageSlot position="Section1B" var="feature" element="div" class="col-xs-6 col-md-12 zone_a thumbnail_detail">
                            <cms:component component="${feature}" />
                        </cms:pageSlot>
                        <cms:pageSlot position="Section1C" var="feature" element="div" class="col-xs-6 col-md-12 zone_b thumbnail_detail">
                            <cms:component component="${feature}" />
                        </cms:pageSlot>
                    </div>
                </div>
            </div>

        </div>
        <div class="row">
            <cms:pageSlot position="Section2A" var="feature" element="div" class="col-xs-6 col-sm-4 col-md-4">
                <cms:component component="${feature}" />
            </cms:pageSlot>
            <cms:pageSlot position="Section2B" var="feature" element="div" class="col-xs-6 col-sm-4 col-md-4">
                <cms:component component="${feature}" />
            </cms:pageSlot>
            <cms:pageSlot position="Section2C" var="feature" element="div" class="col-xs-6 col-sm-4 col-md-4">
                <cms:component component="${feature}" />
            </cms:pageSlot>
        </div>
         <jspcache:cache key="desktop-hompage-carousel">
        <div class="row">
            <cms:pageSlot position="Section3" var="feature" element="div" class="cms_disp-img_slot">
                <cms:component component="${feature}" />
            </cms:pageSlot>
        </div>
        </jspcache:cache>
    </div>
</template:page>