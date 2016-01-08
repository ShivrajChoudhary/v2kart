<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/formElement"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>

<div class="notifyForm">
    <div style="display: none;" id="notifyMe" class="popover large-box right pdpnotifymepopup popoverNotifyMe">
        <div class="popover-inner">
            <a class="popover-close popoverNotifyMeClose" href="javascript:void(0);">x</a>
            <h3 class="popover-title margin0">Notify Me</h3>
            <div class="popover-content">
                <c:url value="/p/notifyCustomer" var="notifyCustomer" />
                <form:form method="post" action="${notifyCustomer}" commandName="v2NotifyCustomerForm" id="notifyMe">
                    <div class="form_field-elements">
                        <input type="hidden" value="NOTIFY_ME" name="type" /> <input type="hidden" value="${product.code}"
                            name="productCode" />
                        <form:hidden path="name" idKey="customerNotification.name" />
                        <form:hidden path="currentUserName" idKey="customerNotification.currentUserName" />
                        <div class="control-group">
                            <formUtil:formInputBox labelKey="Email ID" idKey="currentUserEmailId" path="currentUserEmailId" mandatory="true"
                                inputCSS="clearcontent1" />
                        </div>
                        <div class="form-actions clearfix">
                            <button class="positive margin0" type="submit">Send</button>
                        </div>
                    </div>
                </form:form>
            </div>
        </div>
    </div>
    <!--popover start-->
    <div class="popover WL medium-box right" style="display: none;" id="notifymeresultpopover">
        <div class="popover-inner">
            <a class="popover-close notifymeresultpopoverClose" href="javascript:void(0);">×</a>
            <div class="popover-content">
                <span class="green-link bold" id="notifymeresultmsg"></span>
            </div>
        </div>
    </div>
    <!--popover end-->
</div>
<!-- Notify me ends -->