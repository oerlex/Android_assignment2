package com.example.oerlex.android_assignment2.countryList;

/**
 * Created by Oerlex on 31.08.2016.
 */
public class Country {
    String name;
    String date;

    public Country(String name, String date){
        this.name = name;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
