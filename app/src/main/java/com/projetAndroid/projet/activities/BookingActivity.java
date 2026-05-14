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

    private Trip trip;
    private TripDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

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

        TextView tvSummary       = findViewById(R.id.tvBookingSummary);
        EditText etPassengerName  = findViewById(R.id.etPassengerName);
        EditText etPassengerPhone = findViewById(R.id.etPassengerPhone);
        EditText etBookedSeats    = findViewById(R.id.etBookedSeats);
        Button btnConfirm         = findViewById(R.id.btnConfirmBooking);

        tvSummary.setText(getString(R.string.booking_summary,
                trip.getDepart(), trip.getDestination(), trip.getDate(), trip.getPlaces()));

        btnConfirm.setOnClickListener(v -> {
            String passengerName  = etPassengerName.getText().toString().trim();
            String passengerPhone = etPassengerPhone.getText().toString().trim();
            String seatsText      = etBookedSeats.getText().toString().trim();

            if (!ValidationUtils.isNotBlank(passengerName)
                    || !ValidationUtils.isValidPhone(passengerPhone)
                    || !ValidationUtils.isPositiveInteger(seatsText)) {
                Toast.makeText(this, R.string.booking_validation_error, Toast.LENGTH_SHORT).show();
                return;
            }

            int requestedSeats = Integer.parseInt(seatsText);
            if (requestedSeats > trip.getPlaces()) {
                Toast.makeText(this, R.string.booking_seats_error, Toast.LENGTH_SHORT).show();
                return;
            }

            // Date du jour de la réservation
            String bookingDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .format(new Date());

            // Username du passager connecté
            String username = new SessionManager(this).getUsername();

            // Insertion en base de données
            long id = dbHelper.insertBooking(
                    trip.getId(),
                    username,
                    passengerName,
                    passengerPhone,
                    requestedSeats,
                    bookingDate
            );

            if (id <= 0) {
                Toast.makeText(this, "Erreur lors de la réservation", Toast.LENGTH_SHORT).show();
                return;
            }

            String confirmation = getString(R.string.booking_confirmation,
                    passengerName, trip.getDepart(), trip.getDestination());

            Intent data = new Intent();
            data.putExtra(EXTRA_CONFIRMATION, confirmation);
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
}