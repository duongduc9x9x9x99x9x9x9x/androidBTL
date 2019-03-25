package com.example.pokemongo1;

import android.location.Location;

public class Pokemon {

    private   int Image;
    private String name;
    private boolean isCatch;
    private Location location;
    private String power;
    Pokemon(int Image, String name,String power,  boolean isCatch,double lat, double log){
        this.Image = Image;
        this.name = name;
        this.power = power;
        this.isCatch = isCatch;
        // Location(String provider)
        //Construct a new Location with a named provider.
        location = new Location(name);
        location.setLatitude(lat);
        location.setLongitude(log);
    }

    public int getImage() {
        return Image;
    }

    public void setImage(int image) {
        Image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCatch() {
        return isCatch;
    }

    public void setCatch(boolean aCatch) {
        isCatch = aCatch;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }
}
