<!DOCTYPE html>
<html>
<head>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<style type= "text/css">
    table{
        width :100%;
        min-height:780px
    }
    .header{
        height : 15%;
        padding-left : 5px;
    }
    .header img#logo {
        height: 150px;
        width: 250px;
    }
    .header img#safe {
        height: 15px;
        width: 15px;
    }
    .content{
        text-align:center;
        font-size:1.0em;
        font-weight : bold;
    }
    .content span {
        margin-left:10%;
    }
    .footer{
        background-color : #6d6e71;
        height:5%;
        color :white;
        font-size:15px;
        padding-left:5px;
    }
</style>
</head>
<body onload="document.waitPageForm.submit();" >
<table>
        <tr class="header">
            <td><img src="${siteResourcePath}/images/logo.png" /></td>
            <td style="text-align: right; padding-right: 20px;"><img src="${siteResourcePath}/images/safe.jpg"
                style="vertical-align: middle; margin-right: 5px" />100% Safe & Secure Shopping</td>
        </tr>
        <tr>
            <td colspan="2" class="content"><spring:theme code="checkout.multi.hostedOrderPostPage.header.wait1" /> <br /> <span><spring:theme
                        code="checkout.multi.hostedOrderPostPage.header.wait2" /></span><br /> <img src="${siteResourcePath}/images/spinner.gif" />
            </td>
        </tr>
        <tr>
            <%-- <td colspan="2" class="footer">&copy; <spring:theme code="checkout.multi.hostedOrderPostPage.header.footer" /> </td> --%>
        </tr>
    </table>
    <%-- <div class="item_container">
            <div style="top: 35%; left: 25%; position: absolute;">
                <h3>
                    <spring:theme code="checkout.multi.hostedOrderPostPage.header.wait"/>
                    <br>
                    <img src="${commonResourcePath}/images/spinner.gif" />
                </h3>
            </div>
        </div> --%>
    <form name="waitPageForm" action="${hostedOrderPageData.postUrl}" method='post'>
        <input type="hidden" name="firstname" value="${hostedOrderPageData.parameters.get("firstname")}" />
        <input type="hidden" name="surl" value="${hostedOrderPageData.parameters.get("surl")}" />
        <input type="hidden" name="phone" value="${hostedOrderPageData.parameters.get("phone")}" />
        <input type="hidden" name="key" value="${hostedOrderPageData.parameters.get("key")}" />
        <input type="hidden" name="hash" value="${hostedOrderPageData.parameters.get("hash")}" />
        <input type="hidden" name="curl" value="${hostedOrderPageData.parameters.get("curl")}" />
        <input type="hidden" name="furl" value="${hostedOrderPageData.parameters.get("furl")}" />
        <input type="hidden" name="txnid" value="${hostedOrderPageData.parameters.get("txnid")}" />
        <input type="hidden" name="productinfo" value="${hostedOrderPageData.parameters.get("productinfo")}" />
        <input type="hidden" name="amount" value="${hostedOrderPageData.parameters.get("amount")}" />
        <input type="hidden" name="email" value="${hostedOrderPageData.parameters.get("email")}" />
        <input type="hidden" name="udf1" value="${hostedOrderPageData.parameters.get("udf1")}" />
        <input type="hidden" name="udf2" value="${hostedOrderPageData.parameters.get("udf2")}" />
        <input type="hidden" name="udf3" value="${hostedOrderPageData.parameters.get("udf3")}" />
        <input type="hidden" name="udf4" value="${hostedOrderPageData.parameters.get("udf4")}" />
        <input type="hidden" name="udf5" value="${hostedOrderPageData.parameters.get("udf5")}" />
        <input type="hidden" name="enforce_paymethod" value="${hostedOrderPageData.parameters.get("enforce_paymethod")}" />
        <c:forEach items="${hostedOrderPageData.parameters}" var="parameter">
            <c:if test="${parameter.key eq 'enforce_paymethod'}">
                <c:set var="enforcePaymentMethod" value="${fn:substring(parameter.value,0,3)}"/>
                <c:if test="${fn:contains(enforcePaymentMethod,'EMI')}">
                    <input type="hidden" name="pg" value="${hostedOrderPageData.parameters.get("pg")}" />
                    <input type="hidden" name="bankcode" value="${hostedOrderPageData.parameters.get("enforce_paymethod")}" />
                </c:if>
            </c:if>
        </c:forEach>
<!--         <input type="submit" value="submit"> -->
    </form>
</body>
</html>