<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>

<div class="f-section-2">
    <div class="container">
        <div class="row">
            <c:forEach items="${navigationNodes}" var="node">
                <div class="col-xs-6 col-sm-2">
                    <h3>${node.title}</h3>
                    <c:forEach items="${node.links}" step="${component.wrapAfter}" varStatus="i">
                        <ul>
                            <c:forEach items="${node.links}" var="childlink" begin="${i.index}" end="${i.index + component.wrapAfter - 1}">
                                <cms:component component="${childlink}" evaluateRestriction="true" element="li" />
                            </c:forEach>
                        </ul>
                    </c:forEach>
                    
                </div>
            </c:forEach>
            <div class="col-xs-6 col-sm-4 " id="payicon" >
           
                <ul>
                <h3>SAFE & SECURE PAYMENT METHOD</h3>                

              <c:set var="paymentMethodImg" value="${component.paymentMethodComponent.media.url}"/>
<%--                     <li><img src="${siteResourcePath}/images/payment-image.png"></li> --%>
                    <li><img src="${paymentMethodImg}"></li>
                </ul>
                
            </div>
        </div>
    </div>
</div>
<div class="f-section-3">
    <div class="container">
        <div class="row">
            <div class="col-xs-6 col-sm-4 col-md-3 beASellerLogo">
                <cms:component component="${component.footerSellerComponent}"></cms:component>
            </div>
            <div class="col-xs-6 col-sm-4 col-md-5 text-center sitelogo">
                <cms:component component="${component.footerLogoComponent}"></cms:component>
            </div>
            <div class="col-xs-12 col-sm-4 col-md-4 socialIconAtFooter">
                <h4>JOIN US:</h4>
              <c:set var="scrollCount" value="250"/>
              <c:set var="iconImg" value="${component.socialIconImagesComponent.media.url}"/> 
                <c:forEach items="${footerSocialIconsComponents}" var="socialIconComp">
                    <c:url value="${socialIconComp.urlLink}" var="encodedUrl" />
                    <c:choose>
                        <c:when test="${empty encodedUrl || encodedUrl eq '#'}">
                           <div style="background: url('${iconImg}') ${scrollCount}px 2px; height: 50px; width: 50px; display:inline-block;"></div>
          <%--                   <img title="${socialIconComp.media.altText}" alt="${socialIconComp.media.altText}" src="${socialIconComp.media.url}"> --%>
                        </c:when>
                        <c:otherwise>
                            <a href="${encodedUrl}" target="_blank">
                         <div style="background: url('${iconImg}') ${scrollCount}px 2px; height: 50px; width: 50px; display:inline-block;"></div>
                           <%--  <img title="${socialIconComp.media.altText}" alt="${socialIconComp.media.altText}" src="${socialIconComp.media.url}" > --%>
                                </a>
                        </c:otherwise>
                    </c:choose>
               <c:set var="scrollCount" value="${scrollCount-50}"/>
                </c:forEach>
            </div>
            <div class="col-md-12">
                <p class="copyright">${notice}</p>
            </div>
        </div>
    </div>
</div>
<%-- 
<div class="promotion-strip-right">
    <c:forEach items="${component.fixedRightBannerComponents}" var="rightBannerComp">
        <c:url value="${not empty rightBannerComp.page ? rightBannerComp.page.label : rightBannerComp.urlLink}" var="encodedUrl" />
        <c:choose>
            <c:when test="${empty encodedUrl || encodedUrl eq '#'}">
                <div class="offer-box">
                    <img src="${rightBannerComp.media.url}">
                    <p class="head">${rightBannerComp.headline}</p>
                    <p>${ rightBannerComp.content}</p>
                </div>

            </c:when>
            <c:otherwise>
                <div class="offer-box">
                    <a href="${encodedUrl}"> <img src="${rightBannerComp.media.url}">
                        <p class="head">${rightBannerComp.headline}</p>
                        <p>${ rightBannerComp.content}</p></a>
                </div>
            </c:otherwise>
        </c:choose>
    </c:forEach>
</div>

<div class="promotion-strip-left">
    <c:forEach items="${component.fixedLeftBannerComponents}" var="leftBannerComp">
        <c:url value="${not empty leftBannerComp.page ? leftBannerComp.page.label : leftBannerComp.urlLink}" var="encodedUrl" />
        <c:choose>
            <c:when test="${empty encodedUrl || encodedUrl eq '#'}">
                <div class="offer-box">
                    <img src="${leftBannerComp.media.url}">
                    <p class="head">${leftBannerComp.headline}</p>
                    <p>${ leftBannerComp.content}</p>
                </div>

            </c:when>
            <c:otherwise>
                <div class="offer-box">
                    <a href="${encodedUrl}"> <img src="${leftBannerComp.media.url}">
                        <p class="head">${leftBannerComp.headline}</p>
                        <p>${ leftBannerComp.content}</p></a>
                </div>
            </c:otherwise>
        </c:choose>
    </c:forEach>
</div> --%>