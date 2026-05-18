package me.jddev0.ep.util;

import java.util.TreeMap;

public final class NumberUtils {
    private NumberUtils() {}

    private final static TreeMap<Integer, String> ARABIC_TO_ROMAN_NUMERAL = new TreeMap<>();
    static {
        ARABIC_TO_ROMAN_NUMERAL.put(1000, "M");
        ARABIC_TO_ROMAN_NUMERAL.put(900, "CM");
        ARABIC_TO_ROMAN_NUMERAL.put(500, "D");
        ARABIC_TO_ROMAN_NUMERAL.put(400, "CD");
        ARABIC_TO_ROMAN_NUMERAL.put(100, "C");
        ARABIC_TO_ROMAN_NUMERAL.put(90, "XC");
        ARABIC_TO_ROMAN_NUMERAL.put(50, "L");
        ARABIC_TO_ROMAN_NUMERAL.put(40, "XL");
        ARABIC_TO_ROMAN_NUMERAL.put(10, "X");
        ARABIC_TO_ROMAN_NUMERAL.put(9, "IX");
        ARABIC_TO_ROMAN_NUMERAL.put(5, "V");
        ARABIC_TO_ROMAN_NUMERAL.put(4, "IV");
        ARABIC_TO_ROMAN_NUMERAL.put(1, "I");
    }

    public static String convertToRoman(int num) {
        if(num <= 0) {
            return "";
        }

        int key = ARABIC_TO_ROMAN_NUMERAL.floorKey(num);
        return ARABIC_TO_ROMAN_NUMERAL.get(key) + convertToRoman(num - key);
    }
}
