<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<%-- <cms:pageSlot position="SideContent" var="feature" element="div" class="span-4 accountLeftNavigation icons">
    <cms:component component="${feature}" />
</cms:pageSlot> --%>

<div class="span-23 last  accountContentPane">
    <div class="item_container_holder">
        <div class="headline">
            <spring:theme code="updatePwd.title" />
        </div>

        <div class="item_container">
            <p>
                <spring:theme code="updatePwd.description" />
            </p>
            <form:form method="post" commandName="updatePwdForm">
                <div class="form_field-elements">
                    <div class="form_field-input">
                        <table>
                            <tr>
                                <td><formElement:formPasswordBox idKey="updatePwd-pwd" labelKey="updatePwd.pwd" path="pwd"
                                        inputCSS="text password strength" mandatory="true" /></td>
                            </tr>
                            <tr>
                                <td><formElement:formPasswordBox idKey="updatePwd.checkPwd" labelKey="updatePwd.checkPwd"
                                        path="checkPwd" inputCSS="text password" mandatory="true"/></td>
                            </tr>
                        </table>
                    </div>
                </div>
                <div class="form-field-button">
                    <ycommerce:testId code="update_update_button">
                        <button class="btn btn-red">
                            <spring:theme code="updatePwd.submit" />
                        </button>
                    </ycommerce:testId>
                </div>
            </form:form>
        </div>
    </div>
</div>
