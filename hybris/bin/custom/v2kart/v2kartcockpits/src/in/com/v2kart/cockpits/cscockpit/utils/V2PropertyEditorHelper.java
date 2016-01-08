package in.com.v2kart.cockpits.cscockpit.utils;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;

import de.hybris.platform.cockpit.model.editor.EditorHelper;
import de.hybris.platform.cockpit.model.editor.EditorListener;
import de.hybris.platform.cockpit.model.meta.PropertyDescriptor;
import de.hybris.platform.cockpit.services.config.EditorRowConfiguration;
import de.hybris.platform.cockpit.services.values.ObjectValueContainer;
import de.hybris.platform.cockpit.session.UISessionUtils;
import de.hybris.platform.cockpit.session.impl.CreateContext;
import de.hybris.platform.cockpit.widgets.Widget;
import de.hybris.platform.cscockpit.utils.CssUtils;
import de.hybris.platform.cscockpit.utils.PropertyEditorHelper;


/**
 * This class is used to process the custom parameter in the widgets xml files of cscockpit. The custom parameters
 * like to show optional is true or false if true than show * although it is OOTB but for that we need the attribute optional
 * modifier to be false. We can also provide customLabel to the editors using parameter tag in widgets xml files. Currently 2 new
 * parameters are added optional and customLabelName.
 * 
 * @author busamkumar
 *
 */
public class V2PropertyEditorHelper extends PropertyEditorHelper {
    
    private static final String CSS_EDITOR_WIDGET_EDITOR_LABEL = "editorWidgetEditorLabel";
    private static final String CSS_MANDATORY = "mandatory";
    private static final String CUSTOMLABELNAME = "customLabelName";
    private static final String OPTIONAL = "optional";

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.cscockpit.utils.PropertyEditorHelper#renderEditor(org.zkoss.zk.ui.HtmlBasedComponent,
     * de.hybris.platform.cockpit.services.config.EditorRowConfiguration, de.hybris.platform.cockpit.services.values.ObjectValueContainer,
     * de.hybris.platform.cockpit.model.meta.PropertyDescriptor, de.hybris.platform.cockpit.widgets.Widget, java.lang.String,
     * de.hybris.platform.cockpit.model.editor.EditorListener, boolean, boolean)
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void renderEditor(HtmlBasedComponent parent, EditorRowConfiguration rowConfig, ObjectValueContainer currentObjectValues,
            PropertyDescriptor propertyDescriptor, Widget widget, String isoCode, EditorListener listener, boolean includePropertyName,
            boolean forceNotMandatory) {
        if (rowConfig.isVisible()) {
            Map<String, String> rowConfigParameters = rowConfig.getParameters();
            Hbox hbox = new Hbox();
            hbox.setParent(parent);
            hbox.setWidth("96%");
            if (includePropertyName) {
                Label propLabel;
                // if customLabelName is provided than use its value to display label name else display using propertyRenderHelper which
                // displays the label name from property file
                if (null != rowConfigParameters && rowConfigParameters.size() > 0 && rowConfigParameters.containsKey(CUSTOMLABELNAME)) {
                    propLabel = new Label(rowConfigParameters.get(CUSTOMLABELNAME));
                } else {
                    propLabel = new Label(getPropertyRendererHelper().getPropertyDescriptorName(propertyDescriptor));
                }
                propLabel.setParent(hbox);
                propLabel.setSclass(CSS_EDITOR_WIDGET_EDITOR_LABEL);
                propLabel.setTooltiptext(propertyDescriptor.getQualifier());
                if (!forceNotMandatory && getCockpitPropertyService().isMandatory(propertyDescriptor, true)) {
                    propLabel.setValue((new StringBuilder(String.valueOf(propLabel.getValue()))).append("*").toString());
                    propLabel.setSclass(CssUtils.combine(new String[] { propLabel.getSclass(), CSS_MANDATORY }));
                }
                // check if optional parameter if yes than display the * only if not displayed by above condition
                if (null != rowConfigParameters && rowConfigParameters.size() > 0 && rowConfigParameters.containsKey(OPTIONAL)) {
                    Boolean optional = Boolean.valueOf(rowConfigParameters.get(OPTIONAL));
                    if (!optional.booleanValue() && !propLabel.getSclass().contains(CSS_MANDATORY)) {
                        propLabel.setValue((new StringBuilder(String.valueOf(propLabel.getValue()))).append("*").toString());
                        propLabel.setSclass(CssUtils.combine(new String[] { propLabel.getSclass(), CSS_MANDATORY }));
                    }
                }
            }
            CreateContext ctx = new CreateContext(null, null, propertyDescriptor, null);
            ctx.setExcludedTypes(EditorHelper.parseTemplateCodes(rowConfig.getParameter("excludeCreateTypes"), UISessionUtils
                    .getCurrentSession().getTypeService()));
            ctx.setAllowedTypes(EditorHelper.parseTemplateCodes(rowConfig.getParameter("restrictToCreateTypes"), UISessionUtils
                    .getCurrentSession().getTypeService()));
            Map params = new HashMap<>();
            params.putAll(rowConfig.getParameters());
            params.put("createContext", ctx);
            params.put("eventSource", widget);
            if (propertyDescriptor.isLocalized()) {
                EditorHelper.renderLocalizedEditor(null, propertyDescriptor, hbox, currentObjectValues, true, rowConfig.getEditor(),
                        params, false, listener);
            } else {
                EditorHelper.renderSingleEditor(null, propertyDescriptor, hbox, currentObjectValues, true, rowConfig.getEditor(), params,
                        null, false, listener);
            }
        }
    }

}
