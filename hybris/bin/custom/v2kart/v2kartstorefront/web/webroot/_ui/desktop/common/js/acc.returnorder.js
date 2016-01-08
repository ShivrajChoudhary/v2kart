ACC.returnorder = {

		bindAll: function()
		{
			this.bindReturnOrderLink($('.return-order'));
		},
		
		bindReturnOrderLink: function(link)
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
							$(".return-amount").text(" Rs.0");
							 $('#btn-chng3').attr('disabled','disabled');
							toggleOrderReturnType();	
							validateonReturnAgreement();
							 $("#btn-chng4").click(function(){
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
	ACC.returnorder.bindAll();

});

function toggleOrderReturnType() {
	$("#fullOrderReturn").click(function(e){
		 e.preventDefault();
		$(".fullOrderDetails").slideToggle();
		$(".disableBg").toggle();
		if ($(this).text() === 'Full Order Return') {
         $(this).text('Partial Order Return');
         $('#isReturnFull').val('true');
		}
		else {
			$(this).text('Full Order Return');
			$('#isReturnFull').val('false');
		}
		
	});
}

function validateonReturnAgreement(){
	$('#agreementReturnChk').change(function(){
		{
			if ($('#agreementReturnChk').is(':checked') && $("#fullOrderReturn").text() === 'Full Order Return') {
// validateQuantityReturn();
				if(validateQuantityReturn()=== true){
					getRefundamount();
					
			    	$('#btn-chng3').removeClass("btn-disabled");
			        $('#btn-chng3').addClass("btn-red");
			        $('#fullOrderReturn').removeClass("btn-red");
			        $('#fullOrderReturn').addClass("btn-disabled");
			        $("#fullOrderReturn").attr('disabled','disabled');
			        $('#btn-chng3').removeAttr('disabled');
					$(".disableBg").show();
				}else{
					$(this).removeAttr('checked');
				}
			}else if($('#agreementReturnChk').is(':checked') && $("#fullOrderReturn").text() === 'Partial Order Return'){
				validateReturnReason();
				if(validateReturnReason()=== true){
				getRefundamount();
		    	$('#btn-chng3').removeClass("btn-disabled");
		        $('#btn-chng3').addClass("btn-red");
		        $('#fullOrderReturn').removeClass("btn-red");
		        $('#fullOrderReturn').addClass("btn-disabled");
		        $("#fullOrderReturn").attr('disabled','disabled');
		        // $(".returnReason").attr('disabled','disabled');
		        // $(".returnNote").attr('disabled','disabled');
		        $('#btn-chng3').removeAttr('disabled');
				$(".disableBg").show();
				}else{
					$(this).removeAttr('checked');
				}
			}else {
				$('#btn-chng3').addClass("btn-red");
				 $('#btn-chng3').addClass("btn-disabled");
				 $('#fullOrderReturn').addClass("btn-red");
			        $('#fullOrderReturn').removeClass("btn-disabled");
			        $("#fullOrderReturn").removeAttr('disabled');
			       // $(".returnReason").removeAttr('disabled');
			        // $(".returnNote").removeAttr('disabled');
			       if($("#fullOrderReturn").text() === 'Full Order Return' && $('.disableBg').is(':visible') ){
			        	$(".disableBg").hide();
			        }else if( $('.disableBg').is(':visible') ) {
			        	$(".disableBg").show();
			        }
		    }
			
		}
	});
}

function getRefundamount(){
	$.ajax({
		type : "POST",
		url : ACC.config.contextPath
				+ "/my-account/order/return/getRefundAmount",
		data : $("#returnForm").serialize(),
		dataType : 'text',
		success : function(data) {
			if(data === 'Rs.0'){
				$(".return-amount").text(" Rs.0");
			}else{
				$(".return-amount").text(" "+data);
			}
		},
		error : function(xhr, ajaxOptions, thrownError) {
			console.log("AJAX error in request: " + "status=" + xhr.status
					+ "error=" + thrownError);
		}
	});
}

function validateQuantityReturn(){
	var count=$('#countofreturnableEntries').val();
	var success=true;
	var allBlank = true;
	for(i=0;i<parseInt(count);i++){
		$("#error_boxType_" + i).hide();
		$("#error_boxType3_" + i).hide();
		var qty=(document.getElementsByName("returnableEntries["+i+"].maxQuantity")[0].value);
		var cancelQty=(document.getElementsByName("returnableEntries["+i+"].quantity")[0].value);
		var reason=(document.getElementsByName("returnableEntries["+i+"].reason")[0].value);
		var zero=0;
		if(cancelQty == "" && reason== "" ){
			continue;
		}
		if(cancelQty == ""){
			$(document.getElementsByName("returnableEntries["+i+"].quantity")[0]).css('border-color','red');
			$(".return-amount").text(" Rs.0");
			$("#error_boxType_" + i).text("Please enter value.");
			$("#error_boxType_" + i).show();
			success=false;
		}
		if(cancelQty == "0"){
			$(document.getElementsByName("returnableEntries["+i+"].quantity")[0]).css('border-color','red');
			$(".return-amount").text(" Rs.0");
			$("#error_boxType_" + i).text("Please enter grater then zero.");
			$("#error_boxType_" + i).show();
			success=false;
		}
		allBlank = false;
		if(cancelQty != "" && !validateDouble(cancelQty)){
			$(document.getElementsByName("returnableEntries["+i+"].quantity")[0]).css('border-color','red');
			$(".return-amount").text(" Rs.0");
			$("#error_boxType_" + i).text("Please enter a numeric value.");
			$("#error_boxType_" + i).show();
			success = false;
		}else{
			$(document.getElementsByName("returnableEntries["+i+"].quantity")[0]).css('border-color','');
		}
		if(parseInt(cancelQty) != "" && validateDouble(cancelQty) && parseInt(cancelQty) > parseInt(qty)){
			$(document.getElementsByName("returnableEntries["+i+"].quantity")[0]).css('border-color','red');
			$(".return-amount").text(" Rs.0");
			$("#error_boxType_" + i).text("Return request quantity is greater than maximum ordered quantity.");
			$("#error_boxType_" + i).show();
			success = false;
		}
		if(parseInt(count) == 1){
			if(parseInt(cancelQty) != "" && validateDouble(cancelQty) && parseInt(cancelQty) == 0){
				$(document.getElementsByName("returnableEntries["+i+"].quantity")[0]).css('border-color','red');
				$(".return-amount").text(" Rs.0");
				$("#error_boxType_" + i).text("Please enter a positive quantity for return.");
				$("#error_boxType_" + i).show();
				success = false;
			}
		}
		if(reason== "" || reason == null){
			$(document.getElementsByName("returnableEntries["+i+"].reason")[0]).css('border-color','red');
			$(".refund-amount").text(" Rs.0");
			$("#error_boxType3_" + i).text("Please select a reason for return.");
			$("#error_boxType3_" + i).show();
			success = false;
		}else{
			$(document.getElementsByName("returnableEntries["+i+"].reason")[0]).css('border-color','');
			$("#error_boxType3_" + i).hide();
		}
	}
	if(allBlank){
		success = false;
		$(".return_order_error").html("Please fill atleast one entry");
		
	}
	else
		{
		$(".return_order_error").html("");		
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

function validateReturnReason(){
	var comp=$('.returnReason').val();
	var success=true;
	if(comp == "" || comp == null){
		$(document.getElementsByName("reason")[0]).css('border-color','red');
		$("#error_boxType2").text("Please select a reason for return.");
		$(".return-amount").text(" Rs.0");
		$("#error_boxType2").show();
		success = false;
	}else{
		$(document.getElementsByName("reason")[0]).css('border-color','');
		$("#error_boxType2").hide();
		success = true;
	}
	return success;
}