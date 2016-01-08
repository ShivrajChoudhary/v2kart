<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
    <cms:pageSlot position="NativeAppBodyContent" var="feature">
        <cms:component component="${feature}" element="div"/>
    </cms:pageSlot>
