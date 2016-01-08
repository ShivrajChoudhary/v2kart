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
@XmlType(name = "EcomexpressObject", propOrder = { "object" })
public class EcomexpressObject {

    @XmlElement(name = "object", nillable = true)
    private ResponseObject[] object;

    @XmlAttribute(name = "version")
    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public ResponseObject[] getObject() {
        return object;
    }

    public void setObject(final ResponseObject[] object) {
        this.object = object;
    }
}
