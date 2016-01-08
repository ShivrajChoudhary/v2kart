<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>



<input type="hidden" value="<c:url value="/trackYourOrder/trackGuestOrder" />" id="trackGuestOrderUrl" />

<!--Track order Popup-->
<div class="modal fade track_your_order" id="trackOrder" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg track">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close track_your_order_close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title">
                    <spring:theme code="guest.track.your.order" text="Track your Order" />
                </h4>
            </div>
            <div class="modal-body">

                <div class="container">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="description">
                                <spring:theme code="guest.track.using.order.id" text="Track using Order ID" />
                            </div>

                            <form method="post">
                                <div class="form_field-elements">

                                    <div class="control-group">
                                        <label class="control-label " for="j_username"> Email Address <span class="skip"
                                            id="emailErrorId"></span> <span class="mandatory"> <spring:theme code="login.required"
                                                    var="loginrequiredText" /> <img width="5" height="6" alt="${loginrequiredText}"
                                                title="${loginrequiredText}" src="${commonResourcePath}/images/mandatory.png">
                                        </span>
                                        </label>
                                        <div class="controls">
                                            <input class="text" type="text" id="emailIdForOrderHistory">
                                        </div>
                                    </div>

                                    <div class="control-group">
                                        <label class="control-label " for="j_password"> Order ID <span class="skip"
                                            id="orderNoErrorId"></span> <span class="mandatory"> <spring:theme code="login.required"
                                                    var="loginrequiredText" /> <img width="5" height="6" alt="${loginrequiredText}"
                                                title="${loginrequiredText}" src="${commonResourcePath}/images/mandatory.png">
                                        </span>
                                        </label>
                                        <div class="controls">
                                            <input class="text password" value="" type="text" id="orderIdForOrderHistory">
                                        </div>
                                    </div>

                                </div>
                                <div class="form-actions clearfix">
                                    <button type="button" class="btn btn-red" id="OrderHistoryForGuest">
                                        <spring:theme code="guest.view.order.status" text="View Order Status" />
                                    </button>
                                </div>

                            </form>
                        </div>
                        <div class="col-md-6">
                            <div class="trackinfo">
                                <div class="description">
                                    <spring:theme code="guest.login.and.do.more.text" text="Login and do more!" />
                                </div>
                                <ul>
                                    <li><spring:theme code="guest.track.individual.order.text" text="Track individual Orders" /></li>
                                    <li><spring:theme code="guest.track.view.entire.order.text" text="View your entire Order history" /></li>
                                    <li><spring:theme code="guest.track.cancel.order.text" text="Cancel individual Orders" /></li>
                                    <li><spring:theme code="guest.track.conveniently.review.text"
                                            text="Conveniently review products and sellers." /></li>
                                </ul>
                                <div class="form-actions clearfix">
                                    <a href="<c:url value="/login"/>" class="btn btn-red" title="Login">Login</a>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>
                <!--container end-->
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<!-- /.modal -->


<form style="display: none;" id="trackOrderFormId" method="post" action="<c:url value="/trackYourOrder/trackGuestOrders"/>"
    name="TrackOrderForm">
    <input type="text" id="orderHistoryEmail" name="emailId"> <input type="text" id="orderHistoryOrderNo" name="orderNumber">
    <input type="hidden" name="CSRFToken" value="${CSRFToken}">
</form>
