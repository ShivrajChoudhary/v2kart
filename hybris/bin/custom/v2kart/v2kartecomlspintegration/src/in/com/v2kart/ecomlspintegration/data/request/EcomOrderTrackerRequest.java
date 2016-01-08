/**
 * 
 */
package in.com.v2kart.ecomlspintegration.data.request;

import java.util.List;

/**
 * @author arunkumar
 * 
 */
public class EcomOrderTrackerRequest {
    private final List<String> wayBills;

    private final String username;

    private final String password;

    /**
     * @param wayBills
     * @param username
     * @param password
     */
    public EcomOrderTrackerRequest(final List<String> wayBills, final String username, final String password) {
        this.wayBills = wayBills;
        this.username = username;
        this.password = password;
    }

    /**
     * @return the waybills
     */
    public List<String> getWayBills() {
        return wayBills;
    }

    /**
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return password
     */
    public String getPassword() {
        return password;
    }

}
