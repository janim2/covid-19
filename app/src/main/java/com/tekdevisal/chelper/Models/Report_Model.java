package com.tekdevisal.chelper.Models;

import com.google.android.gms.maps.model.LatLng;

public class Report_Model {
    public String id;
    public String title;
    public String message;
    public LatLng userlocation;

    public Report_Model(String id, String title, String message, LatLng userlocation) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.userlocation = userlocation;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public LatLng getUserlocation() {
        return userlocation;
    }


}
