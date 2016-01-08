package in.com.v2kart.dataimport.services.impl;

import in.com.v2kart.core.dao.CatalogAwareModelDao;
import in.com.v2kart.core.dao.impl.V2CategoryModelDaoImpl;
import in.com.v2kart.core.enums.SwatchColorEnum;
import in.com.v2kart.core.model.V2kartSizeVariantProductModel;
import in.com.v2kart.core.model.V2kartStyleVariantProductModel;
import in.com.v2kart.dataimport.ImportDataSummaryInfo;
import in.com.v2kart.dataimport.dto.V2ProductDto;
import in.com.v2kart.dataimport.exceptions.FeedPersistanceException;
import in.com.v2kart.dataimport.exceptions.FeedPersistanceException.FailureCause;
import in.com.v2kart.dataimport.importengine.CSVReader;
import in.com.v2kart.dataimport.services.V2ProductImportService;
import in.com.v2kart.dataimport.strategies.FeedReaderStrategy;
import in.com.v2kart.core.enums.BoostProductEnum;
import de.hybris.platform.product.UnitService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;

import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.classification.impl.DefaultClassificationService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.enumeration.EnumerationMetaTypeModel;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.product.VariantsService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.util.Config;
import de.hybris.platform.variants.model.VariantProductModel;

/**
 * Default Implementation of V2KProductImportServiceImpl.
 */
public class V2ProductImportServiceImpl extends BaseImportServiceImpl implements V2ProductImportService {

    /** Constants used for No Category found for McCode message. */
    private static final String MC_CODE_NOT_FOUND_INFO = "No Category found for McCode:[%1$s] for Product:[%2$s] and row index:[%3$s]. Assigning this to unassigned category";

    /** Logger Instance for this class. */
    private static final Logger LOG = Logger.getLogger(V2ProductImportServiceImpl.class);

    /** CSVReader bean injection. */
    @Resource(name = "v2ProductCsvReader")
    private CSVReader v2ProductCsvReader;

    /** FeedReaderStrategy bean injection. */
    @Autowired
    private FeedReaderStrategy feedReaderStrategy;

    /** DefaultCatalogAwareModelDao bean injection. */
    @Resource(name = "productModelDao")
    private CatalogAwareModelDao<ProductModel> productModelDao;

    /** V2CategoryModelDaoImpl bean injection. */
    @Autowired
    private V2CategoryModelDaoImpl categoryModelDao;

    /** Article Approval Status. */
    protected ArticleApprovalStatus defaultProductApprovalStatus;

    /** VariantsService bean injection. */
    @Autowired
    private VariantsService variantsService;

    /** EnumnerationService bean injection. */
    @Autowired
    private EnumerationService enumerationService;

    /** TypeService bean injection. */
    @Autowired
    private TypeService typeService;

    /** TypeService bean injection. */
    @Autowired
    private DefaultClassificationService defaultClassificationService;

    /** The features value provider map. */
    private Map<String, String> featuresValueProviderMap;
    
    @Autowired
    private UnitService unitService;

    /**
     * {@inheritDoc}
     */
    @Override
    public final ImportDataSummaryInfo importProductDataFromFeed() {
        // Get product data from csv.
        final List<V2ProductDto> v2kProductDtoList = this.getProductsDataFromFeed();
        final ImportDataSummaryInfo summaryInfo = new ImportDataSummaryInfo();
        summaryInfo.setTotalRecords(v2kProductDtoList.size());
        for (final V2ProductDto v2kProductDto : v2kProductDtoList) {
            try {
                summaryInfo.recordDtoProcessing(v2kProductDto);
                this.createOrUpdateV2Product(v2kProductDto, summaryInfo);
            } catch (final FeedPersistanceException e) {
                LOG.error("Error persisting product Dto: " + v2kProductDto.getVerboseToString(), e);
                summaryInfo.addFailureIncidence(e.getFailureCause());
            } catch (final Exception e) {
                LOG.error("Error persisting product Dto: " + v2kProductDto.getVerboseToString(), e);
                summaryInfo.addFailureIncidence(FailureCause.OTHERS);
            }
        }
        return summaryInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<V2ProductDto> getProductsDataFromFeed() {
        return feedReaderStrategy.<V2ProductDto> readFeed(v2ProductCsvReader);
    }

    /**
     * API is used for create/update V2Product in Hybris platform. If base product code not available then it will create
     * <Code>SingleArticleProduct</Code>.
     * 
     * @param v2ProductDto
     *        the v2 product dto
     * @param summaryInfo
     *        summaryInfo instance
     */
    private void createOrUpdateV2Product(final V2ProductDto v2ProductDto, final ImportDataSummaryInfo summaryInfo) {
        if (v2ProductDto.getBaseProductCode() != null) {
            createOrUpdateV2VariantProduct(v2ProductDto, summaryInfo);
        } else { // if base product code is null then log the info and ESCAPE the ROW
            // log the info
            throw new FeedPersistanceException("No base product with code: " + v2ProductDto.getBaseProductCode()
                    + " found for product with code " + v2ProductDto.getArticleNumber() + " and with row number: "
                    + v2ProductDto.getRowIndex(), FailureCause.BASE_PRODUCT_NOT_FOUND, v2ProductDto.getRowIndex());
        }
    }

    /**
     * API is used to create/update a v2 variant product as per below condition.
     * 
     * <li>If size is NOT NULL and Style is NOT NULL - create Style And Size VariantProducts.</li> <li>If Style is NOT NULL and Size is NULL
     * - create Style VariantProducts.</li> <li>If Size is NOT NULL and Style is NULL - create Size VariantProducts.</li>
     * 
     * @param v2ProductDto
     *        to create/update VariantProducts
     * @param summaryInfo
     *        instance of ImportDataSummaryInfo
     */
    private void createOrUpdateV2VariantProduct(final V2ProductDto v2ProductDto, final ImportDataSummaryInfo summaryInfo) {
        ProductModel v2BaseProduct = (ProductModel) productModelDao.findByCodeAndCatalogVersion(v2ProductDto.getBaseProductCode(),
                getCatalogForProduct(v2ProductDto));
        // if base product is not available then create a new one
        if (v2BaseProduct == null) {
            v2BaseProduct = createV2BaseProduct(v2ProductDto.getBaseProductCode(), ProductModel._TYPECODE,
                    getCatalogForProduct(v2ProductDto), v2ProductDto.getBlockedIndicator());
        }

        populateV2BaseProductFields(v2ProductDto, v2BaseProduct, summaryInfo);
        getModelService().save(v2BaseProduct);
        LOG.debug("Creating a Generic Article with code [" + v2ProductDto.getArticleNumber() + " ]");

        // If Color Code is Empty then set the Default COLOR i.e NOCOLOR
        if (StringUtils.isEmpty(v2ProductDto.getColor())) {
            v2ProductDto.setColor(SwatchColorEnum.NOCOLOR.getCode());
            ProductModel v2StyleVariantProductModel = createOrUpdateStyleVariantProduct(v2BaseProduct, v2ProductDto);
            createOrUpdateSizeVariantProduct(v2StyleVariantProductModel, v2ProductDto);
        } else if (StringUtils.isNotEmpty(v2ProductDto.getColor())) {
            ProductModel v2StyleVariantProductModel = createOrUpdateStyleVariantProduct(v2BaseProduct, v2ProductDto);
            createOrUpdateSizeVariantProduct(v2StyleVariantProductModel, v2ProductDto);
        } else {
            throw new FeedPersistanceException(FailureCause.OTHERS, v2ProductDto.getRowIndex());
        }
    }

    /**
     * Returns {@link CatalogVersionModel}.
     * 
     * @param v2ProductDto
     *        v2ProductDto to get catalog version model
     * @return catalog version model
     */
    public final CatalogVersionModel getCatalogForProduct(final V2ProductDto v2ProductDto) {
        return getCatalogVersionModel();
    }

    /**
     * Create new Base Products in hybris platform.It will also set default product approval status.
     * 
     * @param productCode
     *        to create base product
     * @param typecode
     *        the typecode
     * @param catalogVersionModel
     *        the catalog Version
     * @return ProductModel newly created ProductModel
     */
    private ProductModel createV2BaseProduct(final String productCode, final String typecode,
            final CatalogVersionModel catalogVersionModel, final boolean blockedIndicator) {
        final ProductModel v2BaseProduct = getModelService().create(typecode);
        v2BaseProduct.setCode(productCode);
        v2BaseProduct.setCatalogVersion(catalogVersionModel);
        v2BaseProduct.setApprovalStatus(getApprovalStatus(blockedIndicator));
        v2BaseProduct.setUnit(unitService.getUnitForCode(Config.getParameter("price.unit")));
        return v2BaseProduct;
    }

    /**
     * API used to populate the fields in an end level product of the hierarchy.
     * 
     * @param v2ProductDto
     *        to populate v2ProductDto
     * @param productModel
     *        the ProductModel
     */
    final void populateEndLevelProductFields(final V2ProductDto v2ProductDto, final ProductModel productModel) {
        
    productModel.setMccd(v2ProductDto.getMccd());
        productModel.setMcdesc(v2ProductDto.getMcdesc());
        productModel.setName(getCapitalizeArticleName(v2ProductDto.getArticleName()));       
        if (CollectionUtils.isNotEmpty(categoryModelDao.findCategoriesByMcCode(v2ProductDto.getMccd(), getCatalogVersionModel()))) {
            productModel.setSupercategories(categoryModelDao.findCategoriesByMcCode(v2ProductDto.getMccd(), getCatalogVersionModel()));
        } else {
            throw new FeedPersistanceException(FailureCause.CATEGORY_NOT_FOUND, v2ProductDto.getRowIndex());
        }
    }

    /**
     * API used to populate the fields in a Base Product. Also set Super categories of the product as per product mccd.
     * 
     * @param v2ProductDto
     *        v2ProductDto
     * @param productModel
     *        productModel
     * @param summaryInfo
     *        the summary info
     */
    final void populateV2BaseProductFields(final V2ProductDto v2ProductDto, final ProductModel productModel,
            final ImportDataSummaryInfo summaryInfo) {
        productModel.setName(getCapitalizeArticleName(v2ProductDto.getArticleName()));
        productModel.setMccd(v2ProductDto.getMccd());
        if (CollectionUtils.isNotEmpty(categoryModelDao.findCategoriesByMcCode(v2ProductDto.getMccd(), getCatalogVersionModel()))) {
            productModel.setSupercategories(categoryModelDao.findCategoriesByMcCode(v2ProductDto.getMccd(), getCatalogVersionModel()));
        } else {
            throw new FeedPersistanceException(FailureCause.CATEGORY_NOT_FOUND, v2ProductDto.getRowIndex());
        }

        if (StringUtils.isNotEmpty(v2ProductDto.getBrand())) {
            CategoryModel brandCategory = categoryModelDao.findByCodeAndCatalogVersion(v2ProductDto.getBrand().toUpperCase(),
                    getCatalogForProduct(v2ProductDto));
            if (brandCategory != null) {
                final List<CategoryModel> productCategories;
                if (null != productModel.getSupercategories()) {
                    productCategories = new ArrayList<CategoryModel>(productModel.getSupercategories());
                } else {
                    productCategories = new ArrayList<CategoryModel>(1);
                }
                productCategories.add(brandCategory);
                productModel.setSupercategories(productCategories);
            } else {
                this.logNotFoundException(v2ProductDto.getBrand(), CategoryModel._TYPECODE, v2ProductDto.getArticleNumber(), v2ProductDto,
                        FailureCause.CATEGORY_FOR_BRAND_NOT_FOUND, summaryInfo);
            }
        }
    }

    /**
     * Creates the or update style variant color.
     * 
     * @param v2StyleVariantProductModel
     *        the v2 style variant product model
     * @param colorCode
     *        the color code
     * @return the sets the
     */
    private Set<SwatchColorEnum> createOrUpdateStyleVariantColor(final V2kartStyleVariantProductModel v2StyleVariantProductModel,
            final String colorCode) {
        // check if color code exists or not if exists then set the color code if not then create a new one and save.
        // creating new Collection bcz old collection is UNMODIFIABLE
        Set<SwatchColorEnum> newSwatchColors = new HashSet<>();
        if (null != v2StyleVariantProductModel && null != colorCode) {
            SwatchColorEnum swatchColor;
            Set<SwatchColorEnum> oldSwatchColors = v2StyleVariantProductModel.getSwatchColors();
            if (null != oldSwatchColors) {
                newSwatchColors.addAll(oldSwatchColors);
            }
            try {
                // remove spaces from color code if any
                swatchColor = enumerationService.getEnumerationValue(SwatchColorEnum.class, generateColorCodeWithoutSpace(colorCode));
                // check if color enum value exists and set do not contains the enum value then set the color enum in the set
                if (null != swatchColor && !newSwatchColors.contains(swatchColor)) {
                    newSwatchColors.add(swatchColor);
                }
            } catch (UnknownIdentifierException e) {
                LOG.info("No SwatchColor found with color code " + generateColorCodeWithoutSpace(colorCode));
                swatchColor = createSwatchColorEnum(colorCode);
                newSwatchColors.add(swatchColor);
            }
        } else {
            LOG.info("Color Code or Product Model is null !!!!!!!!!");
        }
        return newSwatchColors;
    }

    /**
     * Creates the or update style variant primary color.
     * 
     * @param v2StyleVariantProductModel
     *        the v2 style variant product model
     * @param colorCode
     *        the color code
     * @return the swatch color enum
     */
    private SwatchColorEnum createOrUpdateStyleVariantPrimaryColor(final V2kartStyleVariantProductModel v2StyleVariantProductModel,
            final String colorCode) {
        // check if color code exists or not if exists then set the color code if not then create a new one and save.
        SwatchColorEnum swatchColor = null;
        if (null != v2StyleVariantProductModel && null != colorCode) {
            try {
                // remove spaces from color code if any
                swatchColor = enumerationService.getEnumerationValue(SwatchColorEnum.class, generateColorCodeWithoutSpace(colorCode));
            } catch (UnknownIdentifierException e) {
                LOG.info("No SwatchColor found with color code " + colorCode);
                swatchColor = createSwatchColorEnum(colorCode);
            }
        } else {
            LOG.info("Color Code or Product Model is null !!!!!!!!!");
        }
        return swatchColor;
    }

    /**
     * Creates the swatch color enum.
     * 
     * @param colorCode
     *        the color code
     * @return the swatch color enum
     */
    private SwatchColorEnum createSwatchColorEnum(final String colorCode) {
        // get the all previous SwatchColorEnum value
        EnumerationMetaTypeModel enumMetaTypeModel = typeService.getEnumerationTypeForCode(SwatchColorEnum._TYPECODE);
        Collection<ItemModel> enumValueModels = enumMetaTypeModel.getValues();
        // start creating a new swatchcolor
        LOG.debug("Creating SwatchColorEnum value with color code " + generateColorCodeWithoutSpace(colorCode));
        EnumerationValueModel enumerationValueModel = getModelService().create(SwatchColorEnum._TYPECODE);
        enumerationValueModel.setCode(generateColorCodeWithoutSpace(colorCode));
        enumerationValueModel.setName(colorCode.toLowerCase());
        // add the newly created swatchcolor to the valueModels
        enumValueModels.add(enumerationValueModel);
        enumMetaTypeModel.setValues(enumValueModels);

        getModelService().save(enumerationValueModel);
        // return the newly created SwatchColorEnum
        return (SwatchColorEnum) getModelService().get(enumerationValueModel.getPk());
    }

    
    
    private BoostProductEnum createOrUpdateStyleVariantBoostingFactor(final V2kartStyleVariantProductModel v2StyleVariantProductModel,
            final String boostingFactor) {
        // check if color code exists or not if exists then set the color code if not then create a new one and save.
    BoostProductEnum factor = null;
        if (null != v2StyleVariantProductModel && null != boostingFactor) {
            try {
                // remove spaces from color code if any
            factor = enumerationService.getEnumerationValue(BoostProductEnum.class,boostingFactor );
            } catch (UnknownIdentifierException e) {
                LOG.info("No boostingFactor found with factor code " + boostingFactor);
            }
        } 
        return factor;
    }

    /**
     * API is used to create or update a Style Variant Product in hybris platform.
     * 
     * @param v2BaseProduct
     *        to create StyleVariantProduct
     * @param v2ProductDto
     *        to create StyleVariantProduct
     * @return the product model
     */
    private ProductModel createOrUpdateStyleVariantProduct(final ProductModel v2BaseProduct, final V2ProductDto v2ProductDto) {
        if (v2BaseProduct.getVariantType() == null) {
            v2BaseProduct.setVariantType(variantsService.getVariantTypeForCode(V2kartStyleVariantProductModel._TYPECODE));
            getModelService().save(v2BaseProduct);
        }
        String styleVariantProductCode = generateArticleCodeForStyleVariant(v2ProductDto);
        V2kartStyleVariantProductModel v2StyleVariantProductModel = (V2kartStyleVariantProductModel) productModelDao
                .findByCodeAndCatalogVersion(styleVariantProductCode, getCatalogForProduct(v2ProductDto));
        if (v2StyleVariantProductModel == null) {
            v2StyleVariantProductModel = (V2kartStyleVariantProductModel) createVariantProduct(styleVariantProductCode,
                    V2kartStyleVariantProductModel._TYPECODE, getCatalogForProduct(v2ProductDto), v2BaseProduct,
                    v2ProductDto.getBlockedIndicator());
        }
        populateEndLevelProductFields(v2ProductDto, v2StyleVariantProductModel);
        populateEndLevelProductFieldsForStyleVariant(v2StyleVariantProductModel, v2ProductDto);
        getModelService().save(v2StyleVariantProductModel);
        // if called before saving the product then it throws exception at run time so called after saving the product.
        populateClassificationAttributeForStyleVariant(v2StyleVariantProductModel, v2ProductDto);
        return v2StyleVariantProductModel;
    }

    /**
     * Generate article code for style variant.
     * 
     * @param v2ProductDto
     *        the v2 product dto
     * @return Generating Style Variant code as COmbination of BaseProductCode+_+ColorName
     */
    private String generateArticleCodeForStyleVariant(final V2ProductDto v2ProductDto) {
        return v2ProductDto.getBaseProductCode() + "_" + generateColorCodeWithoutSpaceLowerCase(v2ProductDto.getColor());
    }

    /**
     * Generate color code without space.
     * 
     * @param colorCode
     *        the color code
     * @return Return color code without spaces and Lower Case
     */
    private String generateColorCodeWithoutSpaceLowerCase(final String colorCode) {
        return colorCode.replaceAll("\\s", "").toLowerCase();
    }

    /**
     * Generate color code without space.
     * 
     * @param colorCode
     *        the color code
     * @return Return color code without spaces and UPPERCASE
     */
    private String generateColorCodeWithoutSpace(final String colorCode) {
        return colorCode.replaceAll("\\s", "").toUpperCase();
    }

    /**
     * Populate end level product fields for style variant.
     * 
     * @param v2StyleVariantProductModel
     *        Set all the other attributes here
     * @param v2ProductDto
     *        the v2 product dto
     */
    private void populateEndLevelProductFieldsForStyleVariant(final V2kartStyleVariantProductModel v2StyleVariantProductModel,
            final V2ProductDto v2ProductDto) {

        // set the color in the swatch color property
        Set<SwatchColorEnum> newSwatchColors = createOrUpdateStyleVariantColor(v2StyleVariantProductModel, v2ProductDto.getColor());
        SwatchColorEnum primarySwatchColor = createOrUpdateStyleVariantPrimaryColor(v2StyleVariantProductModel,
                v2ProductDto.getPrimaryColor());
  
        // adding new field boostingFactor in Style variant
        // added by shivraj
          BoostProductEnum boostingFactor= createOrUpdateStyleVariantBoostingFactor(v2StyleVariantProductModel,
        v2ProductDto.getBoostingFactor());

 
        v2StyleVariantProductModel.setName(getCapitalizeArticleName(v2ProductDto.getArticleName()));
        v2StyleVariantProductModel.setSummary(getCapitalizeArticleName(v2ProductDto.getArticleDescription()));
        v2StyleVariantProductModel.setPattern(v2ProductDto.getPattern());
        // TODO
        v2StyleVariantProductModel.setStyleCode(v2ProductDto.getStyle());
        v2StyleVariantProductModel.setOccasion(getCapitalizeArticleName(v2ProductDto.getOccasion()));
        v2StyleVariantProductModel.setWeight(v2ProductDto.getWeight());
        if (!v2ProductDto.getColor().equals(SwatchColorEnum.NOCOLOR.getCode())) {
            v2StyleVariantProductModel.setStyle(generateColorCodeWithoutSpace(v2ProductDto.getColor()));
        }
        v2StyleVariantProductModel.setType(v2ProductDto.getType());
        v2StyleVariantProductModel.setSwatchColors(newSwatchColors);
        v2StyleVariantProductModel.setPrimaryColor(primarySwatchColor);
        v2StyleVariantProductModel.setBoostingFactor(boostingFactor);
    }

    /**
     * Return Product Name in capitalize case
     * 
     * @param articleName
     * @return Return Product Name in capitalize case
     */
    private String getCapitalizeArticleName(String articleName) {
        if (null != articleName) {
            return WordUtils.capitalizeFully(articleName);
        } else {
            return null;
        }
    }

    /**
     * Populate classification attribute for style variant.
     * 
     * @param v2StyleVariantProductModel
     *        the v2 style variant product model
     * @param v2ProductDto
     *        Set the Products Classification Values
     */
    private void populateClassificationAttributeForStyleVariant(final V2kartStyleVariantProductModel v2StyleVariantProductModel,
            final V2ProductDto v2ProductDto) {
        // setting the classification attributes
        // EXPLICITLY calling the SETTER Function of featuresProviderMap
        this.setFeaturesValueProviderMap(v2ProductDto);
        // get the featureList for product
        FeatureList featuresList = defaultClassificationService.getFeatures(v2StyleVariantProductModel);
        // get the list of features from Feature List
        List<Feature> features = featuresList.getFeatures();
        if (features.size() > 0) {
            Iterator<Feature> featureIterator = features.iterator();
            Feature feature;
            FeatureValue featureValue;
            while (featureIterator.hasNext()) {
                feature = featureIterator.next();
                featureValue = feature.getValue();
                // v2KartClassification/1.0/apps_classification_class.fabric
                String productFeatureCode = feature.getCode().substring(feature.getCode().lastIndexOf(".") + 1);
                if (this.getFeaturesValueProviderMap().containsKey(productFeatureCode)) {
                    // set the feature value
                    if (null != featureValue) {
                        if (null != this.getFeaturesValueProviderMap().get(productFeatureCode)) {
                            featureValue.setValue(getCapitalizeArticleName(this.getFeaturesValueProviderMap().get(productFeatureCode)));
                        }
                    } else { // if feature value is not set or null previously
                        // set new feature value only if the value is not null
                        if (null != this.getFeaturesValueProviderMap().get(productFeatureCode)) {
                            featureValue = new FeatureValue(getCapitalizeArticleName(this.getFeaturesValueProviderMap().get(
                                    productFeatureCode)));
                            feature.addValue(featureValue);
                        }
                    }
                } else {
                    LOG.info("Invalid Feature " + feature.getCode() + " !!!!!");
                }
            }
        }
        // finally set the updated features list
        defaultClassificationService.setFeatures(v2StyleVariantProductModel, featuresList);
    }

    /**
     * API is used to create/update Size Variant Product.
     * 
     * @param v2BaseProduct
     *        to create SizeVariantProduct
     * @param v2ProductDto
     *        to create SizeVariantProduct
     * 
     */
    final void createOrUpdateSizeVariantProduct(final ProductModel v2BaseProduct, final V2ProductDto v2ProductDto) {
        if (v2BaseProduct.getVariantType() == null) {
            v2BaseProduct.setVariantType(variantsService.getVariantTypeForCode(V2kartSizeVariantProductModel._TYPECODE));
            getModelService().save(v2BaseProduct);
        }
        V2kartSizeVariantProductModel v2SizeVariantProductModel = (V2kartSizeVariantProductModel) productModelDao
                .findByCodeAndCatalogVersion(v2ProductDto.getArticleNumber(), getCatalogForProduct(v2ProductDto));
        if (v2SizeVariantProductModel == null) {
            v2SizeVariantProductModel = (V2kartSizeVariantProductModel) createVariantProduct(v2ProductDto.getArticleNumber(),
                    V2kartSizeVariantProductModel._TYPECODE, getCatalogForProduct(v2ProductDto), v2BaseProduct,
                    v2ProductDto.getBlockedIndicator());
        }
        populateEndLevelProductFields(v2ProductDto, v2SizeVariantProductModel);
        v2SizeVariantProductModel.setSize(v2ProductDto.getSize());
        getModelService().save(v2SizeVariantProductModel);
    //adding by shivraj
        // if called before saving the product then it throws exception at run time so called after saving the product.
        populateClassificationAttributeForSizeVariant(v2SizeVariantProductModel, v2ProductDto);
    
    }

    /**
     * Populate classification attribute for size variant.
     * 
     * @param v2SizeVariantProductModel
     *        the v2 size variant product model
     * @param v2ProductDto
     *        Set the Products Classification Values
     */
    private void populateClassificationAttributeForSizeVariant(final V2kartSizeVariantProductModel v2SizeVariantProductModel,
            final V2ProductDto v2ProductDto) {
        // setting the classification attributes
        // EXPLICITLY calling the SETTER Function of featuresProviderMap
        this.setFeaturesValueProviderMapForSize(v2ProductDto);
        // get the featureList for product
        FeatureList featuresList = defaultClassificationService.getFeatures(v2SizeVariantProductModel);
        // get the list of features from Feature List
        List<Feature> features = featuresList.getFeatures();
        if (features.size() > 0) {
            Iterator<Feature> featureIterator = features.iterator();
            Feature feature;
            FeatureValue featureValue;
            while (featureIterator.hasNext()) {
                feature = featureIterator.next();
                featureValue = feature.getValue();
                // v2KartClassification/1.0/apps_classification_class.fabric
                String productFeatureCode = feature.getCode().substring(feature.getCode().lastIndexOf(".") + 1);
                if (this.getFeaturesValueProviderMap().containsKey(productFeatureCode)) {
                    // set the feature value
                    if (null != featureValue) {
                        if (null != this.getFeaturesValueProviderMap().get(productFeatureCode)) {
                            featureValue.setValue(getCapitalizeArticleName(this.getFeaturesValueProviderMap().get(productFeatureCode)));
                        }
                    } else { // if feature value is not set or null previously
                        // set new feature value only if the value is not null
                        if (null != this.getFeaturesValueProviderMap().get(productFeatureCode)) {
                            featureValue = new FeatureValue(getCapitalizeArticleName(this.getFeaturesValueProviderMap().get(
                                    productFeatureCode)));
                            feature.addValue(featureValue);
                        }
                    }
                } else {
                    LOG.info("Invalid Feature " + feature.getCode() + " !!!!!");
                }
            }
        }
        // finally set the updated features list
        defaultClassificationService.setFeatures(v2SizeVariantProductModel, featuresList);
    }
    
    /**
     * API is used to create a Variant Products as per catalogVersionModel and type code. It will also set default product approval status
     * and max order quantity.
     * 
     * @param productCode
     *        the product code
     * @param typecode
     *        the type code
     * @param catalogVersionModel
     *        the catalog Version Model
     * @param baseProduct
     *        the base product
     * @return variantProductModel newly created variant Product Model
     */
    private VariantProductModel createVariantProduct(final String productCode, final String typecode,
            final CatalogVersionModel catalogVersionModel, final ProductModel baseProduct, final boolean blockedIndicator) {
        final VariantProductModel v2VariantProduct = getModelService().create(typecode);
        v2VariantProduct.setCode(productCode);
        v2VariantProduct.setCatalogVersion(catalogVersionModel);
        v2VariantProduct.setBaseProduct(baseProduct);
        v2VariantProduct.setApprovalStatus(getApprovalStatus(blockedIndicator));
        v2VariantProduct.setMaxOrderQuantity(Integer.valueOf(getProductMaxOrderQuantity()));
        v2VariantProduct.setUnit(unitService.getUnitForCode(Config.getParameter("price.unit")));
        return v2VariantProduct;
    }

    /**
     * Set Default Product Approval Status as per {@link local.properties}
     * 
     * @param defaultApprovalStaus
     *        the defaultApprovalStaus to set for product
     */
    @Value("${dataImport.product.defaultApprovalStatus}")
    @Required
    public final void setDefaultProductApprovalStatus(final boolean defaultApprovalStaus) {
        if (defaultApprovalStaus) {
            this.defaultProductApprovalStatus = ArticleApprovalStatus.APPROVED;
        } else {
            this.defaultProductApprovalStatus = ArticleApprovalStatus.CHECK;
        }
    }

    /**
     * @param v2ProductDto
     * @return
     */
    private final ArticleApprovalStatus getApprovalStatus(boolean blockedIndicator) {
        if (blockedIndicator) {
            this.defaultProductApprovalStatus = ArticleApprovalStatus.CHECK;
        }else 
        {
            this.defaultProductApprovalStatus = ArticleApprovalStatus.APPROVED;        	
        }
        return this.defaultProductApprovalStatus;
    }

    /**
     * Gets the features value provider map.
     * 
     * @return the features value provider map
     */
    public final Map<String, String> getFeaturesValueProviderMap() {
        return featuresValueProviderMap;
    }

    /**
     * Sets the features value provider map.
     * 
     * @param v2ProductDto
     *        the new features value provider map
     */
    public final void setFeaturesValueProviderMap(final V2ProductDto v2ProductDto) {
        featuresValueProviderMap = new HashMap<>();
        featuresValueProviderMap.put("fabric", v2ProductDto.getFabric());
        featuresValueProviderMap.put("fitting", v2ProductDto.getFitting());
        featuresValueProviderMap.put("sleeves", v2ProductDto.getSleeves());
        featuresValueProviderMap.put("neck", v2ProductDto.getNeck());
        featuresValueProviderMap.put("subfabric", v2ProductDto.getSubFabric());
        featuresValueProviderMap.put("basematerial", v2ProductDto.getBaseMaterial());
        featuresValueProviderMap.put("pockets", v2ProductDto.getPockets());
        featuresValueProviderMap.put("ankle", v2ProductDto.getAnkle());
        featuresValueProviderMap.put("heal", v2ProductDto.getHeel());
        featuresValueProviderMap.put("closure", v2ProductDto.getClosure());
        featuresValueProviderMap.put("shape", v2ProductDto.getShape());
        featuresValueProviderMap.put("uppermaterial", v2ProductDto.getUpperMaterial());
        featuresValueProviderMap.put("dialmaterial", v2ProductDto.getDialMaterial());
        featuresValueProviderMap.put("dialshape", v2ProductDto.getDialShape());
        featuresValueProviderMap.put("framematerial", v2ProductDto.getFrameMaterial());
        featuresValueProviderMap.put("lenstechnology", v2ProductDto.getLensTechnology());
        featuresValueProviderMap.put("fold", v2ProductDto.getFold());
        featuresValueProviderMap.put("buckleshape", v2ProductDto.getBuckleShape());
        featuresValueProviderMap.put("packsize", v2ProductDto.getPackSize());
        featuresValueProviderMap.put("idealfor", v2ProductDto.getIdealFor());
        featuresValueProviderMap.put("fragrance", v2ProductDto.getFragrance());
        featuresValueProviderMap.put("compatiblelaptopsize", v2ProductDto.getCompatibleLaptopSize());
      //  featuresValueProviderMap.put("length", v2ProductDto.getLength());
        //added by Shivraj
      // featuresValueProviderMap.put("lengthtype", v2ProductDto.getLengthType());      
      //  featuresValueProviderMap.put("chest", v2ProductDto.getChest());
      // featuresValueProviderMap.put("bust", v2ProductDto.getBust());
       // featuresValueProviderMap.put("waist", v2ProductDto.getWaist());
    }
    /**
     * Sets the features value provider map.
     * 
     * @param v2ProductDto
     *        the new features value provider map
     */
  //added by Shivraj
    public final void setFeaturesValueProviderMapForSize(final V2ProductDto v2ProductDto) {
        featuresValueProviderMap = new HashMap<>();
        featuresValueProviderMap.put("fabric", v2ProductDto.getFabric());
        featuresValueProviderMap.put("fitting", v2ProductDto.getFitting());
        featuresValueProviderMap.put("sleeves", v2ProductDto.getSleeves());
        featuresValueProviderMap.put("neck", v2ProductDto.getNeck());
        featuresValueProviderMap.put("subfabric", v2ProductDto.getSubFabric());
        featuresValueProviderMap.put("basematerial", v2ProductDto.getBaseMaterial());
        featuresValueProviderMap.put("pockets", v2ProductDto.getPockets());
        featuresValueProviderMap.put("ankle", v2ProductDto.getAnkle());
        featuresValueProviderMap.put("heal", v2ProductDto.getHeel());
        featuresValueProviderMap.put("closure", v2ProductDto.getClosure());
        featuresValueProviderMap.put("shape", v2ProductDto.getShape());
        featuresValueProviderMap.put("uppermaterial", v2ProductDto.getUpperMaterial());
        featuresValueProviderMap.put("dialmaterial", v2ProductDto.getDialMaterial());
        featuresValueProviderMap.put("dialshape", v2ProductDto.getDialShape());
        featuresValueProviderMap.put("framematerial", v2ProductDto.getFrameMaterial());
        featuresValueProviderMap.put("lenstechnology", v2ProductDto.getLensTechnology());
        featuresValueProviderMap.put("fold", v2ProductDto.getFold());
        featuresValueProviderMap.put("buckleshape", v2ProductDto.getBuckleShape());
        featuresValueProviderMap.put("packsize", v2ProductDto.getPackSize());
        featuresValueProviderMap.put("idealfor", v2ProductDto.getIdealFor());
        featuresValueProviderMap.put("fragrance", v2ProductDto.getFragrance());
        featuresValueProviderMap.put("compatiblelaptopsize", v2ProductDto.getCompatibleLaptopSize());
        featuresValueProviderMap.put("length", v2ProductDto.getLength());
      //added by Shivraj
        featuresValueProviderMap.put("lengthtype", v2ProductDto.getLengthType());      
        featuresValueProviderMap.put("chest", v2ProductDto.getChest());
        featuresValueProviderMap.put("bust", v2ProductDto.getBust());
        featuresValueProviderMap.put("waist", v2ProductDto.getWaist());
    }
        
    
}
