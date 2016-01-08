<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<c:url var="profileUrl" value="/my-account/profile" />
<div class="span-24">
    <div class="span-20 last">

        <div class="accountContentPane clearfix updatePasswordformwidth">
            <div class="headline">
                <spring:theme code="text.account.profile.updatePasswordForm" text="Update Password" />
            </div>
            <div class="required right">
                <spring:theme code="form.required" text="Fields marked * are required" />
            </div>
            <div class="description">
                <spring:theme code="text.account.profile.updatePassword" text="Please use this form to update your account password" />
            </div>

            <form:form action="update-password" method="post" commandName="updatePasswordForm" autocomplete="off">
                <table>
                    <tr>
                        <td><formElement:formPasswordBox idKey="profile.currentPassword" labelKey="profile.currentPassword"
                                path="currentPassword" inputCSS="text password" mandatory="true" /></td>
                    </tr>
                    <tr >
                        <td><formElement:formPasswordBox idKey="profile-newPassword" labelKey="profile.newPassword" path="newPassword"
                                inputCSS="text password strength" mandatory="true" /></td>
                    </tr>
                    <tr>
                        <td><formElement:formPasswordBox idKey="profile.checkNewPassword" labelKey="profile.checkNewPassword"
                                path="checkNewPassword" inputCSS="text password" mandatory="true" /></td>
                    </tr>
                </table>

                <div class="form-actions">
                    <ycommerce:testId code="profilePage_SaveUpdatePasswordButton">
                        <button class="btn btn-red" type="submit">
                            <spring:theme code="text.account.profile.updatePasswordForm" text="Update Password" />
                        </button>
                    </ycommerce:testId>
                    <button type="button" class="btn btn-secondary" onclick="window.location='${profileUrl}'">
                        <spring:theme code="text.account.profile.cancel" text="Cancel" />
                    </button>

                </div>
            </form:form>

        </div>
    </div>
</div>


