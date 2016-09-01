package com.sababado.mcpubs.backend.utils;

/**
 * Created by robert on 9/16/15.
 */
public class StringUtils {

    public static final String PROD_DB = "jdbc:google:mysql://voltaic-flag-141523:us-central1:mcpub?user=root";
    public static final String DEV_DB = "jdbc:mysql://127.0.0.1:8889/mcPubsDev?user=root";

    /**
     * Use this regex to find one of any letter.
     */
    public static final String REGEX_ANY_LETTER = "[a-zA-Z]";

    public static boolean isEmptyOrWhitespace(String string) {
        return string == null || string.trim().equals("");
    }

    /**
     * Return a comma separated string with <code>n</code> question marks in it.
     *
     * @param n Number of question marks to return.
     * @return A comma separated string of <code>n</code> question marks.
     */
    public static String generateQuestionString(int n) {
        if (n < 1) {
            return "";
        }
        String s = "?";
        for (int i = 1; i < n; i++) {
            s = s + ",?";
        }
        return s;
    }

    public static boolean isNumber(String str) {
        if (isEmptyOrWhitespace(str)) return false;
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
