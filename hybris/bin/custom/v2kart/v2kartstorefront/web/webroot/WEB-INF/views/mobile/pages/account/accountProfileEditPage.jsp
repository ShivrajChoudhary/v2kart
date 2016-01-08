<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>

<div class="accountProfileEdit">
	<div class="heading">
			<h1>
				<spring:theme code="mobile.text.account.profile" text="Update Profile" />
			</h1>
			<p><spring:theme code="text.account.profile.updateForm" text="Please use this form to update your personal details" /></p>
			<p><spring:theme code="mobile.review.required1" /><span class="mandatory"><spring:theme code="mobile.review.required.mark" /></span><spring:theme code="mobile.review.required2" /></p>
	</div>
	<div class="innerContent">
			<c:url value="/my-account/update-profile" var="updateProfileUrl" />
			<form:form action="${updateProfileUrl}" method="post" commandName="v2UpdateProfileForm">
				<ul class="mFormList" data-theme="c" data-content-theme="c">
					<%-- <li><formElement:formSelectBox idKey="profile.title" labelKey="profile.title" path="titleCode" mandatory="true" skipBlank="false" skipBlankMessageKey="form.select.empty" items="${titleData}" /></li> --%>
					<li><formElement:formInputBox idKey="profile.firstName" labelKey="profile.firstName" path="firstName" inputCSS="text" mandatory="true" /></li>
					<li><formElement:formInputBox idKey="profile.lastName" labelKey="profile.lastName" path="lastName" inputCSS="text" mandatory="true" /></li>
					<li><formElement:formInputBox idKey="profile.email" labelKey="profile.email" path="email" inputCSS="text" mandatory="false" disabled="true" className="readOnly_inputBox" /></li>
					 <form:input type="hidden" path="email"  />
					<li>
						<template:errorSpanField path="mobileNumber">
                            <ycommerce:testId code="profile.mobileNumber">
                            <div data-role="fieldcontain" data-theme="c" data-content-theme="c" class="ui-field-contain ui-body ui-br">
                                <label for="profile.mobileNumber"> <spring:theme code="profile.mobileNumber" /> <span class="mandatory">
                                        <spring:theme code="login.required" var="loginrequiredText" /> *
                                </span>
                                </label>
                                <div class="ui-grid-a">
                                    <div class="isoCode readOnly_inputBox">
                                        <form:input id="" path="" cssClass="text" readOnly="true" value="+91" />
                                    </div>
                                    <div class="mobileNo">
                                        <form:input cssClass="text" id="profile.mobileNumber" class="text ui-input-text ui-body-c" data-theme="c" type="text" path="mobileNumber" />
                                    </div>
                                    <span class="skip"><form:errors path="mobileNumber" /></span>
                                </div>
                                </div>
                            </ycommerce:testId>
                        </template:errorSpanField> 
					</li>
					<li><formElement:formDateBox idKey="profile-dateOfBirth" labelKey="profile.dateOfBirth" path="mobileDateOfBirth" inputCSS="profile-dateOfBirth"  /></li>
					<li>
					
					
					<li>
					<div data-role="fieldcontain">
					<label for="maritalStatus">
						<spring:theme code="profile.maritalStatus" />
					</label>
					<template:errorSpanField path="maritalStatus">
		                                <form:select id="maritalStatus" path="maritalStatus" data-theme="c">
										<option value=""> <spring:theme code="profile.selectMaritalStatus" /> </option> 
										<form:options items="${maritalStatusList}"/>
										</form:select>
								</template:errorSpanField>
								</div>
								</li>
					<li style="margin-top: 1em;margin-bottom: 1em;">
					<label style="font-weight: 400;">
						<spring:theme code="profile.gender" />
					</label>
					<fieldset data-role="controlgroup" data-mini="true">
					<div class="ui-grid-a">
					<div class="ui-block-a" style="width: 40%">
					<label style="border: none;background-image: none;font-weight: normal;background-color: #f5f5f5"><form:radiobutton path="gender" value="male"/> <spring:theme
                                            code="profile.gender.male" /></label>
					</div>
					<div class="ui-block-b">
					 <label  style="border: none;background-image: none;font-weight: normal;background-color: #f5f5f5"><form:radiobutton
                                            path="gender" value="female" /> <spring:theme code="profile.gender.female" /></label>
					</div>
					</div>
					</fieldset>
					</li>
					<li>
						<fieldset class="ui-grid-a doubleButton">
							<div class="ui-block-a">
								<c:url value="/my-account/profile" var="profileUrl" />
								<ycommerce:testId code="profilePage_CancelButton">
									<a href="${profileUrl}" data-role="button" data-theme="g" class="ignoreIcon">
										<spring:theme code="text.button.cancel" />
									</a>
								</ycommerce:testId>
							</div>
							<div class="ui-block-b">
								<ycommerce:testId code="profilePage_SaveUpdatesButton">
									<button class="form" data-theme="b">
										<spring:theme code="text.button.save" />
									</button>
								</ycommerce:testId>
							</div>
						</fieldset>
					</li>
				</ul>
			</form:form>
	</div>
</div>