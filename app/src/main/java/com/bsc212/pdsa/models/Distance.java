package com.bsc212.pdsa.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;

public class Distance implements Serializable {
    @DocumentId
    String documentId;
    String fromStationId;
    String toStationId;
    String distance;

    public Distance() {
    }

    public Distance(String fromStationId, String toStationId, String distance) {

        this.fromStationId = fromStationId;
        this.toStationId = toStationId;
        this.distance = distance;
    }

    protected Distance(Parcel in) {
        documentId = in.readString();
        fromStationId = in.readString();
        toStationId = in.readString();
        distance = in.readString();
    }



    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getFromStationId() {
        return fromStationId;
    }

    public void setFromStationId(String fromStationId) {
        this.fromStationId = fromStationId;
    }

    public String getToStationId() {
        return toStationId;
    }

    public void setToStationId(String toStationId) {
        this.toStationId = toStationId;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

}
