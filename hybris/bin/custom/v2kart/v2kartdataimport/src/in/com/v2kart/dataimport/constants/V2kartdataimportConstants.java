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
package in.com.v2kart.dataimport.constants;

import org.supercsv.prefs.CsvPreference;

/**
 * Global class for all Nagarro data import constants. You can add global constants for your extension into this class.
 */
public final class V2kartdataimportConstants extends GeneratedV2kartdataimportConstants {
    public static final String EXTENSIONNAME = "v2kartdataimport";

    private V2kartdataimportConstants() {
        // empty to avoid instantiating this constant class
    }

    // implement here constants used by this extension
    public static final String MAPPING_FIELD_PREFIX = "_";
    public static final String MAPPING_FIELD_HASH = MAPPING_FIELD_PREFIX + "hash";

    public final static String CHEQUE_PAYMENT_MODE = "CHEQ";

    public static final CsvPreference PIPE_DELIMITED = new CsvPreference.Builder('"', '|', "\n").build();

}
