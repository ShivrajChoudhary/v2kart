<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div>
		<fmt:formatNumber maxFractionDigits="0" value="${(product.averageRating > 0 ? product.averageRating: 0) * 17}" var="starWidth"/>
		<span class="stars large"
			style="display: inherit;"
			tabindex='4'
			alt='<spring:theme code="product.average.review.rating" arguments="${(product.averageRating > 0 ? product.averageRating: 0)}"/>'>
			<span style="width: ${starWidth}px;"></span>
		</span>
		<%-- <c:if test="${fn:length(product.reviews) > 0}">
			<a href="#" id="averageRatingTopOfPage">
				<div class="numberOfReviews">( ${fn:length(product.reviews)})</div>
			</a>
			<a href="#" id='seeReviewsLink'><spring:theme code="review.see.reviews"/> | </a>
		</c:if> --%>
		<c:url value="/p" var="encodedUrl"/>
		<a id='writeReviewLink' href="${encodedUrl}/${product.code}/writeReview"><spring:theme code="review.write.review"/></a>|
		<c:url value="/p" var="encodedUrl"/>
				<span class="glyphicon glyphicon-envelope"></span><a id='emailFriendLink' href="${product.url}/emailFriend"><spring:theme code="mobile.product.email.friend"/></a>
</div>

  
