<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="deliveryMethod" required="true" type="de.hybris.platform.commercefacades.order.data.DeliveryModeData" %>
<%@ attribute name="isSelected" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<li class="delivery_method_item">
	<input type="radio" name="delivery_method" id="${deliveryMethod.code}" value="${deliveryMethod.code}" ${isSelected ? 'checked="checked"' : ''}/>
    <c:if test="${not empty deliveryMethod.deliveryCost}">
	   <label for="${deliveryMethod.code}">${deliveryMethod.name}&nbsp;-&nbsp;${deliveryMethod.description}&nbsp;-&nbsp;${deliveryMethod.deliveryCost.formattedValue}</label>
    </c:if>
    <c:if test="${empty deliveryMethod.deliveryCost}">
        <label for="${deliveryMethod.code}">${deliveryMethod.name}&nbsp;-&nbsp;${deliveryMethod.description}&nbsp;-&nbsp;Rs. 0.00</label>
    </c:if>
</li>
