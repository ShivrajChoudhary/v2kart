/**
 *
 */
package in.com.v2kart.v2kartpayupaymentintegration.constants;

/**
 * The Class PaymentConstants.
 * 
 * @author gaurav2007
 */
public class PaymentConstants {

	public interface PaymentProperties {

		public interface PAYU {
			String HOP_POST_SUCCESS_URL = "payment.payu.success.url";
			String HOP_POST_FAILURE_URL = "payment.payu.failure.url";
			String HOP_POST_CANCEL_URL = "payment.payu.cancel.url";
			String HOP_POST_REQUEST_URL = "hop.post.payu.url";
			String PAYMENT_MODE = "PAYU";
			String HOP_PAYMENT_MERCHANT_KEY = "payment.payu.merchant.id";
			String HOP_PAYMENT_MERCHANT_SALT = "payment.payu.salt";
			String HASH_ALGORITHM = "SHA-512";
			String FIELD_SEPERATOR = ";";
			String RESPONSE_SUCCESS_VALUE = "CAPTURED";
			String VERBOSE = "payment.payu.verbose";
			String PG_CC = "CC";
			String PG_EMI = "emi";
			String PAYMENT_GATEWAY = "PAYU";

			public enum Commands {
				CANCEL_REFUND_TRANSACTIONS("cancel_refund_transaction"), GET_TRANSACTIONS_DETAILS(
						"get_Transaction_Details");

				private final String value;

				private Commands(final String value) {
					this.value = value;
				}

				public String getValue() {
					return value;
				}
			}

			public enum ResponseParameters {

				ADD_ON("addedon"), BANK_CODE("bankcode"), BANK_REFERENCE_NUMBER(
						"bank_ref_num"), CARD_NUMBER("cardnum"), DISCOUNT(
						"discount"), ERROR("error"), ERROR_MESSAGE(
						"error_Message"), MIH_PAY_ID("mihpayid"), MODE("mode"), NAME_ON_CARD(
						"name_on_card"), NET_AMOUNT_DEBIT("net_amount_debit"), PAYMENT_SOURCE(
						"payment_source"), PG_TYPE("PG_TYPE"), PHONE("phone"), UNMAPPED_STATUS(
						"unmappedstatus"), KEY("key"), PRODUCT_INFO(
						"productinfo"), FIRST_NAME("firstname"), EMAIL("email"), TRANSACTION_ID(
						"txnid"), AMOUNT("amount"), SALT("salt"), UDF_1("udf1"), UDF_2(
						"udf2"), UDF_3("udf3"), UDF_4("udf4"), UDF_5("udf5"), UDF_6(
						"udf6"), UDF_7("udf7"), UDF_8("udf8"), UDF_9("udf9"), UDF_10(
						"udf10"), STATUS("status"), HASH("hash");

				private final String value;

				private ResponseParameters(final String value) {
					this.value = value;
				}

				public String getValue() {
					return value;
				}
			}

			public enum RequestParameters {

				KEY("key"), PRODUCT_INFO("productinfo"), FIRST_NAME("firstname"), EMAIL(
						"email"), PHONE("phone"), TRANSACTION_ID("txnid"), SUCCESS_URL(
						"surl"), CANCEL_URL("curl"), FAILURE_URL("furl"), AMOUNT(
						"amount"), ENFORCE_PAYMENT_METHOD("enforce_paymethod"), SALT(
						"salt"), PG("pg"), BANK_CODE("bankcode"), UDF_1("udf1"), UDF_2(
						"udf2"), UDF_3("udf3"), UDF_4("udf4"), UDF_5("udf5"), UDF_6(
						"udf6"), UDF_7("udf7"), UDF_8("udf8"), UDF_9("udf9"), UDF_10(
						"udf10"), HASH("hash"), COMMAND("command"), VAR1("var1"), VAR2(
						"var2"), VAR3("var3");

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
