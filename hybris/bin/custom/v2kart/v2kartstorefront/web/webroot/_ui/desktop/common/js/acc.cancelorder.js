ACC.cancelorder = {

		bindAll: function()
		{
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
							$(".refund-amount").text(" Rs.0.00");
							 $('#btn-chng').attr('disabled','disabled');
							toggleOrderType();	
							validateonAgreement();
							 $("#btn-chng1").click(function(){
								 parent.$.fn.colorbox.close();
							 });
							ACC.common.refreshScreenReaderBuffer();
						},
						onClosed: function()
						{
							ACC.common.refreshScreenReaderBuffer();
						}
					});
				});
			});
		}
	};


$(document).ready(function(){
	ACC.cancelorder.bindAll();

});

function toggleOrderType() {
	$("#fullOrderCancel").click(function(e){
		 e.preventDefault();
		$(".fullOrderDetails").slideToggle();
		$(".disableBg").toggle();
		if ($(this).text() === 'Full Order Cancellation') {
         $(this).text('Partial Order Cancellation');
         $('#isFull').val('true');
		}
		else {
			$(this).text('Full Order Cancellation');
			$('#isFull').val('false');
		}
		
	});
}

function validateonAgreement(){
	$('#agreementChck').change(function(){
		{
			if ($('#agreementChck').is(':checked') && $("#fullOrderCancel").text() === 'Full Order Cancellation') {
				validateQuantity();
				if(validateQuantity()=== true){
					getrefundamount();
			    	$('#btn-chng').removeClass("btn-disabled");
			        $('#btn-chng').addClass("btn-red");
			        $('#fullOrderCancel').removeClass("btn-red");
			        $('#fullOrderCancel').addClass("btn-disabled");
			        $("#fullOrderCancel").attr('disabled','disabled');
			        $('#btn-chng').removeAttr('disabled');
					$(".disableBg").show();
				}else{
					$(this).removeAttr('checked');
				}
			}else if($('#agreementChck').is(':checked') && $("#fullOrderCancel").text() === 'Partial Order Cancellation'){
				validateCancelReason();
				if(validateCancelReason()=== true){
				getrefundamount();
		    	$('#btn-chng').removeClass("btn-disabled");
		        $('#btn-chng').addClass("btn-red");
		        $('#fullOrderCancel').removeClass("btn-red");
		        $('#fullOrderCancel').addClass("btn-disabled");
		        $("#fullOrderCancel").attr('disabled','disabled');
		        $('#btn-chng').removeAttr('disabled');
				$(".disableBg").show();
				}else{
					$(this).removeAttr('checked');
				}
			}else {
				$('#btn-chng').addClass("btn-red");
				 $('#btn-chng').addClass("btn-disabled");
				 $('#fullOrderCancel').addClass("btn-red");
				 $('#btn-chng').attr('disabled','disabled');
			        $('#fullOrderCancel').removeClass("btn-disabled");
			        $("#fullOrderCancel").removeAttr('disabled');
			       if($("#fullOrderCancel").text() === 'Full Order Cancellation' && $('.disableBg').is(':visible') ){
			        	$(".disableBg").hide();
			        }else if( $('.disableBg').is(':visible') ) {
			        	$(".disableBg").show();
			        }
		    }
			
		}
	});
}

function getrefundamount(){
	$.ajax({
		type : "POST",
		url : ACC.config.contextPath
				+ "/my-account/order/getRefund",
		data : $("#cancelForm").serialize(),
		dataType : 'text',
		success : function(data) {
			if(data === 'Rs.0'){
				$(".refund-amount").text(" Rs.0.00");
			}else{
				$(".refund-amount").text(" "+data);
			}
		},
		error : function(xhr, ajaxOptions, thrownError) {
			console.log("AJAX error in request: " + "status=" + xhr.status
					+ "error=" + thrownError);
		}
	});
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
		if(cancelQty == "" && reason== "" ){
			continue; 
		}
		allBlank = false;
		if(cancelQty == ""){
			$(document.getElementsByName("cancelledEntries["+i+"].quantity")[0]).css('border-color','red');
			$(".refund-amount").text(" Rs.0.00");
			$("#error_boxType1_" + i).text("Please enter cancel Qty.");
			$("#error_boxType1_" + i).show();
			success = false;
		}
		if(cancelQty != "" && !validateDouble(cancelQty)){
			$(document.getElementsByName("cancelledEntries["+i+"].quantity")[0]).css('border-color','red');
			$(".refund-amount").text(" Rs.0.00");
			$("#error_boxType1_" + i).text("Please enter a numeric value.");
			$("#error_boxType1_" + i).show();
			success = false;
		}else{
			$(document.getElementsByName("cancelledEntries["+i+"].quantity")[0]).css('border-color','');
		}
		if(parseInt(cancelQty) != "" && parseInt(cancelQty) < 0){
			$(document.getElementsByName("cancelledEntries["+i+"].quantity")[0]).css('border-color','red');
			$(".refund-amount").text(" Rs.0.00");
			$("#error_boxType1_" + i).text("Please enter a positive quantity for cancellation.");
			$("#error_boxType1_" + i).show();
			success = false;
		}
		if(parseInt(cancelQty) != "" && validateDouble(cancelQty) && parseInt(cancelQty) > parseInt(qty)){
			$(document.getElementsByName("cancelledEntries["+i+"].quantity")[0]).css('border-color','red');
			$(".refund-amount").text(" Rs.0.00");
			$("#error_boxType1_" + i).text("Cancel request quantity is greater than maximum allowed quantity.");
			$("#error_boxType1_" + i).show();
			success = false;
		}
		if(parseInt(count) == 1){
			if(cancelQty != "" && validateDouble(cancelQty) && parseInt(cancelQty) == 0){
				$(document.getElementsByName("cancelledEntries["+i+"].quantity")[0]).css('border-color','red');
				$(".refund-amount").text(" Rs.0.00");
				$("#error_boxType1_" + i).text("Please enter a positive quantity for cancellation.");
				$("#error_boxType1_" + i).show();
				success = false;
			}
		}
		if(reason== "" || reason == null){
			$(document.getElementsByName("cancelledEntries["+i+"].reason")[0]).css('border-color','red');
			$(".refund-amount").text(" Rs.0.00");
			$("#error_boxType2_" + i).text("Please select a reson for cancellation.");
			$("#error_boxType2_" + i).show();
			success = false;
		}else{
			$(document.getElementsByName("cancelledEntries["+i+"].reason")[0]).css('border-color','');
			$("#error_boxType2_" + i).hide();
		}
	}
	if(allBlank){
		success = false;
		$(".cancel_order_error").html("Please fill atleast one entry");
		}
	else
	{
	$(".cancel_order_error").html("");		
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

function validateCancelReason(){
	var comp=$('.selectReason').val();
	var success=true;
	if(comp == "" || comp == null){
		$(document.getElementsByName("reason")[0]).css('border-color','red');
		$("#error_boxType").text("Please select a reason for cancellation.");
		$(".refund-amount").text(" Rs.0.00");
		$("#error_boxType").show();
		success = false;
	}else{
		$(document.getElementsByName("reason")[0]).css('border-color','');
		$("#error_boxType").hide();
		success = true;
	}
	return success;
}