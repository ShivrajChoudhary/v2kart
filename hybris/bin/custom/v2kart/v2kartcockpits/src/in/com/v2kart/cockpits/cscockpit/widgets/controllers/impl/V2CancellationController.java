package in.com.v2kart.cockpits.cscockpit.widgets.controllers.impl;

import java.util.List;

import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.services.values.ObjectValueContainer;
import de.hybris.platform.cscockpit.exceptions.ValidationException;
import de.hybris.platform.cscockpit.widgets.controllers.CancellationController;
import de.hybris.platform.ordercancel.OrderCancelException;
import de.hybris.platform.ordercancel.OrderCancelRequest;

/**
 * V2CancellationController that controls the full and partial order cancellation
 *
 * @author pravesh.gupta@nagarro.com
 */
public interface V2CancellationController extends CancellationController {

    /**
     * Creates the full cancellation preview.
     *
     * @param cancelRequest
     *        the cancel request
     * @return the order cancel request
     * @throws ValidationException
     *         the validation exception
     */
    public OrderCancelRequest createFullCancellationPreview(final ObjectValueContainer cancelRequest) throws ValidationException;

    /**
     * Creates the partial cancellation preview.
     *
     * @param orderEntryCancelRecordEntries
     *        the order entry cancel record entries
     * @param cancelRequest
     *        the cancel request
     * @return the order cancel request
     * @throws ValidationException
     *         the validation exception
     * @throws OrderCancelException
     *         the order cancel exception
     */
    public OrderCancelRequest createPartialCancellationPreview(final List<ObjectValueContainer> orderEntryCancelRecordEntries,
            final ObjectValueContainer cancelRequest) throws ValidationException, OrderCancelException;

    Double getCancellationRefundAmount(boolean isFull);

    TypedObject requestFullCancellation() throws OrderCancelException, ValidationException;

    TypedObject requestPartialCancellation() throws OrderCancelException, ValidationException;

    void cleanUp();

}
