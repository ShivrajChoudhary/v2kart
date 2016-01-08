/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package in.com.v2kart.sapinboundintegration.constants;

/**
 * Global class for all V2kartsapinboundintegration constants. You can add global constants for your extension into this
 * class.
 */
public final class V2kartsapinboundintegrationConstants extends GeneratedV2kartsapinboundintegrationConstants {
    public static final String EXTENSIONNAME = "v2kartsapinboundintegration";

    private V2kartsapinboundintegrationConstants() {
        // empty to avoid instantiating this constant class
    }

    // implement here constants used by this extension
    /**
     * Constant used for RESPONSE_CODE_SUCCESS
     */
    public static final String RESPONSE_CODE_SUCCESS = "S";

    /**
     * Constant used for RESPONSE_CODE_FAILURE
     */
    public static final String RESPONSE_CODE_FAILURE = "F";

    /**
     * Constant for Customer Typecode
     */
    public static final String USER_TYPECODE = "4";

    /**
     * Constants for sending sap inbound boolean data
     */
    public static final String BOOLEAN_INBOUND_VALUE_TRUE = "y";
    public static final String BOOLEAN_INBOUND_VALUE_FALSE = "n";

    public static final String STD_DEL_MODE_1 = "standard-ncr-within-city";
    public static final String STD_DEL_MODE_2 = "standard-within-region-upto-500-km";
    public static final String STD_DEL_MODE_3 = "standard-rest-of-india-above-500-km";
    public static final String STD_DEL_MODE_4 = "standard-north-east";
    public static final String STD_PICKUP = "standard-pickup";
    public static final String EXP_DEL_MODE_1 = "express-ncr-within-city";
    public static final String EXP_DEL_MODE_2 = "express-within-region-upto-500-km";
    public static final String EXP_DEL_MODE_3 = "express-rest-of-india-above-500-km";
    public static final String EXP_PICKUP = "express-pickup";

    public static final String SAP_DELV_MODE_CODE_1 = "01";
    public static final String SAP_DELV_MODE_CODE_2 = "02";
    public static final String SAP_PICKUP_MODE_CODE_3 = "03";

    public final static String BLANK_STR = "";

    public final static String SAP_INBOUND_DATE_FORMAT = "ddMMyyyy";
}
