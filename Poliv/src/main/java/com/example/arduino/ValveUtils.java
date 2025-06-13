package com.example.arduino;

public class ValveUtils {
    public static String normalizeValveId(String valveId) {
        if (valveId != null && valveId.startsWith("v")) {
            return valveId.substring(1);
        }
        return valveId;
    }
}