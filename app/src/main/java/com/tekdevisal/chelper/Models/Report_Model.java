package com.tekdevisal.chelper.Models;

import com.google.android.gms.maps.model.LatLng;

public class Report_Model {
    public String report_id;
    public String person_who_reported_id;
    public String title;
    public String message;
    public LatLng userlocation;
    public String phone_number;

    public Report_Model(String report_id, String person_who_uploaded_id, String title,
                        String message, LatLng userlocation, String phone_number) {
        this.report_id = report_id;
        this.person_who_reported_id = person_who_uploaded_id;
        this.title = title;
        this.message = message;
        this.userlocation = userlocation;
        this.phone_number = phone_number;
    }

    public String getReport_Id() {
        return report_id;
    }

    public String getPerson_who_reported_id() {
        return person_who_reported_id;
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
    public String getPhone_number() {
        return phone_number;
    }




}
