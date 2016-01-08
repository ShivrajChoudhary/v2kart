package in.com.v2kart.storefront.web.mvc;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author gaurav2007
 * 
 */
@ControllerAdvice
public class GlobalExceptionResolver {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionResolver.class);

    public static final String REDIRECT_PREFIX = "redirect:";

    @ExceptionHandler(Exception.class)
    public String exception(final Exception exception, final HttpServletRequest request) {
        LOG.error("Error occured : ", exception);
        return REDIRECT_PREFIX + "/serverError";
    }

}
