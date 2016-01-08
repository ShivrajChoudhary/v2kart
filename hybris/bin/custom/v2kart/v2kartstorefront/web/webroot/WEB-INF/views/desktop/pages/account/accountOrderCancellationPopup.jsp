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
    <div class="modal-content order-cancel">
     <div class="modal-header">
        <!-- <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button> -->
        <h4 class="modal-title"><spring:theme code="account.partial.cancel.header" /> </h4>
      </div>
      <span class="labelErrorEmpty cancel_order_error"></span>
      <form:form commandName="cancelForm" action="/my-account/order/cancel" method="POST" id="cancelForm">
      <input type="hidden" id="countofEntries" value="${fn:length(cancelForm.cancelledEntries) }">
      <form:hidden  path="orderCode" id="orderCode"/>
      <div class="modal-body">
            <div class="order-container">
            <div class="row">
            	<div class="orderList order-rc-pg">
                <div class="disableBg"></div>
                  <table class="orderRCtablePopup">
                    <thead>
                        <th id="header2" ><spring:theme code="order.cancel.product.label"/> </th>
                        <th id="header4"> <spring:theme code="order.cancel.qty.label"/></th>
                        <th id="header5"><spring:theme code="order.cancel.cancelled.qty.label"/></th>
                        <th id="header5"><spring:theme code="order.full.cance.reason"/></th>
                        <th id="header5"><spring:theme code="order.cancel.note.label"/></th>
                    </thead>
                    <tbody>
                    <c:if test="${not empty cancelForm.cancelledEntries }">
                    <c:forEach items="${cancelForm.cancelledEntries}" var="entry" varStatus="current">
                    <form:hidden  path="cancelledEntries[${current.index}].orderEntry"/>
                    <form:hidden  path="cancelledEntries[${current.index}].maxQuantity"/>
                    <form:hidden  path="cancelledEntries[${current.index}].productName"/>
                    <tr><td headers="header2" class="details" style="position: relative; top: 14px;" >${entry.productName }</td>
                    <td headers="header4" class="quantity" style="line-height: 50px;">${entry.maxQuantity }</td>
                   <td headers="header5" class="quantity"><formElement:formInputBox autocomplete="off" idKey="product.cancelled.quantity" path="cancelledEntries[${current.index}].quantity"/>
                   <label class="labelError" id="error_boxType1_${current.index}" style="display: none;"/>
                   </td>
                   <td headers="header6"><div class="controls">
<formElement:formSelectBox items="${cancelReasons }"  idKey="product.cancelled.reason" path="cancelledEntries[${current.index}].reason" skipBlank="false" skipBlankMessageKey="account.cancel.select.reason" firstOptionDisable="true"/>
<label class="labelError" id="error_boxType2_${current.index}" style="display: none;"/>
</div></td>
                   <td headers="header7"><formElement:formInputBox  idKey="product.cancel.note" path="cancelledEntries[${current.index}].note" placeholder="Comment"/></td>
                   </tr>
                   </c:forEach>
                    </c:if>
                    </tbody>
                    </table>
					</div>
					</div>
					</div>
					<!-- <div> <label class="labelError" id="error_boxType" style="display: none;"/>
					 <label class="labelError2" id="error_boxType" style="display: none;"/>
					</div> -->
                     <div style="margin-bottom: 60px"><button type="button" class="btn btn-red" id="fullOrderCancel"><spring:theme code="account.full.cancel.header" /></button>
                     
                     </div>
                     
     <div class="marT10 fullOrderDetails order-container">
            <div class="row">
            <div class="order-cancellation-heading"><spring:theme code="account.full.cancel.header" /></div>
            
            <div class="col-md-12 marB10">
            <formElement:formSelectBox items="${cancelReasons }" labelCSS="order-labels"  labelKey="order.full.cance.reason" idKey="order.cancelled.reason" selectCSSClass="selectReason" path="reason" selectedValue="${cancelForm.reason}" skipBlank="false" skipBlankMessageKey="account.cancel.select.reason"/>
            	<label class="labelError errorBoxlabel" id="error_boxType" style="display: none;"/>
            </div>
            <div class="col-md-12 marB10">
                <formElement:formInputBox labelCSS="order-labels" labelKey="order.full.cancel.notes" autocomplete="off" inputCSS="giveNotes" idKey="order.cancel.note" path="note" />
            </div>
        </div>
      </div>
  </div>

  <form:input id="isFull" type="hidden" value="${full}" path="isFull"/>
  <div class="modal-footer">
    <div class="agreement">
    <input type="checkbox" id="agreementChck">
    <label class="refund"><spring:theme code="account.order.cancel.agreement.msg" />  <span class="refund-amount"> </span></label>
    </div>
    <div class="cancel-buttons">
        <form:button type="submit" class="btn btn-red"  id="btn-chng"><spring:theme code="order.submit.label"/> </form:button>
        <button type="button" class="btn btn-red"  id="btn-chng1"><spring:theme code="order.cancel.label"/></button></div>
      </div>
      </form:form>
</div>
<!-- </div> -->
<!-- </div> -->

