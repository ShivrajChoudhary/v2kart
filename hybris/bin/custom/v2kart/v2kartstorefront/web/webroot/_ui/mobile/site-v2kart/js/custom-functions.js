$(document).ready(function() {
 
  $("div[id='NOW TRENDING']").owlCarousel({
 
      autoPlay: 5000, //Set AutoPlay to 5 seconds
      items : 2,
      itemsMobile : [420,2],
      lazyLoad : true,
      navigation : true,
      pagination : false
  });
  
  $("div[id='NEW ARRIVAL']").owlCarousel({
	  
      autoPlay: 5000, //Set AutoPlay to 5 seconds
      items : 2,
      itemsMobile : [420,2],
      lazyLoad : true,
      navigation : true,
      pagination : false
  });
  
  $("div[id='BEST SELLER']").owlCarousel({
	  
      autoPlay: 5000, //Set AutoPlay to 5 seconds
      items : 2,
      itemsMobile : [420,2],
      lazyLoad : true,
      navigation : true,
      pagination : false
  });
  
  $('.myAccountPage .accountNavigation li').off().on('click', function() {
	  $(this).toggleClass('accntNav-btn-black-bg');
  });
  $('#cancelorderButton').live('click', function() {
	 var content="<div class='cancelpopup'>" + $("#cancelorderButton").data("content")+"</div>";
	  $.mobile.easydialog({
			content: content,
			type: 'info'
		});
  });
  
  $('.order_history_view').on("click", function() {
		var content= $(this).data("content");
		var header = $(this).data("heading");
		$.mobile.easydialog({
			content: content,
			header: header,
			type: 'error'
		});
	});
  
  $('p.prod-title').dotdotdot();
  
  $('#placeOrderBtn').parent().hide();
  
  /*$('#payNowBtn').parent().hide();*/
  
  $('#codAccordion').on("expand", function() {
       $('#placeOrderBtn').parent().show();
       $('#shopMoreBtn').button('enable');
       $('#payNowBtn').parent().hide();
  });
  
  $('#onlinePaymentAccordion').on("expand", function() {
      $('#payNowBtn').parent().show();
      $('#placeOrderBtn').parent().hide();
  });
  
  //otp
  $("#codCheckboxChecked").click(function(){
	  if($(this).is(':checked')) {
		  $('#placeOrderBtn').button('enable');
	  } else {
		  $('#placeOrderBtn').button('disable');
	  }
  });

  $(".otpSmsInput").keyup(function() {
	    var regex = new RegExp(/^\+?([0-9]\d*)$/);
	    if ((regex.test($(this).val())) && ($(this).val().length == 6) && ($('#codCheckboxChecked').is(':checked') )) {
	        $('#placeOrderBtn').removeAttr("disabled");
	    } else {
	        $('#placeOrderBtn').prop('disabled', true);
	    }
	});

	$(".resentOtp").click(function() {
	    $.ajax({url: "cashOnDeliveryOtp", success: function(result){
	    	
	    	$(".resentOtp .ui-btn-text").html("Regenerate OTP");
	$(".otpMessage").html("OTP resent on your mobile number");
	$(".otpMessage").show();
	$(".not-valid-otp").hide();
	}});

	});
$('#placeOrderBtn').click(function(e){
	    if($("#placeOrderBtn").hasClass("placeOrderForOtp")){
	    	
	        e.preventDefault();
	        $(this).removeClass("placeOrderForOtp");
          $(this).html("Confirm Order").button('refresh');
	        $(this).prop('disabled', true);
	        $.ajax({url:'cashOnDeliveryOtp', success: function(result){
	           
	        }});
	        $('.codOtpTextBox').show();
	        $('#codCheckboxCheck').hide();
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
	                            $('#placeOrderBtn').click();
	                        }
	                        else{
	                        	$(".otpMessage").hide();
	                        	$(".not-valid-otp").show();
	                            $('.not-valid-otp').html(
	                            result.errorMessage);
	    
	                        }
	                    }
	                        
	                });
	        }
	    }
	    
	});
  //otp end
  $('#addNewAddress').click(function() {
	  $('div.saved-payment-list').hide();
	  $('div.enter_new_address').show();
  });
  
  $('#useExistingAddress').click(function() {
	  $('div.saved-payment-list').show();
	  $('div.enter_new_address').hide();
  });
  
  $('.orderHistoryTab').click(function(){
		var $active = $(this);
		if ($active.attr('id') == 'accessibletabsnavigation0-6') {
			$('#currentTab').val($(this).attr('id'));
		} else if ($active.attr('id') == 'accessibletabsnavigation0-7') {
			$('#currentTab').val($(this).attr('id'));
		}
	});
  
  	if(isParameterEmpty('q'))
		$("select.sortOptionsPLP").val('default').selectmenu('refresh');
	else
		$("select.sortOptionsPLP option:first-child").attr('disabled','disabled');
  	
  	$('.giftwrapicon').off().on('click', function() {
		$(this).toggleClass('gift-icon');
		$(this).toggleClass('gift-select-icon');
		var hiddenField = $(this).next();
		var val = hiddenField.val();
		hiddenField.val(val === "true" ? "false" : "true");
	});
	$(".continueCheckout").on("click", function(event) {
		$("#giftWrapEntries").val('');
		$(".giftWrap").each(function() {
		    if($(this).val() === "true" ){
		    	var entries = $("#giftWrapEntries").val().concat('_',$(this).attr("id"));
		    	$("#giftWrapEntries").val(entries);
		    }
		});
		var form = $("#checkoutForm");
		form.submit();
	});
	$('#termsAndConditionsAgree').click(function() {
	    if (!$('#termsAndConditionsAgree').is(':checked')) {
	        $('#payNowBtn').button('disable');
	    } else {
	        $('#payNowBtn').button('enable');
	    }
	});
	$('#more-button').click(function(event) {
		 event.preventDefault();
		$('#footerContainer').toggle();
		if($("#footerContainer").css("display") == "none"){
			$('#more-button').removeClass("ui-btn-active");
		}
	});
});

function isParameterEmpty(sParam)
{
    var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    for (var i = 0; i < sURLVariables.length; i++) 
    {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam) 
        {
            return false;
        }
    }
    return true;
}

function loginFormValidate() {
	if (document.LoginForm.j_username.value == "") {

		/*document.getElementById('errfn').innerHTML = "Please enter username and password";
		document.getElementById('errfn').style.display = "block";
*/
		LoginForm.j_username.focus();
		document.getElementById('globalMessages').style.display = "none";
		document.getElementById('loginError').getElementsByTagName('span')[1].innerHTML = 'Requried *';
		document.getElementById('loginError').style.color = "#c60300";
		if (document.LoginForm.j_password.value == "") {
			document.getElementById('passwordError').style.color = "#c60300";
			document.getElementById('passwordError').getElementsByTagName('span')[1].innerHTML = 'Requried *';
		}
		hideLoading()
		return false;
	}
	if (document.LoginForm.j_password.value == "") {

/*		document.getElementById('errfn').innerHTML = "Please enter username and password";
		document.getElementById('errfn').style.display = "block";
*/
		LoginForm.j_password.focus();
		document.getElementById('globalMessages').style.display = "none";
		document.getElementById('passwordError').style.color = "#c60300";
		document.getElementById('passwordError').getElementsByTagName('span')[1].innerHTML = 'Requried *';
		if (document.LoginForm.j_username.value == "") {
			document.getElementById('loginError').style.color = "#c60300";
		}
		hideLoading()
		return false;
	}

}
/*ATs*/

$(".pinSearchInput").ready(function() {

	
	
	$('.checkPin').button('disable');
	$('.track_order').on("click", function() {
		// make fields empty on every click
		$('.mobile_emailIdForOrderHistory').val('');
		$('.mobile_orderIdForOrderHistory').val('');
		$('#mobile_orderNoErrorId').text('');
		$('#mobile_emailErrorId').text('');

		// check user is loggedin or not
		var user = $('#mobile_check_user_login').val();
		if (user == 'false') {
			window.location.href="/trackYourOrder/trackGuestUserOrders"
		} else {
			window.location.href = "/trackYourOrder"
		}
	});


	$('#mobile_OrderHistoryForGuest')
	.click(
			function() {
			
				var emailId = $('.mobile_emailIdForOrderHistory').val();
				var orderNo = $('.mobile_orderIdForOrderHistory').val();
			
	            var containsError = false;
	           // validation on email and order fields
				var orderNoRegex = new RegExp(/^\+?([0-9]\d*)$/);
				var emailIdRegex = new RegExp(
						/[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+(?:[A-Z]{2}|com|org|net|edu|gov|mil|biz|info|mobi|name|aero|asia|jobs|museum)\b/);
				if (!orderNoRegex.test(orderNo)) {
					$('#mobile_orderNoErrorId').text("Order Id should be numeric");
					$('#mobile_orderIdNotFound').text("");
					if(emailIdRegex.test(emailId)){
						$('#mobile_emailErrorId').text("");
						$('#mobile_emailIdNotFound').text("");
					}
					containsError = true;
					
				}
				if(!emailIdRegex.test(emailId)){
					$('#mobile_emailErrorId').text("Enter valid email id");
					$('#mobile_emailIdNotFound').text("");
					if (orderNoRegex.test(orderNo)) {
						$('#mobile_orderNoErrorId').text("");
						$('#mobile_orderIdNotFound').text("");
					}
					containsError = true;
				}
			
				if(!containsError){
				
					$('#mobile_trackOrderFormId').serialize();
					$('#mobile_trackOrderFormId').submit();
			}
			});
			if($(".basket-page-shipping-pickup").text()==""){
				$(".changeStoreLink").hide();
			}else{
				$(".changeStoreLink").show();
			}
			$("#pickUpRadioButton").click(function() {
			    $(".changeStoreLink").show();
			});
			$("#shipRadioButton").click(function() {
			    $(".changeStoreLink").hide();
			});
});




function checkForServiceability() {


	var pinCode = $(".pinSearchInput").val();

	$
			.ajax({
				url : '**/p/getServiceableCheck',
				type : "GET",
				data : "pinCode=" + pinCode,
				success : function(results) {
					if (results.serviceability == "true") {
						$(".pincode-text").removeClass(
								'serviceability_notavilable');
						$(".pincode-text").addClass(
								'serviceability_avilable');
						$(".pincode-text").html(
								'  We deliver at your location. ');

					} else {
						$(".pincode-text").removeClass(
								'serviceability_avilable');
						$(".pincode-text").addClass(
								'serviceability_notavilable');
						$(".pincode-text	 ")
								.html(
										' Sorry, cannot be delivered at your location. ');
					}

					if (results.codServiceability == "true") {
						$(".codServiceability-text").removeClass(
								'serviceability_notavilable');
						$(".codServiceability-text").addClass(
								'serviceability_avilable');
						$(".codServiceability-text").html(
								'COD available on your location');

					} else {
						$(".codServiceability-text").removeClass(
								'serviceability_avilable');
						$(".codServiceability-text").addClass(
								'serviceability_notavilable');
						$(".codServiceability-text ").html(
								' Sorry,COD is not available. ');
					}

					$('.cod-msg').hide();
					$('#checkForm').hide();
					$('#validPin').show();
					$('#validCod').show();

				},
				error : function() {
					$(".pincode-text").removeClass('serviceability_avilable');
					$(".pincode-text").addClass('serviceability_notavilable');
					$(".pincode-text ")
							.html(
									'Currently we are facing some issue on check availiabilty.');
					$('#checkForm').hide();
					$('#validPin').show();
				}

			});


}

$('.pincodeChangeBtn').click(function(){		
	$('#validPin').hide();		
	$('#checkForm').show();
	$(".codServiceability-text").removeClass('serviceability_notavilable');
	$(".codServiceability-text").removeClass('serviceability_avilable');
	$(".codServiceability-text").html('May be available. Enter pincode to confirm');
})

function checkPincode(pincode) {
	alert("regex")
	pincodeRegex = /^\d{6}$/;
	if (!pincode.match(pincodeRegex)) {
		return false;
	}
	return true;
}

$(document).on('pageinit', function(event) {
	$("#searchServiceability").keyup(
			function() {
				
				var regex = new RegExp(/^\+?([0-9]\d*)$/);
				if ((regex.test($(this).val()))) {
					
					$('.checkPin').button('enable');
				} else {
					$('.checkPin').button('disable');
				}
			});
	
	$('.pincodeChangeBtn').click(function(){		
		$('#validPin').hide();		
		$('#checkForm').show();	
		$(".codServiceability-text").removeClass('serviceability_notavilable');
		$(".codServiceability-text").removeClass('serviceability_avilable');
		$(".codServiceability-text").html('May be available. Enter pincode to confirm');
	})
	
	$('#enforcedPaymentMethod4').click(function(){
		  if($(this).val()) {
			  $('#payNowBtn').button('enable');
		  } else {
			  $('#payNowBtn').button('disable');
		  }
	});
	
});

$(document).ready(function(){
	$(".accmob-mainSearch-input").keydown(function (e){
             $('.accmob-search input.accmob-mainSearch-input').css('font-size','12px');
             $('.accmob-search input.accmob-mainSearch-input').css('line-height','1.9em');
    });
	$(".accmob-mainSearch-input").keyup(function(){
		var val=$('.accmob-search input.accmob-mainSearch-input').val();
		 if(val.length == 0){
        	 $('.accmob-search input.accmob-mainSearch-input').css('font-size','10px');	
        	 $('.accmob-search input.accmob-mainSearch-input').css('line-height','2.4em');
         }
	});
	$(window).load(function() {

		var value = $('#isAddedToWishlist').val();
		var content= "<div class='wishpopup'>"+$("#wishlistButton").data("content")+"</div>";
		if (value == 'true') {
			ACCMOB.product.addGlobalMessage($("#wishlistButton").data("heading"),content, 'conf');
			// $('.wishlist').popover('hide');

		}
	});
    });