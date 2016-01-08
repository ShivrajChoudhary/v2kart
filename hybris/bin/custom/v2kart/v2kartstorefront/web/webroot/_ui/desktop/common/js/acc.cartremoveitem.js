ACC.cartremoveitem = {

	bindAll : function() {
		this.bindCartRemoveProduct();
	},
	bindCartRemoveProduct : function() {

		$('.submitRemoveProduct').on(
				"click",
				function() {
					$('.cartPagePopup').removeClass('cboxElement');
					var prodid = $(this).attr('id').split("_")
					var form = $('#updateCartForm' + prodid[1]);
					var productCode = form.find('input[name=productCode]')
							.val();
					var initialCartQuantity = form
							.find('input[name=initialQuantity]');
					var cartQuantity = form.find('input[name=quantity]');
					var cartData = form.data("cart");
					ACC.track.trackRemoveFromCart(productCode,
							initialCartQuantity.val(), cartData);

					cartQuantity.val(0);
					initialCartQuantity.val(0);
					form.submit();
				});

		$('.updateQuantityProduct')
				.on(
						"click",
						function() {
							var prodid = $(this).attr('id').split("_")
							var form = $('#updateCartForm' + prodid[1]);
							var productCode = form.find(
									'input[name=productCode]').val();
							var initialCartQuantity = form.find(
									'input[name=initialQuantity]').val();
							var newCartQuantity = form.find(
									'input[name=quantity]').val();
							var cartData = form.data("cart");

							if (initialCartQuantity != newCartQuantity) {
								ACC.track.trackUpdateCart(productCode,
										initialCartQuantity, newCartQuantity,
										cartData);
								form.submit();
							}

						});
	}
}
function add(id, entryNo) {

	var value = parseInt(document.getElementById(id).value, 10);
	value = isNaN(value) ? 0 : value;
	if (value >= 12) {
		$("#inc" + entryNo).attr('disabled', 'disabled');
	} else {
		value++;
		document.getElementById(id).value = value;
		$("#inc" + entryNo).removeAttr('disabled');
	}
	var $form = $('#updateCartForm' + entryNo);
	var initialCartQuantity = $form.find('input[name=initialQuantity]').val();
	var newCartQuantity = $form.find('input[name=quantity]').val();
	var productCode = $form.find('input[name=productCode]').val();
	if (initialCartQuantity != newCartQuantity) {
		$form.submit();

	}

}

function subtract(id) {

	var value = parseInt(document.getElementById(id).value, 10);
	value = isNaN(value) ? 1 : value;
	if (value > 1) {
		value--;
	}
	document.getElementById(id).value = value;
	var entryNum = id.replace("quantity", "");
	var $form = $('#updateCartForm' + entryNum);
	var initialCartQuantity = $form.find('input[name=initialQuantity]').val();
	var newCartQuantity = $form.find('input[name=quantity]').val();
	var productCode = $form.find('input[name=productCode]').val();
	if (initialCartQuantity != newCartQuantity) {
		$form.submit();

	}
}

$(document).ready(function() {
	ACC.cartremoveitem.bindAll();
});
