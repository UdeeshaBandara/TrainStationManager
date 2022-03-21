package com.bsc212.pdsa;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bsc212.pdsa.adapters.DistanceAdapter;
import com.bsc212.pdsa.adapters.StationSearchArrayAdapter;
import com.bsc212.pdsa.models.Distance;
import com.bsc212.pdsa.models.Station;
import com.bsc212.pdsa.utils.Wrapper;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class UpdateStationActivity extends AppCompatActivity {
    //Data Structures
    LinkedList<Distance> distances = new LinkedList<>();
    ArrayList<Station> stations = new ArrayList<>();


    DistanceAdapter distanceAdapter;

    Button btnAddStation, btnAddDistance;
    EditText stationName;
    LinearLayout lnrStation;
    RecyclerView recyclerDistance;
    TextView fromMsg;

    //Database references
    FirebaseFirestore database;
    CollectionReference distanceReference;
    CollectionReference stationReference;
    CollectionReference transactionTimeReference;

    String updatingStationId = "";
    ArrayList<Station> filterArr;
    int selectedPosition = -1;
    int selectedStationPosition = -1;
    boolean isStationEditedInDistance = false;
    LinkedList<Distance> distanceFilter;


    public static void startActivityForEdit(Context context, ArrayList<Station> stations, LinkedList<Distance> distances, int selectedStationPosition) {
        Intent intent = new Intent(context, UpdateStationActivity.class);

        intent.putExtra("stations", stations);
        intent.putExtra("distances", new Wrapper<>(distances));

        intent.putExtra("selectedStationPosition", selectedStationPosition);
        if (context instanceof HomeActivity) {
            ((HomeActivity) context).startActivityForResult(intent, 1);

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_station);
        initViews();

        updateReferences();

        stations = (ArrayList<Station>) getIntent().getSerializableExtra("stations");
        distances = ((Wrapper<LinkedList<Distance>>) getIntent().getSerializableExtra("distances")).get();
        selectedStationPosition = getIntent().getIntExtra("selectedStationPosition", 0);

        distanceFilter = distances.stream().filter(distance -> (distance.getToStationId().equals(stations.get(selectedStationPosition).getDocumentId()) || distance.getFromStationId().equals(stations.get(selectedStationPosition).getDocumentId())))
                .collect(Collectors.toCollection(LinkedList::new));
        if (distanceFilter.size() > 0) {
            distanceAdapter.setStations(distanceFilter, stations);
            distanceAdapter.setUpdatingLength(distanceFilter.size());
            lnrStation.setVisibility(View.VISIBLE);
        }

        stationName.setText(stations.get(selectedStationPosition).getStationName());
        fromMsg.setText("Distances from " + stationName.getText().toString() + " Railway Station");
        updatingStationId = stations.get(selectedStationPosition).getDocumentId();

        btnAddStation.setOnClickListener(view -> {


            if (TextUtils.isEmpty(stationName.getText().toString()))
                Toast.makeText(getApplicationContext(), "Please enter station name", Toast.LENGTH_LONG).show();
            else {

//                btnAddStation.setEnabled(false);
//                btnAddStation.setAlpha(0.5f);
//                stationName.setEnabled(false);
//                stationName.setAlpha(0.5f);

                stations.get(selectedStationPosition).setStationName(stationName.getText().toString());


                updateExistingStation();
            }

        });
        btnAddDistance.setOnClickListener(view -> {
            showAddDistancePopup(-1, "", "");
        });

    }

    public void showAddDistancePopup(int pos, String station, String dist) {
        isStationEditedInDistance = false;

        final AlertDialog dialog = new AlertDialog.Builder(UpdateStationActivity.this).create();
        View view = View.inflate(UpdateStationActivity.this, R.layout.add_distance_popup, null);

        AutoCompleteTextView searchStation = view.findViewById(R.id.search_station);
        EditText distance = view.findViewById(R.id.distance);
        Button btnConfirm = view.findViewById(R.id.btn_confirm);
        Button btnCancel = view.findViewById(R.id.btn_cancel);

        if (pos != -1) {

            searchStation.setText(station);
            distance.setText(dist);
        }

        filterArr = new ArrayList(stations);

        StationSearchArrayAdapter stockSearchArrayAdapter = new StationSearchArrayAdapter(getApplicationContext(), filterArr);
        searchStation.setAdapter(stockSearchArrayAdapter);

        searchStation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isStationEditedInDistance = true;
                if (s.toString().isEmpty()) {
                    filterArr = new ArrayList(stations);
                    StationSearchArrayAdapter stockSearchArrayAdapter = new StationSearchArrayAdapter(getApplicationContext(), filterArr);
                    searchStation.setAdapter(stockSearchArrayAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchStation.setThreshold(0);
        searchStation.setOnItemClickListener((parent, textView, position, id) -> {

            if (updatingStationId.equals(stations.get(position).getDocumentId())) {
                Toast.makeText(UpdateStationActivity.this, "Cannot select same station", Toast.LENGTH_LONG).show();
                searchStation.setText("");
            } else {
                selectedPosition = position;
                setResult(1, new Intent().putExtra("stations", stations));
                fromMsg.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(parent.getApplicationWindowToken(), 0);
            }

        });


        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {

            if (TextUtils.isEmpty(searchStation.getText().toString()))
                Toast.makeText(getApplicationContext(), "Please select station", Toast.LENGTH_LONG).show();
            else if (TextUtils.isEmpty(distance.getText().toString()))
                Toast.makeText(getApplicationContext(), "Please enter distance", Toast.LENGTH_LONG).show();
            else {

                if (pos == -1) {
                    distances.add(new Distance(updatingStationId, filterArr.get(selectedPosition).getDocumentId(), distance.getText().toString()));

                    distanceAdapter.setUpdatingLength();
                    addNewDistanceToDatabase();
                } else {

//                    String toStationID = distances.get(pos).getToStationId();
//                    distances.remove(pos);
                    if (isStationEditedInDistance) {
                        distanceFilter.get(pos).setToStationId(filterArr.get(selectedPosition).getDocumentId());
                    }
//                        distances.add(pos, new Distance(updatingStationId, filterArr.get(selectedPosition).getDocumentId(), distance.getText().toString()));
                    distanceFilter.get(pos).setDistance(distance.getText().toString());
//                        distances.add(pos, new Distance(updatingStationId, toStationID, distance.getText().toString()));


                    updateDistanceToDatabase(distanceFilter.get(pos));
                }
                dialog.dismiss();
            }


        });

        dialog.setView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

    }

    private void initViews() {

        btnAddDistance = findViewById(R.id.btn_add_distance);
        btnAddStation = findViewById(R.id.btn_add_station);
        stationName = findViewById(R.id.station_name);
        lnrStation = findViewById(R.id.lnr_station);
        fromMsg = findViewById(R.id.from_msg);

        recyclerDistance = findViewById(R.id.recycler_distance);
        distanceAdapter = new DistanceAdapter(UpdateStationActivity.this);
        recyclerDistance.setAdapter(distanceAdapter);
        recyclerDistance.setLayoutManager(new LinearLayoutManager(UpdateStationActivity.this, LinearLayoutManager.VERTICAL, false));
        distanceAdapter.setStations((LinkedList<Distance>) distanceFilter, stations);


    }


    private void updateExistingStation() {

        stationReference.document(stations.get(selectedStationPosition).getDocumentId()).set(stations.get(selectedStationPosition));
//        stationReference.add(stations.get(stations.size() - 1)).addOnSuccessListener(documentReference -> updatingStationId = documentReference.getId());


    }
    private void addNewDistanceToDatabase() {

        distanceReference.add(distanceFilter.get(distanceFilter.size() - 1)).addOnSuccessListener(documentReference -> {

            distanceFilter.get(distanceFilter.size() - 1).setDocumentId(documentReference.getId());
            distanceAdapter.setStations(distances, stations);


        });
    }
    private void updateDistanceToDatabase(Distance distance) {

        distanceReference.document(distance.getDocumentId()).set(distance).addOnSuccessListener(distanceReference->{
            distanceAdapter.setStations(distances, stations);

        });

    }

    private void updateReferences() {
        database = FirebaseFirestore.getInstance();
        distanceReference = database.collection("Distance");
        stationReference = database.collection("Stations");
        transactionTimeReference = database.collection("TransactionTime");

    }
}