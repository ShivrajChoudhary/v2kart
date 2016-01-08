ACCMOB.cartremoveitem = {

	bindAll: function ()
	{
		this.hideQuantity()
		this.bindCartRemoveProduct();
		this.addToWishListFromCart();
	},
	hideQuantity: function()
	{
		$(".hideQuantity").wrap( "<span>" ).parent().hide();	
	},
	bindCartRemoveProduct: function ()
	{
		$('.submitRemoveProduct').click(function ()
		{
			var form = $('#updateCartForm' + $(this).attr('id'));
			var productCode = form.get(0).productCode.value;
			var initialCartQuantity = $('#quantity' + $(this).attr('id')).val();
			var cartData = form.data("cart");
			ACCMOB.cartremoveitem.trackRemoveFromCart(productCode, initialCartQuantity,cartData);
			$(".hideQuantity").unwrap( "<span>" );
			$('#quantity' + $(this).attr('id')).attr('value', '0');
			$('#updateCartForm' + $(this).attr('id')).submit();
		});
	},
	addToWishListFromCart : function() {
		
		$(".addToWishListLinkfromCart").live("click", function (e)
				{
				var option = $(this).data("options");
					if (option.addToWishListProduct != "") {
						//var content= "<div class='wishpopup'>"+$("#" + e.currentTarget.id).data("content")+"</div>";
						$.ajax({
							dataType: 'json',
							url: option.addToWishListUrl,
							data :{productCode : option.addToWishListProduct,entryNumber : option.entryNumber},
							cache: false
						})
						ACCMOB.cartremoveitem.trackRemoveFromCart(option.addToWishListProduct, 0);
						window.location.href = $('#cartWishlist').val();
						$('ul').listview('refresh');
						// window.location.href = '/cart';
					} else {

						window.location.href = option.addToWishListUrl;
					}

				})
	},
	trackRemoveFromCart: function(productCode, initialCartQuantity, data)
	{
		window.mediator.publish('trackRemoveFromCart',{
			productCode: productCode,
			initialCartQuantity: initialCartQuantity,
			cartData:data
		});
	}
};

$(document).ready(function ()
{
	ACCMOB.cartremoveitem.bindAll();
});
