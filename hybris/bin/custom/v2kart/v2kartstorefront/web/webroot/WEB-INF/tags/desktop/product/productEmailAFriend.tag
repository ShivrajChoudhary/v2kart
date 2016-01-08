<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/formElement"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="popover-link" style="display: inline-block;">
    <a href="javascript:void(0)" title="Email a Friend" id="emailafriendpdp" class="clearpopovercontent comparewishlistlink"><span
        class="glyphicon glyphicon-envelope"></span>&nbsp;Email a friend</a>
    <!--popover start-->
    <div class="popover large-box left modal-content" style="display: none; top: 21px;z-index: 1;" id="emailafriendpopover">
        <div class="popover-inner">

            <!-- <a class="popover-close" href="javascript:void(0);">x</a>
            <h2 class="popover-title">Email to a Friend</h2> -->

            <div class="modal-header">
                <!-- <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button> -->
                <a class="popover-close close" href="javascript:void(0);">x</a>
                <h4 class="modal-title">Email a Friend</h4>
            </div>

            <div style="padding-top: 15px; padding-left: 15px;">
                <div class="description">Please enter the email address for your friend. The URL of the product will be emailed to
                    your friend.</div>
            </div>

            <div class="form_field-elements">
                <div class="emailProductPage">
                    <c:url value="/p/emailAFriend" var="emailForm" />
                    <form:form method="post" action="${emailForm}" commandName="v2NotifyCustomerForm" id="emailAFriend">
                        <div class="modal-body forgottenPwd clearfix" style="padding-top: 0px !important;">
                            <br>
                            <form:hidden path="name" idKey="customerNotification.name" />
                            <form:hidden path="currentUserName" idKey="customerNotification.currentUserName" />
                            <form:hidden path="currentUserEmailId" idKey="customerNotification.currentUserEmailId" />
                            <form:hidden path="mediaUrl" idKey="customerNotification.mediaUrl" />
                            <form:hidden path="productPrice" idKey="customerNotification.productPrice" />
                            <form:hidden path="productCode" idKey="customerNotification.productCode" />
                            <div class="control-group">
                                <formUtil:formInputBox readOnly="true" labelKey="Product Link" idKey="url" path="url" mandatory="true"
                                    inputCSS="disabled" />
                            </div>
                            <div class="control-group">
                                <formUtil:formInputBox idKey="emailId" labelKey="Friends' Email ID" path="emailId" placeholder="Email ID"
                                    mandatory="true" inputCSS="clearcontent" />
                            </div>
                            <div class="control-group">
                                <label for="j_username" class="control-label left mright10 "></label>
                                <formUtil:formTextArea idKey="message" labelKey="Message" path="message" mandatory="true"
                                    areaCSS="clearcontent" />
                            </div>

                        </div>

                        <div class="modal-footer" >
                            <button class="btn btn-red" type="submit" style="float: right;">Send Email</button>
                        </div>
                    </form:form>
                </div>
            </div>

        </div>
    </div>
    <!--popover end-->
    <div class="popover WL medium-box right" style="display: none;z-index: 1;" id="emailafriendresultpopover">
        <div class="popover-inner">
            <a class="popover-close" href="javascript:void(0);">x</a>
            <div class="popover-content">
                <span class="green-link bold" id="emailafriendresultmsg"></span>
            </div>
        </div>
    </div>
    <!--popover end-->
</div>
<!--  email a friend ends -->