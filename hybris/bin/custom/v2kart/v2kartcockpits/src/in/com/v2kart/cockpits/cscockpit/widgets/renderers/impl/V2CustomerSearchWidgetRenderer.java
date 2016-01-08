package in.com.v2kart.cockpits.cscockpit.widgets.renderers.impl;

import in.com.v2kart.cockpits.cscockpit.services.search.generic.query.V2CustomerSearchQueryBuilder.TextField;

import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.api.HtmlBasedComponent;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.impl.InputElement;

import de.hybris.platform.cockpit.widgets.ListboxWidget;
import de.hybris.platform.cscockpit.services.search.impl.DefaultCsTextSearchCommand;
import de.hybris.platform.cscockpit.widgets.controllers.search.SearchCommandController;
import de.hybris.platform.cscockpit.widgets.models.impl.TextSearchWidgetModel;
import de.hybris.platform.cscockpit.widgets.renderers.impl.AbstractSearcherWidgetRenderer;
import de.hybris.platform.cscockpit.widgets.renderers.impl.CustomerSearchWidgetRenderer;

/**
 * Class overridden for changing the customer search widget. Including fields email and mobile number.
 * 
 * @author busamkumar
 *
 */
public class V2CustomerSearchWidgetRenderer extends CustomerSearchWidgetRenderer {

    private static final String COCKPIT_ID_SEARCHFORCUSTOMER_CUSTOMEREMAILID_INPUT = "SearchForCustomer_CustomerEmailid_input";
    private static final String COCKPIT_ID_SEARCHFORCUSTOMER_CUSTOMERMOBILENO_INPUT = "SearchForCustomer_CustomerMobileno_input";

    @Override
    protected HtmlBasedComponent createSearchPane(
            ListboxWidget<TextSearchWidgetModel, SearchCommandController<DefaultCsTextSearchCommand>> widget) {

        Div searchPane = new Div();
        Div row1 = new Div();
        Div row2 = new Div();
        row1.setParent(searchPane);
        row2.setParent(searchPane);
        row2.setStyle("padding-bottom:5px;padding-left:41px;");

        Textbox searchNameInput = createNameField(widget, row1);
        Textbox searchPostalcodeInput = createPostcodeField(widget, row1);
        Textbox searchEmailIdInput = createEmailIdField(widget, row2);
        Textbox searchMobileNoInput = createMobileNoField(widget, row2);
        Button searchBtn = createSearchButton(widget, row2);

        attachSearchEventListener(widget,
                createSearchEventListener(widget, searchNameInput, searchPostalcodeInput, searchEmailIdInput, searchMobileNoInput),
                new AbstractComponent[] {
                        searchNameInput,
                        searchPostalcodeInput, searchEmailIdInput, searchMobileNoInput, searchBtn });

        return searchPane;
    }

    protected EventListener createSearchEventListener(
            ListboxWidget<TextSearchWidgetModel, SearchCommandController<DefaultCsTextSearchCommand>> widget, Textbox nameInput,
            Textbox postalcodeInput, Textbox emailIdInput, Textbox mobileNoInput) {
        return new SearchEventListener(widget, nameInput, postalcodeInput, emailIdInput, mobileNoInput);
    }

    protected Textbox createEmailIdField(ListboxWidget widget, Div parent) {
        return createSearchTextField(widget, parent, "email", TextField.Emailid, COCKPIT_ID_SEARCHFORCUSTOMER_CUSTOMEREMAILID_INPUT);
    }

    protected Textbox createMobileNoField(ListboxWidget widget, Div parent) {
        return createSearchTextField(widget, parent, "mobileNo", TextField.Mobileno, COCKPIT_ID_SEARCHFORCUSTOMER_CUSTOMERMOBILENO_INPUT);
    }

    protected class SearchEventListener extends AbstractSearcherWidgetRenderer.AbstractSearchEventListener {

        private final transient InputElement customerNameInput;
        private final transient InputElement customerPostalcodeInput;
        private final transient InputElement customerEmailIdInput;
        private final transient InputElement customerMobileNoInput;

        protected SearchEventListener(ListboxWidget widget, InputElement customerNameInput, InputElement customerPostalcodeInput,
                InputElement customerEmailIdInput, InputElement customerMobileNoInput) {
            super(widget);
            this.customerNameInput = customerNameInput;
            this.customerPostalcodeInput = customerPostalcodeInput;
            this.customerEmailIdInput = customerEmailIdInput;
            this.customerMobileNoInput = customerMobileNoInput;
        }

        @Override
        protected void fillSearchCommand(DefaultCsTextSearchCommand command) {

            if (customerNameInput != null) {
                command.setText(TextField.Name, customerNameInput.getText());
            }
            if (customerPostalcodeInput != null) {
                command.setText(TextField.Postalcode, customerPostalcodeInput.getText());
            }
            if (customerEmailIdInput != null) {
                command.setText(TextField.Emailid, customerEmailIdInput.getText());
            }
            if (customerMobileNoInput != null) {
                command.setText(TextField.Mobileno, customerMobileNoInput.getText());
            }
        }
    }

}
