package in.com.v2kart.core.vouchers;

/**
 * 
 * @author vikrant2480
 * 
 */
public class VoucherCodeResult {
    private boolean applied = false;
    private String rejectMessage = "There was a problem applying the code.";

    public VoucherCodeResult()
    {
    }

    /**
     * @param applied
     * @param rejecMessage
     */
    public VoucherCodeResult(final boolean applied, final String rejecMessage)
    {
        this.applied = applied;
        this.rejectMessage = rejecMessage;
    }

    /**
     * @param applied
     */
    public VoucherCodeResult(final boolean applied)
    {
        this.applied = applied;
    }

    /**
     * @return the applied
     */
    public boolean isApplied()
    {
        return applied;
    }

    /**
     * @param applied
     *        the applied to set
     */
    public void setApplied(final boolean applied)
    {
        this.applied = applied;
    }

    /**
     * @return the rejecMessage
     */
    public String getRejectMessage()
    {
        return rejectMessage;
    }

    /**
     * @param rejecMessage
     *        the rejecMessage to set
     */
    public void setRejectMessage(final String rejectMessage)
    {
        this.rejectMessage = rejectMessage;
    }

}
