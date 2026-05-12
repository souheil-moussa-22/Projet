package com.projetAndroid.projet.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.projetAndroid.projet.R;
import com.projetAndroid.projet.utils.BookingStorage;

import java.util.List;

/**
 * Écran simple listant les réservations enregistrées localement.
 */
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
        TextView tvEmpty = findViewById(R.id.tvEmptyBookings);

        List<String> bookings = BookingStorage.getBookings(this);
        if (bookings.isEmpty()) {
            tvEmpty.setVisibility(android.view.View.VISIBLE);
        } else {
            tvEmpty.setVisibility(android.view.View.GONE);
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
