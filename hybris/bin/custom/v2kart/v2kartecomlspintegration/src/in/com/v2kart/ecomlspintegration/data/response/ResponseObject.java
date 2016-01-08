/**
 * 
 */
package in.com.v2kart.ecomlspintegration.data.response;

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
@XmlType(name = "", propOrder = { "field", "model", "pk" })
public class ResponseObject {

    @XmlElement(name = "field")
    private Fields[] field;
    @XmlAttribute(name = "model")
    private String model;
    @XmlAttribute(name = "pk")
    private String pk;

    public Fields[] getField() {
        return field;
    }

    public void setField(final Fields[] field) {
        this.field = field;
    }

    public String getModel() {
        return model;
    }

    public void setModel(final String model) {
        this.model = model;
    }

    public String getPk() {
        return pk;
    }

    public void setPk(final String pk) {
        this.pk = pk;
    }

}
