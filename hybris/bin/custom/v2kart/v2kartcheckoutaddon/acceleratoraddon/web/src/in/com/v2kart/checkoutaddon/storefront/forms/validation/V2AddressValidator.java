/**
 *
 */
package in.com.v2kart.checkoutaddon.storefront.forms.validation;

import in.com.v2kart.checkoutaddon.storefront.forms.V2AddressForm;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * @author Anuj Kumar
 *
 */
public class V2AddressValidator implements Validator {

	private static final String SPACE = " ";
	private static final int MAX_FIELD_LENGTH = 255;
	private static final int MAX_POSTCODE_LENGTH = 10;
	private static final int MOBILENO_LENGTH = 10;
	public static final String NAME_REGEX = "\\b[A-Za-z ]+\\b";
	private static final String MAX_FIELD_LENGTH_ERROR_KEY = "form.field.invalid.max";
	public static final String INVALID_CHAR_ERROR_STRING = "form.field.invalid.char";
	public static final String INVALID_CHAR_STRING = "form.field.invalid.space";
	private static final int POSTCODE_LENGTH = 6;

	@Override
	public boolean supports(final Class<?> aClass) {
		return V2AddressForm.class.equals(aClass);
	}

	@Override
	public void validate(final Object object, final Errors errors) {
		final V2AddressForm addressForm = (V2AddressForm) object;
		validateStandardFields(addressForm, errors);
		validateCountrySpecificFields(addressForm, errors);
	}

	protected void validateStandardFields(final V2AddressForm addressForm,
			final Errors errors) {
		validateStringField(addressForm.getCountryIso(), AddressField.COUNTRY,
				MAX_FIELD_LENGTH, errors);
		validateStringField(addressForm.getFirstName(), AddressField.FIRSTNAME,
				MAX_FIELD_LENGTH, errors);
		validateStringField(addressForm.getLastName(), AddressField.LASTNAME,
				MAX_FIELD_LENGTH, errors);
		validateStringField(addressForm.getLine1(), AddressField.LINE1,
				MAX_FIELD_LENGTH, errors);
		validateStringField(addressForm.getTownCity(), AddressField.TOWN,
				MAX_FIELD_LENGTH, errors);
		validateFieldNotNull(addressForm.getRegionIso(), AddressField.REGION,
				errors);
		validatePostcode(addressForm.getPostcode(), errors);
		validateMobileNumber(addressForm, errors);
	}

	/**
	 * @param postcode
	 * @param errors
	 */
	private void validatePostcode(final String postcode, final Errors errors) {
		validateStringField(postcode, AddressField.POSTCODE, MAX_FIELD_LENGTH,
				errors);
		if (null == errors.getFieldError(AddressField.POSTCODE.getFieldKey())) {
			if (!StringUtils.isNumeric(postcode)) {
				errors.rejectValue(AddressField.POSTCODE.getFieldKey(),
						"form.field.invalid.numeric");
			} else if (POSTCODE_LENGTH != postcode.length()) {
				errors.rejectValue(AddressField.POSTCODE.getFieldKey(),
						"address.postcode.invalid.length");
			}
		}

	}

	protected void validateCountrySpecificFields(
			final V2AddressForm addressForm, final Errors errors) {
		final String isoCode = addressForm.getCountryIso();
		if (isoCode != null) {
			switch (CountryCode.lookup(isoCode)) {
			case CHINA:
				validateStringField(addressForm.getTitleCode(),
						AddressField.TITLE, MAX_FIELD_LENGTH, errors);
				validateFieldNotNull(addressForm.getRegionIso(),
						AddressField.REGION, errors);
				break;
			case CANADA:
				validateStringField(addressForm.getTitleCode(),
						AddressField.TITLE, MAX_FIELD_LENGTH, errors);
				validateFieldNotNull(addressForm.getRegionIso(),
						AddressField.REGION, errors);
				break;
			case USA:
				validateStringField(addressForm.getTitleCode(),
						AddressField.TITLE, MAX_FIELD_LENGTH, errors);
				validateFieldNotNull(addressForm.getRegionIso(),
						AddressField.REGION, errors);
				break;
			case JAPAN:
				validateFieldNotNull(addressForm.getRegionIso(),
						AddressField.REGION, errors);
				validateStringField(addressForm.getLine2(), AddressField.LINE2,
						MAX_FIELD_LENGTH, errors);
				break;
			default:
				// validateStringField(addressForm.getTitleCode(),
				// AddressField.TITLE, MAX_FIELD_LENGTH, errors);
				break;
			}
		}
	}

	protected static void validateStringField(final String addressField,
			final AddressField fieldType, final int maxFieldLength,
			final Errors errors) {
		if (addressField == null || StringUtils.isEmpty(addressField)) {
			errors.rejectValue(fieldType.getFieldKey(), fieldType.getErrorKey());
		} else if (StringUtils.length(addressField) > maxFieldLength) {
			errors.rejectValue(fieldType.getFieldKey(),
					MAX_FIELD_LENGTH_ERROR_KEY);
		}

		/**
		 * commented by shivraj for removing whitespace validation from field
		 * first name and last name
		 **/
		// else if (fieldType.equals(AddressField.FIRSTNAME) &&
		// addressField.trim().contains(SPACE))
		// {
		// errors.rejectValue(fieldType.getFieldKey(), INVALID_CHAR_STRING);
		// } else if (fieldType.equals(AddressField.LASTNAME) &&
		// addressField.trim().contains(SPACE))
		// {
		// errors.rejectValue(fieldType.getFieldKey(), INVALID_CHAR_STRING);
		// }
		else if (fieldType.equals(AddressField.FIRSTNAME)) {
			validateRegex(addressField, AddressField.FIRSTNAME, NAME_REGEX,
					errors);
		} else if (fieldType.equals(AddressField.LASTNAME)) {
			validateRegex(addressField, AddressField.LASTNAME, NAME_REGEX,
					errors);
		}
	}

	protected static void validateFieldNotNull(final String addressField,
			final AddressField fieldType, final Errors errors) {
		if (addressField == null) {
			errors.rejectValue(fieldType.getFieldKey(), fieldType.getErrorKey());
		}
	}

	protected static void validateRegex(final String addressField,
			final AddressField fieldType, final String regexExp,
			final Errors errors) {
		if (!addressField.matches(regexExp)) {
			errors.rejectValue(fieldType.getFieldKey(),
					INVALID_CHAR_ERROR_STRING);
		}

	}

	/**
	 *
	 */
	protected void validateMobileNumber(final V2AddressForm addressForm,
			final Errors errors) {
		validateStringField(addressForm.getMobileno(), AddressField.MOBILENO,
				MAX_FIELD_LENGTH, errors);
		if (null == errors.getFieldError(AddressField.MOBILENO.getFieldKey())) {
			if (!StringUtils.isNumeric(addressForm.getMobileno())) {
				errors.rejectValue(AddressField.MOBILENO.getFieldKey(),
						"form.field.invalid.numeric");
			} else if (MOBILENO_LENGTH != addressForm.getMobileno().length()) {
				errors.rejectValue(AddressField.MOBILENO.getFieldKey(),
						"address.mobileno.invalid.length");
			}
		}
	}

	protected enum CountryCode {
		USA("US"), CANADA("CA"), JAPAN("JP"), CHINA("CN"), BRITAIN("GB"), GERMANY(
				"DE"), DEFAULT("");

		private final String isoCode;

		private static Map<String, CountryCode> lookupMap = new HashMap<String, CountryCode>();
		static {
			for (final CountryCode code : CountryCode.values()) {
				lookupMap.put(code.getIsoCode(), code);
			}
		}

		private CountryCode(final String isoCodeStr) {
			this.isoCode = isoCodeStr;
		}

		public static CountryCode lookup(final String isoCodeStr) {
			CountryCode code = lookupMap.get(isoCodeStr);
			if (code == null) {
				code = DEFAULT;
			}
			return code;
		}

		public String getIsoCode() {
			return isoCode;
		}
	}

	protected enum AddressField {
		TITLE("titleCode", "form.field.required"), FIRSTNAME("firstName",
				"form.field.required"), LASTNAME("lastName",
				"form.field.required"), LINE1("line1", "form.field.required"), LINE2(
				"line2", "form.field.required"), TOWN("townCity",
				"form.field.required"), POSTCODE("postcode",
				"form.field.required"), REGION("regionIso",
				"form.field.required"), COUNTRY("countryIso",
				"form.field.required"), MOBILENO("mobileno",
				"form.field.required");

		private final String fieldKey;
		private final String errorKey;

		private AddressField(final String fieldKey, final String errorKey) {
			this.fieldKey = fieldKey;
			this.errorKey = errorKey;
		}

		public String getFieldKey() {
			return fieldKey;
		}

		public String getErrorKey() {
			return errorKey;
		}
	}

}
