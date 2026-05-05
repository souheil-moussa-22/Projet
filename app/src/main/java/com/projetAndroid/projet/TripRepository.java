package com.projetAndroid.projet;

import java.util.ArrayList;

/**
 * Dépôt centralisé (pattern Repository) pour les trajets.
 * La liste statique simule une base de données en mémoire partagée
 * entre toutes les activités de l'application.
 */
public class TripRepository {

    // Liste statique unique : toutes les activités partagent la même référence
    private static final ArrayList<Trip> trips = new ArrayList<>();

    // Données de démonstration chargées au démarrage de l'application
    static {
        trips.add(new Trip("Paris",     "Lyon",      "10/06/2025", 3, "Conducteur"));
        trips.add(new Trip("Marseille", "Bordeaux",  "12/06/2025", 2, "Conducteur"));
        trips.add(new Trip("Toulouse",  "Nice",      "15/06/2025", 1, "Passager"));
        trips.add(new Trip("Nantes",    "Strasbourg","18/06/2025", 4, "Conducteur"));
    }

    /** Retourne la liste complète des trajets. */
    public static ArrayList<Trip> getTrips() {
        return trips;
    }

    /** Ajoute un nouveau trajet à la liste. */
    public static void addTrip(Trip trip) {
        trips.add(trip);
    }
}
