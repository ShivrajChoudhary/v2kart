<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>


<div class="downloadAppPage">
        <div class="app-download" style="background: url('${appBackgroundComponent.media.url}') no-repeat;">
            <h1>
                <img src="${appLogoComponent.media.url}" alt="logo"> Shopping App
            </h1>
            <h3>${appHeadlineComponent.content}</h3>
            <a href="${androidLinkComponent.url}" class="btn btn-lg btn-success" target="_blank">ANDROID PLAY STORE</a> <a href="${iosLinkComponent.url}" class="btn btn-lg btn-info" target="_blank">IOS APP STORE</a>
        </div>
    </div>