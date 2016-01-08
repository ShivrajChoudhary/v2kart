package in.com.v2kart.core.maps;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import de.hybris.platform.util.Base64;

/**
 * It is used to encrypt the google maps Key to Base64.
 * 
 * @author vikrant2480
 * 
 */
public class V2UrlSigner {

    // This variable stores the binary key, which is computed from the string (Base64) key
    private byte[] key;

    public void setKey(final String keyParam) throws IOException {
        String keyString = keyParam;
        // Convert the key from 'web safe' base 64 to binary
        keyString = keyString.replace('-', '+');
        keyString = keyString.replace('_', '/');
        key = Base64.decode(keyString);
    }

    public String signRequest(final String path, final String query) throws NoSuchAlgorithmException,
            InvalidKeyException {

        // Retrieve the proper URL components to sign
        final String resource = path + '?' + query;

        // Get an HMAC-SHA1 signing key from the raw key bytes
        final SecretKeySpec sha1Key = new SecretKeySpec(key, "HmacSHA1");

        // Get an HMAC-SHA1 Mac instance and initialize it with the HMAC-SHA1 key
        final Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(sha1Key);

        // compute the binary signature for the request
        final byte[] sigBytes = mac.doFinal(resource.getBytes());

        // base 64 encode the binary signature
        String signature = Base64.encodeBytes(sigBytes);

        // convert the signature to 'web safe' base 64
        signature = signature.replace('+', '-');
        signature = signature.replace('/', '_');

        return resource + "&signature=" + signature;
    }
}
