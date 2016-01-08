/**
 * 
 */
package in.com.v2kart.ecomlspintegration.data.response;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * @author arunkumar
 * 
 */
@XmlRegistry
public class ObjectFactory {
    private final static QName _EcomexpressObjects_QNAME = new QName("", "ecomexpress-objects");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated
     * 
     */
    public ObjectFactory() {
        // TODO
    }

    /**
     * Create an instance of {@link EcomexpressObjectsType }
     * 
     */
    public EcomexpressObjectsType createEcomexpressObjectsType() {
        return new EcomexpressObjectsType();
    }

    /**
     * Create an instance of {@link FieldType }
     * 
     */
    public FieldType createFieldType() {
        return new FieldType();
    }

    /**
     * Create an instance of {@link ObjectType }
     * 
     */
    public ObjectType createObjectType() {
        return new ObjectType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EcomexpressObjectsType }{@code >}
     * 
     */
    @XmlElementDecl(namespace = "", name = "ecomexpress-objects")
    public JAXBElement<EcomexpressObjectsType> createEcomexpressObjects(final EcomexpressObjectsType value) {
        return new JAXBElement<EcomexpressObjectsType>(_EcomexpressObjects_QNAME, EcomexpressObjectsType.class, null, value);
    }
}
