
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>

<template:page pageTitle="${pageTitle}">
    <div id="globalMessages">
        <common:globalMessages />
    </div>


    <div class=" last accountContentPane">
        <cms:pageSlot position="TopContent" var="feature" element="div">
            <cms:component component="${feature}" element="div" class="clearfix" />
        </cms:pageSlot>

        <cms:pageSlot position="BodyContent" var="feature" element="div">
            <cms:component component="${feature}" element="div" class="clearfix" />
        </cms:pageSlot>

        <cms:pageSlot position="BottomContent" var="feature" element="div">
            <cms:component component="${feature}" element="div" class="clearfix" />
        </cms:pageSlot>
    </div>


</template:page>
