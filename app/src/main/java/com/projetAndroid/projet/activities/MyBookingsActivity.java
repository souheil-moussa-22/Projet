package com.projetAndroid.projet.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.projetAndroid.projet.R;
import com.projetAndroid.projet.database.TripDatabaseHelper;
import com.projetAndroid.projet.utils.SessionManager;

import java.util.List;

public class MyBookingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        Toolbar toolbar = findViewById(R.id.toolbarBookings);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ListView listView = findViewById(R.id.listBookings);
        TextView tvEmpty  = findViewById(R.id.tvEmptyBookings);

        TripDatabaseHelper dbHelper = new TripDatabaseHelper(this);
        SessionManager session      = new SessionManager(this);
        String username             = session.getUsername();
        String userType             = session.getUserType();

        List<String> bookings;
        if ("Conducteur".equalsIgnoreCase(userType)) {
            // Conducteur : voir les réservations faites sur ses trajets
            bookings = dbHelper.getBookingsForDriver(username);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Réservations reçues");
            }
        } else {
            // Passager : voir ses propres réservations
            bookings = dbHelper.getBookingsByUser(username);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Mes réservations");
            }
        }

        if (bookings.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    bookings
            );
            listView.setAdapter(adapter);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}