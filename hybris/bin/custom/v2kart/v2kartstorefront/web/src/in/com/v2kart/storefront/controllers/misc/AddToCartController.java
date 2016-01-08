/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package in.com.v2kart.storefront.controllers.misc;

import in.com.v2kart.facades.cart.V2CartFacade;
import in.com.v2kart.storefront.controllers.ControllerConstants;
import in.com.v2kart.storefront.forms.ShowCartForm;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddToCartForm;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;

/**
 * Controller for Add to Cart functionality which is not specific to a certain page.
 */
@Controller
@Scope("tenant")
public class AddToCartController extends AbstractController {
    private static final String TYPE_MISMATCH_ERROR_CODE = "typeMismatch";
    private static final String ERROR_MSG_TYPE = "errorMsg";
    private static final String REDIRECT_PDP = "redirect:/p/";
    private static final String REDIRECT_CART = "redirect:/cart";
    private static final String QUANTITY_INVALID_BINDING_MESSAGE_KEY = "basket.error.quantity.invalid.binding";

    protected static final Logger LOG = Logger.getLogger(AddToCartController.class);

    @Resource(name = "cartFacade")
    private V2CartFacade cartFacade;

    @Resource(name = "accProductFacade")
    private ProductFacade productFacade;

    @RequestMapping(value = "/cart/add", method = RequestMethod.POST, produces = "application/json")
    public String addToCart(@RequestParam("productCodePost") final String code, final Model model, @Valid final AddToCartForm form,
            final BindingResult bindingErrors) {
        if (bindingErrors.hasErrors()) {
            return getViewWithBindingErrorMessages(model, bindingErrors);
        }

        final long qty = form.getQty();

        if (qty <= 0) {
            model.addAttribute(ERROR_MSG_TYPE, "basket.error.quantity.invalid");
            model.addAttribute("quantity", Long.valueOf(0L));
        } else {
            try {
                long qtyWillAdd = getQtyWillAdd(code, qty);
                CartModificationData cartModification = null;
                if(qtyWillAdd != 0L) {
               	 cartModification = cartFacade.addToCart(code, qtyWillAdd);
                   model.addAttribute("quantity", Long.valueOf(cartModification.getQuantityAdded()));
                   model.addAttribute("entry", cartModification.getEntry());
                   model.addAttribute("cartCode", cartModification.getCartCode());
                }
                if(qtyWillAdd == 0L){
               	 model.addAttribute(ERROR_MSG_TYPE,
                         "basket.information.quantity.noItemsAdded");
                }
                else if(qtyWillAdd < qty) {
               	 model.addAttribute(ERROR_MSG_TYPE,
                         "basket.information.quantity.reducedNumberOfItemsAdded");
                }
                else if (cartModification.getQuantityAdded() == 0L) {
                    model.addAttribute(ERROR_MSG_TYPE, "basket.information.quantity.noItemsAdded." + cartModification.getStatusCode());
                } else if (cartModification.getQuantityAdded() < qty) {
                    model.addAttribute(ERROR_MSG_TYPE,
                            "basket.information.quantity.reducedNumberOfItemsAdded." + cartModification.getStatusCode());
                }
            } catch (final CommerceCartModificationException ex) {
                model.addAttribute(ERROR_MSG_TYPE, "basket.error.occurred");
                model.addAttribute("quantity", Long.valueOf(0L));
            }
        }

        model.addAttribute("product", productFacade.getProductForCodeAndOptions(code, Arrays.asList(ProductOption.BASIC)));

        return ControllerConstants.Views.Fragments.Cart.AddToCartPopup;
    }
    
	private long getQtyWillAdd(final String code, final long qty)
	{
		final List<OrderEntryData> entries = cartFacade.getSessionCart().getEntries();
		long qtyWillAdd = qty;
		if(entries != null){
   		for (final OrderEntryData entry : entries)
   		{
   			if (entry.getProduct().getCode().equals(code))
   			{
   				final Long entryQuantity = entry.getQuantity();
   				if (entryQuantity >= 10)
   				{
   					qtyWillAdd = 0;
   				}
   				else if (qtyWillAdd + entryQuantity > 10)
   				{
   					qtyWillAdd = 10 - entryQuantity;
   				}
   			}
   		}
		}
		return qtyWillAdd;
	}

    @RequestMapping(value = "/cart/check", method = RequestMethod.POST, produces = "application/json")
    public String cartCheck(@RequestParam("productCodePost") final String code, final Model model, @Valid final AddToCartForm form,
            final BindingResult bindingErrors) {
        if (bindingErrors.hasErrors()) {
            return getViewWithBindingErrorMessages(model, bindingErrors);
        }

        final long qty = form.getQty();
        List<OrderEntryData> cartEntries = cartFacade.getSessionCart().getEntries();
        if (CollectionUtils.isNotEmpty(cartEntries)) {
            for (OrderEntryData cartEntry : cartEntries) {
                if (cartEntry.getProduct().getCode().equals(code)) {
                    model.addAttribute("cartContainsProduct", "true");
                    return ControllerConstants.Views.Fragments.Cart.AddToCartPopup;
                }
            }
        }

        if (qty <= 0) {
            model.addAttribute(ERROR_MSG_TYPE, "basket.error.quantity.invalid");
            model.addAttribute("quantity", Long.valueOf(0L));
        } else {
            try {
                final CartModificationData cartModification = cartFacade.addToCart(code, qty);
                model.addAttribute("quantity", Long.valueOf(cartModification.getQuantityAdded()));
                model.addAttribute("entry", cartModification.getEntry());
                model.addAttribute("cartCode", cartModification.getCartCode());

                if (cartModification.getQuantityAdded() == 0L) {
                    model.addAttribute(ERROR_MSG_TYPE, "basket.information.quantity.noItemsAdded." + cartModification.getStatusCode());
                } else if (cartModification.getQuantityAdded() < qty) {
                    model.addAttribute(ERROR_MSG_TYPE,
                            "basket.information.quantity.reducedNumberOfItemsAdded." + cartModification.getStatusCode());
                }
            } catch (final CommerceCartModificationException ex) {
                model.addAttribute(ERROR_MSG_TYPE, "basket.error.occurred");
                model.addAttribute("quantity", Long.valueOf(0L));
            }
        }

        model.addAttribute("product", productFacade.getProductForCodeAndOptions(code, Arrays.asList(ProductOption.BASIC)));

        return ControllerConstants.Views.Fragments.Cart.AddToCartPopup;
    }

    protected String getViewWithBindingErrorMessages(final Model model, final BindingResult bindingErrors) {
        for (final ObjectError error : bindingErrors.getAllErrors()) {
            if (isTypeMismatchError(error)) {
                model.addAttribute(ERROR_MSG_TYPE, QUANTITY_INVALID_BINDING_MESSAGE_KEY);
            } else {
                model.addAttribute(ERROR_MSG_TYPE, error.getDefaultMessage());
            }
        }
        return ControllerConstants.Views.Fragments.Cart.AddToCartPopup;
    }

    protected boolean isTypeMismatchError(final ObjectError error) {
        return error.getCode().equals(TYPE_MISMATCH_ERROR_CODE);
    }

    @RequestMapping(value = "/showcart", method = RequestMethod.POST)
    public String showcart(final Model model, final ShowCartForm form, final HttpServletRequest request,
            final RedirectAttributes redirectModel) {

        String redirect = REDIRECT_CART;

        final long qty = form.getQty();
        final String code = form.getCode();
        if (qty <= 0) {
            redirectModel.addAttribute(ERROR_MSG_TYPE, "basket.error.quantity.invalid");
            model.addAttribute("quantity", Long.valueOf(0L));
            redirect = REDIRECT_PDP + code;
        } else {
            try {
                long qtyWillAdd = getQtyWillAdd(code, qty);
                CartModificationData cartModification = null;
                if(qtyWillAdd != 0L){
						cartModification = cartFacade.addToCart(code, qtyWillAdd);
                   model.addAttribute("quantity", Long.valueOf(cartModification.getQuantityAdded()));
                   model.addAttribute("entry", cartModification.getEntry());
                   model.addAttribute("cartCode", cartModification.getCartCode());
                }
                if(qtyWillAdd == 0L){
               	 GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "basket.information.quantity.noItemsAdded");
                }
                else if(qtyWillAdd < qty){
               	 GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "basket.information.quantity.reducedNumberOfItemsAdded");
                }
                else if (cartModification.getQuantityAdded() == 0L) {
                    redirectModel.addAttribute(ERROR_MSG_TYPE,
                            "basket.information.quantity.noItemsAdded." + cartModification.getStatusCode());
                    redirect = REDIRECT_PDP + code;
                } else if (cartModification.getQuantityAdded() < qty) {
                    redirectModel.addAttribute(ERROR_MSG_TYPE,
                            "basket.information.quantity.reducedNumberOfItemsAdded." + cartModification.getStatusCode());
                    redirect = REDIRECT_PDP + code;
                }
            } catch (final CommerceCartModificationException ex) {
                redirectModel.addAttribute(ERROR_MSG_TYPE, "basket.error.occurred");
                model.addAttribute("quantity", Long.valueOf(0L));
                redirect = REDIRECT_PDP + code;
            }
        }

        return redirect;
    }
}
