<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>

<script id="resultsListItemsTemplate" type="text/x-jquery-tmpl">
	{{each(i, result) $data.results}}
<li class="mlist-listItem" data-theme="e">
		<a href="<c:url value='{{= result.url}}' />">
			<div class="ui-grid-a">
				<div class="ui-block-a plp-prod-img">
	{{if !$.isEmptyObject(result.potentialPromotions)}}        
        <span class="offer-badge"></span>
			{{/if}}
				{{tmpl(result) "#resultsListItemImageTemplate"}}
				</div>
				<div class="ui-block-b">
					<h1>{{= result.name}}</h1>
 					 <p class="prod-price">					
					{{if !$.isEmptyObject(result.price)}}
					{{if (result.discountedPrice.value)==(result.price.value)}}
				  <span class="price">{{= result.price.formattedValue || 0}}</span>
					{{/if}}
						{{if (result.discountedPrice.value)!=(result.price.value)}}
				  <span class="price">{{= result.discountedPrice.formattedValue || 0}}</span>
					{{/if}}
				
					{{/if}}
					</p>
 					<p class="prod-price discount">
					{{if (result.discountedPrice.value)!=(result.price.value)}}
					 <span class="old-price">{{= result.price.formattedValue || 0}}</span>
					  <span class="prod-discount">({{= result.percentageDiscount || 0}}% OFF) </span>
					{{/if}}
					</p>
				</div>
			</div>
		</a>
	</li>
	{{/each}}
</script>
<script id="resultsListItemImageTemplate" type="text/x-jquery-tmpl">
	<div class="prod_image_main">
		{{if $.isEmptyObject(images)}}
		<theme:image code="img.missingProductImage.thumbnail" alt="{{= name}}" title="{{= name}}"/>
		{{else}}
	<img class="primaryImage hasThumbnail" src="{{= images[1].url}}" title="{{= images[1].name}}" alt="{{= images[1].name}}" data-thumbnail="{{= images[1].url}}" />
		{{/if}}
	</div>
</script>
<script id="resultsListItemProductRatingTemplate" type="text/x-jquery-tmpl">
	<div>
		<span class="stars large" style="display: inherit;">
			<span style="width: {{= (averageRating > 0 ? averageRating: 0) * 17 }}px;"></span>
		</span>
		{{if !$.isEmptyObject(numberOfReviews)}}
		<div class="numberOfReviews">( {{= numberOfReviews}} )</div>
		{{/if}}
	</div>
</script>
