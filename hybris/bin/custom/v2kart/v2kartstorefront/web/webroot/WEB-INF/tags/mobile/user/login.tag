<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="actionNameKey" required="true" type="java.lang.String"%>
<%@ attribute name="action" required="true" type="java.lang.String"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/mobile/cart"%>
<script type="text/javascript">
function hideLoading(){
	$(".ui-loader").hide();
}
</script>


<div class="loginTag" data-theme="e" data-content-theme="b">
    <div class="heading">
        <h1>
            <spring:theme code="mobile.login.title" />
        </h1>
        <p>
            <spring:theme code="mobile.login.description" />
        </p>
        <p>
            <spring:theme code="form.required1" />
            <span class="mandatory"><spring:theme code="form.required.mark" /></span>
            <spring:theme code="form.required2" />
        </p>
    </div>
    <div>
        <form:form action="${action}" method="post" commandName="loginForm" name="LoginForm" data-ajax="false" onsubmit="return loginFormValidate()">
            <common:errors />
            <c:if test="${not empty accErrorMsgs}">
                <span class="form_field_error">
            </c:if>
            <ul class="mFormList" data-theme="b" data-content-theme="b">
                <li><div id="loginError">
                        <formElement:formInputBox idKey="j_username" labelKey="login.email" path="j_username" inputCSS="text"
                            mandatory="true" />
                    </div></li>
                <li><div id="passwordError">
                        <formElement:formPasswordBox idKey="j_password" labelKey="login.password" path="j_password" inputCSS="text password"
                            mandatory="true" />
                    </div></li>

                <%-- <cart:cartExpressCheckoutEnabled /> --%>

				<div class="form_field_error-message" style="background-color: #f5f5f5;">
		            <input type="checkbox" name="_spring_security_remember_me" id="rememberme"> <label for="rememberme"
		                style="font-weight: 600; background: #f5f5f5;">Remember Me</label>
		        </div>
                <li>
                    <h6 class="descriptionHeadline">
                        <spring:theme code="text.headline.login" text="Click here to login" />
                    </h6> <ycommerce:testId code="login_button">
                        <button type="submit" class="form" data-role="button" data-theme="b">
                            <spring:theme code="${actionNameKey}" />
                        </button>
                    </ycommerce:testId>
                </li>
                <li><ycommerce:testId code="forgotten_password">
                        <a href="#forgotPassword" data-url="<c:url value="/login/pw/request"/>" class="password-forgotten"> <spring:theme
                                code="login.link.forgottenPwd" />
                        </a>
                    </ycommerce:testId></li>
            </ul>
        </form:form>
    </div>
</div>
