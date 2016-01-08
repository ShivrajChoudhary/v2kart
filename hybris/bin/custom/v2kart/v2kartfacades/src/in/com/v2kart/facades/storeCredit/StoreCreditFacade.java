package in.com.v2kart.facades.storeCredit;

import de.hybris.platform.commercefacades.order.data.CartData;

/**
 * The {@code StoreCreditFacade} class is the facade in front of the store credit service.
 *
 * @author Nagarro_Devraj
 * @since 1.2
 */
public interface StoreCreditFacade {

    /**
     * Query the balance of the store credit.
     *
     * @return The store credit balance.
     */
    public Double queryBalance();
    
} // interface
