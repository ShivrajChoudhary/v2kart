<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<template:page pageTitle="${pageTitle}">
	<div class="item_container_holder_error">
		
		<div id="globalMessages">
			<common:globalMessages />
		</div>
		
		<div>
			
			<c:if test="${not empty message}">
			<h1>
			<spring:theme code="${message}" />
			</h1>
			</c:if>
			
		</div>

	</div>
</template:page>
