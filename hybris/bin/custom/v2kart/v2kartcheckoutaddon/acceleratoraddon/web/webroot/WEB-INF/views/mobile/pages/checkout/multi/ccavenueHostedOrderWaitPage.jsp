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
            <td><img src="${siteResourcePath}/images/logo.png"/></td>
              <td style="padding-left: 20px;"><img src="${siteResourcePath}/images/safe.jpg"
                style="vertical-align: middle; margin-right: 5px"/>100% Safe & Secure Shopping</td>
        </tr>
       <tr>
            <td colspan="2" class="content"><spring:theme code="checkout.multi.hostedOrderPostPage.header.wait1" /> <br /> <span><spring:theme
                        code="checkout.multi.hostedOrderPostPage.header.wait2" /></span><br /> <img src="${commonResourcePath}/images/spinner.gif" />
            </td>
        </tr>
        <tr>
          <%--   <td colspan="2" class="footer">&copy; <spring:theme code="checkout.multi.hostedOrderPostPage.header.footer" /> </td> --%>
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
        <input type="hidden" id="encRequest" name="encRequest" value="${hostedOrderPageData.parameters.get("encRequest")}">
        <input type="hidden" name="access_code" id="access_code" value="${hostedOrderPageData.parameters.get("access_code")}">
    </form>
</body>
</html>