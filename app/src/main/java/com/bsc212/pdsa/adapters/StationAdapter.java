package com.bsc212.pdsa.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bsc212.pdsa.HomeActivity;
import com.bsc212.pdsa.R;
import com.bsc212.pdsa.models.Station;

import java.util.ArrayList;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.StationItemHolder> {

    ArrayList<Station> stations = new ArrayList<>();
    Context context;

    public StationAdapter(Context context) {
        this.context = context;
    }

    public void setStations(ArrayList<Station> stations) {
        this.stations = stations;
        notifyDataSetChanged();

    }

    public void updateList(ArrayList<Station> list) {
        stations = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StationItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_station, parent, false);

        return new StationItemHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull StationItemHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.stationName.setText(stations.get(position).getStationName());
        holder.cardRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((HomeActivity) context).openEditActivity(position);


            }
        });
    }

    @Override
    public int getItemCount() {
        return stations.size();
    }

    class StationItemHolder extends RecyclerView.ViewHolder {

        TextView stationName;
        CardView cardRoot;


        public StationItemHolder(@NonNull View itemView) {
            super(itemView);
            stationName = itemView.findViewById(R.id.station_name);
            cardRoot = itemView.findViewById(R.id.card_root);
        }
    }

}
