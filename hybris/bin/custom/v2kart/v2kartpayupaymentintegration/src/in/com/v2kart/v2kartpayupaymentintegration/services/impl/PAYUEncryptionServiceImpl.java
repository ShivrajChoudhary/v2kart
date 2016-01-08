/**
 *
 */
package in.com.v2kart.v2kartpayupaymentintegration.services.impl;

import in.com.v2kart.core.exception.V2Exception;
import in.com.v2kart.v2kartpayupaymentintegration.constants.PaymentConstants.PaymentProperties.PAYU;
import in.com.v2kart.v2kartpayupaymentintegration.services.PAYUEncryptionService;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * The Class PAYUEncryptionServiceImpl.
 * 
 * @author gaurav2007
 */
public class PAYUEncryptionServiceImpl implements PAYUEncryptionService {

    private static final Logger LOG = Logger.getLogger(PAYUEncryptionServiceImpl.class);

    private static final String HASH_SEPERATOR = "|";

    private static final List<String> requestHashParamsForCommandExecution = Arrays.asList(PAYU.RequestParameters.KEY.getValue(),
            PAYU.RequestParameters.COMMAND.getValue(), PAYU.RequestParameters.VAR1.getValue(), PAYU.RequestParameters.SALT.getValue());

    private static final List<String> requestHashParams = Arrays.asList(PAYU.RequestParameters.KEY.getValue(),
            PAYU.RequestParameters.TRANSACTION_ID.getValue(), PAYU.RequestParameters.AMOUNT.getValue(),
            PAYU.RequestParameters.PRODUCT_INFO.getValue(), PAYU.RequestParameters.FIRST_NAME.getValue(),
            PAYU.RequestParameters.EMAIL.getValue(), PAYU.RequestParameters.UDF_1.getValue(), PAYU.RequestParameters.UDF_2.getValue(),
            PAYU.RequestParameters.UDF_3.getValue(), PAYU.RequestParameters.UDF_4.getValue(), PAYU.RequestParameters.UDF_5.getValue(),
            PAYU.RequestParameters.UDF_6.getValue(), PAYU.RequestParameters.UDF_7.getValue(), PAYU.RequestParameters.UDF_8.getValue(),
            PAYU.RequestParameters.UDF_9.getValue(), PAYU.RequestParameters.UDF_10.getValue(), PAYU.RequestParameters.SALT.getValue());

    private static final List<String> reponseHashParams = Arrays.asList(PAYU.ResponseParameters.SALT.getValue(),
            PAYU.ResponseParameters.STATUS.getValue(), PAYU.ResponseParameters.UDF_10.getValue(), PAYU.ResponseParameters.UDF_9.getValue(),
            PAYU.ResponseParameters.UDF_8.getValue(), PAYU.ResponseParameters.UDF_7.getValue(), PAYU.ResponseParameters.UDF_6.getValue(),
            PAYU.ResponseParameters.UDF_5.getValue(), PAYU.ResponseParameters.UDF_4.getValue(), PAYU.ResponseParameters.UDF_3.getValue(),
            PAYU.ResponseParameters.UDF_2.getValue(), PAYU.ResponseParameters.UDF_1.getValue(), PAYU.ResponseParameters.EMAIL.getValue(),
            PAYU.ResponseParameters.FIRST_NAME.getValue(), PAYU.ResponseParameters.PRODUCT_INFO.getValue(),
            PAYU.ResponseParameters.AMOUNT.getValue(), PAYU.ResponseParameters.TRANSACTION_ID.getValue(),
            PAYU.ResponseParameters.KEY.getValue());

    @Override
    public String getRequestHash(final Map<String, String> parameters) throws V2Exception {
        if (!areValidRequestValues(parameters)) {
            throw new V2Exception("Some parameters for hash generation are missing.");
        }

        /*
         * Iterable<String> orderDetailsMap = Iterables.transform(Arrays.asList(PAYU.ResponseParameters.values()), new
         * Function<PAYU.ResponseParameters, String>() {
         * 
         * @Override public String apply(final PAYU.ResponseParameters from) { return from.getValue(); } });
         */
        return generateHash(parameters, requestHashParams);
    }

    /**
     * Are valid request values.
     * 
     * @param parameters
     *        the parameters
     * @return true, if successful
     */
    private boolean areValidRequestValues(final Map<String, String> parameters) {
        boolean isValid = true;
        if (parameters.size() > 0) {
            if (isEmpty(parameters.get(PAYU.RequestParameters.KEY.getValue()))
                    || isEmpty(parameters.get(PAYU.RequestParameters.TRANSACTION_ID.getValue()))
                    || isEmpty(parameters.get(PAYU.RequestParameters.AMOUNT.getValue()))
                    || isEmpty(parameters.get(PAYU.RequestParameters.FIRST_NAME.getValue()))
                    || isEmpty(parameters.get(PAYU.RequestParameters.EMAIL.getValue()))
                    || isEmpty(parameters.get(PAYU.RequestParameters.PHONE.getValue()))
                    || isEmpty(parameters.get(PAYU.RequestParameters.PRODUCT_INFO.getValue()))
                    || isEmpty(parameters.get(PAYU.RequestParameters.SUCCESS_URL.getValue()))
                    || isEmpty(parameters.get(PAYU.RequestParameters.FAILURE_URL.getValue()))) {
                isValid = false;
            }
        }
        return isValid;
    }

    @Override
    public String getResponseHash(final Map<String, String> parameters) throws V2Exception {
        if (!areValidResponseValues(parameters)) {
            throw new V2Exception("Some parameters for hash generation are missing.");
        }
        return generateHash(parameters, reponseHashParams);
    }

    /**
     * Are valid response values.
     * 
     * @param parameters
     *        the parameters
     * @return true, if successful
     */
    private boolean areValidResponseValues(final Map<String, String> parameters) {
        boolean isValid = true;
        if (parameters.size() > 0) {
            if (isEmpty(parameters.get(PAYU.ResponseParameters.KEY.getValue()))
                    || isEmpty(parameters.get(PAYU.ResponseParameters.TRANSACTION_ID.getValue()))
                    || isEmpty(parameters.get(PAYU.ResponseParameters.AMOUNT.getValue()))
                    || isEmpty(parameters.get(PAYU.ResponseParameters.FIRST_NAME.getValue()))
                    || isEmpty(parameters.get(PAYU.ResponseParameters.EMAIL.getValue()))
                    || isEmpty(parameters.get(PAYU.ResponseParameters.PHONE.getValue()))
                    || isEmpty(parameters.get(PAYU.ResponseParameters.PRODUCT_INFO.getValue()))
                    || isEmpty(parameters.get(PAYU.ResponseParameters.STATUS.getValue()))
                    || isEmpty(parameters.get(PAYU.ResponseParameters.MIH_PAY_ID.getValue()))) {
                isValid = false;
            }
        }
        return isValid;
    }

    private String generateHash(final Map<String, String> parameters, final List<String> hashParameters) throws V2Exception {
        final StringBuilder hashString = new StringBuilder();
        String hash = null;
        if (parameters.size() > 0) {
            for (final String part : hashParameters) {
                if (!(isEmpty(parameters.get(part)))) {
                    hashString.append(parameters.get(part));
                }
                hashString.append(HASH_SEPERATOR);
            }
            final String generatedString = hashString.toString();
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Hash String: " + generatedString.substring(0, generatedString.lastIndexOf(HASH_SEPERATOR)));
                }
                hash = calculateHash(generatedString.substring(0, generatedString.lastIndexOf(HASH_SEPERATOR)), PAYU.HASH_ALGORITHM);
            } catch (final NoSuchAlgorithmException e) {
                throw new V2Exception("Error hashing the parameters", e);
            }

        }
        return hash;
    }

    /**
     * Checks if is empty.
     * 
     * @param value
     *        the value
     * @return true, if is empty
     */
    private boolean isEmpty(final String value) {
        if (value == null || value.trim().equals("")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Calculate hash.
     * 
     * @param value
     *        the value
     * @param hashAlgorithm
     *        the hash algorithm
     * @return the hash value
     * @throws NoSuchAlgorithmException
     *         the no such algorithm exception
     */
    private String calculateHash(final String value, final String hashAlgorithm) throws NoSuchAlgorithmException {
        final byte[] hashseq = value.getBytes();
        final StringBuffer hexString = new StringBuffer();
        final MessageDigest algorithm = MessageDigest.getInstance(hashAlgorithm);
        algorithm.reset();
        algorithm.update(hashseq);
        final byte messageDigest[] = algorithm.digest();

        for (int i = 0; i < messageDigest.length; i++) {
            final String hex = Integer.toHexString(0xFF & messageDigest[i]);
            if (hex.length() == 1) {
                hexString.append("0");
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.v2kartpayupaymentintegration.services.PAYUEncryptionService#getRequestHashForCommandExecution(java.util.Map)
     */
    @Override
    public String getRequestHashForCommandExecution(final Map<String, String> parameters) throws V2Exception {

        if (!areValidRequestValuesForCommandExecution(parameters)) {
            throw new V2Exception("Some parameters for hash generation are missing for command execution.");
        }

        return generateHash(parameters, requestHashParamsForCommandExecution);

    }

    /**
     * @param parameters
     * @return
     */
    private boolean areValidRequestValuesForCommandExecution(final Map<String, String> parameters) {
        boolean isValid = true;
        if (parameters.size() > 0) {
            if (isEmpty(parameters.get(PAYU.RequestParameters.KEY.getValue()))
                    || isEmpty(parameters.get(PAYU.RequestParameters.COMMAND.getValue()))
                    || isEmpty(parameters.get(PAYU.RequestParameters.VAR1.getValue()))
                    || isEmpty(parameters.get(PAYU.RequestParameters.SALT.getValue()))) {
                isValid = false;
            }
        }
        return isValid;
    }

}
