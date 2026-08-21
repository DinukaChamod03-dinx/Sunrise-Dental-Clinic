package com.sunrise.dental.util;

/**
 * Tiny, dependency-free JSON building helper.
 * Avoids requiring external libraries (e.g. Gson) to be added to the project.
 */
public class JsonUtil {

    public static String escape(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                     .replace("\"", "\\\"")
                     .replace("\n", "\\n")
                     .replace("\r", "");
    }

    public static String pair(String key, String value) {
        return "\"" + key + "\":\"" + escape(value) + "\"";
    }

    public static String pairRaw(String key, String rawValue) {
        return "\"" + key + "\":" + rawValue;
    }

    public static String pairNum(String key, double value) {
        return "\"" + key + "\":" + value;
    }

    public static String pairBool(String key, boolean value) {
        return "\"" + key + "\":" + value;
    }
}
