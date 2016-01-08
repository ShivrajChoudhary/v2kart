package in.com.v2kart.facades.payment.impl;

import java.math.BigDecimal;
import java.util.Currency;

import org.springframework.beans.factory.annotation.Required;

import in.com.v2kart.core.payment.services.V2PaymentService;
import in.com.v2kart.facades.order.data.V2StoreCreditPaymentInfoData;
import in.com.v2kart.facades.payment.V2PaymentFacade;
import in.com.v2kart.facades.storeCredit.StoreCreditFacade;
import in.com.v2kart.fulfilmentprocess.services.V2StoreCreditService;
import de.hybris.platform.acceleratorfacades.payment.impl.DefaultPaymentFacade;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.V2StoreCreditPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;

public class V2PaymentFacadeImpl extends DefaultPaymentFacade implements V2PaymentFacade {
    
    /** V2PaymentService bean injection */
    private V2PaymentService v2PaymentService;
    
    /** I18NService bean injection */
    private I18NService i18nService;

    /**
     * @return the v2PaymentService
     */
    public V2PaymentService getV2PaymentService() {
        return v2PaymentService;
    }

    /**
     * @param v2PaymentService
     *        the v2PaymentService to set
     */
    public void setV2PaymentService(V2PaymentService v2PaymentService) {
        this.v2PaymentService = v2PaymentService;
    }

    @Override
    public void saveBillingAddress(AddressData addressData) {
        v2PaymentService.saveBillingAddress(addressData);
    }
   
    /**
     * Gets the payment url.
     * 
     * @param relativeUrl
     *        the relative url
     * @return the payment url
     */
    protected String getPaymentUrl(final String relativeUrl)
    {
        final BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();
        return getSiteBaseUrlResolutionService().getWebsiteUrlForSite(currentBaseSite, true, relativeUrl);
    }

    /** {@inheritDoc} */
    @Override
    public PaymentTransactionEntryModel redeemStoreCredit(CartModel cartModel) {
        final Currency currency = i18nService.getBestMatchingJavaCurrency(cartModel.getCurrency().getIsocode());
        //final String merchantTransactionCode = getGenerateMerchantTransactionCodeStrategy().generateCode(cartModel);
        PaymentTransactionEntryModel transactionEntryModel = null;
        if(cartModel != null && cartModel.getStoreCreditPaymentInfo() != null){
            PaymentInfoModel paymentInfoModel= cartModel.getStoreCreditPaymentInfo();
            if(paymentInfoModel!=null && paymentInfoModel instanceof V2StoreCreditPaymentInfoModel){
                V2StoreCreditPaymentInfoModel v2StoreCreditPaymentInfoModel=(V2StoreCreditPaymentInfoModel)paymentInfoModel;
                BigDecimal totalAmount = new BigDecimal(v2StoreCreditPaymentInfoModel.getStoreCreditAmount().doubleValue());
                transactionEntryModel = v2PaymentService.redeemStoreCredit("MerchCode", totalAmount, currency, cartModel);
                return transactionEntryModel;
            }
        }
        return null;
    }

    @Required
    public void setI18nService(I18NService i18nService) {
        this.i18nService = i18nService;
    }
}
