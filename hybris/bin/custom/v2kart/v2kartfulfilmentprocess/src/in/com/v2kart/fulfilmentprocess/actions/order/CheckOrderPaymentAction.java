package in.com.v2kart.fulfilmentprocess.actions.order;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.action.AbstractAction;
import de.hybris.platform.task.RetryLaterException;

import java.util.HashSet;
import java.util.Set;

/**
 * This action implements payment authorization using {@link CreditCardPaymentInfoModel}. Any other payment model could be implemented here,
 * or in a separate action, if the process flow differs.
 */
public class CheckOrderPaymentAction extends AbstractAction<OrderProcessModel> {

    public enum Transition {
        OK, NOK;

        public static Set<String> getStringValues() {
            final Set<String> res = new HashSet<String>();
            for (final Transition transitions : Transition.values()) {
                res.add(transitions.toString());
            }
            return res;
        }
    }

    @Override
    public Set<String> getTransitions() {
        return Transition.getStringValues();
    }

    @Override
    public final String execute(final OrderProcessModel process) throws RetryLaterException, Exception {
        return executeAction(process).toString();
    }

    public Transition executeAction(final OrderProcessModel process) {
        final OrderModel order = process.getOrder();

        if (order != null) {
                // TODO if for individual payment methods capture is to be checked
                for (final PaymentTransactionModel transaction : order.getPaymentTransactions()) {
                    for (final PaymentTransactionEntryModel entry : transaction.getEntries()) {
                        if (entry.getType().equals(PaymentTransactionType.CAPTURE)
                                && TransactionStatus.ACCEPTED.name().equals(entry.getTransactionStatus())) {
                            order.setStatus(OrderStatus.PAYMENT_CAPTURED);
                            modelService.save(order);
                            return Transition.OK;
                        }
                    }
                }
        }
        return Transition.OK;
    }
}
