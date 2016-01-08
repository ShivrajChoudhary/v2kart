$(document).ready(function() {
	ACCMOB.returnorder.bindAll();
	
	$('#return-btn-chng').button('disable');
	$('#returnAgreementChck').change(function() {
		if ($('#returnAgreementChck').is(':checked') && $(".accountReturnOrderPage .ui-bar-a h1").text() === $('#fullOrderReturnText').val()) {
			if(validateReturnReason() === true) {
				getReturnRefundAmount();
				$('#return-btn-chng').button('enable');
			} else {
				$(this).removeAttr('checked');
			}
		}
		else if ($('#returnAgreementChck').is(':checked') && $(".accountReturnOrderPage .ui-bar-a h1").text() === $('#partialOrderReturnText').val()) {
			if(validateQuantity() === true) {
				getReturnRefundAmount();
				$('#return-btn-chng').button('enable');
			} else {
				$(this).removeAttr('checked');
			}
		} else {
			$('#return-btn-chng').button('disable');
		}
	});
	$('#fullOrderReturnDiv').hide();
	$('#partialOrderReturn').parent().hide();
	$('#fullOrderReturn').change(function() {
		$('#returnAgreementChck').removeAttr('checked');
		$('#returnAgreementChck').checkboxradio("refresh");
		if ($('#fullOrderReturn').is(':checked')) {
			$('#isFullReturn').val("true");
			$('#fullOrderReturnDiv').show();
			$('#partialOrderReturnDiv  :input').attr('disabled', 'disabled');
			$('#partialOrderReturnDiv').css('opacity', '0.3');
			$('#partialOrderReturn').prop( "checked", false );
			$('#partialOrderReturn').checkboxradio("refresh");
			$('#partialOrderReturn').parent().show();
			$('#fullOrderReturn').parent().hide();
			$('.accountReturnOrderPage .ui-bar-a h1').html($('#fullOrderReturnText').val());
		}
	});
	$('#partialOrderReturn').change(function() {
		$('#returnAgreementChck').removeAttr('checked');
		$('#returnAgreementChck').checkboxradio("refresh");
		if ($('#partialOrderReturn').is(':checked')) {
			$('#isFullReturn').val("false");
			$('#fullOrderReturnDiv').hide();
			$('#partialOrderReturnDiv  :input').removeAttr('disabled');
			$('#partialOrderReturnDiv').css('opacity', '1');
			$('#partialOrderReturn').parent().hide();
			$('#fullOrderReturn').prop( "checked", false );
			$('#fullOrderReturn').checkboxradio("refresh");
			$('#fullOrderReturn').parent().show();
			$('.accountReturnOrderPage .ui-bar-a h1').html($('#partialOrderReturnText').val());
		}
	});
	
	function validateReturnReason(){
		var comp=$('#order\\.returned\\.reason').val();
		var success=true;
		if(comp == "" || comp == null){
			$(".returnReason_div div.ui-select").css('border','1px solid red');
			$('select').selectmenu('refresh');
			$("#error_boxType").text("Please select a reason for Return.");
			$(".refund-amount").text(" Rs.0");
			$('#returnAgreementChck').checkboxradio("refresh");
			$("#error_boxType").show();
			success = false;
		}else {
			$(".returnReason_div div.ui-select").css('border','');
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
			var qty=(document.getElementsByName("returnableEntries["+i+"].maxQuantity")[0].value);
			var returnQty=(document.getElementsByName("returnableEntries["+i+"].quantity")[0].value);
			var reason=(document.getElementsByName("returnableEntries["+i+"].reason")[0].value);
			
			$(".returnQuantity_div div.ui-input-text").css('border-color','');
			
			if(returnQty == "" && reason== "" ){
				continue;
			}
			allBlank = false;
			if(returnQty == "" || !validateDouble(returnQty)){
				$(".returnQuantity_div div.ui-input-text").css('border-color','red');
				$(".refund-amount").text(" Rs.0");
				$('#returnAgreementChck').checkboxradio("refresh");
				$("#error_boxType1_" + i).text("Please enter valid quantity");
				$("#error_boxType1_" + i).show();
				success = false;
			}
			else if(parseInt(returnQty) != "" && parseInt(returnQty) < 0){
				$(".returnQuantity_div div.ui-input-text").css('border-color','red');
				$(".refund-amount").text(" Rs.0");
				$('#returnAgreementChck').checkboxradio("refresh");
				$("#error_boxType1_" + i).text("Please enter a positive quantity for return.");
				$("#error_boxType1_" + i).show();
				success = false;
			}
			else if(parseInt(returnQty) != "" && validateDouble(returnQty) && parseInt(returnQty) > parseInt(qty)){
				$(".returnQuantity_div div.ui-input-text").css('border-color','red');
				$(".refund-amount").text(" Rs.0");
				$('#returnAgreementChck').checkboxradio("refresh");
				$("#error_boxType1_" + i).text("Return request quantity is greater than maximum allowed quantity.");
				$("#error_boxType1_" + i).show();
				success = false;
			}
			if(parseInt(count) == 1){
				if(returnQty != "" && validateDouble(returnQty) && parseInt(returnQty) == 0){
					$(".returnQuantity_div div.ui-input-text").css('border-color','red');
					$(".refund-amount").text(" Rs.0");
					$('#returnAgreementChck').checkboxradio("refresh");
					$("#error_boxType1_" + i).text("Please enter a positive quantity for return.");
					$("#error_boxType1_" + i).show();
					success = false;
				}
			}
			if(reason== "" || reason == null){
				$(".returnReason_div div.ui-select").css('border','1px solid red');
				$(".refund-amount").text(" Rs.0");
				$('#returnAgreementChck').checkboxradio("refresh");
				$("#error_boxType2_" + i).text("Please select a reason for return.");
				$("#error_boxType2_" + i).show();
				success = false;
			}else{
				$(".returnReason_div div.ui-select").css('border','');
				$("#error_boxType2_" + i).hide();
			}
		}
		if(allBlank){
			success = false;
			$(".return_order_error").html("Please enter valid quantity");
			$(".returnQuantity_div div.ui-input-text").css('border-color','red');
			}
		else
		{
			$(".return_order_error").html("");
			/*$(".returnQuantity_div div.ui-input-text").css('border-color','');*/
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


function getReturnRefundAmount(){
	$.ajax({
		type : "POST",
		url : ACCMOB.config.contextPath
				+ "/my-account/order/return/getRefundAmount",
		data : $("#returnForm").serialize(),
		dataType : 'text',
		success : function(data) {
			if(data === 'Rs.0'){
				$(".refund-amount").text(" Rs.0");
				$('#returnAgreementChck').checkboxradio("refresh");
			}else{
				$(".refund-amount").text(" "+data);
				$('#returnAgreementChck').checkboxradio("refresh");
			}
		},
		error : function(xhr, ajaxOptions, thrownError) {
			console.log("AJAX error in request: " + "status=" + xhr.status
					+ "error=" + thrownError);
		}
	});
}

ACCMOB.returnorder = {

		bindAll: function()
		{
			$(".refund-amount").text(" Rs.0");
			this.bindreturnOrderLink($('.return-order'));
		},
		
		bindreturnOrderLink: function(link)
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
							$("#colorbox").addClass("returnOrderColorbox");
							$("#cboxClose").addClass("close-button");
						},
						onComplete: function()
						{
							$(".refund-amount").text(" Rs.0");
							$('#returnAgreementChck').checkboxradio("refresh");
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
