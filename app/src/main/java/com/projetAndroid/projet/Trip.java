package com.projetAndroid.projet;

import java.io.Serializable;

/**
 * Classe modèle représentant un trajet de covoiturage.
 * Implémente Serializable pour pouvoir être transmise entre activités via Intent.putExtra().
 */
public class Trip implements Serializable {

    private String depart;       // Ville de départ
    private String destination;  // Ville d'arrivée
    private String date;         // Date du trajet (format JJ/MM/AAAA)
    private int places;          // Nombre de places disponibles
    private String type;         // "Conducteur" ou "Passager"

    public Trip(String depart, String destination, String date, int places, String type) {
        this.depart = depart;
        this.destination = destination;
        this.date = date;
        this.places = places;
        this.type = type;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String getDepart()      { return depart; }
    public String getDestination() { return destination; }
    public String getDate()        { return date; }
    public int    getPlaces()      { return places; }
    public String getType()        { return type; }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setDepart(String depart)           { this.depart = depart; }
    public void setDestination(String destination) { this.destination = destination; }
    public void setDate(String date)               { this.date = date; }
    public void setPlaces(int places)              { this.places = places; }
    public void setType(String type)               { this.type = type; }

    @Override
    public String toString() {
        return depart + " → " + destination + " (" + date + ")";
    }
}
