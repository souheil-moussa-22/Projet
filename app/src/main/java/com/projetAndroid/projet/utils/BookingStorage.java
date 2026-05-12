package com.projetAndroid.projet.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Stockage simple des confirmations de réservation pour l'écran "Mes réservations".
 */
public final class BookingStorage {

    private static final String PREF_NAME = "bookings_pref";
    private static final String KEY_BOOKINGS = "bookings";

    private BookingStorage() {
    }

    public static void saveBooking(Context context, String bookingLine) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> bookings = new HashSet<>(prefs.getStringSet(KEY_BOOKINGS, new HashSet<>()));
        bookings.add(bookingLine);
        prefs.edit().putStringSet(KEY_BOOKINGS, bookings).apply();
    }

    public static List<String> getBookings(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return new ArrayList<>(prefs.getStringSet(KEY_BOOKINGS, new HashSet<>()));
    }
}
