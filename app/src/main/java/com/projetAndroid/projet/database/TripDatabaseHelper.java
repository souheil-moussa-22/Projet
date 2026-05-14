package com.projetAndroid.projet.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.projetAndroid.projet.models.Trip;

import java.util.ArrayList;
import java.util.List;

public class TripDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME    = "covoiturage.db";
    public static final int    DATABASE_VERSION = 4;

    public static final String TABLE_TRIPS       = "trips";
    public static final String COL_ID            = "id";
    public static final String COL_DEPART        = "depart";
    public static final String COL_DESTINATION   = "destination";
    public static final String COL_DATE          = "date";
    public static final String COL_PLACES        = "places";
    public static final String COL_PRIX          = "prix";
    public static final String COL_PHONE         = "phone";
    public static final String COL_VEHICLE_TYPE  = "vehicle_type";
    public static final String COL_USER_TYPE     = "user_type";
    public static final String COL_OWNER         = "owner_username";

    public static final String TABLE_USERS    = "users";
    public static final String COL_USERNAME   = "username";
    public static final String COL_PASSWORD   = "password";

    public static final String TABLE_BOOKINGS        = "bookings";
    public static final String COL_BOOKING_ID        = "id";
    public static final String COL_BOOKING_TRIP_ID   = "trip_id";
    public static final String COL_BOOKING_USERNAME  = "username";
    public static final String COL_BOOKING_NAME      = "passenger_name";
    public static final String COL_BOOKING_PHONE     = "passenger_phone";
    public static final String COL_BOOKING_SEATS     = "seats";
    public static final String COL_BOOKING_DATE      = "booking_date";

    public TripDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_TRIPS + " ("
                + COL_ID           + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_DEPART       + " TEXT NOT NULL, "
                + COL_DESTINATION  + " TEXT NOT NULL, "
                + COL_DATE         + " TEXT NOT NULL, "
                + COL_PLACES       + " INTEGER NOT NULL, "
                + COL_PRIX         + " REAL NOT NULL, "
                + COL_PHONE        + " TEXT NOT NULL, "
                + COL_VEHICLE_TYPE + " TEXT NOT NULL, "
                + COL_USER_TYPE    + " TEXT NOT NULL, "
                + COL_OWNER        + " TEXT NOT NULL DEFAULT ''"
                + ")");

        db.execSQL("CREATE TABLE " + TABLE_USERS + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USERNAME + " TEXT NOT NULL UNIQUE, "
                + COL_PASSWORD + " TEXT NOT NULL, "
                + "user_type TEXT NOT NULL DEFAULT 'Passager'"
                + ")");

        db.execSQL("CREATE TABLE " + TABLE_BOOKINGS + " ("
                + COL_BOOKING_ID       + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_BOOKING_TRIP_ID  + " INTEGER NOT NULL, "
                + COL_BOOKING_USERNAME + " TEXT NOT NULL, "
                + COL_BOOKING_NAME     + " TEXT NOT NULL, "
                + COL_BOOKING_PHONE    + " TEXT NOT NULL, "
                + COL_BOOKING_SEATS    + " INTEGER NOT NULL, "
                + COL_BOOKING_DATE     + " TEXT NOT NULL"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        onCreate(db);
    }

    // ─── Trips ───────────────────────────────────────────────────────────────

    public long insertTrip(Trip trip, String ownerUsername) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = buildContentValues(trip);
        values.put(COL_OWNER, ownerUsername);
        return db.insert(TABLE_TRIPS, null, values);
    }

    public List<Trip> getAllTrips() {
        List<Trip> trips = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_TRIPS, null, null, null, null, null, COL_ID + " DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) trips.add(mapCursorToTrip(cursor));
            cursor.close();
        }
        return trips;
    }

    public List<Trip> getTripsByOwner(String username) {
        List<Trip> trips = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_TRIPS, null,
                COL_OWNER + "=?", new String[]{username},
                null, null, COL_ID + " DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) trips.add(mapCursorToTrip(cursor));
            cursor.close();
        }
        return trips;
    }

    public int updateTrip(Trip trip) {
        SQLiteDatabase db = getWritableDatabase();
        return db.update(TABLE_TRIPS, buildContentValues(trip),
                COL_ID + "=?", new String[]{String.valueOf(trip.getId())});
    }

    public int deleteTrip(long tripId) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_TRIPS, COL_ID + "=?", new String[]{String.valueOf(tripId)});
    }

    public int getTotalTripsCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_TRIPS, null);
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    // ─── Users ───────────────────────────────────────────────────────────────

    public boolean registerUser(String username, String password, String userType) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);
        values.put("user_type", userType);
        return db.insert(TABLE_USERS, null, values) != -1;
    }

    public boolean loginUser(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
                COL_USERNAME + "=? AND " + COL_PASSWORD + "=?",
                new String[]{username, password}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public String getUserType(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{"user_type"},
                COL_USERNAME + "=?", new String[]{username},
                null, null, null);
        String userType = "Passager";
        if (cursor != null && cursor.moveToFirst()) {
            userType = cursor.getString(0);
            cursor.close();
        }
        return userType;
    }

    // ─── Bookings ─────────────────────────────────────────────────────────────

    public long insertBooking(long tripId, String username, String name,
                              String phone, int seats, String date) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_BOOKING_TRIP_ID,  tripId);
        values.put(COL_BOOKING_USERNAME, username);
        values.put(COL_BOOKING_NAME,     name);
        values.put(COL_BOOKING_PHONE,    phone);
        values.put(COL_BOOKING_SEATS,    seats);
        values.put(COL_BOOKING_DATE,     date);
        return db.insert(TABLE_BOOKINGS, null, values);
    }

    /**
     * Pour le PASSAGER : ses réservations avec le trajet associé.
     */
    public List<String> getBookingsByUser(String username) {
        List<String> bookings = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query =
                "SELECT b." + COL_BOOKING_NAME   + ", "
                        + "b." + COL_BOOKING_SEATS  + ", "
                        + "b." + COL_BOOKING_DATE   + ", "
                        + "t." + COL_DEPART         + ", "
                        + "t." + COL_DESTINATION    + ", "
                        + "t." + COL_DATE
                        + " FROM " + TABLE_BOOKINGS + " b"
                        + " INNER JOIN " + TABLE_TRIPS + " t ON b." + COL_BOOKING_TRIP_ID + " = t." + COL_ID
                        + " WHERE b." + COL_BOOKING_USERNAME + " = ?"
                        + " ORDER BY b." + COL_BOOKING_ID + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{username});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String line =
                        "Trajet : " + cursor.getString(3) + " → " + cursor.getString(4) + "\n"
                                + "Date trajet : " + cursor.getString(5) + "\n"
                                + "Réservé le : " + cursor.getString(2) + "\n"
                                + "Passager : " + cursor.getString(0) + " | Places : " + cursor.getInt(1);
                bookings.add(line);
            }
            cursor.close();
        }
        return bookings;
    }

    /**
     * Pour le CONDUCTEUR : toutes les réservations faites sur ses trajets.
     */
    public List<String> getBookingsForDriver(String ownerUsername) {
        List<String> bookings = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query =
                "SELECT b." + COL_BOOKING_NAME   + ", "
                        + "b." + COL_BOOKING_PHONE  + ", "
                        + "b." + COL_BOOKING_SEATS  + ", "
                        + "b." + COL_BOOKING_DATE   + ", "
                        + "t." + COL_DEPART         + ", "
                        + "t." + COL_DESTINATION    + ", "
                        + "t." + COL_DATE
                        + " FROM " + TABLE_BOOKINGS + " b"
                        + " INNER JOIN " + TABLE_TRIPS + " t ON b." + COL_BOOKING_TRIP_ID + " = t." + COL_ID
                        + " WHERE t." + COL_OWNER + " = ?"
                        + " ORDER BY b." + COL_BOOKING_ID + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{ownerUsername});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String line =
                        "Trajet : " + cursor.getString(4) + " → " + cursor.getString(5) + "\n"
                                + "Date trajet : " + cursor.getString(6) + "\n"
                                + "Réservé le : " + cursor.getString(3) + "\n"
                                + "Passager : " + cursor.getString(0)
                                + " | Tél : " + cursor.getString(1)
                                + " | Places : " + cursor.getInt(2);
                bookings.add(line);
            }
            cursor.close();
        }
        return bookings;
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private ContentValues buildContentValues(Trip trip) {
        ContentValues values = new ContentValues();
        values.put(COL_DEPART,       trip.getDepart());
        values.put(COL_DESTINATION,  trip.getDestination());
        values.put(COL_DATE,         trip.getDate());
        values.put(COL_PLACES,       trip.getPlaces());
        values.put(COL_PRIX,         trip.getPrix());
        values.put(COL_PHONE,        trip.getPhone());
        values.put(COL_VEHICLE_TYPE, trip.getVehicleType());
        values.put(COL_USER_TYPE,    trip.getUserType());
        return values;
    }

    private Trip mapCursorToTrip(Cursor cursor) {
        return new Trip(
                cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_DEPART)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_DESTINATION)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_PLACES)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRIX)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_VEHICLE_TYPE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_TYPE))
        );
    }
}