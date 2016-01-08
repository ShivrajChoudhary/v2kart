ACC.product = {

	initQuickviewLightbox : function() {
		this.enableAddToCartButton();
		this.bindToAddToCartForm();
	},

	bindProductQuickViewLink : function() {
		$('.quickbtn').bind('click', function() {
			$('.quickbtn').colorbox({
				width : 'auto',
				height : 'auto',
				scrolling : false,
				left: 313.5,
				onLoad: function(){ disablePageScrolling();},
			    onCleanup: function(){ enablePageScrolling();}
			});

		});
	},

	/*enableAddToCartButton : function() {
		$('#addToCartButton').removeAttr("disabled");
	},*/

	enableProductReferencesAddToCartButton : function() {
		$('.addToCartButton.addToReferenceCartButton').removeAttr("disabled");
	},

	enableAddToMultipleCartButton : function() {
		$('.addToMultipleCartButton').removeAttr("disabled");
	},

	enableQuickViewAddToCartButton : function() {
		if ($("#quickviewLightbox .bottom-info #Size").length == 0) {
			$('#addToCartButton').attr('disabled', "disabled");
		} else {
			// if($("#quickviewLightbox .bottom-info #Size").val() !=
			// $("#quickviewLightbox .bottom-info #Style").val()){
			$('#addToCartButton').removeAttr('disabled');
			// }
		}
	},

	bindToAddToCartForm : function() {
		var addToCartForm = $('.add_to_cart_form');
		addToCartForm.ajaxForm({
			success : ACC.product.addToMiniCartPDP
		});
	},
	bindToAddToCartWishlistForm : function() {
		var addToCartForm = $('.addToCartWishlistForm');
		addToCartForm.ajaxForm({
			success : ACC.product.displayAddToCartWishlistPopup
		});
	},
	bindToOpenCartForm : function() {
		var addToCartForm = $('.add_to_cart_form');
		addToCartForm.ajaxForm({
			success : ACC.product.displayAddToCartPopup
		});
	},
	bindToBuyNowForm : function() {
		var addToCartForm = $('#buyNowForm');
		addToCartForm.ajaxForm({
			success : ACC.product.displayBuyNowPopup
		});
	},
	bindToProductReferencesAddToCartForm : function() {
		var addToCartForm = $('.add_to_product_reference_cart_form');
		addToCartForm.ajaxForm({
			success : ACC.product.addToMiniCartCrossSell
		});
	},

	bindToMultipleAddToCartForm : function() {
		var addToCartForm = $('.add_to_multiple_cart_form');
		addToCartForm.ajaxForm({
			success : ACC.product.displayAddToCartPopup
		});
	},

	bindToQuickViewAddToCartForm : function() {
		var addToCartForm = $('.add_to_cart_form');
		addToCartForm.ajaxForm({
			success : ACC.product.displayAddToCartPopup
		});
	},

	bindToAddToCartStorePickUpForm : function() {
		var addToCartStorePickUpForm = $('#pickup_store_results .add_to_cart_storepickup_form');
		addToCartStorePickUpForm.ajaxForm({
			success : ACC.product.displayAddToCartPopup
		});
	},

	addToMiniCartPDP : function(cartResult, statusText, xhr, formElement) {
		if (typeof ACC.minicart.refreshMiniCartCount == 'function') {
			ACC.minicart.refreshMiniCartCount();
		}

		$("#header").append(cartResult.addToCartLayer);
		var errorMessage = $(".cart_popup_error_msg").text();
		if (errorMessage) {
			$('#addToCartErrorMessage').hide();
			$('#addToCartErrorMessage').empty();
			$('#addToCartErrorMessage').text(errorMessage);
			$('#addToCartErrorMessage').show();
			$(".cart_popup_error_msg").empty();
		} else {
			$('#addToCartErrorMessage').hide();
			addToCartFunction();
		}
		var productCode = $('[name=productCodePost]', formElement).val();
		var quantityField = $('[name=qty]', formElement).val();

		var quantity = 1;
		if (quantityField != undefined) {
			quantity = quantityField;
		}

		ACC.track.trackAddToCart(productCode, quantity, cartResult.cartData);

	},
	
	addToMiniCartCrossSell : function(cartResult, statusText, xhr, formElement) {
		if (typeof ACC.minicart.refreshMiniCartCount == 'function') {
			ACC.minicart.refreshMiniCartCount();
		}

		
		var productCode = $('[name=productCodePost]', formElement).val();
		var quantityField = $('[name=qty]', formElement).val();

		var quantity = 1;
		if (quantityField != undefined) {
			quantity = quantityField;
		}

		ACC.track.trackAddToCart(productCode, quantity, cartResult.cartData);
		window.location = $("#crossSellLink").attr("href");
		parent.jQuery.fn.colorbox.close();

	},

//	enableStorePickupButton : function() {
//		$('.pickupInStoreButton').removeAttr("disabled");
//	},

	displayAddToCartPopup : function(cartResult, statusText, xhr, formElement) {
		$('#addToCartLayer').remove();

		if (typeof ACC.minicart.refreshMiniCartCount == 'function') {
			ACC.minicart.refreshMiniCartCount();
		}
		if(cartResult.cartContainsProduct){
			$('#confirmAddPopup').removeClass("cboxElement")
			$('#confirmAddPopup').modal('toggle');
			return;
		}
		$("#header").append(cartResult.addToCartLayer);
		$(".HeaderContainer").append(cartResult.addToCartLayer);
		var errorMessage = $(".cart_popup_error_msg").text();
		if (errorMessage) {
			$('#addToCartErrorMessage').hide();
			$('#addToCartErrorMessage').empty();
			$('#addToCartErrorMessage').text(errorMessage);
			$('#addToCartErrorMessage').show();
			$(".cart_popup_error_msg").empty();
		} else {
			$('#addToCartErrorMessage').hide();
			$("html, body").animate({
				scrollTop : 0
			}, "slow");
			$('#addToCartLayer').fadeIn(function() {
				$.colorbox.close();
				if (typeof timeoutId != 'undefined') {
					clearTimeout(timeoutId);
				}
				timeoutId = setTimeout(function() {
					$('#addToCartLayer').fadeOut(function() {
						$('#addToCartLayer').remove();

					});
				}, 5000);

			});

		}

		var productCode = $('[name=productCodePost]', formElement).val();
		var quantityField = $('[name=qty]', formElement).val();

		var quantity = 1;
		if (quantityField != undefined) {
			quantity = quantityField;
		}

		ACC.track.trackAddToCart(productCode, quantity, cartResult.cartData);

	},
	
	displayBuyNowPopup : function(cartResult, statusText, xhr, formElement) {
		
		if(cartResult.cartContainsProduct){
			$('#confirmBuyNowPopup').removeClass("cboxElement")
			$('#confirmBuyNowPopup').modal('toggle');
			return;
		}
		openCartPage1();

	},
	
	displayAddToCartWishlistPopup : function(cartResult, statusText, xhr, formElement) {
		var productCode = $('[name=productCodePost]', formElement).val();
		if(cartResult.cartContainsProduct){
			$('#confirmAddPopup_' + productCode).removeClass("cboxElement")
			$('#confirmAddPopup_' + productCode).modal('toggle');
			return;
		}
		
		var form = $("#addCartWishlistForm_" + productCode);
		form.submit();
		return;
	},

	bindToSizeDropDown : function() {
		var sizeSelector = $("#quickview .bottom-info #Size");
		sizeSelector.on("change", function(e) {
			var variantUrl = $(this).val() + "/quickView";
			$.ajax({
				url : variantUrl,
				success : function(result) {
					$("#cboxLoadedContent").empty();
					$("#cboxLoadedContent").html(result);
				}
			});
		});
	},

	bindToStyleDropDown : function() {
		var swatchSelector = $("#quickview .bottom-info #Style");
		swatchSelector.on("change", function(e) {
			var variantUrl = $(this).val() + "/quickView";
			$.ajax({
				url : variantUrl,
				success : function(result) {
					$("#cboxLoadedContent").empty();
					$("#cboxLoadedContent").html(result);
				}
			});
		});
	}

};

$(document).ready(function() {
	with (ACC.product) {
		bindProductQuickViewLink();
		bindToMultipleAddToCartForm();
		bindToAddToCartForm();
		bindToAddToCartStorePickUpForm();
		bindToOpenCartForm();
		bindToBuyNowForm();
		bindToAddToCartWishlistForm();
		//enableStorePickupButton();
//		enableAddToCartButton();
		enableAddToMultipleCartButton();
		
	}
});
function addToCartFormSubmit() {
	var form = $("#addToCartPopupForm");
	form.submit();
}
function buyNowFormSubmit() {
	var form = $("#buyNowForm");
	$('#buyNowForm input[name=qty]').val('0');
	form.submit();
}
function openCartPage1() {
	var form = $("#showcartform");
	form.submit();
}