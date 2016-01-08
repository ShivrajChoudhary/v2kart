<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<script id="resultsTileItemsTemplate" type="text/x-jquery-tmpl">
	{{each(i, result) $data.results}}
						{{tmpl(result) "#resultsTileItemProductTemplate"}}
						<div class="lineSeperator"></div>
	{{/each}}
</script>
<script id="resultsTileItemProductTemplate" type="text/x-jquery-tmpl">
<center>
<div id="tile">
	<a href="{{= url}}" class="ui-link">

		<div class="prod_image_main">
			{{if $.isEmptyObject(images)}}
			<theme:image code="img.missingProductImage.product" alt="{{= name}}" title="{{= name}}"/>
			{{else}}
			<img class="primaryImage" id="primaryImage" src="{{= images[0].url}}" title="{{= images[0].name}}" alt="{{= images[0].name}}"/>
			{{/if}}
		</div>
		<div class="productTitle">{{= name}}</div>
	
<!--	{{if !$.isEmptyObject(price)}}
		<span id="productPrice" class="mlist-price">{{= price.formattedValue || 0}}</span>
		{{/if}} -->
					{{if !$.isEmptyObject(price)}}
					{{if discountedPrice.value == price.value}}
				   <span id="productPrice" class="mlist-price">{{= price.formattedValue || 0}}</span>
						{{else}}
  					<div class="ui-grid-b productPriceDetail">
                        <p><span id="productPrice" class="faded">{{= price.formattedValue || 0}}</span></p>
                        <p> <span>{{= discountedPrice.formattedValue || 0}}</span></p>
                        <p><span class="badge badge-red discount">{{= percentageDiscount || 0}}%</span></p>
                    </div>
					{{/if}}
					{{/if}}
     <!--   {{if stock.stockLevelStatus.code=='outOfStock'}}
            <span class='listProductOutOfStock mlist-stock'><spring:theme code="product.variants.out.of.stock"/></span>-->
        {{else stock.stockLevelStatus.code=='lowStock' }}
            <span class='listProductLowStock mlist-stock'><spring:theme code="product.variants.only.left" arguments="{{= stock.stockLevel}}"/></span>
        {{/if}} 
	</a>
</div>
</center>
</script>
  <c:choose>
                <c:when test="${product.discountedPrice.value eq product.price.value}">
    		      <span id="productPrice" class="mlist-price"><format:price priceData="${product.price}"/></span>
                </c:when>
                <c:otherwise>
                    <div class="ui-grid-b productPriceDetail">
                        <p><span id="productPrice" class="faded"><format:price priceData="${product.price}"/></span></p>
                        <p> <span><format:price priceData="${product.discountedPrice}"/></span></p>
                        <p><span class="badge badge-red discount">${product.percentageDiscount}%</span></p>
                    </div>
                </c:otherwise>
            </c:choose>