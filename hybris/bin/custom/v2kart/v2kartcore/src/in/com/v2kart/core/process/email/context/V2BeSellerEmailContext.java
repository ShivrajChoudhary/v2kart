package in.com.v2kart.core.process.email.context;

import in.com.v2kart.facades.core.data.V2SellerData;

import org.apache.velocity.VelocityContext;

/**
 * @author arunkumar
 *
 */
public class V2BeSellerEmailContext extends VelocityContext {
    
    private final V2SellerData sellerData;
    
    /**
     * @param V2SellerData
     */
    public V2BeSellerEmailContext(final V2SellerData sellerData) {
        this.sellerData = sellerData;
    }

    public V2SellerData getSellerData() {
        return sellerData;
    }

   
}
