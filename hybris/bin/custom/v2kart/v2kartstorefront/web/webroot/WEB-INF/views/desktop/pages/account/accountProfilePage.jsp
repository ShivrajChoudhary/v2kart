<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="headline"><spring:theme code="text.account.profile" text="Profile" /></div>

<table class="account-profile-data">
    <%-- 	<tr>
		<td><strong><spring:theme code="profile.title" text="Title"/>: </strong></td>
		<td>${fn:escapeXml(title.name)}</td>
	</tr> --%>
    <tr>
        <td><strong><spring:theme code="profile.firstName" text="First name" />: </strong></td>
        <td>${fn:escapeXml(customerData.firstName)}</td>
    </tr>
    <tr>
        <td><strong><spring:theme code="profile.lastName" text="Last name" />: </strong></td>
        <td>${fn:escapeXml(customerData.lastName)}</td>
    </tr>
    <tr>
        <td><strong><spring:theme code="profile.mobileNumber" text="E-mail" />: </strong></td>
        <td>${fn:escapeXml(customerData.mobileNumber)}</td>
    </tr>
    <tr>
        <td><strong><spring:theme code="profile.email" text="E-mail" />: </strong></td>
        <td class="style-LowerCase">${fn:escapeXml(customerData.displayUid)}</td>
    </tr>
    <c:if test="${not empty customerData.dateOfBirth}">
        <tr>
            <td class="removeCapitalization"><strong><spring:theme code="profile.dateOfBirth" text="Date of Birth" />: </strong></td>
            <td><fmt:formatDate value="${customerData.dateOfBirth}" type="date" pattern="dd/MM/yyyy" /></td>
        </tr>
    </c:if>
    <c:if test="${not empty customerData.maritalStatus}">
        <tr>
            <td><strong><spring:theme code="profile.maritalStatus" text="Marital Status" />: </strong></td>
            <td>${fn:escapeXml(customerData.maritalStatus)}</td>
        </tr>
    </c:if>
    <c:if test="${not empty customerData.gender}">
        <tr>
            <td><strong><spring:theme code="profile.gender" text="Gender" />: </strong></td>
            <td>${fn:escapeXml(customerData.gender)}</td>
        </tr>
    </c:if>
</table>

<a class="btn btn-red" href="update-password"><spring:theme code="text.account.profile.changePassword" text="Change password" /></a>
<a class="btn btn-red" href="update-profile"><spring:theme code="text.account.profile.updatePersonalDetails" text="Update personal details" /></a>
<!-- Uncomment if update email functionality is required -->
<%-- <a class="button" href="update-email"><spring:theme code="text.account.profile.updateEmail" text="Update email"/></a> --%>
