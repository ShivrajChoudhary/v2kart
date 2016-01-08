package in.com.v2kart.sapinboundintegration.order.daos;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.daos.OrderDao;

import java.util.List;

/**
 * @author satvir_nagarro
 *
 */
public interface V2OrderDao extends OrderDao {

    /**
     *
     * @return
     */
    List<OrderModel> findSalesFailureOrders();

    OrderModel findOrderByCode(final String code);

    List<OrderModel> findAllTypeOfFailureOrders();

}
