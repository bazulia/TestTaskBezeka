package com.example.bezeka.testtaskbezeka.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.Log;

import com.example.bezeka.testtaskbezeka.helper.DatabaseHandler;
import com.example.bezeka.testtaskbezeka.model.Image;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
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

                final LatLng latLng = new LatLng(image.getLat(), image.getLng());

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap origBitmap = BitmapFactory.decodeFile(image.getPath(), options);

//                Bitmap bitmap = Bitmap.createScaledBitmap(origBitmap, 100, 100, false);

                Matrix m = new Matrix();
                m.setRectToRect(new RectF(0, 0, origBitmap.getWidth(), origBitmap.getHeight()), new RectF(0, 0, 100, 100), Matrix.ScaleToFit.CENTER);
                Bitmap bitmap = Bitmap.createBitmap(origBitmap, 0, 0, origBitmap.getWidth(), origBitmap.getHeight(), m, true);

                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                        .title(image.getTag()));

                marker.showInfoWindow();

                builder.include(marker.getPosition());
            }

            LatLngBounds bounds = builder.build();

            int padding = 150; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            mMap.animateCamera(cu);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMap = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMap != null) {
            setupAllMarkers();
        }
        setUpMapIfNeeded();
    }

    class MarkersSetupAsync extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {
            return null;
        }
    }

}
