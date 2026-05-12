package com.projetAndroid.projet.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.projetAndroid.projet.R;
import com.projetAndroid.projet.database.TripDatabaseHelper;
import com.projetAndroid.projet.models.Trip;
import com.projetAndroid.projet.utils.ValidationUtils;

/**
 * Formulaire complet d'ajout/modification de trajet avec validations avancées.
 */
public class AddTripActivity extends AppCompatActivity {

    public static final String EXTRA_TRIP = "extra_trip";

    private AutoCompleteTextView etDepart;
    private AutoCompleteTextView etDestination;
    private EditText etDate;
    private EditText etPrice;
    private EditText etPlaces;
    private EditText etPhone;
    private RadioGroup rgUserType;
    private Spinner spinnerVehicle;

    private TripDatabaseHelper dbHelper;
    private Trip editingTrip;

    private static final String[] CITIES = {
            "Tunis", "Ariana", "Ben Arous", "Mannouba", "Nabeul", "Zaghouan",
            "Kef", "Jendouba", "Bizerte", "Sousse", "Mahdia" , "Monastir",
            "Kairouan", "Sfax", "Gabes", "Gafsa", "Touzeur", "Kébili", "Médnine",
            "Tataouine", "Kasserine", "Beja", "Siliana", "Sidi Bouzid"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        dbHelper = new TripDatabaseHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbarAddTrip);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        bindViews();
        initSuggestionsAndSpinner();

        editingTrip = (Trip) getIntent().getSerializableExtra(EXTRA_TRIP);
        if (editingTrip != null) {
            prefillForm(editingTrip);
        }

        Button btnSave = findViewById(R.id.btnSaveTrip);
        btnSave.setOnClickListener(v -> validateAndSave());
    }

    private void bindViews() {
        etDepart = findViewById(R.id.etDepart);
        etDestination = findViewById(R.id.etDestination);
        etDate = findViewById(R.id.etDate);
        etPrice = findViewById(R.id.etPrice);
        etPlaces = findViewById(R.id.etPlaces);
        etPhone = findViewById(R.id.etPhone);
        rgUserType = findViewById(R.id.rgUserType);
        spinnerVehicle = findViewById(R.id.spinnerVehicleType);
    }

    private void initSuggestionsAndSpinner() {
        ArrayAdapter<String> citiesAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, CITIES);
        etDepart.setAdapter(citiesAdapter);
        etDestination.setAdapter(citiesAdapter);

        ArrayAdapter<CharSequence> vehicleAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.vehicle_types,
                android.R.layout.simple_spinner_item
        );
        vehicleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVehicle.setAdapter(vehicleAdapter);
    }

    private void prefillForm(Trip trip) {
        etDepart.setText(trip.getDepart());
        etDestination.setText(trip.getDestination());
        etDate.setText(trip.getDate());
        etPrice.setText(String.valueOf(trip.getPrix()));
        etPlaces.setText(String.valueOf(trip.getPlaces()));
        etPhone.setText(trip.getPhone());

        if ("Conducteur".equalsIgnoreCase(trip.getUserType())) {
            rgUserType.check(R.id.rbConducteur);
        } else {
            rgUserType.check(R.id.rbPassager);
        }

        String[] vehicles = getResources().getStringArray(R.array.vehicle_types);
        for (int i = 0; i < vehicles.length; i++) {
            if (vehicles[i].equalsIgnoreCase(trip.getVehicleType())) {
                spinnerVehicle.setSelection(i);
                break;
            }
        }
    }

    private void validateAndSave() {
        String depart = etDepart.getText().toString().trim();
        String destination = etDestination.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String priceText = etPrice.getText().toString().trim();
        String placesText = etPlaces.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (!ValidationUtils.isNotBlank(depart)
                || !ValidationUtils.isNotBlank(destination)
                || !ValidationUtils.isNotBlank(date)
                || !ValidationUtils.isNotBlank(priceText)
                || !ValidationUtils.isNotBlank(placesText)
                || !ValidationUtils.isNotBlank(phone)) {
            Toast.makeText(this, R.string.error_required_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!ValidationUtils.isValidDate(date)) {
            etDate.setError(getString(R.string.error_invalid_date));
            return;
        }

        if (!ValidationUtils.isPositiveDouble(priceText)) {
            etPrice.setError(getString(R.string.error_invalid_price));
            return;
        }

        if (!ValidationUtils.isPositiveInteger(placesText)) {
            etPlaces.setError(getString(R.string.error_invalid_places));
            return;
        }

        if (!ValidationUtils.isValidPhone(phone)) {
            etPhone.setError(getString(R.string.error_invalid_phone));
            return;
        }

        String userType = rgUserType.getCheckedRadioButtonId() == R.id.rbConducteur
                ? "Conducteur" : "Passager";
        String vehicleType = spinnerVehicle.getSelectedItem().toString();

        Trip trip = new Trip(depart, destination, date,
                Integer.parseInt(placesText),
                Double.parseDouble(priceText),
                phone, vehicleType, userType);

        if (editingTrip != null) {
            trip.setId(editingTrip.getId());
            int updated = dbHelper.updateTrip(trip);
            if (updated > 0) {
                Toast.makeText(this, R.string.trip_updated_success, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, R.string.trip_update_failed, Toast.LENGTH_SHORT).show();
            }
        } else {
            long id = dbHelper.insertTrip(trip);
            if (id > 0) {
                Toast.makeText(this, R.string.trip_added_success, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, R.string.trip_add_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
