/**
 * 
 */
package in.com.v2kart.v2kartpayupaymentintegration.services;

import in.com.v2kart.v2kartpayupaymentintegration.data.response.PayUAbstractCommandResponse;

import java.util.Map;

/**
 * @author pankajaggarwal
 * 
 */
public interface PayUCommand {

    PayUAbstractCommandResponse executeCommand(Map<String, String> params);
}
