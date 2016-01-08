package in.com.v2kart.facades.otp;
import java.util.Map;


/**
 * 
 * @author samikshachandra
 *
 */

public interface V2CashOnDeliveryOtp {
    
        
    /**
     * it will authenticate the otp on COD
     * @param otp
     * @return
     */
   public Map <String ,String> autenticateOtp(String otp);
   
 
}
