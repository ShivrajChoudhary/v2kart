package in.com.v2kart.storefront.forms;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hybris.platform.acceleratorstorefrontcommons.forms.UpdateProfileForm;

public class V2UpdateProfileForm extends UpdateProfileForm {

    private String mobileNumber;
    private String email;
    private Date dateOfBirth;
    private String mobileDateOfBirth;
    private String maritalStatus;
    private String gender;
    private String isdCode;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Date getDateOfBirth() {
        return this.dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getMaritalStatus() {
        return this.maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIsdCode() {
        return isdCode;
    }

    public void setIsdCode(String isdCode) {
        this.isdCode = isdCode;
    }

    public String getMobileDateOfBirth() {
        String birthDate = null;
        if (dateOfBirth != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            birthDate = simpleDateFormat.format(dateOfBirth);
        } else {
            birthDate = mobileDateOfBirth;
        }
        return birthDate;
    }

    public void setMobileDateOfBirth(String mobileDateOfBirth) {
        this.mobileDateOfBirth = mobileDateOfBirth;
    }

    public void setInternalDateOfBirth(String mobileDateOfBirth) {
        if (mobileDateOfBirth != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                dateOfBirth = simpleDateFormat.parse(mobileDateOfBirth);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
