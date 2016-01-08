<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/formElement"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>

<div class="popover-link notifyForm">
    <a href="javascript:void(0)" title="NotifyMyPrice" class="clearpopovercontent comparewishlistlink" id="notifymypricepdp">Notify My
        Price</a>
    <div style="display: none; width: 415px" class="popover large-box right" id="notifymypricepopover">
        <div class="popover-inner">
            <a class="popover-close" href="javascript:void(0);">&times;</a>
            <h2 class="popover-title margin0">Notify My Price</h2>
            <div class="popover-content">
                <c:url value="/p/notifyCustomer" var="notifyCustomer" />
                <form:form method="post" action="${notifyCustomer}" commandName="v2NotifyCustomerForm" id="notifyMyPrice">
                    <div class="form_field-elements">
                        <br> <input type="hidden" value="NOTIFY_MY_PRICE" name="type" /> <input type="hidden" value="${product.code}"
                            name="productCode" />
                        <form:hidden path="name" idKey="customerNotification.name" />
                        <form:hidden path="currentUserName" idKey="customerNotification.currentUserName" />
                        <div class="control-group">
                            <formUtil:formInputBox labelKey="Email ID" idKey="currentUserEmailId" path="currentUserEmailId"
                                placeholder="Email ID" mandatory="true" inputCSS="clearcontent1" />
                            <formUtil:formInputBox labelKey="Price" idKey="notificationPrice" path="notificationPrice"
                                placeholder="Notification Price" mandatory="true" inputCSS="clearcontent" />
                        </div>
                        <div class="form-actions clearfix">
                            <button class="positive margin0" type="submit">Send</button>
                            <span class="green-link bold btnlink left" style="display: none;">Your email id has been saved. We will
                                notify once product is available at this price</span> <span class="red-link bold btnlink left"
                                style="display: none;"></span>
                        </div>
                    </div>
                </form:form>
            </div>
        </div>
    </div>
    <!--popover start-->
    <div class="popover WL medium-box right" style="display: none;" id="notifymypriceresultpopover">
        <div class="popover-inner">
            <a class="popover-close" href="javascript:void(0);">×</a>
            <div class="popover-content">
                <span class="green-link bold" id="notifymypriceresultmsg"></span>
            </div>
        </div>
    </div>
    <!--popover end-->
</div>