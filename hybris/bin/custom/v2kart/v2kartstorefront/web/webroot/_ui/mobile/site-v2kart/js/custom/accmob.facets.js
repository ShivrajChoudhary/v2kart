ACCMOB.facets = {
	params: {},
	cachedJSONResponse: {},
	currentSelection: {},
	currentSearchQuery: "",

	// begin DATA -------------------------------------------------------------------
	setCurrentSearchQuery: function (untouched)
	{
		var pageQuery = $("#facetRefinements-page").data("searchquery");
		if (pageQuery !== undefined) {
		
			if (untouched == null)
			{  // set default value
				var untouched = false;
			}
	
			if (typeof pageQuery !== 'undefined')
			{
				if (untouched)
				{
					ACCMOB.facets.currentSearchQuery = pageQuery
				}
				else
				{
					ACCMOB.facets.currentSearchQuery = pageQuery.split(":").slice(0, 2).join(":"); // take only searchterm and sorting
				}
			}
		}
	},

	createQueryParams: function (initialCreation)
	{
		if (initialCreation == null)
		{  // set default value
			var initialCreation = false;
		}

		ACCMOB.facets.params = {};
		var selectedOptions = $("fieldset.facetValueList input:checked");
		var facets = selectedOptions.map(function ()
		{
			return $(this).data("query");
		}).get().join(":");

		if (facets !== null && facets !== [] && facets !== "")
		{
			ACCMOB.facets.params = {q: [ACCMOB.facets.currentSearchQuery, facets].join(":")};
		}
		else
		{
			if (initialCreation)
			{
				ACCMOB.facets.setCurrentSearchQuery(true);
			}
			ACCMOB.facets.params = {q: ACCMOB.facets.currentSearchQuery};
			if (initialCreation)
			{
				ACCMOB.facets.setCurrentSearchQuery();
			}
		}
		;
	},

	getFacetData: function ()
	{
		ACCMOB.common.showPageLoadingMsg();
		$.ajax({
			url: ACCMOB.common.currentPath + "/facets",
			dataType: "json",
			async: false,
			data: ACCMOB.facets.params,
			success: function (data)
			{
				ACCMOB.facets.cachedJSONResponse = data;
				ACCMOB.common.hidePageLoadingMsg();
			} // cache data
		});

	},
	// end DATA ---------------------------------------------------------------------

	// begin BINDINGS ---------------------------------------------------------------
	bindUpdateFacet: function (trigger, eventName)
	{
		$(document.body).on(eventName, trigger, function (event)
		{
			ACCMOB.facets.updateRefinementsList();
			$("#facetRefinements-page ul").listview();
			ACCMOB.facets.updateFacetContents();
			$('.secondLevelFacetsDiv').css('display', 'none');
			$('.firstLevel').css('display', 'block');
			$('.backToFacets').css('display', 'none');
			$('.ui-loader').css('display', 'none');
		});
	},

	bindClearFacetSelections: function (clearFacetSelectionsButton)
	{
		ACCMOB.facets.setCurrentSearchQuery();

		$(document.body).on('click', clearFacetSelectionsButton, function ()
		{
			$(this).attr("href", "?" + $.param({q: ACCMOB.facets.currentSearchQuery}));
		});

	},

	bindApplyFilter: function (applyFilterButton)
	{
		$(document.body).on('click', applyFilterButton, function ()
		{
			ACCMOB.facets.createQueryParams();
			var selectedOptions = $("fieldset.facetValueList input:checked");
			var facets = selectedOptions.map(function ()
			{
				return $(this).data("query");
			}).get().join(":");

				
				var pageQuery = $("#facetRefinements-page").data("searchquery");
				if(pageQuery.indexOf('category') != -1) {
					pageQueryParts = pageQuery.split(':');
					for(i=0; i< pageQueryParts.length; i++){
						
						if(pageQueryParts[i] == 'category') {
							categoryQuery = pageQueryParts[i]+":"+pageQueryParts[i+1];
							if((ACCMOB.facets.currentSearchQuery).indexOf(categoryQuery) == -1) {
								ACCMOB.facets.currentSearchQuery = [ACCMOB.facets.currentSearchQuery,categoryQuery].join(":");
							}
						
						}
				}
			}
				ACCMOB.facets.createQueryParams();
			$(this).attr("href", "?" + $.param({q: ACCMOB.facets.params.q}));
		});
	},

	bindAddFilterButton: function (addFilterButton)
	{
		$(document.body).on('click', addFilterButton, function ()
		{
			with (ACCMOB.facets)
			{
				updateRefinementsList(true);
				renderFacetPages();
			}
		});
		// set status icon
		var pageQuery = $("#facetRefinements-page").data("searchquery");
		if (pageQuery !== undefined) {
			if (pageQuery.split(":")[2] !== undefined)
			{
				$(addFilterButton).find(".ui-btn-inner span:last").removeClass("ui-icon-checkbox-off").addClass("ui-icon-checkbox-on");
			}
		}
	},

	setTemplates: function ()
	{
		this.refinementsListTemplate = $("#refinementsListTemplate");
		this.refinementFacetPageTemplate = $("#refinementFacetPageTemplate");
		this.refinementFacetContentTemplate = $("#refinementFacetContentTemplate");
	},
	// end BINDINGS ---------------------------------------------------------------

	// begin RENDER ---------------------------------------------------------------

	renderFacetPages: function ()
	{
		$.each(ACCMOB.facets.cachedJSONResponse.facets, function (index, facet)
		{
			ACCMOB.facets.renderFacetPage(facet);
			ACCMOB.facets.renderFacetLink(facet);
		});
	},

	renderFacetLink: function (facet)
	{
		// collect all selected values of facet
		var values="";
		$.each(facet.values, function (i, facetValue)
		{
			
			if (facetValue.selected)
			{
				values=values+facetValue.name+"<span class='refinementFilterDelimiter'>|</span>";
			}
		});
		// merge names of selected facetvalues and append to button
		/*$("#f" + facet.name + "-button .refinementSetFilter").html(values);*/
	},

	renderFacetPage: function (facet)
	{
		/*$.tmpl(ACCMOB.facets.refinementFacetPageTemplate, facet).appendTo("body");*/
	},

	renderRefinementsList: function ()
	{
		/*$("div#facetRefinements-page div[data-role='content'] ul").replaceWith($.tmpl(ACCMOB.facets.refinementsListTemplate, ACCMOB.facets.cachedJSONResponse));*/
	},
	// end RENDER -----------------------------------------------------------------

	// begin UPDATE ---------------------------------------------------------------
	updateFacetContents: function ()
	{
		$.each(ACCMOB.facets.cachedJSONResponse.facets, function (index, facet)
		{
			if ($("#" + facet.name + "-page").length <= 0)
			{
				ACCMOB.facets.renderFacetPage(facet);
			}
			ACCMOB.facets.renderFacetLink(facet);
		});
	},

	updateRefinementsList: function (initialCreation)
	{
		if (initialCreation == null)
		{
			var initialCreation = false; // set default value
		}

		ACCMOB.facets.createQueryParams(initialCreation);
		ACCMOB.facets.getFacetData();
		//ACCMOB.facets.renderRefinementsList();
	},

	updateFormElements: function ()
	{
		$("div.item_container_holder input[type='checkbox']").checkboxradio();
		$("div.item_container_holder input[data-type='search']").textinput();
		$("div.item_container_holder [data-role=button]").button();
		$("div.item_container_holder fieldset").controlgroup();
		$("#changeLocationLink").addClass("ui-link");
		$("div.item_container_holder").addClass("ui-content ui-body-d");
	},
	// end UPDATE ---------------------------------------------------------------

	redirectIfPageWasReloaded: function ()
	{
		var currentUrl = $.mobile.path.parseUrl(window.location.href);

		if (currentUrl.hash !== "" && currentUrl.hash.match(/-page$/))
		{

			if (currentUrl.hash === "#facetRefinements-page")
			{
				history.go(-1);
			}
			else
			{
				history.go(-2);
			}
			return false;
		}
		return true;
	},
	refinecheckbox: function ()
	{
	var pageQuery = $("#facetRefinements-page").data("searchquery");
	if (pageQuery !== undefined) {
		if (pageQuery.split(":")[2] !== undefined)
		{
			$("#addFilters").attr("data-icon","checkbox-on");
		}
	}
	},
	initialize: function ()
	{
		with (ACCMOB.facets)
		{
			bindAddFilterButton("#addFilters");
			setTemplates();
			refinecheckbox();
			setCurrentSearchQuery(null);
			bindUpdateFacet(".facetPage input", "change");
			bindApplyFilter("#applyFilter");
			bindClearFacetSelections("#clearFacetSelections");
		}
	}
};

$(document).ready(function ()
{
	ACCMOB.facets.redirectIfPageWasReloaded();
	ACCMOB.facets.initialize();
	
});
