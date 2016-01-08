<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>

<c:url var="profileUrl" value="/my-account/profile" />
<div class="span-24">
    <div class="span-20 last">
        <div class="accountContentPane clearfix">
            <div class="headline">
                <spring:theme code="text.account.update.profile.heading" text="Update Profile" />
            </div>
            <div class="required right">
                <spring:theme code="form.required" text="Fields marked * are required" />
            </div>
            <div class="description">
                <spring:theme code="text.account.profile.updateForm" text="Please use this form to update your personal details" />
            </div>

            <form:form action="update-profile" method="post" commandName="v2UpdateProfileForm">
                <table>
                    <%-- <tr>
                        <td colspan="2"><formElement:formSelectBox idKey="profile.title" labelKey="profile.title" path="titleCode"
                                mandatory="true" skipBlank="false" skipBlankMessageKey="form.select.empty" items="${titleData}" /></td>
                    </tr> --%>
                    <tr>
                        <td><formElement:formInputBox idKey="profile.firstName" labelKey="profile.firstName" path="firstName"
                                inputCSS="text" mandatory="true" /></td>
                        <td><formElement:formInputBox idKey="profile.lastName" labelKey="profile.lastName" path="lastName"
                                inputCSS="text" mandatory="true" /></td>
                    </tr>
                    <tr>
                        <td>
                            <table>
                                <tr>
                                    <td><input id="isdCode" type="text" placeholder=" +91" name="isdCode" tabindex="-1"
                                        style="width: 41px; margin-top: 22px; height: 28px;" disabled></td>
                                    <td><formElement:formInputBox idKey="profile.mobileNumber" labelKey="profile.mobileNumber"
                                            maxLength="10" path="mobileNumber" inputCSS="text accountProfileUpdateMobile"
                                            labelCSS="mobileNumberLabel" mandatory="true" /></td>
                                </tr>
                            </table>
                        </td>
                        <td><formElement:formInputBox idKey="profile.email" labelKey="profile.email" path="email" inputCSS="email"
                                mandatory="false" disabled="true" />
                    </tr>
                    <tr>
                        <td><fmt:formatDate value="${v2UpdateProfileForm.dateOfBirth}" type="date" pattern="dd/MM/yyyy"
                                var="dobFormatted" /> <formElement:formInputBox idKey="profile-dateOfBirth" labelKey="profile.dateOfBirth"
                                path="dateOfBirth" inputCSS="profile-dateOfBirth" labelCSS="dateOfBirthLabel" mandatory="false"
                                value="${dobFormatted}" /></td>

                        <spring:theme code="profile.maritalStatus" var="maritalStatusPlaceholder" />
                        <td>
                                
                                <template:errorSpanField path="maritalStatus">
	                                <div class="controls">
                                    <label class="control-label genderLabel"><spring:theme code="profile.maritalStatus" /></label>
		                                <form:select id="${profile.maritalStatus}" path="maritalStatus">
										<option value="" > <spring:theme code="profile.selectMaritalStatus" /> </option> 
										<form:options items="${maritalStatusList}"/>
										</form:select>
									</div>
								</template:errorSpanField>
                                </td>
                    </tr>
                
                    <tr>
                        <td><div class="control-group">
                                <label class="control-label genderLabel"><spring:theme code="profile.gender" /></label>
                                <div class="controls">
                                    <label class="inline-control"><form:radiobutton path="gender" value="male" /> <spring:theme
                                            code="profile.gender.male" /></label> <label class="inline-control"><form:radiobutton
                                            path="gender" value="female" /> <spring:theme code="profile.gender.female" /></label>
                                </div>
                            </div></td>
                    </tr>
                </table>
                <div class="form-actions">
                    <ycommerce:testId code="profilePage_SaveUpdatesButton">
                        <button class="btn btn-red" type="submit">
                            <spring:theme code="text.account.profile.saveUpdates" text="Save Changes" />
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
