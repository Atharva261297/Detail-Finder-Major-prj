package com.destro.linkcalculator.util;

public class Constants {
    private Constants() {
    }

    public static final String EMPTY_FIELD = "-";

    public static final String SEPARATOR = "/";

    public static final String REST_LINK = "REST";

    public static final String RETURN_TO_NGP = "/NGP";

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String UNKNOWN_GET_LINK_ERROR_RESPONSE = "Unable to process request to get link";

    public static final String TRAIN_NO_PATTERN = "[0-9]{5}";

    public static final String ONE_WAY_LINK_PATTERN = "^[A-Z]{3}[/][A-Z]{3}";

    public static final String TWO_WAY_LINK_PATTERN = "^[A-Z]{3}[/][A-Z]{3}[/][A-Z]{3}";

    public static final String MULTIPLE_TRAIN_NO_PATTERN = "^.*[1-9]{5}[/].*[1-9]{5}";
}
