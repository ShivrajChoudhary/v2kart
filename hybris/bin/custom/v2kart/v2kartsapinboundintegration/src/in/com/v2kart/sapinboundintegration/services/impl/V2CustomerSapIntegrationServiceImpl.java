/**
 * 
 */
package in.com.v2kart.sapinboundintegration.services.impl;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import in.com.v2kart.sapinboundintegration.constants.V2kartsapinboundintegrationConstants;
import in.com.v2kart.sapinboundintegration.services.V2CustomerSapIntegrationService;
import in.com.v2kart.sapinboundintegration.ws.customer.CustCreateReq;
import in.com.v2kart.sapinboundintegration.ws.customer.CustCreateRes;
import in.com.v2kart.sapinboundintegration.ws.customer.CustUpdateInReq;
import in.com.v2kart.sapinboundintegration.ws.customer.CustUpdateInRes;
import in.com.v2kart.sapinboundintegration.ws.customer.ObjectFactory;

import javax.xml.bind.JAXBElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.ws.WebServiceException;
import org.springframework.ws.client.WebServiceFaultException;
import org.springframework.ws.client.core.WebServiceTemplate;

/**
 * 
 * Integration class for SAP Inbound Integration service like Create Customer, Update Customer
 * 
 * @author satvir_nagarro
 * 
 */
public class V2CustomerSapIntegrationServiceImpl implements V2CustomerSapIntegrationService {

    /** Logger instance */
    private final Logger LOG = LoggerFactory.getLogger(V2CustomerSapIntegrationServiceImpl.class);

    /** WebServiceTemplate bean injection */
    private WebServiceTemplate createCustWebServiceTemplate;

    /** WebServiceTemplate bean injection */
    private WebServiceTemplate updateCustWebServiceTemplate;

    private Converter<CustomerModel, CustCreateReq> createConverter;

    private Converter<CustomerModel, CustUpdateInReq> updateConverter;

    private final ObjectFactory objectFactory = new ObjectFactory();

    /** ModelService */
    private ModelService modelService;

    /**
     * {@inheritDoc}
     */
    @Override
    public CustCreateRes createCustomer(final CustomerModel customer) {

        JAXBElement<CustCreateRes> wrappedResponse = null;
        CustCreateRes response = null;
        final CustCreateReq request = createConverter.convert(customer);
        final JAXBElement<CustCreateReq> wrappedRequest = objectFactory.createCustCreateReq(request);
        LOG.info("Sending creation request to SAP for a new customer [{}], sap custumer Id [{}]", customer.getUid(),
                customer.getSapCustomerId());
        try {
            wrappedResponse = (JAXBElement<CustCreateRes>) createCustWebServiceTemplate.marshalSendAndReceive(wrappedRequest);
        } catch (final WebServiceFaultException faultException) {
            // customer.setSapErpCreated(false);
            LOG.error(String.format("Error in creating customer in SAP for Sap customer id [%s]", customer.getUid()), faultException);
        } catch (final WebServiceException wsException) {
            // customer.setSapErpCreated(false);
            LOG.error(String.format("Error in creating customer in SAP for Sap customer id [%s]", customer.getUid()), wsException);
        } catch (final Exception e) {
            // customer.setSapErpCreated(false);
            LOG.error(String.format("Error in creating customer in SAP for Sap customer id [%s]", customer.getUid()), e);
        }
        if (wrappedResponse != null) {
            response = wrappedResponse.getValue();

            if (V2kartsapinboundintegrationConstants.RESPONSE_CODE_SUCCESS.equalsIgnoreCase(response.getRespCode())) {
                LOG.info("Successfully created customer in SAP with customer id [{}] and  sap custumer Id [{}]", customer.getUid(),
                        customer.getSapCustomerId());
                // customer.setSapErpCreated(true);
                customer.setSapCustomerId(response.getCustId());
            } else {
                LOG.info("Could not created customer in SAP, Persisting it as not created with customer id [{}] and  sap custumer Id [{}]",
                        customer.getUid(), customer.getSapCustomerId());
                // customer.setSapErpCreated(false);
            }
            // Store response message
            customer.setSapResponseDescription(response.getRespMsg());
        }

        modelService.save(customer);
        modelService.refresh(customer);
        return response;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CustUpdateInRes updateCustomer(final CustomerModel customer) {
        JAXBElement<CustUpdateInRes> wrappedResponse = null;
        CustUpdateInRes custUpdateInRes = null;
        CustUpdateInReq request = null;
        request = updateConverter.convert(customer);
        final JAXBElement<CustUpdateInReq> wrappedRequest = objectFactory.createCustUpdateInReq(request);

        LOG.info("Sending update request to SAP for customer with updated attributes, cusomer id  [{}] sap custumer Id [{}]",
                customer.getUid(), customer.getSapCustomerId());
        try {
            wrappedResponse = (JAXBElement<CustUpdateInRes>) updateCustWebServiceTemplate.marshalSendAndReceive(wrappedRequest);
        } catch (final WebServiceFaultException faultException) {
            customer.setSapErpUpdated(false);
            LOG.error(String.format("Error in updating customer in SAP for Sap customer id [%s]", customer.getUid()), faultException);
        } catch (final WebServiceException wsException) {
            customer.setSapErpUpdated(false);
            LOG.error(String.format("Error in updating customer in SAP for Sap customer id [%s]", customer.getUid()), wsException);
        } catch (final Exception e) {
            customer.setSapErpUpdated(false);
            LOG.error(String.format("Error in updating customer in SAP for Sap customer id [%s]", customer.getUid()), e);
        }

        if (wrappedResponse != null) {
            custUpdateInRes = wrappedResponse.getValue();
            if (V2kartsapinboundintegrationConstants.RESPONSE_CODE_SUCCESS.equalsIgnoreCase(custUpdateInRes.getRespCode())) {
                LOG.info("Successfully updated customer in SAP, cusomer id  [{}], sap custumer Id [{}]", customer.getUid(),
                        customer.getSapCustomerId());
                customer.setSapErpUpdated(true);
            } else {
                LOG.info("Could not update customer. Persisting it as not updated, cusomer id  [{}] , sap custumer Id [{}]",
                        customer.getUid(), customer.getSapCustomerId());
                customer.setSapErpUpdated(false);
            }
            // Store response message
            customer.setSapResponseDescription(custUpdateInRes.getRespMsg());
        } else {
            customer.setSapErpUpdated(false);
        }
        modelService.save(customer);
        modelService.refresh(customer);
        return custUpdateInRes;
    }

    /**
     * @param createCustWebServiceTemplate
     *        the createCustWebServiceTemplate to set
     */
    @Required
    public void setCreateCustWebServiceTemplate(final WebServiceTemplate createCustWebServiceTemplate) {
        this.createCustWebServiceTemplate = createCustWebServiceTemplate;
    }

    /**
     * @param updateCustWebServiceTemplate
     *        the updateCustWebServiceTemplate to set
     */
    @Required
    public void setUpdateCustWebServiceTemplate(final WebServiceTemplate updateCustWebServiceTemplate) {
        this.updateCustWebServiceTemplate = updateCustWebServiceTemplate;
    }

    /**
     * @param createConverter
     *        the createConverter to set
     */
    @Required
    public void setCreateConverter(final Converter<CustomerModel, CustCreateReq> createConverter) {
        this.createConverter = createConverter;
    }

    /**
     * @param updateConverter
     *        the updateConverter to set
     */
    @Required
    public void setUpdateConverter(final Converter<CustomerModel, CustUpdateInReq> updateConverter) {
        this.updateConverter = updateConverter;
    }

    /**
     * @param modelService
     *        the modelService to set
     */
    @Required
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

}
