package in.com.v2kart.commercewebservices.strategies;


/**
 * Strategy for identifying is given string is GUID.
 */
public interface OrderCodeIdentificationStrategy {
    boolean isID(String potentialId);
}
