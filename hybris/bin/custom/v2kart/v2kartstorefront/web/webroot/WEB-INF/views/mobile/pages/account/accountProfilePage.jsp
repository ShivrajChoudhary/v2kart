<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<h1 class="heading">
	<spring:theme code="text.account.profile" text="Profile"/>
</h1>
<div class="item_container innerContent">
	<div data-theme="d">
			<ul class="mContentList profilePageData">
			<li class="profileDataName"><span class="profileheader">${fn:escapeXml(customerData.firstName)}</span>&nbsp;${fn:escapeXml(customerData.lastName)}</li>
			<li><span class="profileheader"><spring:theme code="profile.mobileNumber" text="E-mail" /></span>&nbsp;:&nbsp;${fn:escapeXml(customerData.mobileNumber)}</li>
			<li><span class="profileheader"><spring:theme code="profile.email" text="E-mail" /></span>&nbsp;:&nbsp;<span
				class="profileDataEmail">${fn:escapeXml(customerData.displayUid)}</span></li>
			<c:if test="${not empty customerData.dateOfBirth}">
				<li><span class="profileheader"><spring:theme code="profile.dateOfBirth"
						text="Date of Birth" /></span>&nbsp;:&nbsp;<fmt:formatDate
						value="${customerData.dateOfBirth}" type="date"
						pattern="dd/MM/yyyy" /></li>
			</c:if>
			<c:if test="${not empty customerData.maritalStatus}">
				<li><span class="profileheader"><spring:theme code="profile.maritalStatus"
						text="Marital Status" /></span>&nbsp;:&nbsp;${fn:escapeXml(customerData.maritalStatus)}</li>
			</c:if>
			<c:if test="${not empty customerData.gender}">
				<li class="profileDataGender"><span class="profileheader"><spring:theme
						code="profile.gender" text="Gender" /></span>&nbsp;:&nbsp;${fn:escapeXml(customerData.gender)}</li>
			</c:if>
		</ul>
	</div>
	<fieldset>
		<ycommerce:testId code="profile_update">
			<c:url value="/my-account/update-profile" var="updateProfileUrl" />
			<a href="${updateProfileUrl}" data-role="button" data-ajax="false" data-theme="b">
				<spring:theme code="text.account.profile.updateProfile.mobile" text="Update Profile" />
			</a>
		</ycommerce:testId>
		<ycommerce:testId code="password_update">
			<c:url value="/my-account/update-password" var="updatePasswordUrl" />
			<a href="${updatePasswordUrl}" data-role="button" data-ajax="false" data-theme="g">
				<spring:theme code="text.account.profile.changePassword.mobile" text="Change password" />
			</a>
		</ycommerce:testId>
	</fieldset>
</div>