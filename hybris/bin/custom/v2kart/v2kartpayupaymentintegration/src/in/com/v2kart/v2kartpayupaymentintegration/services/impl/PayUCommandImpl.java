/**
 * 
 */
package in.com.v2kart.v2kartpayupaymentintegration.services.impl;

import in.com.v2kart.v2kartpayupaymentintegration.constants.PaymentConstants.PaymentProperties.PAYU;
import in.com.v2kart.v2kartpayupaymentintegration.data.response.PayUAbstractCommandResponse;
import in.com.v2kart.v2kartpayupaymentintegration.services.PayUCommand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * @author pankajaggarwal
 * 
 */
public class PayUCommandImpl implements PayUCommand {

    private static final Logger LOG = Logger.getLogger(PayUCommandImpl.class);

    private Class payUCommandResponseClass;

    public Class<PayUAbstractCommandResponse> getPayUCommandResponseClass() {
        return payUCommandResponseClass;
    }

    public void setPayUCommandResponseClass(final Class<PayUAbstractCommandResponse> payUCommandResponseClass) {
        this.payUCommandResponseClass = payUCommandResponseClass;
    }

    private String postUrl;

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(final String postUrl) {
        this.postUrl = postUrl;
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.v2kartpayupaymentintegration.services.PayUCommand#executeCommand(java.util.Map)
     */
    @Override
    public PayUAbstractCommandResponse executeCommand(final Map<String, String> params) {
        final HttpClient client = new DefaultHttpClient();
        final HttpPost post = new HttpPost(postUrl);
        try {
            final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(params.size());
            if (MapUtils.isNotEmpty(params)) {
                for (final java.util.Map.Entry<String, String> entry : params.entrySet()) {
                    nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
            }
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            final HttpResponse httpResponse = client.execute(post);
            final String response = EntityUtils.toString(httpResponse.getEntity());
            return parseJsonToCommandResponse(response);
        } catch (IOException | ParseException | JsonSyntaxException e) {
            LOG.error("Error on execute command with name " + PAYU.RequestParameters.COMMAND.getValue() + " :" + e.getMessage());
            if (LOG.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * @param response
     * @return
     */
    private PayUAbstractCommandResponse parseJsonToCommandResponse(final String response) {
        return new Gson().fromJson(response.toString(), payUCommandResponseClass);
    }

}
