package com.projetAndroid.projet.utils;

import java.util.regex.Pattern;

/**
 * Utilitaires de validation réutilisables pour éviter de dupliquer la logique.
 */
public final class ValidationUtils {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^(\\+?[0-9]{8,15})$");
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}$");

    private ValidationUtils() {
    }

    public static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidPhone(String phone) {
        return isNotBlank(phone) && PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isValidDate(String date) {
        return isNotBlank(date) && DATE_PATTERN.matcher(date.trim()).matches();
    }

    public static boolean isPositiveInteger(String value) {
        try {
            return Integer.parseInt(value) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isPositiveDouble(String value) {
        try {
            return Double.parseDouble(value) > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
