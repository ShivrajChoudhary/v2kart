ACC.productlisting = {

	infiniteScrollingConfig : {
		offset : '150%'
	},
	currentPage : 0,
	numberOfPages : Number.MAX_VALUE,
	showMoreResultsArea : $('#footer'),
	baseQuery : $("#resultsList").attr("data-current-query") || "",
	searchPath : $("#resultsList").attr("data-current-path"),
	lastDataHtml : "",

	triggerLoadMoreResults : function() {
		if (ACC.productlisting.currentPage < ACC.productlisting.numberOfPages) {
			// show the page loader
			ACC.common.showSpinnerById('spinner');
			ACC.productlisting
					.loadMoreResults(parseInt(ACC.productlisting.currentPage) + 1);
		}
	},

	scrollingHandler : function(event, direction) {
		if (direction === "down") {
			//alert("direction down");
			ACC.productlisting.triggerLoadMoreResults();
		}
	},

	loadMoreResults : function(page) {
		skuIndex = "0";

		if ($("#skuIndexSavedValue").attr("data-sku-index") !== undefined) {
			skuIndex = $("#skuIndexSavedValue").attr("data-sku-index");
		}

		searchResultType = $("[name='searchResultType']:radio:checked").val()
				|| "";

		
		$
				.ajax({
					url : ACC.productlisting.searchPath + "/results?q="
							+ ACC.productlisting.baseQuery + "&page=" + page
							+ "&skuIndex=" + skuIndex + "&searchResultType="
							+ searchResultType,
					success : function(data) {
						if (data.pagination !== undefined) {
							if ($("#resultsList").length > 0
									&& ACC.productlisting.lastDataHtml !== data.results) {
								// Product List Page
								ACC.productlisting.lastDataHtml = data.results;
								$("#resultsList")
										.append(data.results);
								// rebind the add-to-cart ajaxForms
								ACC.product.bindToAddToCartForm({
									enforce : true
								});
								if ($("#skuIndexSavedValue").attr(
										"data-sku-index") !== undefined) {
									$("#skuIndexSavedValue").attr(
											"data-sku-index", data.skuIndex);
								}
							}

							ACC.productlisting
									.updatePaginationInfos(data.pagination);
							ACC.common.hideSpinnerById('spinner');
							ACC.productlisting.showMoreResultsArea
									.waypoint(ACC.productlisting.infiniteScrollingConfig); // reconfigure
																							// waypoint
																							// eventhandler
							ACC.product.bindProductQuickViewLink();

						} else {
							ACC.common.hideSpinner();
						}
					},
					error : function(request, status, error) {
						
					}
				});
	},

	updatePaginationInfos : function(paginationInfo) {
		ACC.productlisting.currentPage = parseInt(paginationInfo.currentPage);
		ACC.productlisting.numberOfPages = parseInt(paginationInfo.numberOfPages);
	},

	bindShowMoreResults : function(showMoreResultsArea) {
		showMoreResultsArea.live("click", function() {
			ACC.productlisting.triggerLoadMoreResults();
		});

		showMoreResultsArea.waypoint(ACC.productlisting.scrollingHandler,
				ACC.productlisting.infiniteScrollingConfig);
	},

	bindSortingSelector : function() {
		$('#sort_form1, #sort_form2').change(function() {
			this.submit();
		});
	},

	initialize : function() {
		with (ACC.productlisting) {
			if (typeof ACC.productlisting.searchPath != 'undefined') {
				bindShowMoreResults(showMoreResultsArea);
				bindSortingSelector();
			}
		}
	}
};

$(document).ready(function() {
	ACC.productlisting.initialize();
	/*var min = $("#categoryMin").val();
	var max = $("#categoryMax").val();
	if(min == max){
	     $(".facetNavigation div:first").css("margin-bottom","0px");
	}*/
});
