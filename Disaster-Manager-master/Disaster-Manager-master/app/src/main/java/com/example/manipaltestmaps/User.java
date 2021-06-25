package com.example.manipaltestmaps;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class User implements ClusterItem {
    private final String username;
    private final LatLng latLng;
    private final String address;
    private final String status;
    private final String details;
    public User(String username, LatLng latLng, String address, String status, String details) {
        this.username = username;
        this.latLng = latLng;
        this.address= address;
        this.status = status;
        this.details = details;
    }
    @Override
    public LatLng getPosition() {  // 1
        return latLng;
    }
    @Override
    public String getTitle() {  // 2
        return username;
    }
    @Override
    public String getSnippet() {
        return status+ "\n" +address+"\n"+details;
    }

    public String getStatus() {
        return status;
    }
}
