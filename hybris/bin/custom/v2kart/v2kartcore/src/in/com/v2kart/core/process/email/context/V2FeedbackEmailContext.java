package in.com.v2kart.core.process.email.context;

import in.com.v2kart.facades.core.data.V2FeedbackData;
import org.apache.velocity.VelocityContext;

public class V2FeedbackEmailContext extends VelocityContext {
    
    private final V2FeedbackData feedbackData;
    
    public V2FeedbackData getFeedbackData() {
		return feedbackData;
	}

	
    public V2FeedbackEmailContext(final V2FeedbackData feedbackData) {
        this.feedbackData = feedbackData;
    }
}

    
