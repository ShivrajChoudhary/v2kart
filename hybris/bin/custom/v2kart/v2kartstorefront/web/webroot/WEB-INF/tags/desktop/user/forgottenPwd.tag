<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<div class="modal-content">
    <div class="modal-header">
        <div class="headline">
            <spring:theme code="forgottenPwd.title" />
        </div>
    </div>
    <form:form method="post" commandName="forgottenPwdForm">
        <div class="modal-body">
            <div class="forgottenPwd clearfix">
                <div class="description">
                    <spring:theme code="forgottenPwd.description" />
                </div>

                <formElement:formInputBox idKey="forgottenPwd.email" labelKey="forgottenPwd.email" path="email" inputCSS="text"
                    mandatory="true" />
            </div>
        </div>
        <div class="modal-footer">
            <button class="btn btn-red" type="submit">
                <spring:theme code="forgottenPwd.submit" />
            </button>

        </div>
    </form:form>
</div>
