package com.projetAndroid.projet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.activity.OnBackPressedCallback;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.projetAndroid.projet.R;
import com.projetAndroid.projet.database.TripDatabaseHelper;
import com.projetAndroid.projet.utils.SessionManager;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private TripDatabaseHelper dbHelper;
    private TextView tvStats;
    private TextView tvWelcomeUser;
    private TextView tvTotalTripsValue;
    private TextView tvAvailablePlacesValue;
    private TextView tvMyBookingsValue;
    private SessionManager session;
    private boolean isConducteur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new TripDatabaseHelper(this);
        session = new SessionManager(this);
        if (!session.isLoggedIn()) {
            redirectToLogin();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        android.view.View header = navigationView.getHeaderView(0);
        ((TextView) header.findViewById(R.id.tvDrawerTitle)).setText(session.getUsername());
        ((TextView) header.findViewById(R.id.tvDrawerSubtitle)).setText(session.getUserType());

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        tvStats = findViewById(R.id.tvMainStats);
        tvWelcomeUser = findViewById(R.id.tvWelcomeUser);
        tvTotalTripsValue = findViewById(R.id.tvTotalTripsValue);
        tvAvailablePlacesValue = findViewById(R.id.tvAvailablePlacesValue);
        tvMyBookingsValue = findViewById(R.id.tvMyBookingsValue);
        tvWelcomeUser.setText(getString(R.string.welcome_user_message, session.getUsername()));

        Button btnAddTrip    = findViewById(R.id.btnAddTrip);
        Button btnListTrips  = findViewById(R.id.btnListTrips);
        Button btnMyBookings = findViewById(R.id.btnMyBookings);
        Button btnMyTrips    = findViewById(R.id.btnMyTrips);
        Button btnLogout     = findViewById(R.id.btnLogout);

        String userType = session.getUserType();
        isConducteur = "Conducteur".equalsIgnoreCase(userType);

        if (isConducteur) {
            btnAddTrip.setVisibility(View.VISIBLE);
            btnMyTrips.setVisibility(View.VISIBLE);
            btnListTrips.setVisibility(View.GONE);
            btnMyBookings.setVisibility(View.VISIBLE);

            btnAddTrip.setOnClickListener(v -> openAddTrip());
            btnMyTrips.setOnClickListener(v -> openMyTrips());
            btnMyBookings.setOnClickListener(v -> openMyBookings());
        } else {
            btnAddTrip.setVisibility(View.GONE);
            btnMyTrips.setVisibility(View.GONE);
            btnListTrips.setVisibility(View.VISIBLE);
            btnMyBookings.setVisibility(View.VISIBLE);

            btnListTrips.setOnClickListener(v -> openListTrips());
            btnMyBookings.setOnClickListener(v -> openMyBookings());
        }

        updateDrawerMenu(navigationView.getMenu(), isConducteur);
        btnLogout.setOnClickListener(v -> {
            session.logout();
            redirectToLogin();
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                    setEnabled(true);
                }
            }
        });
    }

    private void updateDrawerMenu(android.view.Menu menu, boolean isConducteur) {
        menu.findItem(R.id.nav_add_trip).setVisible(isConducteur);
        menu.findItem(R.id.nav_my_trips).setVisible(isConducteur);
        menu.findItem(R.id.nav_list_trips).setVisible(!isConducteur);
        menu.findItem(R.id.nav_bookings).setVisible(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int totalTrips = dbHelper.getTotalTripsCount();
        int availablePlaces = dbHelper.getTotalAvailablePlacesCount();
        int bookings = isConducteur
                ? dbHelper.getBookingsCountForDriver(session.getUsername())
                : dbHelper.getBookingsCountByUser(session.getUsername());

        tvTotalTripsValue.setText(String.valueOf(totalTrips));
        tvAvailablePlacesValue.setText(String.valueOf(availablePlaces));
        tvMyBookingsValue.setText(String.valueOf(bookings));
        tvStats.setText(getString(R.string.total_trips_label, totalTrips));
    }

    private void openAddTrip() {
        startActivity(new Intent(this, AddTripActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void openListTrips() {
        startActivity(new Intent(this, ListTripActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void openMyTrips() {
        Intent intent = new Intent(this, ListTripActivity.class);
        intent.putExtra(ListTripActivity.EXTRA_OWNER_ONLY, true);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void openMyBookings() {
        startActivity(new Intent(this, MyBookingsActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
        int id = item.getItemId();
        if      (id == R.id.nav_add_trip)   openAddTrip();
        else if (id == R.id.nav_my_trips)   openMyTrips();
        else if (id == R.id.nav_list_trips) openListTrips();
        else if (id == R.id.nav_bookings)   openMyBookings();
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
