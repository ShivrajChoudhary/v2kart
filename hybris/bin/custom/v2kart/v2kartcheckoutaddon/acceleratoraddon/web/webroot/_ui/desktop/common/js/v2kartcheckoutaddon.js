$('.address-fields :radio').change(function(event) {
	var id = $(this).data('id');
	$('#' + id).addClass('none').siblings().removeClass('none');
});
$(document).ready(function() {
	var id = $('.address-fields :radio:checked').data('id');
	$('#' + id).addClass('none').siblings().removeClass('none');
});
$(document).ready(function(){
	var isUsingWallet	= $('#v2PaymentInfoForm #isUsingWallet');
	if(isUsingWallet !==null && isUsingWallet !== undefined){
		$(isUsingWallet).change(function(){
			$('#v2PaymentInfoForm').attr('action',$('#isUsingWalletCheckBoxForm').attr('action')+"/billingAddress");
			$('#v2PaymentInfoForm').submit();
			//$('#isUsingWalletCheckBoxForm').submit();
		});
	}
});