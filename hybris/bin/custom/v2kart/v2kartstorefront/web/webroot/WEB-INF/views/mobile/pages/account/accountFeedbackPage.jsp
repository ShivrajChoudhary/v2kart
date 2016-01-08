<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<c:url var="feedbackUrl" value="/feedback" />

<div class="feedbackPage" data-content-theme="d" data-theme="e">
    <div class="heading">
        <h2>
            <spring:theme code="text.account.feedback" text="Feedback" />
        </h2>
        <spring:theme code="text.account.feedback.fillform" />
    </div>
    <form:form action="${feedbackUrl}" method="post" commandName="v2FeedbackForm" cssClass="form-horizontal">
        <div class="innerContent">
            <formElement:formInputBox idKey="feedback.email" labelKey="feedback.email" path="email" inputCSS="text" mandatory="true"
                placeholder="Email" />
            <template:errorSpanField path="mobileNumber">
                <ycommerce:testId code="feedback.mobileno">
                <div data-role="fieldcontain" data-theme="c" data-content-theme="c" class="ui-field-contain ui-body ui-br">
                    <label for="feedback.mobileNumber"> <spring:theme code="feedback.mobileNumber" /> <span class="mandatory">
                            <spring:theme code="login.required" var="loginrequiredText" /> *
                    </span>
                    </label>
                    <div class="ui-grid-a">
                        <div class="isoCode readOnly_inputBox">
                            <form:input id="" path="" cssClass="text" readOnly="true" value="+91" />
                        </div>
                        <div class="mobileNo">
                            <form:input cssClass="text" id="feedback.mobileNumber" class="text ui-input-text ui-body-c" data-theme="c" type="text" path="mobileNumber" />
                        </div>
                        <span class="form_field_error"><form:errors path="mobileNumber" /></span>
                    </div>
                    </div>
                </ycommerce:testId>
            </template:errorSpanField>
            <template:errorSpanField path="category">
                <ycommerce:testId code="feedback.category">
                    <label for="feedback.category" class="select-category"> <spring:theme code="feedback.category" /> <span class="mandatory">
                            <spring:theme code="login.required" var="loginrequiredText" /> *
                    </span>
                    </label>
                        <div class="feedbackCategory">
                            <form:select idKey="feedback.category"  path="category">
                                <option value="" >Select the category</option> 
                                <form:options items="${category}" mandatory="true" inlineError="true" />
                            </form:select>
                        </div>
                        <span class="form_field_error"><form:errors path="category" /></span>
                        <br>
                </ycommerce:testId>
            </template:errorSpanField>
            <template:errorSpanField path="rating">
                <ycommerce:testId code="feedback.rating">
                    <label for="feedback.rating" class="select-category"> <spring:theme code="feedback.rating" /> <span class="mandatory">
                            <spring:theme code="login.required" var="loginrequiredText" /> *
                    </span>
                    </label>
                        <div class="feedbacRrating">
                            <form:radiobuttons idKey="feedback.rating" path="rating" items="${rating}" class="feedbackradiostyle" inlineError="true"/>
                        </div>
                        <span class="form_field_error"><form:errors path="rating" /></span>
                </ycommerce:testId>
            </template:errorSpanField>
            <formElement:formTextArea idKey="feedback.message" labelKey="feedback.message" areaCSS="text" path="message" mandatory="true"/>
            <div id="sendBtn">
                <button class="btn btn-red" type="submit" data-theme="b">
                    <spring:theme code="text.account.feedback.submit" text="Send" />
                </button>
            </div>
        </div>
    </form:form>
</div>
