package in.com.v2kart.storefront.forms.validation;

import in.com.v2kart.storefront.forms.V2AddressForm;

import java.util.HashMap;
import java.util.Map;





import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("v2addressValidator")
public class V2AddressFormValidator implements Validator {

    private static final int MAX_FIELD_LENGTH = 255;
    private static final int MAX_POSTCODE_LENGTH = 6;
    private static final int PHONE_LENGTH = 10;
    public static final String NAME_REGEX = "\\b[A-Za-z]+[A-Za-z ]*\\b";

    @Override
    public boolean supports(final Class<?> aClass)
    {
    return V2AddressForm.class.equals(aClass);
    }

    @Override
    public void validate(final Object object, final Errors errors)
    {
    final V2AddressForm addressForm = (V2AddressForm) object;
    validateStandardFields(addressForm, errors);
    //validateCountrySpecificFields(addressForm, errors);
    }

    protected void validateStandardFields(final V2AddressForm addressForm, final Errors errors)
    {
    //validateStringField(addressForm.getCountryIso(), AddressField.COUNTRY, MAX_FIELD_LENGTH, errors);
    validateName(addressForm.getFirstName(), AddressField.FIRSTNAME, MAX_FIELD_LENGTH, errors);
    validateName(addressForm.getLastName(), AddressField.LASTNAME, MAX_FIELD_LENGTH, errors);
    validateStringField(addressForm.getLine1(), AddressField.LINE1, MAX_FIELD_LENGTH, errors);
    validateStringField(addressForm.getTownCity(), AddressField.TOWN, MAX_FIELD_LENGTH, errors);
    validatePostCodeField(addressForm.getPostcode(), AddressField.POSTCODE, MAX_POSTCODE_LENGTH, errors);
    validatePhoneNoField(addressForm.getPhoneNo(), AddressField.PHONENO, PHONE_LENGTH, errors);
    validateFieldNotNull(addressForm.getRegionIso(), AddressField.REGION, errors);
    }

    protected void validateCountrySpecificFields(final V2AddressForm addressForm, final Errors errors)
    {
    final String isoCode = addressForm.getCountryIso();
    if (isoCode != null)
    {
    switch (CountryCode.lookup(isoCode))
    {
    case CHINA:
    validateStringField(addressForm.getTitleCode(), AddressField.TITLE, MAX_FIELD_LENGTH, errors);
    validateFieldNotNull(addressForm.getRegionIso(), AddressField.REGION, errors);
    break;
    case CANADA:
    validateStringField(addressForm.getTitleCode(), AddressField.TITLE, MAX_FIELD_LENGTH, errors);
    validateFieldNotNull(addressForm.getRegionIso(), AddressField.REGION, errors);
    break;
    case USA:
    validateStringField(addressForm.getTitleCode(), AddressField.TITLE, MAX_FIELD_LENGTH, errors);
    validateFieldNotNull(addressForm.getRegionIso(), AddressField.REGION, errors);
    break;
    case JAPAN:
    validateFieldNotNull(addressForm.getRegionIso(), AddressField.REGION, errors);
    validateStringField(addressForm.getLine2(), AddressField.LINE2, MAX_FIELD_LENGTH, errors);
    break;
    default:
    validateStringField(addressForm.getTitleCode(), AddressField.TITLE, MAX_FIELD_LENGTH, errors);
    break;
    }
    }
    }

    protected static void validateStringField(final String addressField, final AddressField fieldType,
      final int maxFieldLength, final Errors errors)
    {
    if (addressField == null || StringUtils.isEmpty(addressField) || (StringUtils.length(addressField) > maxFieldLength))
    {
    errors.rejectValue(fieldType.getFieldKey(), fieldType.getErrorKey());
    }
    }

    protected static void validateFieldNotNull(final String addressField, final AddressField fieldType,
       final Errors errors)
    {
    if (addressField == null)
    {
    errors.rejectValue(fieldType.getFieldKey(), fieldType.getErrorKey());
    }
    }

    protected enum CountryCode
    {
    USA("US"), CANADA("CA"), JAPAN("JP"), CHINA("CN"), BRITAIN("GB"), GERMANY("DE"), DEFAULT("");

    private String isoCode;

    private static Map<String, CountryCode> lookupMap = new HashMap<String, CountryCode>();
    static
    {
    for (final CountryCode code : CountryCode.values())
    {
    lookupMap.put(code.getIsoCode(), code);
    }
    }

    private CountryCode(final String isoCodeStr)
    {
    this.isoCode = isoCodeStr;
    }

    public static CountryCode lookup(final String isoCodeStr)
    {
    CountryCode code = lookupMap.get(isoCodeStr);
    if (code == null)
    {
    code = DEFAULT;
    }
    return code;
    }

    public String getIsoCode()
    {
    return isoCode;
    }
    }

    protected static void validatePhoneNoField(final String addressField, final AddressField fieldType,
            final int FieldLength, final Errors errors)
          {
          if (addressField == null || StringUtils.isEmpty(addressField) || (StringUtils.length(addressField) != FieldLength))
          {
          errors.rejectValue(fieldType.getFieldKey(), fieldType.getErrorKey());
          }else{
              if (!StringUtils.isNumeric(addressField)) {
                  errors.rejectValue(fieldType.getFieldKey(),"address.phone.alphanumeric");
              }
          }
         
          }
    
    
    
    
    protected static void validateName(final String addressField, final AddressField fieldType,
            final int maxFieldLength, final Errors errors)
          {
        if (addressField == null || StringUtils.isEmpty(addressField) || (StringUtils.length(addressField) > maxFieldLength))
        {
        errors.rejectValue(fieldType.getFieldKey(), fieldType.getErrorKey());
        }else{
                  
              if (!validateRegex(addressField, NAME_REGEX))
              {
                  errors.rejectValue(fieldType.getFieldKey(), "profile.validName.invalid");
              }
          }
         
          }
    
    protected static boolean validateRegex(final String str, final String regex) {
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
    
    
    
    
    
    
    
    
    
    
    
    protected static void validatePostCodeField(final String postCode, final AddressField fieldType,
            final int FieldLength, final Errors errors)
          {
          if (postCode == null || StringUtils.isEmpty(postCode) || (StringUtils.length(postCode) != FieldLength))
          {
          errors.rejectValue(fieldType.getFieldKey(), fieldType.getErrorKey());
          }else{
              if (!StringUtils.isNumeric(postCode)) {
                  errors.rejectValue(fieldType.getFieldKey(),"address.pincode.alphanumeric");
              }
          }
         
          }
    
    protected enum AddressField
    {
    TITLE("titleCode", "address.title.invalid"), FIRSTNAME("firstName", "address.firstName.invalid"),
    LASTNAME("lastName", "address.lastName.invalid"), LINE1("line1", "address.line1.invalid"),
    LINE2("line2", "address.line2.invalid"), TOWN("townCity", "address.townCity.invalid"),
    POSTCODE("postcode", "address.postcode.invalid"), REGION("regionIso", "address.regionIso.invalid"),
    COUNTRY("countryIso", "address.country.invalid"), PHONENO("phoneNo", "address.phone.invalid");

    private String fieldKey;
    private String errorKey;

    private AddressField(final String fieldKey, final String errorKey)
    {
    this.fieldKey = fieldKey;
    this.errorKey = errorKey;
    }

    public String getFieldKey()
    {
    return fieldKey;
    }

    public String getErrorKey()
    {
    return errorKey;
    }
    }
}
