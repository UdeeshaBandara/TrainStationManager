package com.bsc212.pdsa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bsc212.pdsa.R;
import com.bsc212.pdsa.models.Station;

import java.util.ArrayList;

public class StationSearchArrayAdapter extends ArrayAdapter<Station> implements Filterable {
    private Context mContext;
    private ArrayList<Station> stations;
    private ArrayList<Station> suggestions;
    private ArrayList<Station> itemsAll;

    public StationSearchArrayAdapter(@NonNull Context context,ArrayList<Station> station) {
        super(context, 0,station);
        mContext = context;
        stations = station;
        itemsAll = station;
        suggestions = new ArrayList<>();
        notifyDataSetChanged();
    }




    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.item_search_station, parent, false);

        Station station = stations.get(position);

        TextView stationName = listItem.findViewById(R.id.station_name);

        stationName.setText(station.getStationName());

        return listItem;

    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        public String convertResultToString(Object resultValue) {
            return ((Station) (resultValue)).getStationName();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (Station stock : itemsAll) {
                    if (stock.getStationName().toLowerCase()
                            .startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(stock);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            ArrayList<Station> filteredList = (ArrayList<Station>) results.values;
            if (results.count > 0) {
                clear();
                for (Station c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
    };
}
