package in.com.v2kart.storefront.forms;

import in.com.v2kart.facades.cancel.data.CancelEntryData;

import java.util.ArrayList;
import java.util.List;

/**
 * form for order cancellation
 * 
 * @author shailja gupta
 *
 */
public class OrderCancellationForm extends OrderModificationForm {

    // entries to be cancelled if partial order cancellation
    private List<CancelEntryData> cancelledEntries = new ArrayList<CancelEntryData>();

    public List<CancelEntryData> getCancelledEntries() {
        return cancelledEntries;
    }

    public void setCancelledEntries(List<CancelEntryData> cancelledEntries) {
        this.cancelledEntries = cancelledEntries;
    }

    public OrderCancellationForm() {
        setCancelledEntries(new ArrayList<CancelEntryData>());
    }

    

   

}
