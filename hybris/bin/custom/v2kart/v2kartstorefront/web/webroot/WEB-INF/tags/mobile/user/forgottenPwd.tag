<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<div data-role="content" data-content-theme="c" data-theme="c" id="forgottenPwd" class="forgottenPwd" >
	<div class="accmob-navigationHolder">
		<div class="accmob-navigationContent">
			<div id="breadcrumb" class="accmobBackLink accmobBackLinkSingle registerNewCustomerLinkHolderBack">
				<a href="/login" data-role="link" class="productLink ui-link">
					<span class="breadcrumbsArrow">&laquo;</span>
					<spring:theme code="register.back.login" />
				</a>
			</div>
		</div>
	</div>
	<div data-role="content" class="innerContent" style="border:none;">
		<h1><spring:theme code="forgottenPwd.title"/></h1>
		<div>
			<p><spring:theme code="forgottenPwd.description"/></p>
			<form:form method="post" commandName="forgottenPwdForm">
				<common:errors/>
				<ul class="mFormList"  data-theme="d">
					<li>
						<formElement:formInputBox idKey="forgottenPwd.email" labelKey="forgottenPwd.email" path="email" inputCSS="text" mandatory="true"/>
					</li>
					<li style="clear: both;">
							<button data-theme="b" data-role="button" class="form" type="submit">
								<spring:theme code="forgottenPwd.submit"/>
							</button>
					</li>
				</ul>
			</form:form>
		</div>
	</div>
</div>