function disablePageScrolling() {
	var scrollPosition = [
			self.pageXOffset || document.documentElement.scrollLeft
					|| document.body.scrollLeft,
			self.pageYOffset || document.documentElement.scrollTop
					|| document.body.scrollTop ];
	var html = jQuery('html');
	html.data('scroll-position', scrollPosition);
	html.data('previous-overflow', html.css('overflow'));
	html.css('overflow', 'hidden');
	window.scrollTo(scrollPosition[0], scrollPosition[1]);
}

function enablePageScrolling() {
	var html = jQuery('html');
	var scrollPosition = html.data('scroll-position');
	html.css('overflow', html.data('previous-overflow'));
	window.scrollTo(scrollPosition[0], scrollPosition[1]);
}

/* Scroll to top */
$(window).scroll(function() {
	if ($(this).scrollTop() >= 50) { // If page is scrolled more than 50px
		$('#return-to-top').fadeIn(200); // Fade in the arrow
	} else {
		$('#return-to-top').fadeOut(200); // Else fade out the arrow
	}
});
$('#return-to-top').click(function() { // When arrow is clicked
	$('body,html').animate({
		scrollTop : 0
	// Scroll to top of body
	}, 500);
});

/* Scroll to top ends */
$(".pinSearchInput").ready(function() {
	$('.checkPin').prop('disabled', true);
});

$(".pinSearchInput").keyup(function() {
	var regex = new RegExp(/^\+?([0-9]\d*)$/);
	if ((regex.test($(this).val())) && ($(this).val().length == 6)) {
		$('.checkPin').removeAttr("disabled");
	} else {
		$('.checkPin').prop('disabled', true);
	}
});

$(".pinSearchInput").keypress(function(e) {
		var code = e.which || e.keyCode;
		if((code>=37 && code<=40) || code==46 ||code==8){
			return true;
		}
		var digit = String.fromCharCode(code);
		if (!($.isNumeric(digit))) {
			return false;
		}
	
});

$(".pinSearchInput").change(function() {
	var regex = new RegExp(/^\+?([0-9]\d*)$/);
	if ((regex.test($(this).val())) && ($(this).val().length == 6)) {
		$('.checkPin').removeAttr("disabled");
	} else {
		$('.checkPin').prop('disabled', true);
	}
});

$(document).ready(function() {
	$(".pinSearchInput").keydown(function(event) {
		if (event.keyCode == 32) {
			event.preventDefault();
		}
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
						$(".serviceability-text").removeClass(
								'serviceability_notavilable');
						$(".serviceability-text").addClass(
								'serviceability_avilable');
						$(".serviceability-text").html(
								'  We deliver at your location. ');

					} else {
						$(".serviceability-text").removeClass(
								'serviceability_avilable');
						$(".serviceability-text").addClass(
								'serviceability_notavilable');
						$(".serviceability-text ")
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

/*
 * $(function() { var pinCode = $(".pinSearchInput").val(); if (pinCode != null &&
 * checkPincode(pinCode)) { checkForServiceability(); } });
 */

$("#pay-now-button").click(function() {
	var phn = $(".phone-no-sms").val();
	var regex = new RegExp(/^\+?([0-9]\d*)$/);
	if (phn == "") {
		return true;
	}
	if ((regex.test(phn)) && (phn.length == 10)) {
		$("#phoneNumberError").css("display", "none");
		return true;
	} else {
		$("#smsmobileno\\.errors").text('');
		$("#phoneNumberError").css("display", "block");
		return false;
	}

});


$('.v2PaymentInfoForm').submit(
		  function() {
		   if ($(".hiddenCountSubmit").val() == 1) {
		    $(".hiddenCountSubmit").val(
		      parseInt($(".hiddenCountSubmit").val()) + 1);
		    return true;
		   } else {
		    return false;
		   }
		  });


$(".faq_block li a").click(function(){
	$(".faq_block li").removeClass("active");
	
	$(this).parent().addClass("active");
	
});