<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/mobile/address"%>

<div class="item_container_holder accountAddressPage" data-content-theme="d" data-theme="e">
	<div data-content-theme="d" data-theme="e">
        <div class="innerContent heading ui-bar ui-bar-d">
		<h1>
			<spring:theme code="text.account.addressBook.addressDetails" text="Address Details"/>
		</h1>
        <p class="continuous-text"  style="font-size: 13px">
                <spring:theme code="text.account.addressBook.addEditform" text="Please use this form to add/edit an address."/>
                <br><spring:theme code="mobile.review.required1" /><span class="mandatory"><spring:theme code="mobile.review.required.mark" /></span><spring:theme code="mobile.review.required2" /></p>
		</div>
		<div class="item_container">
			<div class="innerContent">
			<address:addressFormSelector supportedCountries="${countries}" regions="${regions}"
																	 cancelUrl="/my-account/address-book"/>

			<address:suggestedAddresses selectedAddressUrl="/my-account/select-suggested-address"/>
            </div>
		</div>
	</div>
</div>
