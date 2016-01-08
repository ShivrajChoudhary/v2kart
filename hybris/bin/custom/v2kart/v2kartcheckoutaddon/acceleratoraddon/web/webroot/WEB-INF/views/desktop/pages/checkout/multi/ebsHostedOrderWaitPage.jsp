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
    .content{
        text-align:center;
        font-size:20px;
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
            <td><img src="${siteResourcePath}/images/v2kart-logo.png" /></td>
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
        <input type="hidden" name="account_id" value="${hostedOrderPageData.parameters.get("account_id")}" />
        <input type="hidden" name="return_url" value="${hostedOrderPageData.parameters.get("return_url")}" />
        <input type="hidden" name="mode" value="${hostedOrderPageData.parameters.get("mode")}" />
        <input type="hidden" name="reference_no" value="${hostedOrderPageData.parameters.get("reference_no")}" />
        <input type="hidden" name="amount" value="${hostedOrderPageData.parameters.get("amount")}" />
        <input type="hidden" name="description" value="${hostedOrderPageData.parameters.get("description")}" />
        <input type="hidden" name="name" value="${hostedOrderPageData.parameters.get("name")}" />
        <input type="hidden" name="address" value="${hostedOrderPageData.parameters.get("address")}" />
        <input type="hidden" name="city" value="${hostedOrderPageData.parameters.get("city")}" />
        <input type="hidden" name="state" value="${hostedOrderPageData.parameters.get("state")}" />
        <input type="hidden" name="postal_code" value="${hostedOrderPageData.parameters.get("postal_code")}" />
        <input type="hidden" name="country" value="${hostedOrderPageData.parameters.get("country")}" />
        <input type="hidden" name="phone" value="${hostedOrderPageData.parameters.get("phone")}" />
        <input type="hidden" name="email" value="${hostedOrderPageData.parameters.get("email")}" />
        <input type="hidden" name="secure_hash" value="${hostedOrderPageData.parameters.get("secure_hash")}" />
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