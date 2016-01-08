/**
 * 
 */
package in.com.v2kart.v2kartebspaymentintegration.facades.impl;

import de.hybris.platform.acceleratorfacades.payment.data.PaymentSubscriptionResultData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentSubscriptionResultItem;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.V2PGPaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;

import in.com.v2kart.core.exception.V2Exception;
import in.com.v2kart.core.payment.services.V2EncryptionService;
import in.com.v2kart.facades.payment.impl.V2PaymentFacadeImpl;
import in.com.v2kart.fulfilmentprocess.model.V2OrderModificationRefundInfoModel;
import in.com.v2kart.v2kartebspaymentintegration.constants.PaymentConstants.PaymentProperties.EBS;
import in.com.v2kart.v2kartebspaymentintegration.data.request.EBSActionRequest;
import in.com.v2kart.v2kartebspaymentintegration.data.request.EBSPaymentRequest;
import in.com.v2kart.v2kartebspaymentintegration.data.response.EBSActionResponse;
import in.com.v2kart.v2kartebspaymentintegration.data.response.EBSPaymentResponse;
import in.com.v2kart.v2kartebspaymentintegration.enums.EBSDEecisionEnum;
import in.com.v2kart.v2kartebspaymentintegration.facades.EBSPaymentFacade;
import in.com.v2kart.v2kartebspaymentintegration.services.EBSPaymentService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author vikrant2480
 * 
 */
public class EBSPaymentFacadeImpl extends V2PaymentFacadeImpl implements
        EBSPaymentFacade {

    private static final Logger LOG = Logger
            .getLogger(EBSPaymentFacadeImpl.class);

    private final static String STORE_CREDIT = "StoreCredit";

    private static final String SUCCESS = "SUCCESS";

    private static final String REFUNDED = "Refunded";

    private static final String CANCELLED = "Cancelled";

    private static final String OUTPUT_TAG = "output";

    private EBSPaymentService ebsPaymentService;
    private V2EncryptionService ebsEncryptionService;
    private Converter<EBSPaymentRequest, PaymentData> ebsPaymentRequestDataConverter;

    private Converter<OrderModel, OrderData> orderConverter;

    private CartService cartService;
    private Converter<CartModel, CartData> cartConverter;
    private ModelService modelService;
    private CommonI18NService commonI18NService;

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.v2kartebspaymentintegration.facades.EBSPaymentFacade#
     * beginHopCreatePayment(de.hybris.platform.commercefacades.user. data.AddressData,
     * de.hybris.platform.commercefacades.user.data.AddressData, java.lang.String)
     */
    @Override
    public PaymentData beginHopCreatePayment(final String description,
            final AddressData billingAddressData, final String phoneNumber)
            throws V2Exception {

        final CustomerModel customerModel = getCurrentUserForCheckout();

        final EBSPaymentRequest request = ebsPaymentService
                .beginHopCreatePayment(
                        description,
                        getPaymentUrl(getSiteConfigService().getProperty(
                                EBS.HOP_POST_RETURN_URL)), billingAddressData,
                        phoneNumber, customerModel);

        PaymentData paymentData = ebsPaymentRequestDataConverter
                .convert(request);
        if (paymentData == null) {
            paymentData = new PaymentData();
            paymentData.setParameters(new HashMap<String, String>());
        }
        final HashMap<String, String> parameters = (HashMap<String, String>) paymentData
                .getParameters();
        parameters.put(EBS.RequestParameters.SECURE_HASH.getValue(),
                ebsEncryptionService.getRequestHash(parameters));
        return paymentData;
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.v2kartebspaymentintegration.facades.EBSPaymentFacade# beginHopRefundPayment()
     */
    @Override
    public EBSActionRequest createActionRequest(final OrderModel orderModel,
            final String refundAmount, final String action) throws V2Exception {

        final OrderData orderData = getOrderConverter().convert(orderModel);
        final String orderCode = orderData.getCode();
        final String paymentId = getIdForPaymentTransaction(((V2PGPaymentInfoModel) (orderModel
                .getPaymentInfo())).getTxnid());
        final EBSActionRequest request = ebsPaymentService.createActionRequest(
                paymentId, refundAmount, orderCode, action);
        return request;
    }

    /**
     * @param txnid
     * @return String
     */
    private String getIdForPaymentTransaction(final String txnid) {

        final String paymentId = ebsPaymentService
                .getPaymentIdForTransaction(txnid);
        return paymentId;
    }

    /**
     * @param ebsPaymentService
     *        the ebsPaymentService to set
     */
    @Required
    public void setEbsPaymentService(final EBSPaymentService ebsPaymentService) {
        this.ebsPaymentService = ebsPaymentService;
    }

    /**
     * @param ebsPaymentRequestDataConverter
     *        the ebsPaymentRequestDataConverter to set
     */
    @Required
    public void setEbsPaymentRequestDataConverter(
            final Converter<EBSPaymentRequest, PaymentData> ebsPaymentRequestDataConverter) {
        this.ebsPaymentRequestDataConverter = ebsPaymentRequestDataConverter;
    }

    /**
     * @param ebsEncryptionService
     *        the ebsEncryptionService to set
     */
    @Required
    public void setEbsEncryptionService(
            final V2EncryptionService ebsEncryptionService) {
        this.ebsEncryptionService = ebsEncryptionService;
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.v2kartebspaymentintegration.facades.EBSPaymentFacade# savePaymentInfo(java.util.Map)
     */
    @Override
    public void savePaymentInfo(final Map<String, String> parameters) {
        ebsPaymentService.savePaymentInfo(parameters);
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.v2kartebspaymentintegration.facades.EBSPaymentFacade# completeHopCreatePayment(java.util.Map, boolean,
     * de.hybris.platform.core.model.order.CartModel)
     */
    @Override
    public PaymentSubscriptionResultData completeHopCreatePayment(
            final Map<String, String> parameters, final boolean saveInAccount,
            final CartModel cartModel) throws V2Exception {
        final String transactionId = parameters
                .get(EBS.ResponseParameters.MERCHANT_REFERENCE_NUMBER
                        .getValue());
        if (!transactionId.contains(cartModel.getCode())) {
            LOG.error("Merchant referance id does not match for transaction "
                    + transactionId);
            throw new V2Exception("Merchant reference id does not match!!");
        }

        final String amount = parameters.get(EBS.ResponseParameters.AMOUNT
                .getValue());
        if (!Double.valueOf(amount)
                .equals(Double.valueOf(cartModel.getTotalPayableBalance()
                        .doubleValue()))) {
            LOG.error("Amount in response does not match for transaction"
                    + transactionId);
            throw new V2Exception("Amount does not match!!");
        }

        final CustomerModel customerModel = getCurrentUserForCheckout();

        final EBSPaymentResponse response = ebsPaymentService.capture(
                customerModel, parameters, cartModel);

        final PaymentSubscriptionResultItem paymentSubscriptionResultItem = new PaymentSubscriptionResultItem();
        paymentSubscriptionResultItem.setSuccess(EBSDEecisionEnum.SUCCESS
                .getValue().equalsIgnoreCase(response.getResponseCode()));
        paymentSubscriptionResultItem
                .setDecision(response.getResponseMessage());

        return getPaymentSubscriptionResultDataConverter().convert(
                paymentSubscriptionResultItem);

    }

    protected CartService getCartService() {
        return cartService;
    }

    protected void setCartService(final CartService cartService) {
        this.cartService = cartService;
    }

    protected Converter<CartModel, CartData> getCartConverter() {
        return cartConverter;
    }

    protected void setCartConverter(
            final Converter<CartModel, CartData> cartConverter) {
        this.cartConverter = cartConverter;
    }

    /**
     * @return the orderConverter
     */
    public Converter<OrderModel, OrderData> getOrderConverter() {
        return orderConverter;
    }

    /**
     * @param orderConverter
     *        the orderConverter to set
     */
    public void setOrderConverter(
            final Converter<OrderModel, OrderData> orderConverter) {
        this.orderConverter = orderConverter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.v2kartebspaymentintegration.facades.EBSPaymentFacade#
     * processRefund(de.hybris.platform.core.model.order.OrderModel,
     */
    @Override
    public EBSActionRequest processRefund(final OrderModel refundOrderPreview,
            final V2OrderModificationRefundInfoModel refundInfoModel)
            throws V2Exception {

        EBSActionRequest request = null;
        final OrderModel originalOrder = refundOrderPreview
                .getOriginalVersion();
        // Debugging...
        if (LOG.isDebugEnabled()) {
            // logRefundDetails(refundOrderPreview, originalOrder);
        }
        final String action = EBS.ACTION_TYPE_REFUND;

        request = createActionRequest(originalOrder,
                String.valueOf(refundInfoModel.getAmountTobeRefunded()), action);

        if (request.getPaymentId() == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Failed to fetch Payment Id for order "
                        + originalOrder.getCode());
            }
            throw new V2Exception(
                    "Refund Error : Failed to fetch Payment Id for order "
                            + originalOrder.getCode());
        }

        final EBSActionResponse response = sendRequestToGateway(request);

        if (response == null || response.getError() != null
                || !(response.getError().isEmpty())) {
            final StringBuilder errMsgBuilder = new StringBuilder();
            errMsgBuilder.append("Refund Error");
            errMsgBuilder.append(response != null ? ": " + response.getError()
                    : "");
            LOG.error(errMsgBuilder.toString());
            throw new V2Exception(errMsgBuilder.toString());
        }
        return request;
    }

    private EBSActionResponse sendRequestToGateway(
            final EBSActionRequest request) {

        final HttpClient httpClient = new DefaultHttpClient();
        final HttpPost httpPost = new HttpPost(request.getRequestUrl());
        final List<NameValuePair> nameValuePairList = populateParametersList(request);
        BufferedReader rd = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairList));
            HttpResponse httpResponse;
            httpResponse = httpClient.execute(httpPost);
            rd = new BufferedReader(new InputStreamReader(httpResponse
                    .getEntity().getContent()));
            final StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            final DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder builder = null;
            InputSource is = null;
            Document doc = null;
            builder = factory.newDocumentBuilder();
            is = new InputSource(new StringReader(result.toString()));
            doc = builder.parse(is);
            final NodeList list = doc.getElementsByTagName(OUTPUT_TAG);
            final EBSActionResponse response = getRefundResponse(list);
            return response;
        } catch (IOException | ParserConfigurationException | SAXException e) {
            LOG.error("Error occured during " + request.getAction() + "request"
                    + e.getMessage());
            if (LOG.isDebugEnabled()) {
                e.printStackTrace();
            }
        } finally {
            if (rd != null) {
                try {
                    rd.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * @param request
     * @return List<NameValuePair>
     */
    private List<NameValuePair> populateParametersList(
            final EBSActionRequest request) {

        final List<NameValuePair> nameValuePairList = new ArrayList<>();

        final NameValuePair accountId = new BasicNameValuePair(
                EBS.ActionRequestParameters.ACCOUNT_ID.getValue(),
                request.getAccountId());
        final NameValuePair secretKey = new BasicNameValuePair(
                EBS.ActionRequestParameters.SECRET_KEY.getValue(),
                request.getSecretKey());
        final NameValuePair action = new BasicNameValuePair(
                EBS.ActionRequestParameters.ACTION.getValue(),
                request.getAction());
        final NameValuePair paymentId = new BasicNameValuePair(
                EBS.ActionRequestParameters.PAYMENT_ID.getValue(),
                request.getPaymentId());
        final NameValuePair amount = new BasicNameValuePair(
                EBS.ActionRequestParameters.AMOUNT.getValue(),
                request.getAmount());

        nameValuePairList.add(accountId);
        nameValuePairList.add(secretKey);
        nameValuePairList.add(action);
        nameValuePairList.add(paymentId);
        nameValuePairList.add(amount);

        return nameValuePairList;
    }

    /**
     * @param list
     * @return EBSActionResponse
     */
    private EBSActionResponse getRefundResponse(final NodeList list) {

        final EBSActionResponse response = new EBSActionResponse();
        for (int i = 0; i < list.getLength(); i++) {
            final Node node1 = list.item(i);
            if (node1 != null) {
                for (int j = 0; j < node1.getAttributes().getLength(); j++) {
                    final Node node = node1.getAttributes().item(j);

                    if (node.getNodeName().equalsIgnoreCase(
                            EBS.ActionResponseParameters.PAYMENT_ID.getValue())) {
                        response.setPaymentId(node.getNodeValue());
                    } else if (node.getNodeName().equalsIgnoreCase(
                            EBS.ActionResponseParameters.AMOUNT.getValue())) {
                        response.setAmount(node.getNodeValue());
                    } else if (node.getNodeName().equalsIgnoreCase(
                            EBS.ActionResponseParameters.REFERENCE_NO
                                    .getValue())) {
                        response.setReferenceNo(node.getNodeValue());
                    } else if (node.getNodeName().equalsIgnoreCase(
                            EBS.ActionResponseParameters.TRANSACTION_TYPE
                                    .getValue())) {
                        response.setTransactionType(node.getNodeValue());
                    } else if (node.getNodeName().equalsIgnoreCase(
                            EBS.ActionResponseParameters.STATUS.getValue())) {
                        response.setStatus(node.getNodeValue());
                    } else if (node.getNodeName().equalsIgnoreCase(
                            EBS.ActionResponseParameters.TRANSACTION_ID
                                    .getValue())) {
                        response.setTransactionId(node.getNodeValue());
                    } else if (node.getNodeName().equalsIgnoreCase(
                            EBS.ActionResponseParameters.MODE.getValue())) {
                        response.setMode(node.getNodeValue());
                    } else if (node.getNodeName().equalsIgnoreCase(
                            EBS.ActionResponseParameters.DATE_TIME.getValue())) {
                        response.setDateTime(node.getNodeValue());
                    } else if (node.getNodeName().equalsIgnoreCase(
                            EBS.ActionResponseParameters.ERROR.getValue())) {
                        response.setError(node.getNodeValue());
                    }
                }
            }

        }
        return response;
    }

    /**
     * @return the modelService
     */
    public ModelService getModelService() {
        return modelService;
    }

    /**
     * @param modelService
     *        the modelService to set
     */
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.v2kartebspaymentintegration.facades.EBSPaymentFacade#
     * initiateCancelRequest(de.hybris.platform.core.model.order.OrderModel , java.lang.String)
     */
    @Override
    public EBSActionResponse initiateCancelRequest(final OrderModel orderModel,
            final String amount) throws V2Exception {
        // Debugging...
        if (LOG.isDebugEnabled()) {
            // logRefundDetails(refundOrderPreview, originalOrder);
        }
        final String action = EBS.ACTION_TYPE_CANCEL;
        final EBSActionRequest request = createActionRequest(orderModel,
                amount, action);
        if (request.getPaymentId() == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Failed to fetch Payment Id for order "
                        + orderModel.getCode());
            }
            throw new V2Exception(
                    "Cancellation Error : Failed to fetch Payment Id for order "
                            + orderModel.getCode());
        }

        final EBSActionResponse response = sendRequestToGateway(request);

        if (response == null) {
            final String errMsg = "Cancellation Error : response is null";
            LOG.error(errMsg);
            throw new V2Exception(errMsg);
        }

        if (!(StringUtils.isEmpty(response.getError()))) {
            final String errMsg = "Cancellation Error : " + response.getError();
            LOG.error(errMsg);
            throw new V2Exception(errMsg);
        }
        return response;
    }
}
