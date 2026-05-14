package com.projetAndroid.projet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.projetAndroid.projet.R;
import com.projetAndroid.projet.database.TripDatabaseHelper;
import com.projetAndroid.projet.models.Trip;
import com.projetAndroid.projet.utils.SessionManager;
import com.projetAndroid.projet.utils.ValidationUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {

    public static final String EXTRA_TRIP = "extra_trip";
    public static final String EXTRA_CONFIRMATION = "extra_confirmation";
    public static final String EXTRA_UPDATED_TRIP = "extra_updated_trip";

    private Trip trip;
    private TripDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        if (!new SessionManager(this).isLoggedIn()) {
            redirectToLogin();
            return;
        }

        dbHelper = new TripDatabaseHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbarBooking);
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

        Trip latestTrip = dbHelper.getTripById(trip.getId());
        if (latestTrip != null) {
            trip = latestTrip;
        }

        TextView tvSummary       = findViewById(R.id.tvBookingSummary);
        EditText etPassengerName  = findViewById(R.id.etPassengerName);
        EditText etPassengerPhone = findViewById(R.id.etPassengerPhone);
        EditText etBookedSeats    = findViewById(R.id.etBookedSeats);
        Button btnConfirm         = findViewById(R.id.btnConfirmBooking);

        updateSummary(tvSummary);
        if (trip.getAvailablePlaces() == 0) {
            btnConfirm.setEnabled(false);
            Toast.makeText(this, R.string.trip_full, Toast.LENGTH_SHORT).show();
        }

        btnConfirm.setOnClickListener(v -> {
            String passengerName  = etPassengerName.getText().toString().trim();
            String passengerPhone = etPassengerPhone.getText().toString().trim();
            String seatsText      = etBookedSeats.getText().toString().trim();

            if (!ValidationUtils.isNotBlank(seatsText)) {
                Toast.makeText(this, R.string.booking_empty_seats, Toast.LENGTH_SHORT).show();
                return;
            }

            if (!ValidationUtils.isNotBlank(passengerName)
                    || !ValidationUtils.isValidPhone(passengerPhone)) {
                Toast.makeText(this, R.string.booking_validation_error, Toast.LENGTH_SHORT).show();
                return;
            }

            if (!ValidationUtils.isPositiveInteger(seatsText)) {
                Toast.makeText(this, R.string.booking_invalid_seats, Toast.LENGTH_SHORT).show();
                return;
            }

            Trip currentTrip = dbHelper.getTripById(trip.getId());
            if (currentTrip != null) {
                trip = currentTrip;
                updateSummary(tvSummary);
            }

            if (trip.getAvailablePlaces() == 0) {
                Toast.makeText(this, R.string.trip_full, Toast.LENGTH_SHORT).show();
                btnConfirm.setEnabled(false);
                return;
            }

            int requestedSeats = Integer.parseInt(seatsText);
            if (requestedSeats > trip.getAvailablePlaces()) {
                Toast.makeText(this, R.string.insufficient_places, Toast.LENGTH_SHORT).show();
                return;
            }

            // Date du jour de la réservation
            String bookingDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .format(new Date());

            // Username du passager connecté
            String username = new SessionManager(this).getUsername();

            // Réservation + décrément automatique des places disponibles en base.
            long id = dbHelper.bookTrip(
                    trip.getId(),
                    username,
                    passengerName,
                    passengerPhone,
                    requestedSeats,
                    bookingDate
            );

            if (id <= 0) {
                Trip latest = dbHelper.getTripById(trip.getId());
                if (latest != null) {
                    trip = latest;
                    updateSummary(tvSummary);
                }
                Toast.makeText(this, R.string.insufficient_places, Toast.LENGTH_SHORT).show();
                return;
            }

            Trip updatedTrip = dbHelper.getTripById(trip.getId());
            if (updatedTrip != null) {
                trip = updatedTrip;
            }

            String confirmation = getString(R.string.booking_confirmation,
                    passengerName, trip.getDepart(), trip.getDestination());

            Toast.makeText(this, R.string.booking_success, Toast.LENGTH_SHORT).show();
            Intent data = new Intent();
            data.putExtra(EXTRA_CONFIRMATION, confirmation);
            data.putExtra(EXTRA_UPDATED_TRIP, trip);
            setResult(RESULT_OK, data);
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(RESULT_CANCELED);
        finish();
        return true;
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void updateSummary(TextView tvSummary) {
        tvSummary.setText(getString(R.string.booking_summary,
                trip.getDepart(),
                trip.getDestination(),
                trip.getDate(),
                trip.getAvailablePlaces()));
    }
}
