<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<c:url var="feedbackUrl" value="/feedback" />

<div class="userRegister">
    <div class="headline">
        <spring:theme code="text.account.feedback " text="Feedback" />
    </div>
    <div class="description" style="font-weight: bold">
        <spring:theme code="text.account.feedback.fillform" />
    </div>
    <%-- 
                <form id="registerForm" class="form-horizontal" method="post" action="my-account.html">
                </form>
                 --%>

    <form:form action="${feedbackUrl}" method="post" commandName="v2FeedbackForm" cssClass="form-horizontal">
        <table id="feedbacktable">
            <tr>
                <td><formElement:formInputBox idKey="feedback.email" inlineError="true" labelKey="feedback.email" path="email"
                        inputCSS="form-control" labelCSS="col-sm-2 col-md-2 " mandatory="true" placeholder="Email" /></td>
            </tr>
            <tr>
                <td><formElement:formInputBox idKey="feedback.mobileNumber" inlineError="true" labelKey="feedback.mobileNumber"
                        path="mobileNumber" inputCSS="form-control" labelCSS="col-sm-2 col-md-2" mandatory="true" placeholder="MobileNumber" /></td>
            </tr>

            <tr>
                <td><template:errorSpanField path="category">

                        <label class="control-label col-sm-2 col-md-2 "> Category <span class="mandatory"> <spring:theme
                                    code="login.required" var="loginrequiredText" /> <img alt="" title=""
                                src="${commonResourcePath}/images/mandatory.png">
                        </span>
                        </label>

                        <form:select idKey="feedback.category" cssClass="feedbackSelect" path="category">
                            <option value="">Select the category</option>
                            <form:options items="${category}" />
                        </form:select>
                    </template:errorSpanField></td>
            </tr>
            <tr>
                <td class="rating"><template:errorSpanField path="rating">
                        <label class="control-label col-sm-2 col-md-2 "> Rating <span class="mandatory"> <spring:theme
                                    code="login.required" var="loginrequiredText" /> <img alt="" title=""
                                src="${commonResourcePath}/images/mandatory.png">
                        </span>
                        </label>

                        <form:radiobuttons path="rating" idKey="feedback.rating" items="${rating}" cssClass="" />
                    </template:errorSpanField></td>
            </tr>
            <tr>
                <td><formElement:formTextArea idKey="feedback.message" labelKey="feedback.message" path="message"
                        areaCSS="form-control" mandatory="true" labelCSS="col-sm-2 col-md-2" /></td>
            </tr>


        </table>
        <div class="form-group">
            <div class="col-sm-offset-2 col-sm-9 col-md-8 feedbackButton">
                <ycommerce:testId code="beASellerPage_SubmitButton">
                    <button class="btn btn-red" type="submit">
                        <spring:theme code="text.beASeller.submit" text="Submit" />
                    </button>
                </ycommerce:testId>
            </div>
        </div>
    </form:form>
</div>
