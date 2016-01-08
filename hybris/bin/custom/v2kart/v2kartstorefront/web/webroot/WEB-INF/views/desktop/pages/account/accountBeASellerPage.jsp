<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="formElement"
	tagdir="/WEB-INF/tags/desktop/formElement"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<c:url var="beASellerUrl" value="/beADealer" />

<div class="userRegister">
	<div class="headline">
		<spring:theme code="text.account.beASeller" text="Profile" />
	</div>
	<div class="description" style="font-weight: bold">
		<spring:theme code="text.account.beASeller.fillform" />
	</div>
	<%-- 
				<form id="registerForm" class="form-horizontal" method="post" action="my-account.html">
				</form>
				 --%>

	<form:form action="${beASellerUrl}" method="post"
		commandName="addSellerForm" cssClass="form-horizontal">
		<table id ="beasellertable">
			<tr>
				<td><formElement:formInputBox idKey="beASeller.name" inlineError="true"
						labelKey="beASeller.name" path="name" inputCSS="form-control"
						labelCSS="col-sm-2 col-md-2 " mandatory="true" placeholder="Name" />
				</td>
			</tr>
			<tr>
				<td><formElement:formInputBox idKey="beASeller.email" inlineError="true"
						labelKey="beASeller.email" path="email" inputCSS="form-control"
						labelCSS="col-sm-2 col-md-2" mandatory="true" placeholder="Email" /></td>
			</tr>
			<tr>
				<td><formElement:formInputBox idKey="beASeller.phone" inlineError="true"
						labelKey="beASeller.phone" path="phone" inputCSS="form-control"
						mandatory="true" labelCSS="col-sm-2 col-md-2" 
						placeholder="Phone Number" /></td>
			</tr>
            <tr>
                <td><formElement:formInputBox idKey="beASeller.category" inlineError="true"
                        labelKey="beASeller.category" path="category" inputCSS="form-control"
                        mandatory="true" labelCSS="col-sm-2 col-md-2" 
                        placeholder="Category" /></td>
            </tr>
			<tr>
				<td><formElement:formTextArea idKey="beASeller.message"
						labelKey="beASeller.message" labelCSS="col-sm-2 col-md-2"
						areaCSS="form-control" path="message" /></td>
			</tr>
		</table>
		<div class="form-group">
			<div class="col-sm-offset-2 col-sm-9 col-md-8 beASellerButton">
				<ycommerce:testId code="beASellerPage_SubmitButton">
					<button class="btn btn-red" type="submit">
						<spring:theme code="text.beASeller.submit" text="Submit" />
					</button>
				</ycommerce:testId>
			</div>
		</div>
	</form:form>
</div>
