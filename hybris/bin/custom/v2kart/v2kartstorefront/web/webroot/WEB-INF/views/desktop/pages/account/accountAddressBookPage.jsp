<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>

		<div class="col-md-11">
			<div class="accountContentPane clearfix">
				<div class="headline">
					<spring:theme code="text.account.addressBook" text="My Address"/>
				</div>
               
				<div class="description">
					<spring:theme code="text.account.addressBook.manageYourAddresses" text="Manage your address book"/>
				</div>


				<c:choose>
					<c:when test="${not empty addressData}">
						<c:forEach items="${addressData}" var="address">
							<div class="addressItem">
								<ycommerce:testId code="addressBook_address_label">
									<ul>
										<li>${fn:escapeXml(address.firstName)}&nbsp;${fn:escapeXml(address.lastName)}</li>
										<li class="addresslistformat">${fn:escapeXml(address.line1)}</li>
										<li class="addresslistformat">${fn:escapeXml(address.line2)}</li>
										<li>${fn:escapeXml(address.town)}&nbsp;-${fn:escapeXml(address.postalCode)}</li>
										<li>${fn:escapeXml(address.region.name)},${fn:escapeXml(address.country.name)}</li>
                                        <li>${fn:escapeXml(address.phone)}</li>
														</ul>
								</ycommerce:testId>
                                

								<div class="buttons">
									<ycommerce:testId code="addressBook_addressOptions_label">
										<c:if test="${not address.defaultAddress}">
											<ycommerce:testId code="addressBook_isDefault_button"><a class="button" href="set-default-address/${address.id}">
												<spring:theme code="text.setDefault" text="Set as default"/>
											</a></ycommerce:testId>
										</c:if>
										<c:if test="${address.defaultAddress}">
											<ycommerce:testId code="addressBook_isDefault_label">
												<span class="is-default-address"><spring:theme code="text.default" text="Default"/></span>
											</ycommerce:testId>
										</c:if>
										<ycommerce:testId code="addressBook_editAddress_button">
											<a class="" href="edit-address/${address.id}">
												<span class="glyphicon glyphicon-pencil"></span>
											</a>
										</ycommerce:testId>
                                        &nbsp;&nbsp;
										<ycommerce:testId code="addressBook_removeAddress_button">
											<a href="#" class=" removeAddressMyAccount" data-toggle="modal" data-target="#confirmationPopup_${address.id}" style = "padding-right : 10px;"/>
												<span class="glyphicon glyphicon-trash"></span>
											</a>
										</ycommerce:testId>
									</ycommerce:testId>
								</div>
							</div>
							<%-- <div style="display:none">
								<div id="popup_confirm_address_removal_${address.id}">
									<div class="addressItem">
										<ul>
											<li>${fn:escapeXml(address.title)}&nbsp;${fn:escapeXml(address.firstName)}&nbsp;${fn:escapeXml(address.lastName)}</li>
											<li>${fn:escapeXml(address.line1)}</li>
											<li>${fn:escapeXml(address.line2)}</li>
											<li>${fn:escapeXml(address.town)}</li>
											<li>${fn:escapeXml(address.region.name)}</li>
											<li>${fn:escapeXml(address.postalCode)}</li>
											<li>${fn:escapeXml(address.country.name)}</li>
										</ul>

										<spring:theme code="text.adress.remove.confirmation" text="Are you sure you would like to delete this address?"/></a>

										<div class="buttons">
											<a class="button removeAddressButton" data-address-id="${address.id}" href="remove-address/${address.id}">
												<spring:theme code="text.yes" text="Yes"/>
											</a>
											<a class="button closeColorBox" data-address-id="${address.id}">
												<spring:theme code="text.no" text="No"/></a>
										</div>
									</div>
								</div>
							</div> --%>
                            <div class="modal fade in accountAddressPopup" id="confirmationPopup_${address.id}" role="dialog" aria-labelledby="myModalLabel" aria-hidden="false" >
                              <div class="modal-dialog">
                                <div class="modal-content">
                                  <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
                                    <h4 class="modal-title">Confirmation</h4>
                                  </div>
                                  <div class="modal-body">
                                    <div class="description">Are you sure you would like to delete this address?</div>
                                  </div>
                                  <div class="modal-footer">
                                    <a class="btn btn-red" href="remove-address/${address.id}">
												<spring:theme code="text.yes" text="Yes"/>
											</a>
                                    <button type="button" class="btn btn-secondary" data-dismiss="modal" aria-label="Close">No</button>
                                  </div>
                                  <!-- /.modal-content --> 
                                </div>
                                <!-- /.modal-dialog --> 
                              </div>
                            </div>
						</c:forEach>

					</c:when>
					<c:otherwise>
						<p class="emptyMessage">
							<spring:theme code="text.account.addressBook.noSavedAddresses"/>
						</p>
					</c:otherwise>
				</c:choose>


				<ycommerce:testId code="addressBook_addNewAddress_button">
					<a href="add-address" class="btn btn-red">
						<spring:theme code="text.account.addressBook.addAddress" text="Add new address"/>
					</a>
				</ycommerce:testId>

			</div>
		</div>
