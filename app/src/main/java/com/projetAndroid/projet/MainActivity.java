package com.projetAndroid.projet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activité principale — page d'accueil de l'application.
 *
 * Propose deux actions principales :
 *   • Ajouter un trajet  → AddTripActivity
 *   • Voir les trajets   → ListTripActivity
 *
 * La navigation utilise des Intents explicites (TP2).
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bouton "Ajouter un trajet" → Intent explicite vers AddTripActivity
        Button btnAddTrip = findViewById(R.id.btnAddTrip);
        btnAddTrip.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddTripActivity.class))
        );

        // Bouton "Voir les trajets" → Intent explicite vers ListTripActivity
        Button btnListTrip = findViewById(R.id.btnListTrip);
        btnListTrip.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ListTripActivity.class))
        );
    }
}