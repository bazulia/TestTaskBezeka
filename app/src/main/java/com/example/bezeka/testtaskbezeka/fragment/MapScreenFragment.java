package com.example.bezeka.testtaskbezeka.fragment;

import android.util.Log;

import com.example.bezeka.testtaskbezeka.helper.DatabaseHandler;
import com.example.bezeka.testtaskbezeka.model.Image;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by Bezeka on 18.01.2016.
 */
public class MapScreenFragment extends SupportMapFragment implements OnMapReadyCallback {

    private static GoogleMap mMap;


    private void setUpMapIfNeeded() {
        getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);

        setupAllMarkers();
    }

    private void setupAllMarkers() {
        DatabaseHandler db = new DatabaseHandler(getActivity());
        ArrayList<Image> images = (ArrayList) db.getAllImages();

        if(images.isEmpty())
        {
            return;
        } else {

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Image image : images) {

                final LatLng latLng = new LatLng((double) image.getLat(), (double) image.getLng());
                Marker perth = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(image.getTag()));
                perth.showInfoWindow();

                Log.d("--->", image.getLat() + " - " + image.getLng());

                builder.include(perth.getPosition());
            }

            LatLngBounds bounds = builder.build();

            int padding = 50; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            mMap.animateCamera(cu);
        }



    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMap != null) {
            setupAllMarkers();
        }
        setUpMapIfNeeded();
    }

}
