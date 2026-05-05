package com.projetAndroid.projet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activité de réservation d'un trajet.
 *
 * TP5 : Retourne un résultat (RESULT_OK ou RESULT_CANCELED) à TripDetailsActivity
 *       via setResult() + finish(), géré par ActivityResultLauncher côté appelant.
 */
public class BookingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Récupérer le trajet passé par TripDetailsActivity (TP2)
        Trip trip = (Trip) getIntent().getSerializableExtra("trip");

        // Afficher le résumé du trajet
        TextView tvSummary = findViewById(R.id.tvBookingSummary);
        if (trip != null) {
            tvSummary.setText(
                    "Trajet : " + trip.getDepart() + " → " + trip.getDestination() + "\n" +
                    "Date   : " + trip.getDate() + "\n" +
                    "Places : " + trip.getPlaces()
            );
        }

        // ── Bouton Confirmer ─────────────────────────────────────────────────
        Button btnConfirm = findViewById(R.id.btnConfirmBooking);
        btnConfirm.setOnClickListener(v -> {
            // Construire le message de confirmation
            String msg = (trip != null)
                    ? "✅ Réservation confirmée ! " + trip.getDepart() + " → " + trip.getDestination()
                    : "✅ Réservation confirmée !";

            // Retourner RESULT_OK avec les données vers TripDetailsActivity (TP5)
            Intent resultIntent = new Intent();
            resultIntent.putExtra("confirmation", msg);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        // ── Bouton Annuler ───────────────────────────────────────────────────
        Button btnCancel = findViewById(R.id.btnCancelBooking);
        btnCancel.setOnClickListener(v -> {
            // Retourner RESULT_CANCELED sans données (TP5)
            setResult(RESULT_CANCELED);
            finish();
        });
    }
}
