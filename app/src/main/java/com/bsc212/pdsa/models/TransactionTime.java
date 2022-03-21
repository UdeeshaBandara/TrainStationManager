package com.bsc212.pdsa.models;

import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;

public class TransactionTime  implements Serializable {

    @DocumentId
    String documentId;
    String collectionType;
    String transactionTime;
    String updatedDocumentId;

    public TransactionTime() {
    }

    public TransactionTime(String collectionType, String transactionTime, String updatedDocumentId) {

        this.collectionType = collectionType;
        this.transactionTime = transactionTime;
        this.updatedDocumentId = updatedDocumentId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getCollectionType() {
        return collectionType;
    }

    public void setCollectionType(String collectionType) {
        this.collectionType = collectionType;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getUpdatedDocumentId() {
        return updatedDocumentId;
    }

    public void setUpdatedDocumentId(String updatedDocumentId) {
        this.updatedDocumentId = updatedDocumentId;
    }
}
