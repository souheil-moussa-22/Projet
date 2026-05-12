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
    private int places;
    private double prix;
    private String phone;
    private String vehicleType;
    private String userType;

    public Trip() {
        // Constructeur vide utile pour certaines opérations de mapping.
    }

    public Trip(String depart, String destination, String date, int places, double prix,
                String phone, String vehicleType, String userType) {
        this.depart = depart;
        this.destination = destination;
        this.date = date;
        this.places = places;
        this.prix = prix;
        this.phone = phone;
        this.vehicleType = vehicleType;
        this.userType = userType;
    }

    public Trip(long id, String depart, String destination, String date, int places, double prix,
                String phone, String vehicleType, String userType) {
        this(depart, destination, date, places, prix, phone, vehicleType, userType);
        this.id = id;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getDepart() { return depart; }
    public void setDepart(String depart) { this.depart = depart; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public int getPlaces() { return places; }
    public void setPlaces(int places) { this.places = places; }

    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
}
