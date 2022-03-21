package com.bsc212.pdsa.models;

import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;

public class Station implements Serializable {

    @DocumentId
    String documentId;
    String stationName;

    public Station() {
    }

    public Station(String documentId, String stationName) {
        this.documentId = documentId;
        this.stationName = stationName;
    }

    public Station(String stationName) {
        this.stationName = stationName;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }
}
