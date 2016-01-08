<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="actionNameKey" required="true" type="java.lang.String"%>
<%@ attribute name="action" required="true" type="java.lang.String"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<div class="userRegister">
    <div class="headline">
        <spring:theme code="register.new.customer" />
    </div>
    <div class="required right">
        <%-- <spring:theme code="form.required" /> --%>
    </div>
    <div class="description">
        <spring:theme code="register.description" />
    </div>


    <form:form method="post" commandName="registerForm" action="${action}">
        <div class="form_field-elements js-recaptcha-captchaaddon">
            <table style="width: 100%;">
               <%--  <tr>
                    <td colspan="2"><formElement:formSelectBox idKey="register.title" labelKey="register.title" path="titleCode"
                            mandatory="true" skipBlank="false" skipBlankMessageKey="form.select.empty" items="${titles}" /></td>
                </tr> --%>
                <tr>
                    <td colspan="2"><formElement:formInputBox idKey="register.firstName" labelKey="register.firstName" path="firstName"
                            inputCSS="text" mandatory="true" /></td>
                </tr>
                <tr>
                    <td colspan="2"><formElement:formInputBox idKey="register.lastName" labelKey="register.lastName" path="lastName"
                            inputCSS="text" mandatory="true" /></td>
                </tr>
                <tr>
                    <td colspan="2">
                        <table style="width: 100%;">
                            <tr>
                                <td style = "width :55px;"><input id="isdCode" type="text" disabled="disabled" readonly="readonly" placeholder="+91" name="isdCode" tabindex="-1"
                                    style="width: 80%; height:30px; margin-top:25px; background-color: #eeeeee;"></td>
                                <td class="foReqFld"><formElement:formInputBox idKey="register.mobileNumber" labelKey="register.mobileNumber"
                                        path="mobileNumber" inputCSS="text"  mandatory="true" maxLength="10" labelCSS="phoneNumberLabel" autocomplete="off"/></td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td colspan="2"><formElement:formInputBox idKey="register.email" labelKey="register.email" path="email"
                            inputCSS="text" mandatory="true" /></td>
                </tr>
                <tr>
                    <td colspan="2" class = "passwordHelpLine"><formElement:formPasswordBox idKey="password" labelKey="register.pwd" path="pwd"
                            inputCSS="text password strength" mandatory="true" /></td>
                </tr>
                <tr>
                    <td colspan="2"><formElement:formPasswordBox idKey="register.checkPwd" labelKey="register.checkPwd" path="checkPwd"
                            inputCSS="text password" mandatory="true"/></td>
                </tr>
                <tr>
                    <td colspan="2"><input type="hidden" id="recaptchaChallangeAnswered"
                        value="${requestScope.recaptchaChallangeAnswered}" /></td>
                </tr>
            </table>

        </div>
        <div class="form-actions clearfix">
            <ycommerce:testId code="register_Register_button">
                <button type="submit" class="btn btn-red">
                    <spring:theme code='${actionNameKey}' />
                </button>
            </ycommerce:testId>
        </div>
    </form:form>
</div>



