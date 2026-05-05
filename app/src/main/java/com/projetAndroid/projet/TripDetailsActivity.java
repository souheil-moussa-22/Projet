package com.projetAndroid.projet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activité affichant le détail complet d'un trajet.
 *
 * TP2 : Reçoit un objet Trip via getIntent().getSerializableExtra().
 * TP3 : Intents implicites — Google Maps, SMS, Partage.
 * TP5 : Intent bidirectionnel vers BookingActivity via ActivityResultLauncher.
 */
public class TripDetailsActivity extends AppCompatActivity {

    private Trip trip;

    /**
     * ActivityResultLauncher remplace l'ancienne méthode onActivityResult (TP5).
     * Doit être initialisé AVANT onCreate, d'où l'initialisation en champ.
     */
    private final ActivityResultLauncher<Intent> bookingLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    this::handleBookingResult
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        // Récupérer le trajet passé par putExtra (TP2)
        trip = (Trip) getIntent().getSerializableExtra("trip");

        if (trip == null) {
            Toast.makeText(this, "Erreur : trajet introuvable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Afficher les informations du trajet dans les TextView
        ((TextView) findViewById(R.id.tvDetailDepart)).setText("🚀 Départ : " + trip.getDepart());
        ((TextView) findViewById(R.id.tvDetailDestination)).setText("🏁 Destination : " + trip.getDestination());
        ((TextView) findViewById(R.id.tvDetailDate)).setText("📅 Date : " + trip.getDate());
        ((TextView) findViewById(R.id.tvDetailPlaces)).setText("💺 Places disponibles : " + trip.getPlaces());
        ((TextView) findViewById(R.id.tvDetailType)).setText("👤 Type : " + trip.getType());

        // ── Boutons d'action ─────────────────────────────────────────────────

        // TP5 : Intent bidirectionnel → BookingActivity
        findViewById(R.id.btnBook).setOnClickListener(v -> openBooking());

        // TP3 : Intent implicite → Google Maps
        findViewById(R.id.btnMaps).setOnClickListener(v -> openMaps());

        // TP3 : Intent implicite → Application SMS
        findViewById(R.id.btnSms).setOnClickListener(v -> sendSms());

        // TP3 : Intent implicite → Partage de texte
        findViewById(R.id.btnShare).setOnClickListener(v -> shareTrip());
    }

    // ── TP3 : Intents implicites ─────────────────────────────────────────────

    /**
     * Ouvre la destination sur Google Maps.
     * Utilise le schéma URI "geo:" reconnu par les applications cartographiques.
     */
    private void openMaps() {
        Uri geoUri = Uri.parse("geo:0,0?q=" + Uri.encode(trip.getDestination()));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, geoUri);
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            // Fallback navigateur si aucune app cartographique n'est installée
            Uri webUri = Uri.parse("https://maps.google.com/?q=" + Uri.encode(trip.getDestination()));
            startActivity(new Intent(Intent.ACTION_VIEW, webUri));
        }
    }

    /**
     * Ouvre l'application SMS avec un message pré-rempli.
     * setData("smsto:") cible uniquement les applications de messagerie SMS.
     */
    private void sendSms() {
        String message = "Bonjour, je suis intéressé par votre trajet de "
                + trip.getDepart() + " à " + trip.getDestination()
                + " le " + trip.getDate() + ". Êtes-vous disponible ?";
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.setData(Uri.parse("smsto:"));   // Cible les apps SMS uniquement
        smsIntent.putExtra("sms_body", message);
        if (smsIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(smsIntent);
        } else {
            Toast.makeText(this, "Aucune application SMS disponible", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Partage les informations du trajet via n'importe quelle application.
     * Intent.createChooser affiche une boîte de dialogue de sélection.
     */
    private void shareTrip() {
        String shareText = "🚗 Trajet disponible !\n"
                + "De : " + trip.getDepart() + "\n"
                + "À  : " + trip.getDestination() + "\n"
                + "📅 Date : " + trip.getDate() + "\n"
                + "💺 Places : " + trip.getPlaces() + "\n"
                + "👤 Type : " + trip.getType();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Partager le trajet via"));
    }

    // ── TP5 : Intent bidirectionnel ──────────────────────────────────────────

    /**
     * Lance BookingActivity et attend un résultat grâce à ActivityResultLauncher.
     */
    private void openBooking() {
        Intent intent = new Intent(this, BookingActivity.class);
        intent.putExtra("trip", trip); // Passer le trajet à BookingActivity
        bookingLauncher.launch(intent);
    }

    /**
     * Appelée automatiquement par le launcher quand BookingActivity se termine.
     * Affiche le message de confirmation retourné si la réservation est validée.
     */
    private void handleBookingResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            String confirmation = result.getData().getStringExtra("confirmation");
            Toast.makeText(this, confirmation, Toast.LENGTH_LONG).show();
        } else if (result.getResultCode() == RESULT_CANCELED) {
            Toast.makeText(this, "Réservation annulée", Toast.LENGTH_SHORT).show();
        }
    }
}
