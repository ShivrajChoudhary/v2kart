/**
 * 
 */
package in.com.v2kart.ecomlspintegration.data.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author arunkumar
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "responseObject" })
@XmlRootElement(name = "ecomexpress-objects")
public class EcomOrderTrackerResponse {

    @XmlElement(name = "ecomexpress-objects")
    private EcomexpressObject responseObject;

    public EcomexpressObject getResponseObject() {
        return responseObject;
    }

    public void setResponseObject(final EcomexpressObject responseObject) {
        this.responseObject = responseObject;
    }

}
