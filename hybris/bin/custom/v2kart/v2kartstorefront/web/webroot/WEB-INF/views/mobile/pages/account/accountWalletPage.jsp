<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/mobile/formElement"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="form_field-elements" id="walletPage">
	<div class="control-group">
		<div class="controls">
			<div class="ui-bar ui-bar-b">
				<h1>
					<spring:theme code="text.account.wallet" text="My Wallet" />
				</h1>
				<fmt:parseNumber  integerOnly="true" type="number" var="balanceVal"  value="${balance}" />
				<p class="walletBalance">Your wallet Balance is ${balanceVal } Pts.</p>
                <span class="walletRel"><spring:theme code="wallet.points.relation.mobile" text="1 Pts. = 1 Rs."/></span>
			</div>
			<div class="innerContent">
				<c:forEach items="${transactions }" var="trans">
					<div class="walletTransaction">
						<div class = "walletTransactionImage">
							<c:if test="${not empty trans.debit }"><img src='${commonResourcePath}/images/wallet-I.png'/></c:if>
							<c:if test="${not empty trans.credit }"><img src='${commonResourcePath}/images/wallet-II.png'/></c:if>
						</div>
						<div class = "walletTransactionDetail">
							<p>${trans.transactionDate }</p>
							<p>${trans.description }</p>
						</div>
						<fmt:parseNumber  integerOnly="true" type="number" var="creditVal"  value="${trans.credit }" />
                <fmt:parseNumber  integerOnly="true" type="number"  value="${trans.debit }" var="debitVal" />
						<c:if test="${not empty trans.credit }"><div class = "walletTransactionValue creditTransactionValue"> +  ${creditVal } Pts.</div></c:if>
                   		<c:if test="${not empty trans.debit }"><div class = "walletTransactionValue debitTransactionValue"> -  ${debitVal } Pts.</div></c:if>
					</div>
				</c:forEach>
			</div>
		</div>
	</div>
</div>