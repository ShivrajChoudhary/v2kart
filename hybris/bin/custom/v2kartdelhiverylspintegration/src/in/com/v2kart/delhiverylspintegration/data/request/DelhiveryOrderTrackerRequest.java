/**
 * 
 */
package in.com.v2kart.delhiverylspintegration.data.request;

import in.com.v2kart.delhiverylspintegration.enums.Verbose;

import java.util.List;

/**
 * @author vikrant2480
 * 
 */
public class DelhiveryOrderTrackerRequest
{
    private final String token;

    private final List<String> wayBills;

    private final Verbose verbose;

    /**
	 * 
	 */
    public DelhiveryOrderTrackerRequest(final String token, final List<String> wayBills, final Verbose verbose)
    {
        this.token = token;
        this.wayBills = wayBills;
        this.verbose = verbose;
    }

    /**
     * @return the token
     */
    public String getToken()
    {
        return token;
    }

    /**
     * @return the wayBills
     */
    public List<String> getWayBills()
    {
        return wayBills;
    }

    /**
     * @return the verbose
     */
    public Verbose getVerbose()
    {
        return verbose;
    }
}
