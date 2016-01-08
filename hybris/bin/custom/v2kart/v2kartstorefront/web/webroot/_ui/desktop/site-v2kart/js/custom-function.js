$(document)
		.ready(
				function() {
					if($('input:radio[name="shipMode"]:checked').val()=='pickUp'){
						        	 $(".changeStoreLink").show();
						        }
					if($('input:radio[name="shipMode"]:checked').val()=='ship'){
						        	$(".changeStoreLink").hide();
						        }
					if ($('#termsAndConditionsAgree').is(':checked')) {
				        $('#payNowBtn').removeAttr('disabled');
				    }
						    
					/* rating js for IE11 */
					$(".ratingStars").click(function() {
						var arr = $(this).attr("id").split("_");
						var length = 5;
						for (var btnNo = 1; btnNo <= length; btnNo++) {

							var ratingId = '#rating' + btnNo;
							var rateStars = '#ratingStars_' + btnNo;
							if (btnNo <= arr[1]) {
								$(rateStars).removeClass('no_star');
								var rating = 'rating' + btnNo;
								document.getElementById(rating).checked = true;
							} else {
								$(rateStars).addClass('no_star');
							}
						}
					});

					$("#sizeGuideId").click(
							function() {
								$('html, body').animate(
										{
											scrollTop : $("#productTabs")
													.offset().top - 150
										}, 1000);

								$('#accessibletabsnavigation0-0').addClass(
										'current');
								$('#accessibletabsnavigation0-01').removeClass(
										'current');
								$('#tab-reviews').removeClass('current');
								$('.productTabDescription').css("display",
										"block");
								$('#accessibletabscontent0-01')
										.next('.tabBody').hide();
								$('#review-tab').css("display", "none");

							});

					$("input[name='delivery_method']").change(
							function() {

								var action = $("input[name='currentStepUrl']")
										.prop('value');
								// alert(action);
								var method = $(this).prop('value');
								window.location.href = action
										+ '/selected/?delivery_method='
										+ method

								// alert(
								// action+'/selected/?delivery_method='+method)

							});

					/*
					 * $('.miniCart').hover( function() { // added to remove add
					 * to cart popup(having // fadeout effect of 5 sec) due to
					 * popup // conflicting issue(add to cart popup and mini //
					 * cart popup) $('#addToCartLayer').remove();
					 * 
					 * $(this).find('.miniCartPopup').stop(true, true)
					 * .slideDown('slow'); }, function() {
					 * $(this).find('.miniCartPopup').stop(true, true)
					 * .slideUp('fast'); })
					 */

					$('.miniCart').hover(function() {
						$('#addToCartLayer').remove();

					}, function() {

					})

					// Slider
					$('#main-slider').bxSlider({
						auto : true,
						controls : true,
						adaptiveHeight : true,
						pager: false

					});

					// Slider Now Trending
					$("div[id='NOW TRENDING']").bxSlider({
						slideWidth : 176,
						minSlides : 2,
						maxSlides : 5,
						slideMargin : 10,
						pager : false
					});

					// Slider New Arrival

					$("div[id='NEW ARRIVAL']").bxSlider({
						slideWidth : 176,
						minSlides : 2,
						maxSlides : 5,
						slideMargin : 10,
						pager : false
					});

					// Slider Best Seller

					$("div[id='BEST SELLER']").bxSlider({
						slideWidth : 176,
						minSlides : 2,
						maxSlides : 5,
						slideMargin : 10,
						pager : false
					});

					// Forgot password popup
					$('#forgotPwdPopup').on('shown.bs.modal', function() {
					})

					// Popover
					$('.popover').hide();
					$('.popover-link a').click(function() {
						$('.popover').hide();
						$(this).next('.popover').fadeToggle();
					});

					$('.popover.WL').hide();
					$('.popover-close.WL').click(function() {
						$('.popover.WL').show();
						$('.popover.WL').fadeToggle();
					});

					$('.popover-close.LG').click(function() {
						$('.popoverLG').hide();
					});

					$(".popoverNotifyMeClose").click(function() {
						$(".popoverNotifyMe").hide();
					});

					$(".notifymeresultpopoverClose").click(function() {
						$("#notifymeresultpopover").hide();
					});

					$('.emailProductPage form')
							.submit(
									function(e) {
										e.preventDefault();
										$('span.skip').show();
										var $form = $(this);
										$('input:text,textarea', $form)
												.removeClass('validation_error');
										$('#emailafriendpopover').hide();
										$
												.ajax({
													type : 'POST',
													url : this.action,
													data : $(this).serialize(),
													success : function(data) {
														$('span.skip', $form)
																.empty();
														if (data.status === 'FAIL') {
															$(
																	'#emailafriendpopover')
																	.show();
															$(data.errors)
																	.each(
																			function() {
																				$(
																						'.emailProductPage #'
																								+ this.fieldName)
																						.addClass(
																								'validation_error');
																				$(
																						'label[for="'
																								+ this.fieldName
																								+ '"]',
																						$form)
																						.find(
																								'span.skip')
																						.append(
																								jQuery('<span>'
																										+ this.message
																										+ '</span>'));
																			});
														}
														if (data.status === 'UNABLE') {
															$(
																	'#emailafriendresultmsg')
																	.html(
																			'Error Occured. Please try again later.');
															$(
																	'#emailafriendresultmsg')
																	.css(
																			'color',
																			'red');
															$(
																	'#emailafriendresultpopover')
																	.show();
														}
														if (data.status === 'SUCCESS') {
															$(
																	'#emailafriendresultmsg')
																	.html(
																			'Product URL succesfully sent to your friend').css('color','green');
															$(
																	'#emailafriendresultpopover')
																	.show();
														}
													}
												});
										return false;
									});

					$('.notifyForm form')
							.submit(
									function(e) {
										e.preventDefault();
										$('span.skip').show();
										var $form = $(this);
										var formId = $form.attr('id');
										$('input:text,textarea', $form)
												.removeClass('validation_error');
										if (formId === 'notifyMe') {
											$('#notifyMe').hide();
										}
										if (formId === 'notifyMyPrice') {
											$('#notifymypricepopover').hide();
										}
										$
												.ajax({
													type : 'POST',
													url : this.action,
													data : $(this).serialize(),
													success : function(data) {
														$('span.skip', $form)
																.empty();
														if (data.status === 'FAIL') {
															if (formId === 'notifyMe') {
																$('#notifyMe')
																		.show();
															}
															if (formId === 'notifyMyPrice') {
																$(
																		'#notifymypricepopover')
																		.show();
															}
															$(data.errors)
																	.each(
																			function() {
																				$(
																						'.notifyForm #'
																								+ this.fieldName)
																						.addClass(
																								'validation_error');
																				$(
																						'label[for="'
																								+ this.fieldName
																								+ '"]',
																						$form)
																						.find(
																								'span.skip')
																						.append(
																								jQuery('<span>'
																										+ this.message
																										+ '</span>'));
																			});
														}
														if (data.status === 'UNABLE') {
															if (formId === 'notifyMe') {
																$(
																		'#notifymeresultmsg')
																		.html(
																				'Error Occured. Please try again later.');
																$(
																		'#notifymeresultmsg')
																		.css(
																				'color',
																				'red');
																$(
																		'#notifymeresultpopover')
																		.show();
															}
															if (formId === 'notifyMyPrice') {
																$(
																		'#notifymypriceresultmsg')
																		.html(
																				'Error Occured. Please try again later.');
																$(
																		'#notifymypriceresultmsg')
																		.css(
																				'color',
																				'red');
																$(
																		'#notifymypriceresultpopover')
																		.show();
															}
														}
														if (data.status === 'SUCCESS') {
															if (formId === 'notifyMe') {
																$(
																		'#notifymeresultmsg')
																		.html(
																				'Your email id has been saved. We will notify once product is in stock');
																$(
																		'#notifymeresultpopover')
																		.show();
															}
															if (formId === 'notifyMyPrice') {
																$(
																		'#notifymypriceresultmsg')
																		.html(
																				'Your email id has been saved. We will notify once product is available at this price');
																$(
																		'#notifymypriceresultpopover')
																		.show();
															}
														}
													}
												});
										return false;
									});
					
					// Submit gift wrap entries on checkout
					$(".submitCheckoutForm").on("click", function(event) {
						$('#checkoutProgress li').first().addClass('disabled');
						$("#giftWrapEntries").val('');
						$(".giftWrap").each(function() {
						    if($(this).attr("checked") ){
						    	var entries = $("#giftWrapEntries").val().concat('_',$(this).attr("value"));
						    	$("#giftWrapEntries").val(entries);
						    }
						});
						
						var form = $("#checkoutForm");
						form.submit();
					});
					$("#checkoutForm").on("submit",function(event) {
						$('#checkoutProgress').addClass('test');
					});
						
					
					// My account remove address popup
					$('.removeAddressMyAccount').on("click", function() {
						$("div").removeClass("cboxElement")
					});
					
					// for faq page
					$('#accordion').accordion({
						collapsible : true,
						// animated:'slide',
						heightStyle : "content",
						autoHeight : false,
						clearStyle : true
					});

					$('#accordion h2').bind('click', function() {
						var self = this;
						setTimeout(function() {
							theOffset = $(self).offset();
							$('body,html').animate({
								scrollTop : theOffset.top - 200
							});
						}, 310); // ensure the collapse animation is done
					});

					var $accordion = $("#accordion");

					$accordion.accordion();

					$(".faq_block li a").on("click", function() {
						var $this = $(this), toOpen = $this.data("panel");

						$accordion.accordion("option", "active", toOpen);

						return true;
					});

					// Slider Best Seller

					$('.bestSeller').bxSlider({
						slideWidth : 176,
						minSlides : 2,
						maxSlides : 5,
						slideMargin : 20,
						pager : false
					});

					// Fixed select dropdown issue
					var nav = document.querySelector('#navbar');
					var select = document.querySelector('select');
					if (null != select) {
						nav.addEventListener('mouseover', function() {
							setTimeout(function() {
								select.blur();
							}, 10);
						});
						select.addEventListener('click', function() {
							this.focus();
						});
					}

					/**/
					$('.refinementToggle').click(
							function() {
								$(this).toggleClass('closefacet');
								$(this).parent().next('.facetValues')
										.slideToggle('fast');

							})

					// Price Slider

					/*
					 * $("#priceslider").slider(); $("#priceslider").on("slide",
					 * function(slideEvt) {
					 * $("#priceSliderVal").text(slideEvt.value); });
					 */

					$("#ex2").slider({});
					$('#ex2')
							.slider()
							.on(
									'slideStop',
									function(ev) {
										var adjustValue1 = 15;
										var adjustValue2 = 0;
										if (ev.value[0] == ev.value[1]) {
											if ($('#ex2').data('slider-min')
													+ adjustValue1 > ev.value[0]) {
												adjustValue1 = 0;
												adjustValue2 = 15;
											}
											$('#ex2')
													.slider(
															'setValue',
															[
																	ev.value[0]
																			- adjustValue1,
																	ev.value[1]
																			+ adjustValue2 ]); // if
											// equal
											// change
											// value

										}
										validateAndPostPrice(ev);

									});
					$('#ex2').slider().on('slide', function(ev) {
						$('#left').text(ev.value[0]);
						$('#right').text(ev.value[1]);
					});

					// Action btn hover

					$('.action-btn').hover(
							function() {
								$(this).prev('.productMainLink ').addClass(
										'gridborder');
							},
							function() {
								$(this).prev('.productMainLink').removeClass(
										'gridborder');
							})

					// Quick view popup
					$(".quickbtn").colorbox({
						width : "50%",
						height : "auto",
						title : true,
						reposition : true
					});

					// Payment Page Billing address
					if ($('#isUsingShippingAddress').is(':checked')) {
						$('#bill-address-content').hide();
					}
					/**/

					/*
					 * Code for the Order History Tab Switching and Sort By
					 * Functionality
					 */
					$('.orderHistoryTab')
							.click(
									function() {
										var $active = $(this);
										if ($active.attr('id') == 'accessibletabsnavigation0-6') {
											$('#accessibleanchor0-6').addClass(
													'current');
											$('#accessibleanchor0-7')
													.removeClass('current');

											$('#currentTab').val(
													$(this).attr('id'));
										} else if ($active.attr('id') == 'accessibletabsnavigation0-7') {
											$('#accessibleanchor0-7').addClass(
													'current');
											$('#accessibleanchor0-6')
													.removeClass('current');

											$('#currentTab').val(
													$(this).attr('id'));
										}
									});

					/*
					 * Code for the Order History Tab Switching and Sort By
					 * Functionality
					 */

					/*$('.GoTohomePage').click(function(){
						var homepage = $("#homepageUrl").val();
						window.location.href = homepage;
					});*/
					
					$("#payNowDiv").click(function(e) {
						var button = $("#payNowBtn");
						if(button.is(":disabled")) {
							button.popover('show');
							setTimeout(function() {
								     button.popover('hide');
								}, 3000);
							e.stopPropagation();
						}
					});
				})

function showNotifyMe() {

	$('.popover').hide();
	$('#notifyMe').fadeToggle();
}

/*
 * $(document).ready(function(){ $('.miniCart').hover(function(){
 * $(this).find('.miniCartPopup').stop(true, true).slideDown('slow');
 * },function(){ $(this).find('.miniCartPopup').stop(true,
 * true).slideUp('fast'); }) // Slider $('#main-slider').bxSlider({ auto: true,
 * controls : false, adaptiveHeight: true
 * 
 * }); // Slider Now Trending $("div[id='NOW TRENDING']").bxSlider({ slideWidth:
 * 130, minSlides: 2, maxSlides: 6, slideMargin: 20, pager: false }); // Slider
 * New Arrival
 * 
 * $("div[id='NEW ARRIVAL']").bxSlider({ slideWidth: 130, minSlides: 2,
 * maxSlides: 6, slideMargin: 20, pager: false }); // Slider Best Seller
 * 
 * $("div[id='BEST SELLER']").bxSlider({ slideWidth: 130, minSlides: 2,
 * maxSlides: 6, slideMargin: 25, pager: false }); // Forgot password popup
 * $('#forgotPwdPopup').on('shown.bs.modal', function () { }) // Facet accordion
 * 
 * $('.refinementToggle').click(function(){ $(this).toggleClass('closefacet');
 * $(this).parent().next('.facetValues').slideToggle('fast'); }) // Price Slider
 * 
 * $("#priceslider").slider(); $("#priceslider").on("slide", function(slideEvt) {
 * $("#priceSliderVal").text(slideEvt.value); });
 * 
 * 
 * $("#ex2").slider({}); $('#ex2').slider() .on('slideStop', function(ev){ var
 * adjustValue1 = 15; var adjustValue2 = 0; if(ev.value[0] == ev.value[1]){
 * if($('#ex2').data('slider-min') + adjustValue1 > ev.value[0]){ adjustValue1 =
 * 0; adjustValue2 = 15; }
 * $('#ex2').slider('setValue',[ev.value[0]-adjustValue1,ev.value[1]+adjustValue2]); //
 * if // equal // change // value } validateAndPostPrice(ev);
 * 
 * }); $('#ex2').slider() .on('slide', function(ev){
 * $('#left').text(ev.value[0]); $('#right').text(ev.value[1]); }); // Action
 * btn hover
 * 
 * $('.action-btn').hover(function(){ $(this).prev('.productMainLink
 * ').addClass('gridborder'); }, function(){
 * $(this).prev('.productMainLink').removeClass('gridborder'); }) // Quick view
 * popup $(".quickbtn").colorbox({width:"auto", height:"auto", title:true,
 * reposition: true}); // Cart Page Popup
 * $('.cartPagePopup').removeClass('cboxElement'); // Payment Page Billing
 * address if($('#isUsingShippingAddress').is(':checked')){
 * $('#bill-address-content').hide(); } })
 */

$('#isUsingShippingAddress').click(function(e) {
	if ($(this).is(':checked')) {
		$('#bill-address-content').hide();
	} else {
		$('#bill-address-content').show();
	}
});

$(window).resize(function() {

	if ($('#cboxOverlay').is(':visible')) {
		$.colorbox.load(true);
	}

});
$(window).load(function() {

	var value = $('#isAddedToWishlist').val();
	if (value == 'true') {
		$('.wishlist').popover('show');
		setTimeout(function() {
			$('.wishlist').popover('hide');
		}, 3000);
		// $('.wishlist').popover('hide');

	}
});

function validateAndPostPrice(elem) {
	var minPrice = elem.value[0];
	var maxPrice = elem.value[1];
	if ($.isNumeric(minPrice) && $.isNumeric(maxPrice)) {
		/*
		 * $(".customPriceError").html("&nbsp;"); $(".customPriceError").hide();
		 * $(".customPriceErrorBelow").show();
		 */
		var intMin = parseInt(minPrice, 10);
		var intMax = parseInt(maxPrice, 10);
		if (!(intMin < 0 || intMin == -0 || intMax < 0 || intMax == -0 || intMin >= intMax)) {
			var currentQuery = $("form.customPriceForm").find("input[name=q]")
					.val();
			currentQuery = removePriceFromQuery(currentQuery);
			$("form.customPriceForm").find("input[name=q]").val(
					currentQuery + ":price:" + minPrice + "-" + maxPrice);
			/*
			 * $("form.customPriceForm").find("input[name=customMinPrice]").prop(
			 * "disabled", true);
			 * $("form.customPriceForm").find("input[name=customMaxPrice]").prop(
			 * "disabled", true);
			 */
			$("form.customPriceForm").submit();
			return;
		}
	}
	/*
	 * var msg = "Invalid Range!!" $(".customPriceError").show();
	 * $(".customPriceError").html(msg); $(".customPriceErrorBelow").hide();
	 */
}

function removePriceFromQuery(query) {
	return query.replace(/,/g, "").replace(
			/:price:[0-9]*\.?[0-9]*-[0-9]*\.?[0-9]*/gi, "");
}

function sizeClick(productUrl) {
	var url = "";
	// var selectedIndex = 0;
	var parentClass = "";
	// url = $('#'+buttonId).attr('value');
	url = productUrl;
	// selectedIndex = $(this).attr("index");
	parentClass = $('.pdp-size-btn').parent().parent().parent().parent()
			.parent().parent().parent().parent().parent().parent()
			.attr('class');

	if (parentClass == 'quickview') {
		url = url + "/quickView";
		showSpinnerById("quickViewSpinner");
		$.ajax({
			url : url,
			success : function(result) {
				hideSpinnerById("quickViewSpinner");
				$("#cboxLoadedContent").empty();
				$("#cboxLoadedContent").html(result);
			}
		});

	} else {
		// if (selectedIndex != 0) {
		window.location.href = url;
		// }

	}

}

function hideSpinnerById(id) {
	$('#' + id).hide();
}
function showSpinnerById(id) {
	$('#' + id).show();
}

function cancelOrder() {
	$("#orderCancelResultpopover").show();
}

$(".orderCancelResultpopoverClose").click(function() {
	$("#orderCancelResultpopover").hide();
});

$(".clearpopovercontent").click(function() {
	$('.clearcontent').val('');
	$('.clearcontent').removeClass('validation_error');
	$('span.skip').hide();
});

// notfiy me functionality for plp and add to wishlist page

$('.notifyplpaddtowishlistform form')
		.submit(
				function(e) {
					e.preventDefault();
					$('span.skip').show();
					var $form = $(this);
					var formId = $form.attr('id');
					$('input:text', $form).removeClass('validation_error');
					// id is used to hide/show the
					// respective popover and its msgs if
					// there are multiple popovers on page
					var id = formId.split("_");
					id = id[1];
					$('#notifymepopover_' + id).hide();
					$
							.ajax({
								type : 'POST',
								url : this.action,
								data : $(this).serialize(),
								success : function(data) {
									$('span.skip', $form).empty();
									if (data.status === 'FAIL') {
										$('#notifymepopover_' + id).show();
										$(data.errors)
												.each(
														function() {
															$(
																	'.notifyplpaddtowishlistform #'
																			+ this.fieldName
																			+ '_'
																			+ id)
																	.addClass(
																			'validation_error');
															$(
																	'label[for="'
																			+ this.fieldName
																			+ '_'
																			+ id
																			+ '"]',
																	$form)
																	.find(
																			'span.skip')
																	.append(
																			jQuery('<span>'
																					+ this.message
																					+ '</span>'));
														});
									}
									if (data.status === 'UNABLE') {
										$('#notifymeresultmsg_' + id)
												.html(
														'Error Occured. Please try again later.');
										$('#notifymeresultmsg_' + id).css(
												'color', 'red');
										$('#notifymeresultpopover_' + id)
												.show();
									}
									if (data.status === 'SUCCESS') {
										$('#notifymeresultmsg_' + id)
												.html(
														'Your email id has been saved. We will notify once product is in stock');
										$('#notifymeresultpopover_' + id)
												.show();
									}
								}
							});
					return false;
				});
//OTP on COD

$(".otpSmsInput").keyup(function() {
    var regex = new RegExp(/^\+?([0-9]\d*)$/);
    if ((regex.test($(this).val())) && ($(this).val().length == 6) && ($('#codCheckboxChecked').is(':checked') )) {
        $('#codChargesApplicableOrderButton').removeAttr("disabled");
    } else {
        $('#codChargesApplicableOrderButton').prop('disabled', true);
    }
});

$("#codCheckboxChecked").click(function() {
    if (!$('#codCheckboxChecked').is(':checked')) {
        $('#codChargesApplicableOrderButton').attr("disabled", true);
    }
//    if ($('#codCheckboxChecked').is(':checked') && (regex.test($('.codOtpTextBox').val())) && ($('.codOtpTextBox').val().length == 6) ) {
    if ($('#codCheckboxChecked').is(':checked')) {
                $('#codChargesApplicableOrderButton').removeAttr("disabled");
    } else {
        $('#codChargesApplicableOrderButton').removeAttr("disabled");
        $('#codChargesApplicableOrderButton').prop('disabled', true);
    }
});


$(".resentOtp").click(function() {
    $.ajax({url: "cashOnDeliveryOtp", success: function(result){
$(".otpMessage").html("OTP resent on your mobile number");
$(".otpMessage").show();
$(".not-valid-otp").hide();

}});

});

/*
$('#codChargesApplicableOrderButton').click(function(e){
    if($("#codChargesApplicableOrderButton").hasClass("placeOrderForOtp")){
        e.preventDefault();
        $(this).removeClass("placeOrderForOtp");
        $(this).html("Confirm Order");
        $(this).prop('disabled', true);
        
        $.ajax({url:'cashOnDeliveryOtp', success: function(result){
           
        }});
        $('.codOtpTextBox').show();
        $('#codCheckboxChecked').hide();
        $('.textcod').hide();
        
        
    }
    else{
        if (e.originalEvent !== undefined){
            e.preventDefault();
            var otp = $('.otpSmsInput').val();
                       $    .ajax({
                    url : 'checkCashOnDeliveryOtp',
                    type : "GET",
                    data : "otpdata=" + otp,
                    success : function(result) {
                        if(result.isMatch == 'true'){
                            $('#codChargesApplicableOrderButton').click();
                            $('.codOtpTextBox').show();
                	        $('#codCheckboxCheck').hide();
                	        $('.textcod').hide();
                	        
                        }
                        
                        else{
                            $('.not-valid-otp').html(
                            result.errorMessage);
                            $(".otpMessage").hide();
                        	$(".not-valid-otp").show();
                          
    
                        }
                    }
                        
                });
        }
    }
    
});

*/


$('.pincodeChangeBtn').click(function() {
	$('#validPin').hide();
    $('.cod-msg').show();
    $('#checkForm').show();
    $('#validCod').hide();
	
});

$('.order_history_view').on("click", function() {
	$('.order_history_popup').removeClass('cboxElement');
});

$('.track_order').on("click", function() {
	// make fields empty on every click
	$('#emailIdForOrderHistory').val('');
	$('#orderIdForOrderHistory').val('');
	$('#orderNoErrorId').text('');
	$('#emailErrorId').text('');

	// check user is loggedin or not
	var user = $('#check_user_login').val();
	if (user == 'false') {
		$('#trackOrder').modal();
		$('.track_your_order').removeClass('cboxElement');
	} else {
		window.location.href = "/trackYourOrder"
	}
});

$('#OrderHistoryForGuest')
		.click(
				function() {
					var emailId = $('#emailIdForOrderHistory').val();
					var orderNo = $('#orderIdForOrderHistory').val();
                    var containsError = false;
					
                   // validation on email and order fields
					var orderNoRegex = new RegExp(/^\+?([0-9]\d*)$/);
					var emailIdRegex = new RegExp(
							/[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+(?:[A-Z]{2}|com|org|net|edu|gov|mil|biz|info|mobi|name|aero|asia|jobs|museum)\b/);
					if (!orderNoRegex.test(orderNo)) {
						$('#orderNoErrorId').text("Enter valid order id");
						if(emailIdRegex.test(emailId)){
							$('#emailErrorId').text("");
						}
						containsError = true;
					}
					if(!emailIdRegex.test(emailId)){
						$('#emailErrorId').text("Enter valid email id");
						if (orderNoRegex.test(orderNo)) {
							$('#orderNoErrorId').text("");
						}
						containsError = true;
					}
                  
					if(!containsError){
					var url = $('#trackGuestOrderUrl').val();
					var obj = {};
					obj.emailId = $('#emailIdForOrderHistory').val();
					obj.orderNumber = $('#orderIdForOrderHistory').val();

					var $data = {
						emailId : emailId,
						orderNumber : orderNo
					};
					$.ajax({
						type : 'GET',
						data : $data,
						url : url,
						dataType : "json",
						success : function(data) {
							if (data.success) {
								$('#orderHistoryEmail').val(emailId);
								$('#orderHistoryOrderNo').val(orderNo);
								$('#trackOrderFormId').serialize();
								$('#trackOrderFormId').submit();
							} else {
								$('#emailErrorId').text("");
								$('#orderNoErrorId').text("");
								if (data.orderError) {
									$('#orderNoErrorId').text(data.orderMsg);
									if (data.emailError) {
										$('#emailErrorId').text(data.emailMsg);
									}
								}
								if (data.emailError) {
									$('#emailErrorId').text(data.emailMsg);
									if (data.orderError) {
										$('#orderNoErrorId')
												.text(data.orderMsg);
									}
								}
							}

						},
					});
				}
				});

/**
 * ******************************************* PLP Facet Refresh Restriction
 * code***********************************
 */
function submitFacetForm(facetQuery){
	$('#unfilteredQuery').val(facetQuery);
	$('form#customForm').submit();
}

function submitSortOptions(){
	$('form#sort_form_page1').submit();
}

/*$('form#customForm').submit(function (e) {
    e.preventDefault();
    showSpinnerById("plpSpinner");
    var options = {
            success: updateListPage,
        }
        $(this).ajaxSubmit(options);
});

$('form#sort_form_page1').submit(function (e) {
    e.preventDefault();
    showSpinnerById("plpSpinner");
    var options = {
            success: updateListPage,
        }
        $(this).ajaxSubmit(options);
});


$('form.customPriceForm').submit(function (e) {
    e.preventDefault();
    showSpinnerById("plpSpinner");
    var options = {
            success: updateListPage,
        }
        $(this).ajaxSubmit(options);
});
*/
function updateListPage(data) {
	hideSpinnerById("plpSpinner");
    $('#categoryPage').html(data);
}

function facetClick(queryUrl){
	var url=queryUrl;
	showSpinnerById("plpSpinner");
	$.ajax({
		type : 'GET',
		url : url,
		success : function(result) {
			hideSpinnerById("plpSpinner");
			 $('#categoryPage').html(result);
		}
	});	
}


/**
 * ******************************************* PLP Facet Refresh Restriction
 * code ends***********************************
 */

function payThroughCashCard(){
	$("#cashCard").prop("checked", true); 
}

// for Date of Birth date picker
$(function() {

	var currentYear = (new Date).getFullYear();
	var currentMonth = (new Date).getMonth();
	var currentDay = (new Date).getDate();
	$(".profile-dateOfBirth").datepicker({
		showOn : "focus",
		buttonImageOnly : true,
		dateFormat : 'dd/mm/yy',
		changeYear : true,
		yearRange : "-100:+0",
		maxDate : new Date(currentYear, currentMonth, currentDay)
	});
});


$(function(){
    if($('body').is('.page-productDetails')){
  CloudZoom.quickStart();
    }
});



$('#termsAndConditionsAgree').click(function() {
    if (!$('#termsAndConditionsAgree').is(':checked')) {
        $('#payNowBtn').attr('disabled','disabled');
    } else {
        $('#payNowBtn').removeAttr('disabled');
    }
});

$("input[id$='shipModePickUp']").click(function() {
    $(".changeStoreLink").show();
}); 

$("input[id$='shipMode']").click(function() {
    $(".changeStoreLink").hide();
}); 


$(document).ready(function() {
	  if($('body').is('.page-multiStepCheckoutSummaryPage')){
	if($("#pickup").val() == 'true'){
		$('#checkoutProgress li>a').click(function(e){e.preventDefault();});
	}	
	}

});
