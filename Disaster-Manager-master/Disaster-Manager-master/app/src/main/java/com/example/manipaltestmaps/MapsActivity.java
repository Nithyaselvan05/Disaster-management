package com.example.manipaltestmaps;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {  // 2
            @Override
            public void onMapReady(GoogleMap googleMap) {
                setUpClusterManager(googleMap);
            }

        });
    }
//setting up a cluster
    private void setUpClusterManager(GoogleMap googleMap) {
        ClusterManager<User> clusterManager = new ClusterManager(this, googleMap);  // 3
        clusterManager.setRenderer(new MarkerClusterRenderer(this, googleMap, clusterManager));
        googleMap.setInfoWindowAdapter(clusterManager.getMarkerManager());  // 3
        clusterManager.getMarkerCollection().setOnInfoWindowAdapter(new MarkerClusterRenderer.MyCustomClusterItemInfoView());
        googleMap.setOnCameraIdleListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);
        List<User> items = getItems();
        clusterManager.addItems(items);  // 4
        clusterManager.cluster();  // 5
    }
    private List<User> getItems() {
        return Arrays.asList(
                new User("Nimisha", new LatLng(-31.563910, 147.154312), "K K Nagar", "need help", "Road Blocked"),
                new User("Palani", new LatLng(-33.718234, 150.363181), "K K Nagar", "need help", "No electricity"),
                new User("Sriram", new LatLng(-33.727111, 150.371124), " ", "give help", " 20 Food packets available"),
                new User("Ragul", new LatLng(-33.848588, 151.209834), " ", "need help", "No water"),
                new User("Raja", new LatLng(-33.851702, 151.216968), "Anna Nagar ","public announcement", "Heavy winds and rain"),
                new User("Nithyaselvan", new LatLng(-34.671264, 150.863657), " ", "give help", "Has solar panels and has extra electricity"),
                new User("PSBB",new LatLng(13.0399,80.1994),"K K Nagar","give help","Has medical camp"));
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney")); //.snippet("Howdy mate!")-to add info
        float zoomLevel = 20.0f;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,zoomLevel));
        setUpClusterManager(mMap);
    }
}

