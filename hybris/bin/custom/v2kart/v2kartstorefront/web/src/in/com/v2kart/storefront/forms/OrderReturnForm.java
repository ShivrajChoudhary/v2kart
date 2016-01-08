package in.com.v2kart.storefront.forms;

import in.com.v2kart.facades.order.data.ReturnEntryData;

import java.util.ArrayList;
import java.util.List;

/**
 * Form for Order Return
 * 
 * @author shailjagupta
 *
 */
public class OrderReturnForm extends OrderModificationForm {

    // Action to be taken
    private String action;

    // Entries to be returned
    private List<ReturnEntryData> returnableEntries = new ArrayList<ReturnEntryData>();

    public List<ReturnEntryData> getReturnableEntries() {
        return returnableEntries;
    }

    public void setReturnableEntries(List<ReturnEntryData> returnableEntries) {
        this.returnableEntries = returnableEntries;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

   

}
