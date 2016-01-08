package in.com.v2kart.core.services;

import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import in.com.v2kart.facades.core.data.V2FeedbackData;

public interface V2FeedbackService {
    void saveFeedbackData(V2FeedbackData feedback)
            throws DuplicateUidException;
}
