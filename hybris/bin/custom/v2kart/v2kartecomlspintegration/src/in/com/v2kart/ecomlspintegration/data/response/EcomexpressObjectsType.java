/**
 * 
 */
package in.com.v2kart.ecomlspintegration.data.response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author arunkumar
 * 
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ecomexpress-objectsType", propOrder = { "object" })
@XmlRootElement(name = "ecomexpress-objects")
public class EcomexpressObjectsType {

    @XmlElement(required = true)
    protected List<ObjectType> object;
    @XmlAttribute(name = "version")
    protected BigDecimal version;

    /**
     * Gets the value of the object property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list
     * will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the object property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getObject().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list {@link ObjectType }
     * 
     * 
     */
    public List<ObjectType> getObject() {
        if (object == null) {
            object = new ArrayList<ObjectType>();
        }
        return this.object;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return possible object is {@link BigDecimal }
     * 
     */
    public BigDecimal getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *        allowed object is {@link BigDecimal }
     * 
     */
    public void setVersion(final BigDecimal value) {
        this.version = value;
    }

}
