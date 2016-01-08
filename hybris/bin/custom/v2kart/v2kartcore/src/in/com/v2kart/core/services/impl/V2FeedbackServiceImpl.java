package in.com.v2kart.core.services.impl;

import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.servicelayer.model.ModelService;
import in.com.v2kart.core.model.V2FeedbackModel;
import in.com.v2kart.core.services.V2FeedbackService;
import in.com.v2kart.facades.core.data.V2FeedbackData;


public class V2FeedbackServiceImpl implements V2FeedbackService{
    private V2FeedbackModel feedback;
    
    /**
     * @return the feedback
     */
    public V2FeedbackModel getFeedback() {
        return feedback;
    }

    /**
     * @param feedback the feedback to set
     */
    public void setFeedback(V2FeedbackModel feedback) {
        this.feedback = feedback;
    }

    /**
     * @return the modelService
     */
    public ModelService getModelService() {
        return modelService;
    }

    /**
     * @param modelService the modelService to set
     */
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    private ModelService modelService;

    @Override
    public void saveFeedbackData(V2FeedbackData feedback) throws DuplicateUidException {
       
        final V2FeedbackModel v2FeedbackModel = modelService
                .create(V2FeedbackModel.class);
        saveFeedback(feedback,v2FeedbackModel);
        modelService.save(v2FeedbackModel);
        
    }
    
    private void saveFeedback(final V2FeedbackData feedback,
            final V2FeedbackModel v2FeedbackModel){
        v2FeedbackModel.setCategory(feedback.getCategory());
        v2FeedbackModel.setEmail(feedback.getEmail());
        v2FeedbackModel.setMobileNumber(feedback.getMobileNumber());
        v2FeedbackModel.setRating(feedback.getRating());
        v2FeedbackModel.setMessage(feedback.getMessage());
        
    }
}
