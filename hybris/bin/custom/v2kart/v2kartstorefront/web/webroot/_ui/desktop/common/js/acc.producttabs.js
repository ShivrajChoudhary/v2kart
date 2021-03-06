ACC.productTabs = {

	bindAll : function() {
		if ($('#productTabs').length > 0) {

			// only load review one at init
			ACC.productTabs.showReviewsAction("reviews");

			ACC.productTabs.productTabs = $('#productTabs').accessibleTabs({
				wrapperClass : 'content',
				currentClass : 'current',
				tabhead : '.tabHead',
				tabbody : '.tabBody',
				fx : 'show',
				fxspeed : null,
				currentInfoText : 'current tab: ',
				currentInfoPosition : 'prepend',
				currentInfoClass : 'current-info',
				autoAnchor : true
			});

			$(document).on("click",
					'#write_review_action_main, #write_review_action',
					function(e) {
						e.preventDefault();
						ACC.productTabs.scrollToReviewTab('#write_reviews')
						$('#reviewForm input[name=headline]').focus();
					});

			$('#based_on_reviews, #read_reviews_action').bind("click",
					function(e) {
						e.preventDefault();
						ACC.productTabs.scrollToReviewTab('#reviews')
					});

			$(document)
					.on(
							"click",
							'#show_all_reviews_top_action, #show_all_reviews_bottom_action',
							function(e) {
								e.preventDefault();
								ACC.productTabs.showReviewsAction("allreviews");
								$(this).hide();
							});

		}

	},

	scrollToReviewTab : function(pane) {
	
		$('html,body').animate({
			scrollTop: $("#productTabs").offset().top - 160
			}, 800);

		ACC.productTabs.productTabs.showAccessibleTabSelector('#tab-reviews');
		$('#write_reviews,#reviews').hide();
		$(pane).show();

		$('#accessibletabsnavigation0-0').removeClass('current');
		$('#accessibletabsnavigation0-01').removeClass('current');
		$('.productTabDescription').css("display", "none");
		$('#accessibletabscontent0-01').next('.tabBody').hide();
		$('#tab-reviews').addClass('current');
		$('#review-tab').css("display", "block");
	},

	showReviewsAction : function(s) {
		$.get($("#reviews").data(s), function(result) {
			$('#reviews').html(result);
		});
	}
};

$(document).ready(function() {
	ACC.productTabs.bindAll();
	
	if ($("#reviewError").text()) {
		$('#accessibletabsnavigation0-0').removeClass('current');
		$('#accessibletabsnavigation0-01').removeClass('current');
		$('.productTabDescription').css("display", "none");
		$('#accessibletabscontent0-01').next('.tabBody').hide();
		$('#tab-reviews').addClass('current');
		$('#review-tab').css("display", "block");
		$('#reviews').css("display", "none");
		$('#write_reviews').css("display", "block");
	}
});
