package com.turinmachin.unilife.email;

public class EmailUtils {

    public static String extractDomain(final String email) {
        return email.substring(email.indexOf('@') + 1);
    }

}
