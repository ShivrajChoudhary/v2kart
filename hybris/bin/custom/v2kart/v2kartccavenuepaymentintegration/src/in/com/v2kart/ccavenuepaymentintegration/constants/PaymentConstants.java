/**
 *
 */
package in.com.v2kart.ccavenuepaymentintegration.constants;

/**
 * The Class PaymentConstants.
 *
 * @author yamini2280
 */
public class PaymentConstants {

    public interface PaymentProperties {

        public interface CCAVENUE {
            String HOP_POST_SUCCESS_URL = "payment.ccavenue.success.url";
            String HOP_POST_CANCEL_URL = "payment.ccavenue.cancel.url";
            String HOP_POST_REQUEST_URL = "hop.post.ccavenue.url";
            String PAYMENT_MODE = "CCAV";
            String HOP_PAYMENT_MERCHANT_KEY = "payment.ccavenue.merchant.id";
            String HOP_PAYMENT_CURRENCY = "payment.ccavenue.currency";
            String WORKING_KEY = "payment.ccavenue.working.key";
            String HOP_ACCESS_CODE = "payment.ccavenue.access.code";
            String PG_CC = "CC";
            String PG_EMI = "emi";
            String HOP_PAYMENT_LANGUAGE = "payment.ccavenue.language";
            String ENCRYPTED_RESPONSE_PARAM = "encResp";
            String ATTRIBUTE_TOKEN = "&";
            String KEY_VALUE_PAIR_TOKEN = "=";
            String PAYMENT_GATEWAY = "CCAVENUE";

            public enum ResponseParameters {

                AMOUNT("amount"), BANK_REFERENCE_NUMBER("bank_ref_no"), CARD_NAME("card_name"), CURRENCY("currency"), ORDER_ID("order_id"), ORDER_STATUS(
                        "order_status"), PAYMENT_MODE("payment_mode"), TRACKING_ID("tracking_id"), BILLING_CITY("billing_city"), BILLING_COUNTRY(
                        "billing_country"), BILLING_EMAIL("billing_email"), BILLING_NAME("billing_name"), BILLING_TELEPHONE("billing_tel"), BILLING_ZIP(
                        "billing_zip"), BILLING_STATE("billing_state"), BILLING_ADDRESS("billing_address"), STATUS_CODE("status_code"), STATUS_MESSAGE(
                        "status_message"), FAILURE_MESSAGE("failure_message"), MERCHANT_PARAM_1("merchant_param1"), MERCHANT_PARAM_2(
                        "merchant_param2"), MERCHANT_PARAM_3("merchant_param3"), MERCHANT_PARAM_4("merchant_param4"), MERCHANT_PARAM_5(
                        "merchant_param5"), DISCOUNT_VALUE("discount_value"), OFFER_TYPE("offer_type"), OFFER_CODE("offer_code"), VAULT(
                        "vault"), STATUS("status");

                private final String value;

                private ResponseParameters(final String value) {
                    this.value = value;
                }

                public String getValue() {
                    return value;
                }
            }

            public enum RequestParameters {

                CANCEL_URL("cancel_url"), REDIRECT_URL("redirect_url"), AMOUNT("amount"), ORDER_ID("order_id"), CURRENCY("currency"), LANGUAGE(
                        "language"), MERCHANT_ID("merchant_id"), BILLING_CITY("billing_city"), BILLING_COUNTRY("billing_country"), BILLING_EMAIL(
                        "billing_email"), BILLING_NAME("billing_name"), BILLING_TELEPHONE("billing_tel"), BILLING_ZIP("billing_zip"), BILLING_STATE(
                        "billing_state"), BILLING_ADDRESS("billing_address"), DELIVERY_CITY("delivery_city"), DELIVERY_COUNTRY(
                        "delivery_country"), DELIVERY_NAME("delivery_name"), DELIVERY_TELEPHONE("delivery_tel"), DELIVERY_ZIP(
                        "delivery_zip"), DELIVERY_STATE("delivery_state"), DELIVERY_ADDRESS("delivery_address"), ENCRYPTED_REQUEST(
                        "encRequest"), ACCESS_CODE("access_code"), PAYMENT_OPTION("payment_option"), MERCHANT_PARAM_1("merchant_param1"), MERCHANT_PARAM_2(
                        "merchant_param2"), MERCHANT_PARAM_3("merchant_param3"), MERCHANT_PARAM_4("merchant_param4"), MERCHANT_PARAM_5(
                        "merchant_param5");

                private final String value;

                private RequestParameters(final String value) {
                    this.value = value;
                }

                public String getValue() {
                    return value;
                }
            }

        }
    }

}
