package com.projetAndroid.projet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activité affichant la liste de tous les trajets disponibles.
 *
 * TP4 : Utilise une ListView avec un TripAdapter personnalisé.
 * TP2 : Navigue vers TripDetailsActivity en passant le trajet sélectionné via putExtra.
 */
public class ListTripActivity extends AppCompatActivity {

    private TripAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_trip);

        ListView listView = findViewById(R.id.listViewTrips);

        // Créer l'adaptateur personnalisé avec la liste statique partagée (TP4)
        adapter = new TripAdapter(this, TripRepository.getTrips());
        listView.setAdapter(adapter);

        // Clic sur un item : naviguer vers TripDetailsActivity avec putExtra (TP2)
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Trip selectedTrip = TripRepository.getTrips().get(position);
            Intent intent = new Intent(this, TripDetailsActivity.class);
            intent.putExtra("trip", selectedTrip); // Passer le trajet sérialisé
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Rafraîchir la liste quand l'utilisateur revient sur cet écran
        // (ex. après avoir ajouté un nouveau trajet)
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
