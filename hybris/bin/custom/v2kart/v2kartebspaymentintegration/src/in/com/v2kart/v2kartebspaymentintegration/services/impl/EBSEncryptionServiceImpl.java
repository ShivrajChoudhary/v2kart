/**
 * 
 */
package in.com.v2kart.v2kartebspaymentintegration.services.impl;

import in.com.v2kart.core.exception.V2Exception;
import in.com.v2kart.core.payment.services.V2EncryptionService;
import in.com.v2kart.v2kartebspaymentintegration.constants.PaymentConstants.PaymentProperties.EBS;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author vikrant2480
 * 
 */
public class EBSEncryptionServiceImpl implements V2EncryptionService {

    private static final Logger LOG = Logger.getLogger(EBSEncryptionServiceImpl.class);

    private static final String HASH_SEPERATOR = "|";

    private static final List<String> requestHashParams = Arrays.asList(EBS.RequestParameters.KEY.getValue(),
            EBS.RequestParameters.ACCOUNT_ID.getValue(), EBS.RequestParameters.AMOUNT.getValue(),
            EBS.RequestParameters.REFERENCE_NUMBER.getValue(), EBS.RequestParameters.RETURN_URL.getValue(),
            EBS.RequestParameters.MODE.getValue());

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.core.payment.services.V2EncryptionService#getRequestHash(java.util.Map)
     */
    @Override
    public String getRequestHash(final Map<String, String> parameters) throws V2Exception {
        if (!areValidRequestValues(parameters)) {
            throw new V2Exception("Some parameters for hash generation are missing.");
        }

        return generateHash(parameters, requestHashParams);
    }

    private boolean areValidRequestValues(final Map<String, String> parameters) {
        boolean isValid = true;
        if (parameters.size() > 0) {
            if (isEmpty(parameters.get(EBS.RequestParameters.KEY.getValue()))
                    || isEmpty(parameters.get(EBS.RequestParameters.ACCOUNT_ID.getValue()))
                    || isEmpty(parameters.get(EBS.RequestParameters.AMOUNT.getValue()))
                    || isEmpty(parameters.get(EBS.RequestParameters.REFERENCE_NUMBER.getValue()))
                    || isEmpty(parameters.get(EBS.RequestParameters.RETURN_URL.getValue()))
                    || isEmpty(parameters.get(EBS.RequestParameters.MODE.getValue()))) {
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
                hash = calculateHash(generatedString.substring(0, generatedString.lastIndexOf(HASH_SEPERATOR)), EBS.HASH_ALGORITHM);
            } catch (final NoSuchAlgorithmException e) {
                throw new V2Exception("Error hashing the parameters", e);
            }

        }
        return hash;
    }

    private boolean isEmpty(final String value) {
        if (value == null || value.trim().equals("")) {
            return true;
        } else {
            return false;
        }
    }

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
     * @see in.com.v2kart.core.payment.services.V2EncryptionService#getResponseHash(java.util.Map)
     */
    @Override
    public String getResponseHash(final Map<String, String> parameters) throws V2Exception {
        // YTODO Auto-generated method stub
        return null;
    }

}
