<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav"%>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/desktop/order"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="breadcrumb"
    tagdir="/WEB-INF/tags/desktop/nav/breadcrumb"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="formElement"
    tagdir="/WEB-INF/tags/mobile/formElement"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<div data-content-theme="d" data-theme="e"
    class="accountReturnOrderPage">
    <spring:theme code="account.partial.return.header" var="partialOrderReturnText"/>
    <spring:theme code="account.full.return.header" var="fullOrderReturnText"/>
	
	<input id="partialOrderReturnText" type="hidden" value="${partialOrderReturnText}"/>
	<input id="fullOrderReturnText" type="hidden" value="${fullOrderReturnText}"/>
    <div class="ui-bar ui-bar-a">
        <h1>${partialOrderReturnText}</h1>
    </div>
    <div class="item_container innerContent">
        <form:form commandName="returnForm" action="/my-account/order/return"
            method="POST" id="returnForm">
            <form:hidden path="orderCode" id="orderCode" />
            <input type="hidden" id="countofEntries" value="${fn:length(returnForm.returnableEntries) }">
            <form:hidden path="action" />
            <div data-role="collapsible-set" data-theme="b"
                data-collapsed-icon="arrow-d" data-expanded-icon="arrow-u"
                data-iconpos="right" id="partialOrderReturnDiv">
                <c:forEach items="${returnForm.returnableEntries}" var="entry"
                    varStatus="current">
                    <div data-role="collapsible" data-collapsed="false"
                        class="partialOrderReturnItem">
                        <h2>
                            <span class="returnEntryName">${entry.productName}</span><span
                                class="returnEntryQty">Qty-${entry.maxQuantity}</span>
                        </h2>
                        <span class="labelErrorEmpty return_order_error" style="color:red;"></span>
                        <ul data-role="controlgroup" data-theme="a">
                            <form:hidden path="returnableEntries[${current.index}].orderEntry"/>
                            <form:hidden
                                path="returnableEntries[${current.index}].returnAction"/>
                            <form:hidden
                            	path="returnableEntries[${current.index}].maxQuantity"/>
                            <form:hidden
                                path="returnableEntries[${current.index}].productName"/>
                            <li data-theme="c"><formElement:formInputBox
                                    idKey="product.returned.quantity" fieldcontainCSSClass="returnQuantity_div"
                                    path="returnableEntries[${current.index}].quantity"
                                    labelKey="product.return.order.quantity" mandatory="true"/>
                            	<label class="labelError" id="error_boxType1_${current.index}" style="color: red; display: none;"></label>
                            </li>
                            <li><formElement:formSelectBox items="${refundReasons}"
                                    idKey="product.returned.reason" fieldcontainCSSClass="returnReason_div"
                                    path="returnableEntries[${current.index}].reason"
                                    skipBlank="false" labelKey="product.return.order.reason" skipBlankMessageKey="account.cancel.select.reason" mandatory="true"/>
                            	<label class="labelError" id="error_boxType2_${current.index}" style="color: red; display: none;"></label>
                            </li>
                            <li><formElement:formInputBox idKey="product.return.note"
                                    path="returnableEntries[${current.index}].note"
                                    labelKey="product.return.order.comment"/></li>
                        </ul>
                    </div>
                </c:forEach>
            </div>
            <input type="checkbox" id="fullOrderReturn">
            <label for="fullOrderReturn">${fullOrderReturnText}</label>
            <input type="checkbox" id="partialOrderReturn">
            <label for="partialOrderReturn">${partialOrderReturnText}</label>
            <form:input id="isFullReturn" type="hidden" value="${full}"
                path="isFull" />
            <div data-role="collapsible-set" data-theme="b"
                id="fullOrderReturnDiv">
                <div data-role="collapsible" data-collapsed="false"
                    class='listItemNoIcon'>
                    <h2>${fullOrderReturnText}</h2>
	    <div>
                    <ul data-role="controlgroup" data-theme="a">
                        <li><formElement:formSelectBox items="${refundReasons}"
                                idKey="order.returned.reason" fieldcontainCSSClass="returnReason_div" path="reason"
                                skipBlank="false" labelKey="product.return.order.reason" skipBlankMessageKey="account.cancel.select.reason" mandatory="true"/>
                        	<label class="labelError errorBoxlabel" id="error_boxType" style="color:red; display: none;"></label>
                        </li>
                        <li><formElement:formInputBox idKey="order.return.note"
                                path="note" labelKey="product.return.order.comment"/></li>
                    </ul>
                </div>
            </div>
	</div>
            <input type="checkbox" id="returnAgreementChck">
            <label class="refund" for="returnAgreementChck">I agree to return
                the order with refund calculated <span class="refund-amount"></span>
            </label>
            <fieldset class="ui-grid-a doubleButton">
                <div class="ui-block-a">
                    <a href="/my-account/order/${orderCode}" data-role="button"
                        data-theme="g">Cancel</a>
                </div>
                <div class="ui-block-b">
                    <form:button type="submit" class="btn btn-disabled" id="return-btn-chng"
                        data-theme="b">Submit</form:button>
                </div>
            </fieldset>
        </form:form>
    </div>
</div>
