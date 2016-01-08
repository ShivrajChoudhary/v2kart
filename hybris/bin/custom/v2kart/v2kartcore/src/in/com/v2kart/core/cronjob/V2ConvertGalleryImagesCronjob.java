package in.com.v2kart.core.cronjob;

import in.com.v2kart.core.model.V2kartStyleVariantProductModel;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.commercefacades.product.ImageFormatMapping;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.media.MediaContainerService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.search.SearchResult;

/**
 * CS Cockpit cronjob for setting thumbnail image from first media container
 * 
 * @author arunkumar
 * 
 */
public class V2ConvertGalleryImagesCronjob extends AbstractJobPerformable<CronJobModel> {

    final private Logger LOG = Logger.getLogger(V2ConvertGalleryImagesCronjob.class);
    private MediaService mediaService;
    private MediaContainerService mediaContainerService;
    private ImageFormatMapping imageFormatMapping;

    @Override
    public PerformResult perform(CronJobModel arg0) {
        final String query = "select {pk} from {V2kartStyleVariantProduct!}";
        MediaContainerModel mediaContainer = null;
        final String imageFormat = "thumbnail";
        PerformResult result = new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);

        final SearchResult<V2kartStyleVariantProductModel> v2kartStyleVariantProducts = flexibleSearchService.search(query);
        for (V2kartStyleVariantProductModel v2KartStyleVariantProduct : v2kartStyleVariantProducts.getResult()) {
            final List<MediaContainerModel> mediaContainers = v2KartStyleVariantProduct.getGalleryImages();

            if (CollectionUtils.isNotEmpty(mediaContainers)) {
                mediaContainer = mediaContainers.get(0);

                if (mediaContainer != null) {
                    final String mediaFormatQualifier = getImageFormatMapping().getMediaFormatQualifierForImageFormat(imageFormat);
                    if (mediaFormatQualifier != null) {
                        final MediaFormatModel mediaFormat = getMediaService().getFormat(mediaFormatQualifier);
                        if (mediaFormat != null) {
                            try {
                                v2KartStyleVariantProduct.setThumbnail(getMediaContainerService()
                                        .getMediaForFormat(mediaContainer, mediaFormat));
                                modelService.save(v2KartStyleVariantProduct);
                            } catch (ModelNotFoundException e) {
                                LOG.error("Product does not have media with format-'Product-96Wx96H'");
                                result = new PerformResult(CronJobResult.ERROR, CronJobStatus.FINISHED);
                            }
                        }
                    }
                }
            }

        }
        return result;
    }

    protected MediaService getMediaService() {
        return mediaService;
    }

    @Required
    public void setMediaService(final MediaService mediaService) {
        this.mediaService = mediaService;
    }

    protected MediaContainerService getMediaContainerService() {
        return mediaContainerService;
    }

    @Required
    public void setMediaContainerService(final MediaContainerService mediaContainerService) {
        this.mediaContainerService = mediaContainerService;
    }

    protected ImageFormatMapping getImageFormatMapping() {
        return imageFormatMapping;
    }

    @Required
    public void setImageFormatMapping(final ImageFormatMapping imageFormatMapping) {
        this.imageFormatMapping = imageFormatMapping;
    }

}
