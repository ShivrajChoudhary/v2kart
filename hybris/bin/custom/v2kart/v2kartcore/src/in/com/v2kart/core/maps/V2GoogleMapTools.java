package in.com.v2kart.core.maps;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.apache.catalina.util.URLEncoder;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import de.hybris.platform.storelocator.GPS;
import de.hybris.platform.storelocator.GeolocationResponseParser;
import de.hybris.platform.storelocator.data.AddressData;
import de.hybris.platform.storelocator.data.MapLocationData;
import de.hybris.platform.storelocator.exception.GeoLocatorException;
import de.hybris.platform.storelocator.exception.GeoServiceWrapperException;
import de.hybris.platform.storelocator.impl.DefaultGPS;
import de.hybris.platform.storelocator.impl.GoogleMapTools;
import de.hybris.platform.util.Config;

/**
 * It is used to get the google maps API via client ID and encrypted private key.
 * 
 * @author vikrant2480
 * 
 */
public class V2GoogleMapTools extends GoogleMapTools {

    protected static final Logger LOG = Logger.getLogger(V2GoogleMapTools.class);
    private GeolocationResponseParser<MapLocationData> addressLocationParser;
    private V2UrlSigner urlSigner;
    private String baseUrl;
    private static final String GOOGLE_CLIENT_ID = "googleClientId";
    private static final String GOOGLE_PRIVATE_KEY = "googlePrivateKey";

    @Override
    public GPS geocodeAddress(final AddressData addressData) throws GeoServiceWrapperException {

        try {
            final String clientId = Config.getString(GOOGLE_CLIENT_ID, null);
            final String privateKey = Config.getString(GOOGLE_PRIVATE_KEY, null);

            if (StringUtils.isBlank(clientId) || StringUtils.isBlank(privateKey)) {
                // Use free service google map Web Service having limit to requests per day
                return super.geocodeAddress(addressData);
            }

            if (StringUtils.isNotEmpty(addressData.getStreet())) {
                addressData.setStreet(java.net.URLEncoder.encode(addressData.getStreet(), "UTF-8"));
            }

            if (StringUtils.isNotEmpty(addressData.getBuilding())) {
                addressData.setBuilding(java.net.URLEncoder.encode(addressData.getBuilding(), "UTF-8"));
            }

            final RestTemplate restTemplate = new RestTemplate();

            urlSigner.setKey(privateKey);
            final URLEncoder urlEncoder = new URLEncoder();
            final StringBuffer urlAddress = new StringBuffer(32);

            urlAddress.append(baseUrl).append("xml?address=").append(urlEncoder.encode(getGoogleQuery(addressData)))
                    .append("&sensor=true").append("&client=").append(clientId);

            final URL url = new URL(urlAddress.toString());
            final String request = urlSigner.signRequest(url.getPath(), url.getQuery());
            final URI uri = new URI(url.getProtocol() + "://" + url.getHost() + request);

            final MapLocationData locationData = restTemplate.execute(uri, HttpMethod.GET, null, addressLocationParser);

            final String latitude = locationData.getLatitude();
            final String longitude = locationData.getLongitude();

            if ((StringUtils.isNotBlank(latitude)) && (StringUtils.isNotBlank(longitude))) {
                return new DefaultGPS().create(Double.parseDouble(latitude), Double.parseDouble(longitude));
            }

            throw new GeoServiceWrapperException(GeoServiceWrapperException.errorMessages.get(locationData.getCode()));
        } catch (final GeoLocatorException | ResourceAccessException | InvalidKeyException | IOException | NoSuchAlgorithmException
                | URISyntaxException | HttpClientErrorException e) {
            throw new GeoServiceWrapperException(e);
        }
    }

    /**
     * @param urlSigner
     *        the urlSigner to set
     */
    public void setUrlSigner(final V2UrlSigner urlSigner) {
        this.urlSigner = urlSigner;
    }

    @Override
    public void setBaseUrl(final String baseUrl) {
        this.baseUrl = baseUrl;
        super.setBaseUrl(baseUrl);
    }

    @Override
    @Required
    public void setAddressLocationParser(final GeolocationResponseParser<MapLocationData> addressLocationParser) {
        this.addressLocationParser = addressLocationParser;
        super.setAddressLocationParser(addressLocationParser);
    }
}
