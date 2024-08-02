package ru.msvdev.ds.server.module.upload.base;

public class Base64Calculator {

    public static int base64StringToCountOfBytes(String string) {
        int stringLength = string.length();

        if (stringLength == 0) return 0;
        if (stringLength % 4 != 0) throw new RuntimeException("Error base64 string length");

        int correction = string.endsWith("=") ? string.endsWith("==") ? 2 : 1 : 0;

        return stringLength * 3 / 4 - correction;
    }

}
