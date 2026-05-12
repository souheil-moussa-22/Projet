package com.projetAndroid.projet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.projetAndroid.projet.R;
import com.projetAndroid.projet.database.TripDatabaseHelper;

/**
 * Accueil avec Toolbar, Drawer, boutons de navigation et statistique des trajets.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private TripDatabaseHelper dbHelper;
    private TextView tvStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new TripDatabaseHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        tvStats = findViewById(R.id.tvMainStats);

        Button btnAddTrip = findViewById(R.id.btnAddTrip);
        Button btnListTrips = findViewById(R.id.btnListTrips);
        Button btnMyBookings = findViewById(R.id.btnMyBookings);

        btnAddTrip.setOnClickListener(v -> openAddTrip());
        btnListTrips.setOnClickListener(v -> openListTrips());
        btnMyBookings.setOnClickListener(v -> openMyBookings());
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvStats.setText(getString(R.string.total_trips_label, dbHelper.getTotalTripsCount()));
    }

    private void openAddTrip() {
        startActivity(new Intent(this, AddTripActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void openListTrips() {
        startActivity(new Intent(this, ListTripActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void openMyBookings() {
        startActivity(new Intent(this, MyBookingsActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_add_trip) {
            openAddTrip();
        } else if (itemId == R.id.nav_list_trips) {
            openListTrips();
        } else if (itemId == R.id.nav_bookings) {
            openMyBookings();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }
}
