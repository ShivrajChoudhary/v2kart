<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="shopMoreAmount" required="true" type="de.hybris.platform.commercefacades.product.data.PriceData"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:if test="${shopMoreAmount ne null && shopMoreAmount.value > 0}">
    <spring:theme code="basket.page.spendmore.shipping" arguments="${shopMoreAmount.formattedValue}"
        text="Spend ${shopMoreAmount.formattedValue} more to get Shipping Free" var="shopMoreText" />
    <spring:theme code="text.button.shopMore" text="Shop More" var="shopMoreButtonText"></spring:theme>

    <div class="col-md-12">
        <div class="free-shipping">
            <span class="">${shopMoreText}</span> <a href="${continueShoppingUrl}" title="${shopMoreButtonText}" class="btn btn-defaultred">${shopMoreButtonText}</a>
        </div>
    </div>
</c:if>
