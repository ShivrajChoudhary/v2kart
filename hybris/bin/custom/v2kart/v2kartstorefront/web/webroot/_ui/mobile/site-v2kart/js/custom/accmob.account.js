ACCMOB.account = {
	

	createDialog: function (content,headerText)
	{
		$.mobile.easydialog({content:content, header: headerText });
	},

	bindToRemoveAddressButton: function ()
	{
		$('.removeAddressForm').live("submit", function (event)
		{
			if ($(this).data("removeConfirmed") == "true")
			{
				return true;
			}
			else
			{
				ACCMOB.common.preventDefault(event);
			}
		});

		$('.removeAddressButton').live("click", function (event)
		{
			var pid = $(this).attr("pid");
			var removePaymentCardForm = $("#removeAddressForm" + pid);
			ACCMOB.common.preventDefault(event);
			
			ACCMOB.account.createDialog('<div class="confirmRemoveAddress">'+$(this).data('message')+'<a href="#" data-role="button" data-theme="b" id="okRemoveAddress">OK</a><a href="#" data-role="button" data-theme="g" id="cancelRemoveAddress">Cancel</a></div>', $(this).data('headertext'));
			
		});
		
		
		$(document).on("click","#okRemoveAddress", function (e)
		{
			e.preventDefault();
			var pid = $('.removeAddressButton').attr("pid");
			var removePaymentCardForm = $("#removeAddressForm" + pid);
			removePaymentCardForm.data("removeConfirmed", "true");
			removePaymentCardForm.submit();
			return true;
		
		});
		
		$(document).on("click","#cancelRemoveAddress", function (e)
		{
			e.preventDefault();
			
			$.mobile.easyDialog.close();
		
		});
		
		
	},

	bindToRemovePaymentCardButton: function ()
	{
		/* prevent payment remove form from submitting normaly */
		$('.removePaymentCardForm').live("submit", function (event)
		{
			if ($(this).data("removeConfirmed") == "true")
			{
				return true;
			}
			else
			{
				ACCMOB.common.preventDefault(event);
			}
		});

		$('.removePaymentCardButton').live("click", function (event)
		{
			var pid = $(this).attr("pid");
			var removePaymentCardForm = $("#removePaymentCardForm" + pid);
			ACCMOB.common.preventDefault(event);
			ACCMOB.account.createDialog($(this).data('message')+'<a href="#" data-role="button"  data-icon="check" data-theme="b" id="okRemovePaymentCard">OK</a><a href="#" data-role="button" data-icon="delete" data-theme="c" id="cancelRemovePaymentCard">Cancel</a>', $(this).data('headertext'));
			
		});
		
		
		$(document).on("click","#okRemovePaymentCard", function (e)
		{
			e.preventDefault();
			var pid = $('.removePaymentCardButton').attr("pid");
			var removePaymentCardForm = $("#removePaymentCardForm" + pid);
			removePaymentCardForm.data("removeConfirmed", "true");
			removePaymentCardForm.submit();
			return true;
		
		});
		
		$(document).on("click","#cancelRemovePaymentCard", function (e)
		{
			e.preventDefault();
			
			$.mobile.easyDialog.close();
		
		});
		
		
		
	},

	bindToSetDefaultPaymentButton: function ()
	{
		$('.setDefaultPayment').live("click", function ()
		{
			$('#setDefaultPaymentDetails' + $(this).attr("pid")).submit();
			return false;
		});
	},

	bindToRemovePaymentDetailsButton: function ()
	{
		$('.removePaymentDetail').live("click", function ()
		{
			$('#removePaymentDetails' + $(this).attr("pid")).submit();
			return false;
		});
	},
	phoneNumberError: function()
	{
		if($(".phoneNo").length){
			if($(".phoneNo .form_field_error").length)
			{
				$(".phoneNo .phoneNoLabel").addClass("form_field_error");
			}
			else{
				$(".phoneNo .phoneNoLabel").removeClass("form_field_error");
			}
			}	
	},

	initialize: function ()
	{
		with (ACCMOB.account)
		{
			bindToRemoveAddressButton();
			bindToSetDefaultPaymentButton();
			bindToRemovePaymentCardButton();
			bindToRemovePaymentDetailsButton();
		}
	}

};

$(document).ready(function ()
{
	ACCMOB.account.initialize();
	ACCMOB.account.phoneNumberError();
});
