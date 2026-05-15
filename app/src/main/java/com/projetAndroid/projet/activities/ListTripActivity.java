package com.projetAndroid.projet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.projetAndroid.projet.R;
import com.projetAndroid.projet.adapters.TripAdapter;
import com.projetAndroid.projet.database.TripDatabaseHelper;
import com.projetAndroid.projet.models.Trip;
import com.projetAndroid.projet.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListTripActivity extends AppCompatActivity implements TripAdapter.OnTripActionListener {

    public static final String EXTRA_OWNER_ONLY = "extra_owner_only";

    private TripDatabaseHelper dbHelper;
    private TripAdapter adapter;
    private final List<Trip> allTrips = new ArrayList<>();

    private EditText etSearch;
    private Spinner spinnerFilterCity;
    private TextView tvEmpty;
    private RecyclerView recyclerView;
    private boolean ownerOnly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_trip);
        if (!new SessionManager(this).isLoggedIn()) {
            redirectToLogin();
            return;
        }

        dbHelper = new TripDatabaseHelper(this);
        ownerOnly = getIntent().getBooleanExtra(EXTRA_OWNER_ONLY, false);

        Toolbar toolbar = findViewById(R.id.toolbarListTrip);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(ownerOnly
                    ? getString(R.string.my_trips_title)
                    : getString(R.string.list_trips_title));
        }

        etSearch          = findViewById(R.id.etSearchTrips);
        spinnerFilterCity = findViewById(R.id.spinnerFilterCity);
        tvEmpty           = findViewById(R.id.tvEmptyList);

        recyclerView = findViewById(R.id.recyclerTrips);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TripAdapter(this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fabAddTrip = findViewById(R.id.fabAddTrip);
        // Le FAB n'est visible que pour le conducteur en mode "mes trajets"
        fabAddTrip.setVisibility(ownerOnly ? android.view.View.VISIBLE : android.view.View.GONE);
        fabAddTrip.setOnClickListener(v -> {
            startActivity(new Intent(this, AddTripActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) { }
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) { applyFilters(); }
            @Override public void afterTextChanged(Editable s) { }
        });

        spinnerFilterCity.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                applyFilters();
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTrips();
    }

    private void loadTrips() {
        allTrips.clear();
        if (ownerOnly) {
            String username = new SessionManager(this).getUsername();
            allTrips.addAll(dbHelper.getTripsByOwner(username));
        } else {
            allTrips.addAll(dbHelper.getAllTrips());
        }
        setupCityFilter(allTrips);
        applyFilters();
    }

    private void setupCityFilter(List<Trip> trips) {
        List<String> cities = new ArrayList<>();
        cities.add(getString(R.string.filter_all_cities));
        for (Trip trip : trips) {
            if (!cities.contains(trip.getDepart())) {
                cities.add(trip.getDepart());
            }
        }
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, cities);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterCity.setAdapter(cityAdapter);
    }

    private void applyFilters() {
        String query = etSearch.getText().toString().trim().toLowerCase(Locale.getDefault());
        String selectedCity = spinnerFilterCity.getSelectedItem() == null
                ? getString(R.string.filter_all_cities)
                : spinnerFilterCity.getSelectedItem().toString();

        List<Trip> filtered = new ArrayList<>();
        for (Trip trip : allTrips) {
            boolean matchesQuery = query.isEmpty()
                    || trip.getDepart().toLowerCase(Locale.getDefault()).contains(query)
                    || trip.getDestination().toLowerCase(Locale.getDefault()).contains(query)
                    || trip.getDate().toLowerCase(Locale.getDefault()).contains(query);

            boolean matchesCity = selectedCity.equals(getString(R.string.filter_all_cities))
                    || trip.getDepart().equalsIgnoreCase(selectedCity);

            if (matchesQuery && matchesCity) filtered.add(trip);
        }

        adapter.submitList(filtered);
        recyclerView.scheduleLayoutAnimation();
        tvEmpty.setVisibility(filtered.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
    }

    @Override
    public void onTripClick(Trip trip) {
        Intent intent = new Intent(this, TripDetailsActivity.class);
        intent.putExtra(TripDetailsActivity.EXTRA_TRIP, trip);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onEditClick(Trip trip) {
        Intent intent = new Intent(this, AddTripActivity.class);
        intent.putExtra(AddTripActivity.EXTRA_TRIP, trip);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onDeleteClick(Trip trip) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_trip_title)
                .setMessage(R.string.delete_trip_message)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    int deleted = dbHelper.deleteTrip(trip.getId());
                    if (deleted > 0) {
                        Toast.makeText(this, R.string.trip_deleted_success, Toast.LENGTH_SHORT).show();
                        loadTrips();
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
