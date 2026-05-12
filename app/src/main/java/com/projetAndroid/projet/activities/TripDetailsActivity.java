package com.projetAndroid.projet.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.Locale;

/**
 * Détails complets du trajet + réservation + intents implicites Maps/SMS/partage.
 */
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

        findViewById(R.id.btnReserve).setOnClickListener(v -> openBooking());
        findViewById(R.id.btnOpenMaps).setOnClickListener(v -> openMaps());
        findViewById(R.id.btnSendSms).setOnClickListener(v -> sendSms());
        findViewById(R.id.btnShareTrip).setOnClickListener(v -> shareTrip());
        findViewById(R.id.btnEditTrip).setOnClickListener(v -> editTrip());
        findViewById(R.id.btnDeleteTrip).setOnClickListener(v -> deleteTrip());
    }

    private void bindTripData() {
        ((TextView) findViewById(R.id.tvDetailDepart)).setText(getString(R.string.detail_depart, trip.getDepart()));
        ((TextView) findViewById(R.id.tvDetailDestination)).setText(getString(R.string.detail_destination, trip.getDestination()));
        ((TextView) findViewById(R.id.tvDetailDate)).setText(getString(R.string.detail_date, trip.getDate()));
        ((TextView) findViewById(R.id.tvDetailPlaces)).setText(getString(R.string.detail_places, trip.getPlaces()));
        ((TextView) findViewById(R.id.tvDetailPrice)).setText(getString(R.string.detail_price,
                String.format(Locale.getDefault(), "%.2f", trip.getPrix())));
        ((TextView) findViewById(R.id.tvDetailPhone)).setText(getString(R.string.detail_phone, trip.getPhone()));
        ((TextView) findViewById(R.id.tvDetailVehicle)).setText(getString(R.string.detail_vehicle, trip.getVehicleType()));
        ((TextView) findViewById(R.id.tvDetailUserType)).setText(getString(R.string.detail_user_type, trip.getUserType()));
    }

    private void openBooking() {
        Intent intent = new Intent(this, BookingActivity.class);
        intent.putExtra(BookingActivity.EXTRA_TRIP, trip);
        bookingLauncher.launch(intent);
    }

    private void onBookingResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            String confirmation = result.getData().getStringExtra(BookingActivity.EXTRA_CONFIRMATION);
            Toast.makeText(this, confirmation, Toast.LENGTH_LONG).show();
        }
    }

    private void openMaps() {
        String query = trip.getDepart() + " to " + trip.getDestination();
        Uri uri = Uri.parse("geo:0,0?q=" + Uri.encode(query));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.no_maps_app, Toast.LENGTH_SHORT).show();
        }
    }

    private void sendSms() {
        String body = getString(R.string.sms_template,
                trip.getDepart(), trip.getDestination(), trip.getDate());
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.setData(Uri.parse("smsto:" + trip.getPhone()));
        smsIntent.putExtra("sms_body", body);
        if (smsIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(smsIntent);
        } else {
            Toast.makeText(this, R.string.no_sms_app, Toast.LENGTH_SHORT).show();
        }
    }

    private void shareTrip() {
        String text = getString(R.string.share_template,
                trip.getDepart(), trip.getDestination(), trip.getDate(),
                trip.getPlaces(), String.format(Locale.getDefault(), "%.2f", trip.getPrix()));
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
                        Toast.makeText(this, R.string.trip_deleted_success, Toast.LENGTH_SHORT).show();
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
}
