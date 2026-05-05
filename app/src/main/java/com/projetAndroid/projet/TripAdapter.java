package com.projetAndroid.projet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Adaptateur personnalisé (Custom Adapter) pour la ListView de trajets.
 *
 * Hérite de ArrayAdapter<Trip> et gonfle (inflate) le layout item_trip.xml
 * pour chaque ligne de la liste. Utilise le pattern convertView pour recycler
 * les vues et améliorer les performances.
 */
public class TripAdapter extends ArrayAdapter<Trip> {

    private final Context context;
    private final ArrayList<Trip> trips;

    public TripAdapter(Context context, ArrayList<Trip> trips) {
        super(context, R.layout.item_trip, trips);
        this.context = context;
        this.trips = trips;
    }

    /**
     * Appelée pour chaque élément visible de la ListView.
     *
     * @param position    Index de l'élément dans la liste
     * @param convertView Vue recyclée (peut être null si aucune n'est disponible)
     * @param parent      ViewGroup parent (la ListView)
     * @return La vue configurée pour cet élément
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Recycler la vue existante ou en créer une nouvelle
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_trip, parent, false);
        }

        // Récupérer le trajet correspondant à cette position
        Trip trip = trips.get(position);

        // Lier les données aux composants de la vue
        TextView tvDepart      = convertView.findViewById(R.id.tvItemDepart);
        TextView tvDestination = convertView.findViewById(R.id.tvItemDestination);
        TextView tvDate        = convertView.findViewById(R.id.tvItemDate);
        TextView tvPlaces      = convertView.findViewById(R.id.tvItemPlaces);
        TextView tvType        = convertView.findViewById(R.id.tvItemType);

        tvDepart.setText("De : " + trip.getDepart());
        tvDestination.setText("À : " + trip.getDestination());
        tvDate.setText("📅 " + trip.getDate());
        tvPlaces.setText("💺 " + trip.getPlaces() + " place(s)");
        tvType.setText(trip.getType());

        return convertView;
    }
}
