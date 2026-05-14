package com.projetAndroid.projet.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.projetAndroid.projet.R;
import com.projetAndroid.projet.database.TripDatabaseHelper;
import com.projetAndroid.projet.models.Trip;
import com.projetAndroid.projet.utils.SessionManager;

import java.util.Locale;

public class TripDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_TRIP = "extra_trip";

    private Trip trip;
    private TripDatabaseHelper dbHelper;

    private final ActivityResultLauncher<Intent> bookingLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    this::onBookingResult);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        dbHelper = new TripDatabaseHelper(this);
        if (!new SessionManager(this).isLoggedIn()) {
            redirectToLogin();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbarTripDetails);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        trip = (Trip) getIntent().getSerializableExtra(EXTRA_TRIP);
        if (trip == null) {
            Toast.makeText(this, R.string.trip_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindTripData();

        Button btnReserve   = findViewById(R.id.btnReserve);
        Button btnOpenMaps  = findViewById(R.id.btnOpenMaps);
        Button btnShareTrip = findViewById(R.id.btnShareTrip);
        Button btnEditTrip  = findViewById(R.id.btnEditTrip);
        Button btnDeleteTrip= findViewById(R.id.btnDeleteTrip);

        // Récupérer le rôle de l'utilisateur connecté
        String userType = new SessionManager(this).getUserType();
        boolean isConducteur = "Conducteur".equalsIgnoreCase(userType);

        if (isConducteur) {
            // Conducteur : peut modifier et supprimer, mais PAS réserver
            btnReserve.setVisibility(View.GONE);
            btnEditTrip.setVisibility(View.VISIBLE);
            btnDeleteTrip.setVisibility(View.VISIBLE);
        } else {
            // Passager : peut réserver, mais PAS modifier ni supprimer
            btnReserve.setVisibility(View.VISIBLE);
            btnEditTrip.setVisibility(View.GONE);
            btnDeleteTrip.setVisibility(View.GONE);
        }

        btnReserve.setOnClickListener(v -> openBooking());
        btnOpenMaps.setOnClickListener(v -> openMaps());
        btnShareTrip.setOnClickListener(v -> shareTrip());
        btnEditTrip.setOnClickListener(v -> editTrip());
        btnDeleteTrip.setOnClickListener(v -> deleteTrip());
    }

    private void bindTripData() {
        ((TextView) findViewById(R.id.tvDetailDepart)).setText(
                getString(R.string.detail_depart, trip.getDepart()));
        ((TextView) findViewById(R.id.tvDetailDestination)).setText(
                getString(R.string.detail_destination, trip.getDestination()));
        ((TextView) findViewById(R.id.tvDetailDate)).setText(
                getString(R.string.detail_date, trip.getDate()));
        ((TextView) findViewById(R.id.tvDetailPlaces)).setText(
                getString(R.string.detail_places, trip.getPlaces()));
        ((TextView) findViewById(R.id.tvDetailPrice)).setText(
                getString(R.string.detail_price,
                        String.format(Locale.getDefault(), "%.2f", trip.getPrix())));
        ((TextView) findViewById(R.id.tvDetailPhone)).setText(
                getString(R.string.detail_phone, trip.getPhone()));
        ((TextView) findViewById(R.id.tvDetailVehicle)).setText(
                getString(R.string.detail_vehicle, trip.getVehicleType()));
        ((TextView) findViewById(R.id.tvDetailUserType)).setText(
                getString(R.string.detail_user_type, trip.getUserType()));
    }

    private void openBooking() {
        Intent intent = new Intent(this, BookingActivity.class);
        intent.putExtra(BookingActivity.EXTRA_TRIP, trip);
        bookingLauncher.launch(intent);
    }

    private void onBookingResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            String confirmation = result.getData()
                    .getStringExtra(BookingActivity.EXTRA_CONFIRMATION);
            Toast.makeText(this, confirmation, Toast.LENGTH_LONG).show();
        }
    }

    private void openMaps() {
        // URI Google Maps directions : de départ à destination
        String uri = "https://www.google.com/maps/dir/?api=1"
                + "&origin=" + Uri.encode(trip.getDepart() + ", Tunisie")
                + "&destination=" + Uri.encode(trip.getDestination() + ", Tunisie")
                + "&travelmode=driving";

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps"); // forcer Google Maps

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // Google Maps non installé → ouvrir dans le navigateur
            intent.setPackage(null);
            startActivity(intent);
        }
    }

    private void shareTrip() {
        String text = getString(R.string.share_template,
                trip.getDepart(), trip.getDestination(), trip.getDate(),
                trip.getPlaces(),
                String.format(Locale.getDefault(), "%.2f", trip.getPrix()));
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_trip)));
    }

    private void editTrip() {
        Intent intent = new Intent(this, AddTripActivity.class);
        intent.putExtra(AddTripActivity.EXTRA_TRIP, trip);
        startActivity(intent);
    }

    private void deleteTrip() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_trip_title)
                .setMessage(R.string.delete_trip_message)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    int deleted = dbHelper.deleteTrip(trip.getId());
                    if (deleted > 0) {
                        Toast.makeText(this, R.string.trip_deleted_success,
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
