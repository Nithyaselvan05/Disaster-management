package com.example.manipaltestmaps;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import static android.content.ContentValues.TAG;

public class MarkerClusterRenderer extends DefaultClusterRenderer<User> implements ClusterManager.OnClusterClickListener<User>, GoogleMap.OnInfoWindowClickListener  {   // 1
    private static final int MARKER_DIMENSION = 48;  // 2
    private final IconGenerator iconGenerator;
    private final ImageView markerImageView;
    private static LayoutInflater layoutInflater;
    private final View clusterItemView;
    private GoogleMap googleMap;
    public MarkerClusterRenderer(Context context, GoogleMap map, ClusterManager<User> clusterManager) {
        super(context, map, clusterManager);
        this.googleMap = map;
        layoutInflater = LayoutInflater.from(context);
        clusterItemView = layoutInflater.inflate(R.layout.single_cluster_marker_view, null);
        iconGenerator = new IconGenerator(context);
        Drawable drawable = ContextCompat.getDrawable(context, android.R.color.transparent);
        iconGenerator.setBackground(drawable);// 3
        markerImageView = new ImageView(context);
        markerImageView.setLayoutParams(new ViewGroup.LayoutParams(100, 100));
        iconGenerator.setContentView(markerImageView);  // 4
        clusterManager.setOnClusterClickListener(this);
        googleMap.setInfoWindowAdapter(clusterManager.getMarkerManager());
        googleMap.setOnInfoWindowClickListener(this);
        clusterManager.getMarkerCollection().setOnInfoWindowAdapter(new MyCustomClusterItemInfoView());

        googleMap.setOnCameraIdleListener(clusterManager);

        googleMap.setOnMarkerClickListener(clusterManager);
        }
    @Override
    protected void onBeforeClusterItemRendered(User item, MarkerOptions markerOptions) {
        if(item.getStatus()=="need help") {
            markerImageView.setImageResource(R.drawable.sos_marker);

        }
        else if(item.getStatus()=="give help"){
            markerImageView.setImageResource(R.drawable.sos_blue);

        }
        else if(item.getStatus()=="public announcement"){
            markerImageView.setImageResource(R.drawable.sos_yellow);

        }
        Bitmap icon = iconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        markerOptions.title(item.getTitle());
        markerOptions.snippet(item.getSnippet());
    }
   /* @Override
    protected void onBeforeClusterRendered(Cluster<User> cluster, MarkerOptions markerOptions) {
        TextView singleClusterMarkerSizeTextView = clusterItemView.findViewById(R.id.singleClusterMarkerSizeTextView);
        singleClusterMarkerSizeTextView.setText(String.valueOf(cluster.getSize()));
        Bitmap icon = iconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }*/
    @Override
    protected void onClusterItemRendered(User clusterItem, Marker marker) {
        marker.setTag(clusterItem);
    }
    @Override
    public void onInfoWindowClick(Marker marker) {
        Context context = markerImageView.getContext();
        User user = (User) marker.getTag(); //  handle the clicked marker object
       /* if (context != null && user != null)
            Toast.makeText(context, user.getTitle(), Toast.LENGTH_SHORT).show();*/
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Open Google Maps to navigate to:"+user.getTitle())
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            String latitude = String.valueOf(marker.getPosition().latitude);
                            String longitude = String.valueOf(marker.getPosition().longitude);
                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");

                            try{
                                if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                                    context.startActivity(mapIntent);
                                }
                            }catch (NullPointerException e){
                                Log.e(TAG, "onClick: NullPointerException: Couldn't open map." + e.getMessage() );
                                Toast.makeText(context, "Couldn't open map", Toast.LENGTH_SHORT).show();
                            }

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }

    @Override
    public boolean onClusterClick(Cluster<User> cluster) {
        if (cluster == null) return false;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (User user : cluster.getItems())
            builder.include(user.getPosition());
        LatLngBounds bounds = builder.build();
        try {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    static class MyCustomClusterItemInfoView implements GoogleMap.InfoWindowAdapter {

    private final View clusterItemView;

    MyCustomClusterItemInfoView() {
        clusterItemView = layoutInflater.inflate(R.layout.marker_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        User user = (User) marker.getTag();
        if (user == null) return clusterItemView;
        TextView itemNameTextView = clusterItemView.findViewById(R.id.itemNameTextView);
        TextView itemAddressTextView = clusterItemView.findViewById(R.id.itemAddressTextView);
        itemNameTextView.setText(marker.getTitle());
        itemAddressTextView.setText(user.getSnippet());
        return clusterItemView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
}

