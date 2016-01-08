/**
 * 
 */
package in.com.v2kart.v2kartebspaymentintegration.constants;

/**
 * @author vikrant2480
 * 
 */
public class PaymentConstants {
    public interface PaymentProperties {
        public interface EBS {
            String HOP_POST_REQUEST_URL = "hop.post.ebs.url";
            String REFUND_URL = "ebs.refund.url";
            String HOP_PAYMENT_ACCOUNT_ID = "payment.ebs.merchant.id";
            String HOP_PAYMENT_REFERENCE_NUMBER = "payment.ebs.reference.number";
            String HOP_PAYMENT_MODE = "payment.ebs.mode";
            String HOP_POST_RETURN_URL = "hop.post.ebs.return.url";
            String FIELD_SEPERATOR = ";";
            String HOP_POST_SECURE_KEY = "hop.post.ebs.secure.key";
            String HASH_ALGORITHM = "MD5";
            String PAYMENT_MODE = "EBS";
            String HOP_RESPONSE_DATA_PARAMETER = "DR";
            String PG_DC = "DC";
            String ACTION_TYPE_REFUND = "refund";
            String ACTION_TYPE_CANCEL = "cancel";
            String PAYMENT_GATEWAY = "EBS";

            public enum RequestParameters {

                ACCOUNT_ID("account_id"), REFERENCE_NUMBER("reference_no"), AMOUNT("amount"), MODE("mode"), DESCRIPTION("description"), RETURN_URL(
                        "return_url"), BILLING_NAME("name"), BILLING_ADDRESS("address"), BILLING_CITY("city"), BILLING_STATE("state"), BILLING_COUNTRY(
                        "country"), BILLING_POSTAL_CODE("postal_code"), BILLING_PHONE("phone"), EMAIL("email"), SHIPPING_NAME("name"), SHIPPING_ADDRESS(
                        "address"), SHIPPING_CITY("city"), SHIPPING_STATE("state"), SHIPPING_COUNTRY("country"), SHIPPING_POSTAL_CODE(
                        "postal_code"), SHIPPING_PHONE("phone"), SECURE_HASH("secure_hash"), KEY("key"), PAYMENT_ID("paymentId");

                private final String value;

                private RequestParameters(final String value) {
                    this.value = value;
                }

                public String getValue() {
                    return value;
                }
            }

            public enum ResponseParameters {

                RESPONSE_CODE("ResponseCode"), RESPONSE_MESSAGE("ResponseMessage"), DATE_CREATED("DateCreated"), PAYMENT_ID("PaymentID"), MERCHANT_REFERENCE_NUMBER(
                        "MerchantRefNo"), AMOUNT("Amount"), MODE("Mode"), BILLING_NAME("BillingName"), BILLING_ADDRESS("BillingAddress"), BILLING_CITY(
                        "BillingCity"), BILLING_STATE("BillingState"), BILLING_POSTAL_CODE("BillingPostalCode"), BILLING_COUNTRY(
                        "BillingCountry"), BILLING_PHONE("BillingPhone"), BILLING_EMAIL("BillingEmail"), TRANSACTION_ID("TransactionID"), IS_FLAGGED(
                        "IsFlagged"), DESCRIPTION("Description"), PAYMENT_METHOD("PaymentMethod");

                private final String value;

                private ResponseParameters(final String value) {
                    this.value = value;
                }

                public String getValue() {
                    return value;
                }
            }

            public enum ActionRequestParameters {

                ACTION("Action"), SECRET_KEY("SecretKey"), PAYMENT_ID("PaymentID"), ACCOUNT_ID("AccountID"), AMOUNT("Amount");

                private final String value;

                private ActionRequestParameters(final String value) {
                    this.value = value;
                }

                public String getValue() {
                    return value;
                }
            }

            public enum ActionResponseParameters {

                PAYMENT_ID("paymentId"), TRANSACTION_ID("transactionId"), AMOUNT("amount"), MODE("mode"),
                REFERENCE_NO("referenceNo"), TRANSACTION_TYPE("transactionType"), STATUS("status"), DATE_TIME("dateTime"), RESPONSE(
                        "response"), ERROR("error");

                private final String value;

                private ActionResponseParameters(final String value) {
                    this.value = value;
                }

                public String getValue() {
                    return value;
                }
            }
        }
    }
}
