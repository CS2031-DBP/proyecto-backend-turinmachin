package com.turinmachin.unilife.common.utils;

import java.text.Normalizer;

public class StringUtils {

    public static String removeAccents(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }

}
