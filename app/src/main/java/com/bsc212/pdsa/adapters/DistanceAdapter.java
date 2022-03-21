package com.bsc212.pdsa.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bsc212.pdsa.AddStationActivity;
import com.bsc212.pdsa.R;
import com.bsc212.pdsa.UpdateStationActivity;
import com.bsc212.pdsa.models.Distance;
import com.bsc212.pdsa.models.Station;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DistanceAdapter extends RecyclerView.Adapter<DistanceAdapter.DistanceItemHolder> {

    LinkedList<Distance> distance = new LinkedList<>();
    ArrayList<Station> station = new ArrayList<>();
    int updatingLength = 0;
    Context context;

    public DistanceAdapter(Context context) {
        this.context = context;
    }

    public void setStations(LinkedList<Distance> distance, ArrayList<Station> station) {
        this.distance = distance;
        this.station = station;
        notifyDataSetChanged();

    }

    public void setUpdatingLength(int... length) {
        if (length.length > 0)
            updatingLength = length[0];
        else
            ++updatingLength;
        notifyDataSetChanged();
    }

    public int getUpdatingLength() {
        return updatingLength;
    }

    @NonNull
    @Override
    public DistanceItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_distance, parent, false);

        return new DistanceItemHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull DistanceItemHolder holder, @SuppressLint("RecyclerView") int position) {

        Predicate<Station> stationPredicate = station -> station.getDocumentId().equals(distance.get((distance.size() - 1) - position).getToStationId());
        List<Station> stationFilter = station.stream().filter(stationPredicate)
                .collect(Collectors.toList());
        holder.stationName.setText("To : " + stationFilter.get(0).getStationName());
        holder.stationDistance.setText("Distance : " + distance.get((distance.size() - 1) - position).getDistance() + " Km");

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (context instanceof AddStationActivity) {

                    ((AddStationActivity) context).showAddDistancePopup(position, stationFilter.get(0).getStationName(), distance.get((distance.size() - 1) - position).getDistance());

                } else if (context instanceof UpdateStationActivity) {

                    ((UpdateStationActivity) context).showAddDistancePopup(position, stationFilter.get(0).getStationName(), distance.get((distance.size() - 1) - position).getDistance());

                }


            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                notifyItemRemoved((distance.size() - 1 - position));
                distance.remove((distance.size() - 1 - position));

            }
        });
    }

    @Override
    public int getItemCount() {
        return updatingLength;
    }

    class DistanceItemHolder extends RecyclerView.ViewHolder {

        TextView stationName, stationDistance;
        ImageView delete, edit;
        CardView cardRoot;


        public DistanceItemHolder(@NonNull View itemView) {
            super(itemView);
            stationName = itemView.findViewById(R.id.station_name);
            stationDistance = itemView.findViewById(R.id.station_distance);
            delete = itemView.findViewById(R.id.delete);
            edit = itemView.findViewById(R.id.edit);
            cardRoot = itemView.findViewById(R.id.card_root);
        }
    }
}
