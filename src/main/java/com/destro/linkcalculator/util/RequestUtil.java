package com.destro.linkcalculator.util;

import static com.destro.linkcalculator.util.Constants.EMPTY_FIELD;
import static com.destro.linkcalculator.util.Constants.SEPARATOR;

public class RequestUtil {

    private RequestUtil(){}

    public static String getStringFromPreparedString(final String preparedString) {
        return preparedString.replaceAll(EMPTY_FIELD, SEPARATOR).replaceAll("_", " ");
    }
}
