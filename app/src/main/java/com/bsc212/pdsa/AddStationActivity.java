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
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.LinkedList;

public class AddStationActivity extends AppCompatActivity {

    //Data Structures
    LinkedList<Distance> distances = new LinkedList<>();
    ArrayList<Station> stations = new ArrayList<>();
    Station selectedStation;

    DistanceAdapter distanceAdapter;

    Button btnAddStation, btnAddDistance;
    EditText stationName;
    LinearLayout lnrStation;
    RecyclerView recyclerDistance;
    TextView fromMsg;
    private KProgressHUD hud;

    //Database references
    FirebaseFirestore database;
    CollectionReference distanceReference;
    CollectionReference stationReference;
    CollectionReference transactionTimeReference;

    String updatedId = "";
    ArrayList<Station> filterArr;
    int selectedPosition = -1;
    boolean isStationEditedInDistance = false;

    public static void startActivity(Context context, ArrayList<Station> stations, LinkedList<Distance> distances, int type) {
        Intent intent = new Intent(context, AddStationActivity.class);

        intent.putExtra("stations", stations);
        intent.putExtra("distances", new Wrapper<>(distances));
        intent.putExtra("type", type);
        if (context instanceof HomeActivity) {
            ((HomeActivity) context).startActivityForResult(intent, 1);

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_station);
        initViews();

        updateReferences();

        stations = (ArrayList<Station>) getIntent().getSerializableExtra("stations");
        distances = ((Wrapper<LinkedList<Distance>>) getIntent().getSerializableExtra("distances")).get();


        btnAddStation.setOnClickListener(view -> {


            if (TextUtils.isEmpty(stationName.getText().toString()))
                Toast.makeText(getApplicationContext(), "Please enter station name", Toast.LENGTH_LONG).show();
            else {

                btnAddStation.setEnabled(false);
                btnAddStation.setAlpha(0.5f);
                stationName.setEnabled(false);
                stationName.setAlpha(0.5f);

                stations.add(new Station(stationName.getText().toString()));
                fromMsg.setText("Distances from " + stationName.getText().toString() + " Railway Station");
                addNewStationToDatabase();
            }

        });
        btnAddDistance.setOnClickListener(view -> {
            showAddDistancePopup(-1, "", "");
        });

    }

    public void showAddDistancePopup(int pos, String station, String dist) {
        isStationEditedInDistance = false;

        final AlertDialog dialog = new AlertDialog.Builder(AddStationActivity.this).create();
        View view = View.inflate(AddStationActivity.this, R.layout.add_distance_popup, null);

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

            if (updatedId.equals(stations.get(position).getDocumentId())) {
                Toast.makeText(AddStationActivity.this, "Cannot select same station", Toast.LENGTH_LONG).show();
                searchStation.setText("");
            } else {
                selectedPosition = position;


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
                fromMsg.setVisibility(View.VISIBLE);
                if (pos == -1) {
                    distances.add(new Distance(updatedId, filterArr.get(selectedPosition).getDocumentId(), distance.getText().toString()));

                    distanceAdapter.setUpdatingLength();
                    addNewDistanceToDatabase();
                } else {

                    if (isStationEditedInDistance) {
                        distances.get(pos).setToStationId(filterArr.get(selectedPosition).getDocumentId());
                    }
                    distances.get(pos).setDistance(distance.getText().toString());

                    updateDistanceToDatabase(distances.get(pos));

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


        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);


        btnAddDistance = findViewById(R.id.btn_add_distance);
        btnAddStation = findViewById(R.id.btn_add_station);
        stationName = findViewById(R.id.station_name);
        lnrStation = findViewById(R.id.lnr_station);
        fromMsg = findViewById(R.id.from_msg);

        recyclerDistance = findViewById(R.id.recycler_distance);
        distanceAdapter = new DistanceAdapter(AddStationActivity.this);
        recyclerDistance.setAdapter(distanceAdapter);
        recyclerDistance.setLayoutManager(new LinearLayoutManager(AddStationActivity.this, LinearLayoutManager.VERTICAL, false));


    }

    private void addNewStationToDatabase() {
        showHUD();
        stationReference.add(stations.get(stations.size() - 1)).addOnSuccessListener(documentReference -> {
            updatedId = documentReference.getId();
            stations.get(stations.size() - 1).setDocumentId(updatedId);
            distanceAdapter.setStations(distances, stations);
            lnrStation.setVisibility(View.VISIBLE);
            setResult(1, new Intent().putExtra("stations", stations).putExtra("distances", new Wrapper<>(distances)));
            hideHUD();
        });


    }

    private void addNewDistanceToDatabase() {
        showHUD();
        distanceReference.add(distances.get(distances.size() - 1)).addOnSuccessListener(documentReference -> {

            distances.get(distances.size() - 1).setDocumentId(documentReference.getId());
            distanceAdapter.setStations(distances, stations);
            setResult(1, new Intent().putExtra("stations", stations).putExtra("distances", new Wrapper<>(distances)));

            hideHUD();
        });
    }

    private void updateDistanceToDatabase(Distance distance) {

        showHUD();
        distanceReference.document(distance.getDocumentId()).set(distance).addOnSuccessListener(distanceReference -> {
            distanceAdapter.setStations(distances, stations);
            setResult(1, new Intent().putExtra("stations", stations).putExtra("distances", new Wrapper<>(distances)));
            hideHUD();

        });

    }


    private void updateReferences() {
        database = FirebaseFirestore.getInstance();
        distanceReference = database.collection("Distance");
        stationReference = database.collection("Stations");
        transactionTimeReference = database.collection("TransactionTime");

    }

    private void showHUD() {
        if (hud.isShowing()) {
            hud.dismiss();
        }
        hud.show();
    }

    private void hideHUD() {
        if (hud.isShowing()) {
            hud.dismiss();
        }
    }

}