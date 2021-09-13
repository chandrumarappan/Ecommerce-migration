package com.paloit.ecom.utility;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

public class CsvUtility {

    public static String CONTENT_TYPE = "text/csv";
    public static final int HEADER = 14;
    public static final int RANDOM_LOWER_BOUND = 1000000;
    public static final int RANDOM_UPPER_BOUND = 9000000;
    public static final String START_NRIC_CHAR = "STFG";
    public static final String END_NRIC_CHAR = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("M/d/yyyy", Locale.ENGLISH);

    public final static String getRandomNricId() {
        Random random = new Random();
        int n = RANDOM_LOWER_BOUND + random.nextInt(RANDOM_UPPER_BOUND);
        char[] start = START_NRIC_CHAR.toCharArray();
        char[] end = END_NRIC_CHAR.toCharArray();
        return new StringBuilder().append(start[random.nextInt(start.length)]).append(n).append(end[random.nextInt(end.length)]).toString();
    }
}
