package in.com.v2kart.core.services;

import java.util.List;

import de.hybris.platform.ordersplitting.model.ConsignmentModel;

/**
 * Interface for LSP Integration.
 * @author vikrant2480
 *
 */
public interface LSPTrackerService {
    
    /**
     * Update the consignment statuses
     * @param consignments
     */
    void updateOrderConsignmentStatus(final List<ConsignmentModel> consignments);
    
}
