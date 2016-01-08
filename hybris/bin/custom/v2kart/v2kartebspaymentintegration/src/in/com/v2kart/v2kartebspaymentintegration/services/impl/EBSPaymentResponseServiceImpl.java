/**
 * 
 */
package in.com.v2kart.v2kartebspaymentintegration.services.impl;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.util.Base64;

import in.com.v2kart.core.payment.services.PaymentResponseService;
import in.com.v2kart.v2kartebspaymentintegration.constants.PaymentConstants.PaymentProperties.EBS;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.bouncycastle.crypto.engines.RC4Engine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.springframework.beans.factory.annotation.Required;

/**
 * @author vikrant2480
 * 
 */
public class EBSPaymentResponseServiceImpl implements PaymentResponseService {

    private SiteConfigService siteConfigService;

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.core.payment.services.PaymentResponseService#getRequestParameterMap(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public Map<String, String> getRequestParameterMap(final HttpServletRequest request) throws Exception {
        final RC4Engine rc4 = new RC4Engine();
        rc4.init(true, new KeyParameter(siteConfigService.getProperty(EBS.HOP_POST_SECURE_KEY).getBytes()));

        final StringBuffer responseEncodedData = new StringBuffer(request.getParameter(EBS.HOP_RESPONSE_DATA_PARAMETER));
        for (int i = 0; i < responseEncodedData.length(); i++) {
            if (responseEncodedData.charAt(i) == ' ') {
                responseEncodedData.setCharAt(i, '+');
            }
        }

        final byte[] responseDecodedData = Base64.decode(responseEncodedData.toString());
        final byte[] result = new byte[responseDecodedData.length];
        rc4.processBytes(responseDecodedData, 0, responseDecodedData.length, result, 0);

        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(result, 0, result.length);
        final DataInputStream inputStream = new DataInputStream(byteArrayInputStream);
        String recvString1 = "";
        String recvString = "";
        recvString1 = inputStream.readLine();

        int i = 0;
        while (recvString1 != null) {
            i++;
            if (i > 705) {
                break;
            }
            recvString += recvString1 + "\n";
            recvString1 = inputStream.readLine();
        }
        recvString = recvString.replace("=&", "=--&");
        final Map<String, String> map = new HashMap<String, String>();
        final StringTokenizer st = new StringTokenizer(recvString, "=&");
        while (st.hasMoreTokens()) {
            map.put(st.nextToken(), st.nextToken());
        }
        return map;
    }

    /**
     * @param siteConfigService
     *        the siteConfigService to set
     */
    @Required
    public void setSiteConfigService(final SiteConfigService siteConfigService) {
        this.siteConfigService = siteConfigService;
    }

}
