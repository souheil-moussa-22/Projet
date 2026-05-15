package com.projetAndroid.projet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.projetAndroid.projet.R;
import com.projetAndroid.projet.models.Trip;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adaptateur RecyclerView avec ViewHolder et click listener pour chaque action.
 */
public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    public interface OnTripActionListener {
        void onTripClick(Trip trip);
        void onEditClick(Trip trip);
        void onDeleteClick(Trip trip);
    }

    private final OnTripActionListener listener;
    private final List<Trip> items = new ArrayList<>();

    public TripAdapter(OnTripActionListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Trip> trips) {
        items.clear();
        items.addAll(trips);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class TripViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvDepart;
        private final TextView tvDestination;
        private final TextView tvDate;
        private final TextView tvPlaces;
        private final TextView tvPrix;
        private final TextView tvBadge;
        private final ImageButton btnEdit;
        private final ImageButton btnDelete;

        TripViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDepart = itemView.findViewById(R.id.tvItemDepart);
            tvDestination = itemView.findViewById(R.id.tvItemDestination);
            tvDate = itemView.findViewById(R.id.tvItemDate);
            tvPlaces = itemView.findViewById(R.id.tvItemPlaces);
            tvPrix = itemView.findViewById(R.id.tvItemPrice);
            tvBadge = itemView.findViewById(R.id.tvItemType);
            btnEdit = itemView.findViewById(R.id.btnItemEdit);
            btnDelete = itemView.findViewById(R.id.btnItemDelete);
        }

        void bind(Trip trip) {
            tvDepart.setText(trip.getDepart());
            tvDestination.setText(trip.getDestination());
            tvDate.setText("Date : " + trip.getDate());
            if (trip.getAvailablePlaces() == 0) {
                tvPlaces.setText(itemView.getContext().getString(R.string.trip_complete));
            } else {
                tvPlaces.setText(itemView.getContext().getString(
                        R.string.item_places_available,
                        trip.getAvailablePlaces(),
                        trip.getTotalPlaces()));
            }
            tvPrix.setText(String.format(Locale.getDefault(), "%.2f TND", trip.getPrix()));
            tvBadge.setText(trip.getUserType());
            int badgeBackground = "Conducteur".equalsIgnoreCase(trip.getUserType())
                    ? R.drawable.bg_chip_conducteur
                    : R.drawable.bg_chip_passager;
            tvBadge.setBackground(ContextCompat.getDrawable(itemView.getContext(), badgeBackground));

            itemView.setOnClickListener(v -> listener.onTripClick(trip));
            btnEdit.setOnClickListener(v -> listener.onEditClick(trip));
            btnDelete.setOnClickListener(v -> listener.onDeleteClick(trip));
        }
    }
}
