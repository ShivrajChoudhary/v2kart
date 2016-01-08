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
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>

<!-- <div class="modal fade bs-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true" id="partial-order-cancel">
<div class="modal-dialog modal-lg order-cancel">  -->
    <div class="modal-content order-return">
     <div class="modal-header">
        <!-- <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button> -->
        <h4 class="modal-title"><spring:theme code="account.partial.return.header" /></h4>
      </div>
      <span class="labelErrorEmpty return_order_error"></span>
      <form:form commandName="returnForm" action="/my-account/order/return" method="POST" id="returnForm">
      <input type="hidden" id="countofreturnableEntries" value="${fn:length(returnForm.returnableEntries) }">
      <form:hidden  path="orderCode" id="orderCode"/>
      <div class="modal-body">
            <div class="order-container">
            <div class="row">
            	<div class="orderList order-rc-pg">
                <div class="disableBg"></div>
                  <table class="orderRCtablePopup">
                    <thead>
                        <th id="header2" ><spring:theme code="order.cancel.product.label" /> </th>
                        <th id="header4"> <spring:theme code="order.return.qty.label" /></th>
                        <th id="header5"><spring:theme code="order.return.cancelled.qty.label" /></th>
                        <th id="header5"><spring:theme code="order.return.reason" /></th>
                        <th id="header5"><spring:theme code="order.cancel.note.label" /></th>
                    </thead>
                    <tbody>
                    <c:if test="${not empty returnForm.returnableEntries }">
                    <c:forEach items="${returnForm.returnableEntries}" var="entry" varStatus="current">
                    <form:hidden  path="returnableEntries[${current.index}].orderEntry"/>
                    <form:hidden  path="returnableEntries[${current.index}].maxQuantity"/>
                    <form:hidden  path="returnableEntries[${current.index}].productName"/>
                    <form:hidden  path="returnableEntries[${current.index}].returnAction"/>
                    <tr><td headers="header2" class="details" style="position: relative; top: 14px;">${entry.productName }</td>
                    <td headers="header4" class="quantity" style="line-height: 50px;">${entry.maxQuantity } </td>
                   <td headers="header5"><formElement:formInputBox autocomplete="off" idKey="product.cancelled.quantity" path="returnableEntries[${current.index}].quantity"/>
                    <label class="labelError" id="error_boxType_${current.index}" style="display: none;"/>
                   </td>
                   <td headers="header5"><div class="controls"><formElement:formSelectBox items="${refundReasons }"    selectCSSClass="returnpopup" idKey="product.cancelled.reason" path="returnableEntries[${current.index}].reason" skipBlank="false" skipBlankMessageKey="account.cancel.select.reason"  firstOptionDisable="true"/>
                  <label class="labelError" id="error_boxType3_${current.index}" style="display: none;"/>
                  </div></td>

                   <td headers="header5"><formElement:formInputBox  idKey="product.cancel.note" path="returnableEntries[${current.index}].note" placeholder="Comment"/></td>
                   </tr>
                   </c:forEach>
                    </c:if>
                    </tbody>
                    </table>
					</div>
					</div>
					</div>
                     <div style="margin-bottom: 60px"><button type="button" class="btn btn-red" id="fullOrderReturn"><spring:theme code="account.full.return.header" /></button></div>
                     
     <div class="marT10 fullOrderDetails order-container">
            <div class="row">
            <div class="order-cancellation-heading"><spring:theme code="account.full.return.header" /></div>
            
            <div class="col-md-12 marB10">
            <formElement:formSelectBox items="${refundReasons }" labelKey="account.return.reason.label" selectCSSClass="returnReason" labelCSS="order-labels" idKey="order.cancelled.reason" path="reason" skipBlank="false" skipBlankMessageKey="account.cancel.select.reason"/>
            <label class="labelError errorBoxlabel" id="error_boxType2" style="display: none;"/>
            </div>
            <form:hidden  path="action"/>
            <div class="col-md-12 marB10">
            <formElement:formInputBox  idKey="order.cancel.note" inputCSS="returnNote" labelKey="account.return.note.label" labelCSS="order-labels" path="note" />
            </div>
        </div>
      </div>
  </div>

  <form:input id="isReturnFull" type="hidden" path="isFull"/>
  <div class="modal-footer">
    <div class="agreement">
    <input type="checkbox" id="agreementReturnChk">
    <label class="refund"><spring:theme code="account.order.return.agreement.msg"/><span class="return-amount"><format:price priceData="${order.totalPrice}"/></span></label>
    </div>
    <div class="return-buttons">
        <form:button type="submit" class="btn btn-red"  id="btn-chng3"><spring:theme code="order.submit.label"/></form:button>
        <button type="button" class="btn btn-red"  id="btn-chng4"><spring:theme code="order.cancel.label"/></button></div>
      </div>
    <label class="refundText"><spring:theme code="account.order.return.agreement.rule"/></label>
      </form:form>
</div>
<!-- </div> -->
<!-- </div> -->

