package com.projetAndroid.projet.models;

import java.io.Serializable;

/**
 * Modèle principal de l'application.
 * Cette classe représente un trajet complet de covoiturage et implémente Serializable
 * pour pouvoir transiter simplement entre activités via les Intents.
 */
public class Trip implements Serializable {

    private long id;
    private String depart;
    private String destination;
    private String date;
    private int totalPlaces;
    private int availablePlaces;
    private double prix;
    private String phone;
    private String vehicleType;
    private String userType;

    public Trip() {
        // Constructeur vide utile pour certaines opérations de mapping.
    }

    public Trip(String depart, String destination, String date, int totalPlaces, double prix,
                String phone, String vehicleType, String userType) {
        this.depart = depart;
        this.destination = destination;
        this.date = date;
        this.totalPlaces = totalPlaces;
        this.availablePlaces = totalPlaces;
        this.prix = prix;
        this.phone = phone;
        this.vehicleType = vehicleType;
        this.userType = userType;
    }

    public Trip(long id, String depart, String destination, String date, int totalPlaces, double prix,
                String phone, String vehicleType, String userType) {
        this(depart, destination, date, totalPlaces, prix, phone, vehicleType, userType);
        this.id = id;
    }

    public Trip(long id, String depart, String destination, String date, int totalPlaces,
                int availablePlaces, double prix, String phone, String vehicleType, String userType) {
        this(id, depart, destination, date, totalPlaces, prix, phone, vehicleType, userType);
        this.availablePlaces = availablePlaces;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getDepart() { return depart; }
    public void setDepart(String depart) { this.depart = depart; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    // Compatibilité : "places" représente le nombre total de places du trajet.
    public int getPlaces() { return totalPlaces; }
    public void setPlaces(int places) { this.totalPlaces = places; }

    public int getTotalPlaces() { return totalPlaces; }
    public void setTotalPlaces(int totalPlaces) { this.totalPlaces = totalPlaces; }

    public int getAvailablePlaces() { return availablePlaces; }
    public void setAvailablePlaces(int availablePlaces) { this.availablePlaces = availablePlaces; }

    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
}
