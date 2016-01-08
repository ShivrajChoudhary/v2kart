<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<c:url var="beASellerUrl" value="/beADealer" />

<div class="beASellerPage" data-content-theme="d" data-theme="e">
    <div class="heading">
        <h2>
            <spring:theme code="text.account.beASeller" text="Profile" />
        </h2>
        <spring:theme code="text.account.beASeller.fillform" />
    </div>
    <form:form action="${beASellerUrl}" method="post" commandName="addSellerForm" cssClass="form-horizontal">
        <div class="innerContent">
            <formElement:formInputBox idKey="beASeller.name" labelKey="beASeller.name" path="name" inputCSS="text"
                labelCSS="col-sm-2 col-md-2 " mandatory="true" placeholder="Name" />
            <formElement:formInputBox idKey="beASeller.email" labelKey="beASeller.email" path="email" inputCSS="text" mandatory="true"
                placeholder="Email" />
            <formElement:formInputBox idKey="beASeller.phone" labelKey="beASeller.phone" path="phone" inputCSS="text" mandatory="true"
                placeholder="Phone Number" />
            <formElement:formInputBox idKey="beASeller.category" labelKey="beASeller.category" path="category" inputCSS="text"
                mandatory="true" placeholder="Category" />
            <formElement:formTextArea idKey="beASeller.message" labelKey="beASeller.message" areaCSS="text" path="message" />
            <button class="btn btn-red" type="submit" data-theme="b">
                <spring:theme code="text.beASeller.submit" text="Submit" />
            </button>
        </div>
    </form:form>
</div>
<%-- <div class="userRegister">
    <div class="headline">
        <spring:theme code="text.account.beASeller" text="Profile" />
    </div>
    <div class="description" style="font-weight: bold">
        <spring:theme code="text.account.beASeller.fillform" />
    </div>
    
				<form id="registerForm" class="form-horizontal" method="post" action="my-account.html">
				</form>
				


</div> --%>
