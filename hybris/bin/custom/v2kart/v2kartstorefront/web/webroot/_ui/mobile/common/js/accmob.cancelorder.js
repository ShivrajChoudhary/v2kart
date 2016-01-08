$(document).ready(function() {
	ACCMOB.cancelorder.bindAll();
	
	$('#cancel-btn-chng').button('disable');
	$('#cancelAgreementChck').change(function() {
		if ($('#cancelAgreementChck').is(':checked') && $(".accountCancelOrderPage .ui-bar-a h1").text() === $('#fullOrderCancellationText').val()) {
			if(validateCancelReason() === true) {
				getCancelRefundAmount();
				$('#cancel-btn-chng').button('enable');
			} else {
				$(this).removeAttr('checked');
			}
		}
		else if ($('#cancelAgreementChck').is(':checked') && $(".accountCancelOrderPage .ui-bar-a h1").text() === $('#partialOrderCancellationText').val()) {
			if(validateQuantity() === true) {
				getCancelRefundAmount();
				$('#cancel-btn-chng').button('enable');
			} else {
				$(this).removeAttr('checked');
			}
		} else {
			$('#cancel-btn-chng').button('disable');
		}
	});
	$('#fullOrderCancelDiv').hide();
	$('#partialOrderCancellation').parent().hide();
	$('#fullOrderCancellation').change(function() {
		$('#cancelAgreementChck').removeAttr('checked');
		$('#cancelAgreementChck').checkboxradio("refresh");
		if ($('#fullOrderCancellation').is(':checked')) {
			$('#isFull').val("true");
			$('#fullOrderCancelDiv').show();
			$('#partialOrderCancelDiv  :input').attr('disabled', 'disabled');
			$('#partialOrderCancelDiv').css('opacity', '0.3');
			$('#partialOrderCancellation').prop("checked", false);
			$('#partialOrderCancellation').checkboxradio("refresh");
			$('#partialOrderCancellation').parent().show();
			$('#fullOrderCancellation').parent().hide();
			$('.accountCancelOrderPage .ui-bar-a h1').html($('#fullOrderCancellationText').val());
		}
	});
	$('#partialOrderCancellation').change(function() {
		$('#cancelAgreementChck').removeAttr('checked');
		$('#cancelAgreementChck').checkboxradio("refresh");
		if ($('#partialOrderCancellation').is(':checked')) {
			$('#isFull').val("false");
			$('#fullOrderCancelDiv').hide();
			$('#partialOrderCancelDiv  :input').removeAttr('disabled');
			$('#partialOrderCancelDiv').css('opacity', '1');
			$('#partialOrderCancellation').parent().hide();
			$('#fullOrderCancellation').prop( "checked", false );
			$('#fullOrderCancellation').checkboxradio("refresh");
			$('#fullOrderCancellation').parent().show();
			$('.accountCancelOrderPage .ui-bar-a h1').html($('#partialOrderCancellationText').val());
		}
	});
	
	function validateCancelReason(){
		var comp=$('#order\\.cancelled\\.reason').val();
		var success=true;
		if(comp == "" || comp == null){
			$(".cancelReason_div div.ui-select").css('border','1px solid red');
			$('select').selectmenu('refresh');
			$("#error_boxType").text("Please select a reason for cancellation.");
			$(".refund-amount").text(" Rs.0");
			$('#cancelAgreementChck').checkboxradio("refresh");
			$("#error_boxType").show();
			success = false;
		}else {
			$(".cancelReason_div div.ui-select").css('border','');
			$("#error_boxType").hide();
			success = true;
		}
		return success;
	}
	
	function validateQuantity(){
		var count=$('#countofEntries').val();
		var success=true;
		var allBlank = true;
		for(i=0;i<parseInt(count);i++){
			$("#error_boxType1_" + i).hide();
			$("#error_boxType2_" + i).hide();
			var qty=(document.getElementsByName("cancelledEntries["+i+"].maxQuantity")[0].value);
			var cancelQty=(document.getElementsByName("cancelledEntries["+i+"].quantity")[0].value);
			var reason=(document.getElementsByName("cancelledEntries["+i+"].reason")[0].value);
			
			$(".cancelQuantity_div div.ui-input-text").css('border-color','');
			
			if(cancelQty == "" && reason== "" ){
				continue;
			}
			allBlank = false;
			if(cancelQty == "" || !validateDouble(cancelQty)){
				$(".cancelQuantity_div div.ui-input-text").css('border-color','red');
				$(".refund-amount").text(" Rs.0");
				$('#cancelAgreementChck').checkboxradio("refresh");
				$("#error_boxType1_" + i).text("Please enter valid quantity");
				$("#error_boxType1_" + i).show();
				success = false;
			}
			else if(parseInt(cancelQty) != "" && parseInt(cancelQty) < 0){
				$(".cancelQuantity_div div.ui-input-text").css('border-color','red');
				$(".refund-amount").text(" Rs.0");
				$('#cancelAgreementChck').checkboxradio("refresh");
				$("#error_boxType1_" + i).text("Please enter a positive quantity for cancellation.");
				$("#error_boxType1_" + i).show();
				success = false;
			}
			else if(parseInt(cancelQty) != "" && validateDouble(cancelQty) && parseInt(cancelQty) > parseInt(qty)){
				$(".cancelQuantity_div div.ui-input-text").css('border-color','red');
				$(".refund-amount").text(" Rs.0");
				$('#cancelAgreementChck').checkboxradio("refresh");
				$("#error_boxType1_" + i).text("Cancel request quantity is greater than maximum allowed quantity.");
				$("#error_boxType1_" + i).show();
				success = false;
			}
			if(parseInt(count) == 1){
				if(cancelQty != "" && validateDouble(cancelQty) && parseInt(cancelQty) == 0){
					$(".cancelQuantity_div div.ui-input-text").css('border-color','red');
					$(".refund-amount").text(" Rs.0");
					$('#cancelAgreementChck').checkboxradio("refresh");
					$("#error_boxType1_" + i).text("Please enter a positive quantity for cancellation.");
					$("#error_boxType1_" + i).show();
					success = false;
				}
			}
			if(reason== "" || reason == null){
				$(".cancelReason_div div.ui-select").css('border','1px solid red');
				$(".refund-amount").text(" Rs.0");
				$('#cancelAgreementChck').checkboxradio("refresh");
				$("#error_boxType2_" + i).text("Please select a reason for cancellation.");
				$("#error_boxType2_" + i).show();
				success = false;
			}else{
				$(".cancelReason_div div.ui-select").css('border','');
				$("#error_boxType2_" + i).hide();
			}
		}
		if(allBlank){
			success = false;
			$(".cancel_order_error").html("Please enter valid quantity");
			$(".cancelQuantity_div div.ui-input-text").css('border-color','red');
			}
		else
		{
		$(".cancel_order_error").html("");
		/*$(".cancelQuantity_div div.ui-input-text").css('border-color','');*/
		}

		return success;
	}
	
	function validateDouble(value) {
		var doubeRegex = /^[0-9]+$/;
		if (!value.match(doubeRegex)) {
			return false;
		}
		return true;
	}
});

function getCancelRefundAmount(){
	$.ajax({
		type : "POST",
		url : ACCMOB.config.contextPath
				+ "/my-account/order/getRefund",
		data : $("#cancelForm").serialize(),
		dataType : 'text',
		success : function(data) {
			if(data === 'Rs.0'){
				$(".refund-amount").text(" Rs.0");
				$('#cancelAgreementChck').checkboxradio("refresh");
			}else{
				$(".refund-amount").text(" "+data);
				$('#cancelAgreementChck').checkboxradio("refresh");
			}
		},
		error : function(xhr, ajaxOptions, thrownError) {
			console.log("AJAX error in request: " + "status=" + xhr.status
					+ "error=" + thrownError);
		}
	});
}

ACCMOB.cancelorder = {

		bindAll: function()
		{
			$(".refund-amount").text(" Rs.0");
			this.bindCancelOrderLink($('.cancel-order'));
		},
		
		bindCancelOrderLink: function(link)
		{
			link.click(function()
			{
				$.get(link.data('url')).done(function(data) {
					$.colorbox({
						html: data,
						width:900,
						height:false,
						overlayClose: true,
						onOpen: function() {
							$("#colorbox").addClass("cancelOrderColorbox");
							$("#cboxClose").addClass("close-button");
						},
						onComplete: function()
						{
							$(".refund-amount").text(" Rs.0");
							$('#cancelAgreementChck').checkboxradio("refresh");
							 $('#btn-chng').attr('disabled','disabled');
							toggleOrderType();	
							validateonAgreement();
							 $("#btn-chng1").click(function(){
								 parent.$.fn.colorbox.close();
							 });
							MOBACC.common.refreshScreenReaderBuffer();
						},
						onClosed: function()
						{
							MOBACC.common.refreshScreenReaderBuffer();
						}
					});
				});
			});
		}
	};