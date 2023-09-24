package com.hfad.smstestfinal;

import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;

public class SmsDto {

    public String  TransactionType, Parsed;
    public Double amount;
    public String body;
    public String senderId;

    public String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getTransactionType() {
        return TransactionType;
    }

    public void setTransactionType(String transactionType) {
        TransactionType = transactionType;
    }

    public String getParsed() {
        return Parsed;
    }

    public void setParsed(String parsed) {
        Parsed = parsed;
    }

    public String getBody()
    {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }


}
