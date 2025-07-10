package com.turinmachin.unilife.common.utils;

import java.text.Normalizer;

public class StringUtils {

    public static String removeAccents(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD);
    }

}
