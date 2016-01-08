ACC.productDetail = {

	initPageEvents : function() {

		$('.productImageGallery .jcarousel-skin').jcarousel({
			vertical : true
		});

		$(document).on(
				"click",
				"#imageLink, .productImageZoomLink",
				function(e) {
					e.preventDefault();

					$.colorbox({
						href : $(this).attr("href"),
						height :'97%',
						width:'100%',
						scrolling:true,
						scalePhotos:true,
						onComplete : function() {
							ACC.common.refreshScreenReaderBuffer();

							$('#colorbox .productImageGallery .jcarousel-skin')
									.jcarousel({
										vertical : true
									});

						},
						onClosed : function() {
							ACC.common.refreshScreenReaderBuffer();
						}
					});
				});
		
		$(".productImageGallery img").click(
				function(e) {
//					var image=$(this).attr("data-primaryimagesrc");
					$(".productImagePrimary img").attr("src",
							$(this).attr("data-primaryimagesrc"));
					$(".productImagePrimary img").attr("data-cloudzoom",
							"zoomImage:'"+$(this).attr("data-zoomImgae")+"',captionSource:'', zoomWidth:450, zoomHeight:520");
					$("#zoomLink, #imageLink").attr(
							"href",
							$("#zoomLink").attr("data-href")
									+ "?galleryPosition="
									+ $(this).attr("data-galleryposition"));
					$(".productImageGallery .thumb").removeClass("active");
					$(this).parent(".thumb").addClass("active");
					options={}; 
					myInstance = new CloudZoom($('.cloudzoom'),options); 
//					myInstance.loadImage($(this).attr("data-primaryimagesrc"),$(this).attr("data-primaryimagesrc"));
					myInstance.destroy();
					CloudZoom.quickStart();
				});

		$(document).on(
				"click",
				"#colorbox .productImageGallery img",
				function(e) {
					$("#colorbox  .productImagePrimary img").attr("src",
							$(this).attr("data-zoomurl"));
					$("#colorbox .productImageGallery .thumb").removeClass(
							"active");
					$(this).parent(".thumb").addClass("active");
				});

		$( "select[name=qtyInput]" ).change(function() {
			var input = $(this);
			var value = input.val();
			var qty_css = 'input[name=qty]';
			while (input.parent()[0] != document) {
				input = input.parent();
				if (input.find(qty_css).length > 0) {
					input.find(qty_css).val(value);
					return;
				}
			}
		});

		$("#Size").change(function() {
			var url = "";
			var selectedIndex = 0;
			var parentClass="";
			$("#Size option:selected").each(function() {
				url = $(this).attr('value');
				selectedIndex = $(this).attr("index");
				parentClass = $(this).parent().parent().parent().parent().parent().parent().parent().parent().parent().parent().attr('class');               
			});
			
			if(parentClass=='quickview'){
				url = url + "/quickView";
				$.ajax({
					url : url,
					success : function(result) {
						$("#cboxLoadedContent").empty();
						$("#cboxLoadedContent").html(result);
					}
				});
				
			} else {
				if (selectedIndex != 0) {
					window.location.href = url;
				}
				
			}
			
		});

		$("#variant").change(function() {
			var url = "";
			var selectedIndex = 0;

			$("#variant option:selected").each(function() {
				url = $(this).attr('value');
				selectedIndex = $(this).attr("index");
			});
			if (selectedIndex != 0) {
				window.location.href = url;
			}
		});

	},
	addToWishList : function() {
		$(".pdplinks .addToWishListLink").click(function(e) {
			var option = $(".pdplinks .addToWishListLink").data("options");
			if (option.addToWishListProduct != "") {

				$.getJSON(option.addToWishListUrl, {
					productCode : option.addToWishListProduct
				}, function(data) {
					$('.wishlist').popover('show');
					setTimeout(function() {
						     $('.wishlist').popover('hide');
						}, 3000);
				});
			} else {

				window.location.href = option.addToWishListUrl;
			}
		});

	},
	
	addToCartBuyNow : function() {
		$(".addToCartButton").click(function(e) {
			var button = $(this).children('button');
			if(button.is(":disabled")) {
			$('.addToCartButton').not(this).children('button').popover('hide');
				button.popover('show');
				setTimeout(function() {
					     button.popover('hide');
					}, 3000);
				
			}
			e.stopPropagation();
		});
		$("html").click(function() {
			var button = $(".addToCartButton button");
			if(button.is(":disabled")) {
			$('.addToCartButton button').popover('hide');
			}
		});
	},
	

	addToWishListFromCart : function() {
		$(".addToWishListLinkfromCart").click(
				function(e) {

					var option = $("#" + e.currentTarget.id).data("options");
					if (option.addToWishListProduct != "") {

						$.getJSON(option.addToWishListUrl, {
							productCode : option.addToWishListProduct,
							entryNumber : option.entryNumber
						}, function(data) {
							$(".addToWishListLinkfromCart").next('.popover')
									.fadeToggle();

							window.location.href = $('#cartWishlist').val();
							$(".addToWishListLinkfromCart").unbind('click');

						});
						ACC.track.trackRemoveFromCart(productCode, 0);

						// window.location.href = '/cart';
					} else {

						window.location.href = option.addToWishListUrl;
					}

				});

	}

};

$(document).ready(function() {
	with (ACC.productDetail) {
		initPageEvents();
		addToWishList();
		addToWishListFromCart();
		addToCartBuyNow();
	}
	$('.productImageGallery .jcarousel-skin').jcarousel({vertical : true});
});

$(document).bind('cbox_open', function() {
	    $('body').css({ overflow: 'hidden' });
	}).bind('cbox_closed', function() {
	    $('body').css({ overflow: '' });
	});