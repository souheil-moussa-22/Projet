package com.projetAndroid.projet.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.projetAndroid.projet.models.Trip;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestionnaire SQLite central.
 * Fournit un CRUD complet pour la table trips afin de garantir la persistance locale.
 */
public class TripDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "covoiturage.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_TRIPS = "trips";
    public static final String COL_ID = "id";
    public static final String COL_DEPART = "depart";
    public static final String COL_DESTINATION = "destination";
    public static final String COL_DATE = "date";
    public static final String COL_PLACES = "places";
    public static final String COL_PRIX = "prix";
    public static final String COL_PHONE = "phone";
    public static final String COL_VEHICLE_TYPE = "vehicle_type";
    public static final String COL_USER_TYPE = "user_type";

    public TripDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTripsTable = "CREATE TABLE " + TABLE_TRIPS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_DEPART + " TEXT NOT NULL, "
                + COL_DESTINATION + " TEXT NOT NULL, "
                + COL_DATE + " TEXT NOT NULL, "
                + COL_PLACES + " INTEGER NOT NULL, "
                + COL_PRIX + " REAL NOT NULL, "
                + COL_PHONE + " TEXT NOT NULL, "
                + COL_VEHICLE_TYPE + " TEXT NOT NULL, "
                + COL_USER_TYPE + " TEXT NOT NULL"
                + ")";
        db.execSQL(createTripsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIPS);
        onCreate(db);
    }

    public long insertTrip(Trip trip) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = buildContentValues(trip);
        return db.insert(TABLE_TRIPS, null, values);
    }

    public List<Trip> getAllTrips() {
        List<Trip> trips = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_TRIPS, null, null, null, null, null, COL_ID + " DESC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                trips.add(mapCursorToTrip(cursor));
            }
            cursor.close();
        }
        return trips;
    }

    public int deleteTrip(long tripId) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_TRIPS, COL_ID + "=?", new String[]{String.valueOf(tripId)});
    }

    public int updateTrip(Trip trip) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = buildContentValues(trip);
        return db.update(TABLE_TRIPS, values, COL_ID + "=?", new String[]{String.valueOf(trip.getId())});
    }

    public int getTotalTripsCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_TRIPS, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    private ContentValues buildContentValues(Trip trip) {
        ContentValues values = new ContentValues();
        values.put(COL_DEPART, trip.getDepart());
        values.put(COL_DESTINATION, trip.getDestination());
        values.put(COL_DATE, trip.getDate());
        values.put(COL_PLACES, trip.getPlaces());
        values.put(COL_PRIX, trip.getPrix());
        values.put(COL_PHONE, trip.getPhone());
        values.put(COL_VEHICLE_TYPE, trip.getVehicleType());
        values.put(COL_USER_TYPE, trip.getUserType());
        return values;
    }

    private Trip mapCursorToTrip(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID));
        String depart = cursor.getString(cursor.getColumnIndexOrThrow(COL_DEPART));
        String destination = cursor.getString(cursor.getColumnIndexOrThrow(COL_DESTINATION));
        String date = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE));
        int places = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PLACES));
        double prix = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRIX));
        String phone = cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE));
        String vehicleType = cursor.getString(cursor.getColumnIndexOrThrow(COL_VEHICLE_TYPE));
        String userType = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_TYPE));
        return new Trip(id, depart, destination, date, places, prix, phone, vehicleType, userType);
    }
}
