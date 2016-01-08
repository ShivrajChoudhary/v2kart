/**
 * 
 */
package in.com.v2kart.dataimport.context;

/**
 * ImportCommandContextHolder - Class to expose import command context
 * 
 * Context will be set during the initialization of import commands and can be used during the lifecycle of the thread execution.
 * 
 * It currently written with the assumption that import cron job would be based on single threaded model i.e. it will execute import
 * commands sequentially and there won't be multiple parallel commands running through different-2 sub threads of parent cron job thread .
 * 
 */
public final class ImportCommandContextHolder {

    /**
     * Thread context holder
     */
    private static final ThreadLocal<ImportCommandContext> CONTEXT_HOLDER = new ThreadLocal();

    private ImportCommandContextHolder() {
        // private constructor
    }

    /**
     * API to clear thread context
     */
    public static void clearContext() {
        CONTEXT_HOLDER.remove();
    }

    /**
     * Function to retrieve thread local import command context
     * 
     * @return ImportCommandContext instance
     */
    public static ImportCommandContext getContext() {
        ImportCommandContext ctx = CONTEXT_HOLDER.get();

        if (ctx == null) {
            ctx = createEmptyContext();
            CONTEXT_HOLDER.set(ctx);
        }

        return ctx;
    }

    /**
     * Function to create empty context
     * 
     * @return ImportCommandContext instance
     */
    private static ImportCommandContext createEmptyContext() {
        return new ImportCommandContext();
    }

}
