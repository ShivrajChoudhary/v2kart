/**
 * 
 */
package in.com.v2kart.ecomlspintegration.data.response;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author arunkumar
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "objectType", propOrder = { "field" })
public class ObjectType {
    @XmlElement(name = "field")
    protected List<FieldType> field;
    @XmlAttribute(name = "pk")
    protected Integer pk;
    @XmlAttribute(name = "model")
    protected String model;

    /**
     * Gets the value of the field property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list
     * will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the field property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getField().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list {@link FieldType }
     * 
     * 
     */
    public List<FieldType> getField() {
        if (field == null) {
            field = new ArrayList<FieldType>();
        }
        return this.field;
    }

    /**
     * Gets the value of the pk property.
     * 
     * @return possible object is {@link Integer }
     * 
     */
    public Integer getPk() {
        return pk;
    }

    /**
     * Sets the value of the pk property.
     * 
     * @param value
     *        allowed object is {@link Integer }
     * 
     */
    public void setPk(final Integer value) {
        this.pk = value;
    }

    /**
     * Gets the value of the model property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getModel() {
        return model;
    }

    /**
     * Sets the value of the model property.
     * 
     * @param value
     *        allowed object is {@link String }
     * 
     */
    public void setModel(final String value) {
        this.model = value;
    }
}
