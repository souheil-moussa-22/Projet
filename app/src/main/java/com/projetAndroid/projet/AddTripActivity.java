package com.projetAndroid.projet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activité pour ajouter un nouveau trajet.
 *
 * TP1 : Utilise EditText, TextView, Button, RadioGroup pour le formulaire.
 * TP2 : Passe les données du trajet créé à TripDetailsActivity via putExtra.
 * TP4 : Utilise AutoCompleteTextView pour la saisie des villes.
 */
public class AddTripActivity extends AppCompatActivity {

    private AutoCompleteTextView etDepart;
    private AutoCompleteTextView etDestination;
    private EditText etDate;
    private EditText etPlaces;
    private RadioGroup rgType;

    // Liste de villes françaises pour l'auto-complétion (TP4)
    private static final String[] VILLES = {
            "Paris", "Lyon", "Marseille", "Toulouse", "Nice",
            "Nantes", "Strasbourg", "Montpellier", "Bordeaux", "Lille",
            "Rennes", "Reims", "Le Havre", "Grenoble", "Dijon",
            "Angers", "Nîmes", "Villeurbanne", "Saint-Étienne", "Toulon"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        // Liaison des composants XML (TP1)
        etDepart      = findViewById(R.id.etDepart);
        etDestination = findViewById(R.id.etDestination);
        etDate        = findViewById(R.id.etDate);
        etPlaces      = findViewById(R.id.etPlaces);
        rgType        = findViewById(R.id.rgType);
        Button btnSave = findViewById(R.id.btnSave);

        // Configurer l'AutoCompleteTextView avec la liste des villes (TP4)
        ArrayAdapter<String> villesAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, VILLES);
        etDepart.setAdapter(villesAdapter);
        etDestination.setAdapter(villesAdapter);

        btnSave.setOnClickListener(v -> saveTrip());
    }

    /**
     * Valide le formulaire, crée un objet Trip et le stocke dans le Repository.
     * Navigue ensuite vers TripDetailsActivity en passant le trajet via putExtra (TP2).
     */
    private void saveTrip() {
        String depart      = etDepart.getText().toString().trim();
        String destination = etDestination.getText().toString().trim();
        String date        = etDate.getText().toString().trim();
        String placesStr   = etPlaces.getText().toString().trim();

        // Validation : tous les champs sont obligatoires
        if (depart.isEmpty() || destination.isEmpty() || date.isEmpty() || placesStr.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // Récupérer le type sélectionné dans le RadioGroup (TP1)
        int selectedId = rgType.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Veuillez sélectionner un type", Toast.LENGTH_SHORT).show();
            return;
        }
        // Utiliser l'ID plutôt que le texte du bouton pour éviter de stocker les emojis
        String type = (selectedId == R.id.rbConducteur) ? "Conducteur" : "Passager";

        // Convertir le nombre de places
        int places;
        try {
            places = Integer.parseInt(placesStr);
            if (places <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Nombre de places invalide (doit être > 0)", Toast.LENGTH_SHORT).show();
            return;
        }

        // Créer et stocker le trajet
        Trip trip = new Trip(depart, destination, date, places, type);
        TripRepository.addTrip(trip);

        Toast.makeText(this, "Trajet ajouté avec succès !", Toast.LENGTH_SHORT).show();

        // Intent explicite avec putExtra : naviguer vers TripDetailsActivity (TP2)
        Intent intent = new Intent(this, TripDetailsActivity.class);
        intent.putExtra("trip", trip); // Trip est Serializable
        startActivity(intent);
        finish(); // Retirer AddTripActivity de la pile de retour
    }
}
