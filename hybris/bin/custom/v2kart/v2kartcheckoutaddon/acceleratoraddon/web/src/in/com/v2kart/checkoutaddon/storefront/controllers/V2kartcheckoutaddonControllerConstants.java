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
package in.com.v2kart.checkoutaddon.storefront.controllers;

/**
 */
public interface V2kartcheckoutaddonControllerConstants
{
    // implement here controller constants used by this extension

    String ADDON_PREFIX = "addon:/v2kartcheckoutaddon/";

    /**
     * Class with view name constants
     */
    interface Views {

        interface Pages {

            interface MultiStepCheckout {
                String AddEditDeliveryAddressPage = ADDON_PREFIX + "pages/checkout/multi/addEditDeliveryAddressPage";
                String ChooseDeliveryMethodPage = ADDON_PREFIX + "pages/checkout/multi/chooseDeliveryMethodPage";
                String ChoosePickupLocationPage = ADDON_PREFIX + "pages/checkout/multi/choosePickupLocationPage";
                String AddPaymentMethodPage = ADDON_PREFIX + "pages/checkout/multi/addPaymentMethodPage";
                String CheckoutSummaryPage = ADDON_PREFIX + "pages/checkout/multi/checkoutSummaryPage";
                String HostedOrderPageErrorPage = ADDON_PREFIX + "pages/checkout/multi/hostedOrderPageErrorPage";
                String HostedOrderPostPage = ADDON_PREFIX + "pages/checkout/multi/hostedOrderPostPage";
                String PayUHostedOrderWaitPage = ADDON_PREFIX + "pages/checkout/multi/payUHostedOrderWaitPage";
                String CCAvenueHostedOrderWaitPage = ADDON_PREFIX + "pages/checkout/multi/ccavenueHostedOrderWaitPage";
                String NativeCCAvenueHostedOrderWaitPage = ADDON_PREFIX + "pages/checkout/multi/nativeCCavenueHostedOrderWaitPage";
                String HostedOrderSavePage = ADDON_PREFIX + "pages/checkout/multi/hostedOrderSavePage";
                String SilentOrderPostPage = ADDON_PREFIX + "pages/checkout/multi/silentOrderPostPage";
                String GiftWrapPage = ADDON_PREFIX + "pages/checkout/multi/giftWrapPage";
                String EBSHostedOrderPostPage = ADDON_PREFIX + "pages/checkout/multi/ebsHostedOrderWaitPage";
            }

            interface Error {
                String ServerErrorPage = "pages/error/serverError";
            }
        }

        interface Fragments {

            interface Checkout {
                String TermsAndConditionsPopup = ADDON_PREFIX + "fragments/checkout/termsAndConditionsPopup";
                String BillingAddressForm = ADDON_PREFIX + "fragments/checkout/billingAddressForm";
            }

        }
    }
}
