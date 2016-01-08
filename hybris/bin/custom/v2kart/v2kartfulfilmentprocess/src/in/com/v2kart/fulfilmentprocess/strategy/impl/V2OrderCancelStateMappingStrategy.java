package in.com.v2kart.fulfilmentprocess.strategy.impl;

import java.util.HashMap;
import java.util.Map;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.impl.DefaultOrderCancelStateMappingStrategy;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;

public class V2OrderCancelStateMappingStrategy extends DefaultOrderCancelStateMappingStrategy{
	
	public Map<AbstractOrderEntryModel, Long> getAllCancelableEntries(final OrderModel order) {
        final Map<AbstractOrderEntryModel, Long> uncancelableEntriesMap = new HashMap<AbstractOrderEntryModel, Long>();
        boolean consignmentUnavailableForCancel;
        for (final ConsignmentModel cm : order.getConsignments()) {
            consignmentUnavailableForCancel = isConsignmentUnavailableForCancel(cm.getStatus());
            for (final ConsignmentEntryModel cem : cm.getConsignmentEntries()) {
                if (!consignmentUnavailableForCancel) {
                    mergeEntries(uncancelableEntriesMap, cem.getOrderEntry(), cem.getShippedQuantity() != null ? cem.getShippedQuantity()
                            : Long.valueOf(0));
                }
            }
        }

        final Map<AbstractOrderEntryModel, Long> cancelableEntries = new HashMap<AbstractOrderEntryModel, Long>();
        for (final AbstractOrderEntryModel entry : order.getEntries()) {
            final long totalEntryQuantity = entry.getQuantity().longValue();
            final long uncancelableEntryQuantity;
            if (uncancelableEntriesMap.containsKey(entry)) {
                uncancelableEntryQuantity = ((Long) uncancelableEntriesMap.get(entry)).longValue();
            } else {
                uncancelableEntryQuantity = 0L;
            }

            final long cancelableQuantity = totalEntryQuantity - uncancelableEntryQuantity;
            cancelableEntries.put(entry, Long.valueOf(cancelableQuantity));
        }

        return cancelableEntries;
    }
	
	/**
     * Returns <coe>true</code> if Consignment is Unavailable For Cancel.
     * 
     * @param cmStatus
     * @return true if ConsignmentUnavailableForCancel
     */
    private boolean isConsignmentUnavailableForCancel(final ConsignmentStatus cmStatus) {
        if (ConsignmentStatus.CANCELLED.equals(cmStatus) || ConsignmentStatus.DISPATCHED.equals(cmStatus)
                || ConsignmentStatus.READY_TO_DISPATCH.equals(cmStatus)) {
            return true;
        }

        return false;
    }
    
    private void mergeEntries(final Map<AbstractOrderEntryModel, Long> unavailableEntries, final AbstractOrderEntryModel entry,
            final Long unavailableQuantity) {
        if (unavailableQuantity == null) {
            return;
        }
        final long newUnavailableQuantity;
        if (unavailableEntries.containsKey(entry)) {
            newUnavailableQuantity = (unavailableEntries.get(entry)).longValue() + unavailableQuantity.longValue();
        } else {
            newUnavailableQuantity = unavailableQuantity.longValue();
        }

        unavailableEntries.put(entry, Long.valueOf(newUnavailableQuantity));
    }


}
