<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/formElement"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="headline">
	<spring:theme code="text.account.wallet" text="My Wallet"/>
</div>
<div class="wallet-strip clearfix">
              	<div class="pull-left"><spring:theme code="account.wallet.transaction" text="Recent Transactions"/></div>
              	<fmt:parseNumber  integerOnly="true" type="number" var="balanceVal"  value="${balance}" />
                <div class="pull-right"><spring:theme code="account.wallet.money" text="Your Wallet Money "/>${balanceVal} Pts.</div>
              </div>
              <div class="walletTable">
              <table class="table table-responsive table-striped">
                <thead>
                  <tr>
                    <th><spring:theme code="account.wallet.header.date"/></th>
                    <th><spring:theme code="account.wallet.header.transaction"/></th>
                    <th><spring:theme code="account.wallet.header.credit"/></th>
                    <th><spring:theme code="account.wallet.header.debit"/></th>                   
                  </tr>
                </thead>
                <c:if test="${not empty transactions }">
                <tbody>
                <c:forEach items="${transactions }" var="trans">
                <fmt:parseNumber  integerOnly="true" type="number" var="creditVal"  value="${trans.credit }" />
                <fmt:parseNumber  integerOnly="true" type="number"  value="${trans.debit }" var="debitVal" />
                <tr>
                   <td>${trans.transactionDate }</td>
                   <td>${trans.description }</td>
                   <td><c:choose><c:when test="${not empty trans.credit }">${creditVal } Pts.</c:when><c:otherwise> - </c:otherwise> </c:choose></td>
                  <td><c:choose><c:when test="${not empty trans.debit }">${debitVal } Pts.</c:when><c:otherwise> - </c:otherwise> </c:choose></td>
                   
                  </tr>
                  </c:forEach>
                  </tbody>
                  </c:if>
                  </table>
                  </div>
              <spring:theme code="wallet.points.relation" text="1 Pts.= 1 Rs."/>
              <div style="float: right;"><spring:theme code="maxTrx" text="Only last 50 transaction will be displayed "  /></div> 