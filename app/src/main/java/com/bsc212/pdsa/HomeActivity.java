package com.bsc212.pdsa;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bsc212.pdsa.adapters.StationAdapter;
import com.bsc212.pdsa.models.Distance;
import com.bsc212.pdsa.models.Station;
import com.bsc212.pdsa.models.TransactionTime;
import com.bsc212.pdsa.utils.Wrapper;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.LinkedList;

public class HomeActivity extends AppCompatActivity {

    //UI components
    RecyclerView recyclerStations;
    ImageView addStation;
    EditText searchStation;

    //Adapters
    StationAdapter stationAdapter;

    //Database references as global objects
    FirebaseFirestore database;
    CollectionReference distanceReference;
    CollectionReference stationReference;
    CollectionReference transactionTimeReference;

    //Data Structures
    LinkedList<Distance> distances = new LinkedList<>();
    ArrayList<Station> stations = new ArrayList<>();
    ArrayList<Station> filteredStations = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();

        connectRemoteDatabase();

        loadDataFromDatabase();


    }

    private void initViews() {

        recyclerStations = findViewById(R.id.recycler_stations);
        stationAdapter = new StationAdapter(HomeActivity.this);
        recyclerStations.setAdapter(stationAdapter);
        recyclerStations.setLayoutManager(new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.VERTICAL, false));

        addStation = findViewById(R.id.add_station);
        addStation.setOnClickListener(view -> AddStationActivity.startActivity(HomeActivity.this, stations, distances, 1));

        searchStation = findViewById(R.id.search_station);
        searchStation.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                filter(s.toString());
            }
        });


    }

    void filter(String text) {

        filteredStations = new ArrayList();

        for (Station station : stations)
            if (station.getStationName().toLowerCase().contains(text.toLowerCase()))
                filteredStations.add(station);

        stationAdapter.updateList(filteredStations);
    }

    private void loadDataFromDatabase() {

        long startTimeStationQuery = System.currentTimeMillis();

        stationReference.get().addOnSuccessListener(queryDocumentSnapshots -> {

            stations.addAll(queryDocumentSnapshots.toObjects(Station.class));

            //updating database operation time
            transactionTimeReference.add(new TransactionTime("1", String.valueOf((System.currentTimeMillis() - startTimeStationQuery)), "data load"));

            stationAdapter.setStations(stations);


        });

        long startTimeDistanceQuery = System.currentTimeMillis();

        distanceReference.get().addOnSuccessListener(queryDocumentSnapshots -> {

            distances.addAll(queryDocumentSnapshots.toObjects(Distance.class));

            //updating database operation time
            transactionTimeReference.add(new TransactionTime("2", String.valueOf((System.currentTimeMillis() - startTimeDistanceQuery)), "data load"));

        });

    }

    private void connectRemoteDatabase() {
        database = FirebaseFirestore.getInstance();
        distanceReference = database.collection("Distance");
        stationReference = database.collection("Stations");
        transactionTimeReference = database.collection("TransactionTime");

    }

    public void openEditActivity(int position) {


        if (searchStation.getText().toString().isEmpty())
            UpdateStationActivity.startActivityForEdit(HomeActivity.this, stations, distances, position);
        else
            UpdateStationActivity.startActivityForEdit(HomeActivity.this, stations, distances, stations.indexOf(filteredStations.get(position)));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            stations = (ArrayList<Station>) data.getSerializableExtra("stations");
            distances = ((Wrapper<LinkedList<Distance>>) data.getSerializableExtra("distances")).get();
            stationAdapter.setStations(stations);

        }
    }
}