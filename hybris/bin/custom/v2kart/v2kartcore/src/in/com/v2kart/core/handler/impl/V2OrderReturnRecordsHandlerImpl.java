/**
 * 
 */
package in.com.v2kart.core.handler.impl;

import de.hybris.platform.basecommerce.enums.OrderModificationEntryStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.refund.model.OrderRefundRecordEntryModel;
import de.hybris.platform.returns.OrderReturnRecordsHandlerException;
import de.hybris.platform.returns.impl.DefaultOrderReturnRecordsHandler;
import de.hybris.platform.returns.model.OrderEntryReturnRecordEntryModel;
import de.hybris.platform.returns.model.OrderReturnRecordEntryModel;
import de.hybris.platform.returns.model.OrderReturnRecordModel;
import de.hybris.platform.returns.model.RefundEntryModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 * <V2OrderReturnRecordsHandlerImpl> does some custom changes for creating OrderReturnRecordEntry.
 * 
 * @author Nagarro_Devraj802
 * @since 1.2
 * 
 */
public class V2OrderReturnRecordsHandlerImpl extends DefaultOrderReturnRecordsHandler {

    /** {@inheritDoc} */
    @Override
    protected OrderReturnRecordEntryModel createRefundRecordEntry(final OrderModel order, final OrderReturnRecordModel returnRecord,
            final OrderHistoryEntryModel snapshot, final List<RefundEntryModel> refunds, final UserModel principal)
            throws OrderReturnRecordsHandlerException {
        returnRecord.setInProgress(false);
        final OrderRefundRecordEntryModel refundRecordEntry = (OrderRefundRecordEntryModel) getModelService().create(
                OrderRefundRecordEntryModel.class);
        refundRecordEntry.setTimestamp(new Date());
        refundRecordEntry.setCode(generateEntryCode(snapshot));
        refundRecordEntry.setOriginalVersion(snapshot);
        refundRecordEntry.setModificationRecord(returnRecord);
        refundRecordEntry.setPrincipal(principal);
        refundRecordEntry.setOwner(returnRecord);
        refundRecordEntry.setStatus(OrderModificationEntryStatus.SUCCESSFULL);

        getModelService().save(refundRecordEntry);

        final List orderEntriesRecords = new ArrayList();

        for (final RefundEntryModel refundEntry : refunds) {
            final OrderEntryReturnRecordEntryModel orderEntryRefundEntry = (OrderEntryReturnRecordEntryModel) getModelService().create(
                    OrderEntryReturnRecordEntryModel.class);
            orderEntryRefundEntry.setCode(refundEntry.getOrderEntry().getPk().toString());
            orderEntryRefundEntry.setExpectedQuantity(Long.valueOf(refundEntry.getExpectedQuantity().longValue()));
            orderEntryRefundEntry.setModificationRecordEntry(refundRecordEntry);
            orderEntryRefundEntry.setOriginalOrderEntry(getOriginalOrderEntry(snapshot, refundEntry));
            getModelService().save(orderEntryRefundEntry);
            orderEntriesRecords.add(orderEntryRefundEntry);
        }

        refundRecordEntry.setOrderEntriesModificationEntries(orderEntriesRecords);

        getModelService().saveAll();

        return refundRecordEntry;
    }
}
