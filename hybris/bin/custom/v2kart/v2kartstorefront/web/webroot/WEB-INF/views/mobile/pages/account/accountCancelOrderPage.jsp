<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav"%>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/mobile/order"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="breadcrumb"
	tagdir="/WEB-INF/tags/mobile/nav/breadcrumb"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="formElement"
	tagdir="/WEB-INF/tags/mobile/formElement"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<div data-content-theme="d" data-theme="e"
	class="accountCancelOrderPage">
	<spring:theme code="account.partial.cancel.header" var="partialOrdercancellationText"/>
	<spring:theme code="account.full.cancel.header" var="fullOrdercancellationText"/>
	
	<input id="partialOrderCancellationText" type="hidden" value="${partialOrdercancellationText}"/>
	<input id="fullOrderCancellationText" type="hidden" value="${fullOrdercancellationText}"/>
	<div class="ui-bar ui-bar-a">
		<h1>${partialOrdercancellationText}</h1>
	</div>
	<div class="item_container innerContent">
		<form:form commandName="cancelForm" action="/my-account/order/cancel"
			method="POST" id="cancelForm">
			<form:hidden path="orderCode" id="orderCode" />
            <input type="hidden" id="countofEntries" value="${fn:length(cancelForm.cancelledEntries) }">
			<div data-role="collapsible-set" data-theme="b"
				data-collapsed-icon="arrow-d" data-expanded-icon="arrow-u"
				data-iconpos="right" id="partialOrderCancelDiv">
				<c:forEach items="${cancelForm.cancelledEntries}" var="entry"
					varStatus="current">
					<div data-role="collapsible" data-collapsed="false"
						class="partialOrderCancelItem" data-collapsed-icon="false" data-expanded-icon="false">
						<h2>
							<span class="cancelEntryName">${entry.productName}</span><span
								class="cancelEntryQty">Qty-${entry.maxQuantity}</span>
						</h2>
						<ul data-role="controlgroup" data-theme="a">
						 <form:hidden  path="cancelledEntries[${current.index}].orderEntry"/>
                    	<form:hidden  path="cancelledEntries[${current.index}].maxQuantity"/>
                    	<form:hidden  path="cancelledEntries[${current.index}].productName"/>
							<li data-theme="c"><formElement:formInputBox
									idKey="product.cancelled.quantity${current.index}" fieldcontainCSSClass="cancelQuantity_div"
									path="cancelledEntries[${current.index}].quantity"
									labelKey="product.cancel.order.quantity" mandatory="true"/>
                            	<label class="labelError" id="error_boxType1_${current.index}" style="color: red; display: none;"></label>
                            </li>
                            <span class="labelErrorEmpty cancel_order_error" style="color:red;"></span>
							<li><formElement:formSelectBox items="${cancelReasons}"
									idKey="product.cancelled.reason" fieldcontainCSSClass="cancelReason_div"
									path="cancelledEntries[${current.index}].reason"
									skipBlank="false" labelKey="product.cancel.order.reason" skipBlankMessageKey="account.cancel.select.reason" mandatory="true"/>
                                <label class="labelError" id="error_boxType2_${current.index}" style="color: red; display: none;"></label>        
                            </li>
							<li><formElement:formInputBox idKey="product.cancel.note"
									path="cancelledEntries[${current.index}].note"
									labelKey="order.cancel.note.label" placeholder="Comment"/></li>
						</ul>
					</div>
				</c:forEach>
			</div>
			<input type="checkbox" id="fullOrderCancellation">
			<label for="fullOrderCancellation">${fullOrdercancellationText}</label>
			<input type="checkbox" id="partialOrderCancellation">
			<label for="partialOrderCancellation">${partialOrdercancellationText}</label>
			<form:input id="isFull" type="hidden" value="${full}"
				path="isFull" />
			<div data-role="collapsible-set" data-theme="b"
				id="fullOrderCancelDiv">
				<div data-role="collapsible" data-collapsed="false" data-collapsed-icon="false" data-expanded-icon="false">
					<h2>${fullOrdercancellationText}</h2>	
                    <div>
					<ul data-role="controlgroup" data-theme="a">
						<li><formElement:formSelectBox items="${cancelReasons}"
								idKey="order.cancelled.reason" fieldcontainCSSClass="cancelReason_div" path="reason"
								skipBlank="false" labelKey="product.cancel.order.reason" skipBlankMessageKey="account.cancel.select.reason" mandatory="true"/>
                            <label class="labelError errorBoxlabel" id="error_boxType" style="color:red; display: none;"></label>
                        </li>	
						<li><formElement:formInputBox idKey="order.cancel.note"
								path="note" labelKey="order.full.cancel.notes"/></li>
					</ul>
                    </div>
				</div>
			</div>
			<input type="checkbox" id="cancelAgreementChck">
			<label class="refund" for="cancelAgreementChck" style="font-weight: normal;">I agree to cancel
				the order with refund calculated <span class="refund-amount"></span>
			</label>
			<fieldset class="ui-grid-a doubleButton">
				<div class="ui-block-a">
					<a href="/my-account/order/${orderCode}" data-role="button"
						data-theme="g">Cancel</a>
					<!-- <button type="button" class="btn btn-red" id="btn-chng1"
						data-theme="g">Cancel</button> -->
				</div>
				<div class="ui-block-b">
					<form:button type="submit" class="btn btn-disabled" id="cancel-btn-chng"
						data-theme="b">Submit</form:button>
				</div>
			</fieldset>
		</form:form>
	</div>
</div>