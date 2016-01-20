package com.example.bezeka.testtaskbezeka.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Bezeka on 16.01.2016.
 */
public class LocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = LocationService.class.getSimpleName();

    public static final String SP_KEY = "sp_key_test";
    public static final String SP_LAT = "sp_lat";
    public static final String SP_LNG = "sp_lng";
    public static final String SP_KEY_DISTANCE = "sp_key_test_distance";
    public static final String SP_KEY_DAY_OF_YEAR = "sp_key_test_update_time";

    private Location mLastLocation;

    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;

    private static int UPDATE_INTERVAL  = 10000; // 10 sec
    private static int FASTEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; //10 meters

    private static int PRIVATE_MODE = 0;

    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate");

        sp = getSharedPreferences(SP_KEY, PRIVATE_MODE);

        context = getApplicationContext();

        buildGoogleApiClient();

        mGoogleApiClient.connect();
    }

    @Override
    public void onDestroy() {
        stopLocationUpdates();
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.d(TAG, "onConnected");

        createLocationRequest();

        startLocationUpdates();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi
                .requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi
                .removeLocationUpdates(mGoogleApiClient, this);
    }

    protected void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "NEW  LOCATION IS : lat=" + location.getLatitude() + ", lng=" + location.getLongitude());

        int resultDistance = 0;

        if (mLastLocation != null) {
            Log.i(TAG, "LAST LOCATION IS : lat=" + mLastLocation.getLatitude() + ", lng=" + mLastLocation.getLongitude());

            saveLatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

            float distance = mLastLocation.distanceTo(location);

            Log.i(TAG, "Distance = " + distance);

            distance = round(distance, 3);

            distance += getDistancePref();

            resultDistance = (int) distance;

            Calendar calendar = Calendar.getInstance();

            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            int minutes = calendar.get(Calendar.MINUTE);

            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int dayOfYear = calendar.get(Calendar.DAY_OF_MONTH);



            Log.i(TAG, "ResultDistance = " + resultDistance + " , time = " + hours + ":" + minutes+", date = "+day+" / "+month);

            if(isSameDay(dayOfYear)){
                saveDistanceAndDate(resultDistance, dayOfYear);
            } else {                //if new day - start getting new location information
                saveDistanceAndDate(0, 0);
            }
            mLastLocation = location;

            Log.d(TAG, location.getLatitude()+" - "+location.getLongitude());
        } else {
            getLastKnownLocation();
            Log.i(TAG, "LAST LOCATION IS : lat=" + mLastLocation.getLatitude() + ", lng=" + mLastLocation.getLongitude());
        }
        sendMessageToActivity(mLastLocation.getLatitude(),mLastLocation.getLongitude(), resultDistance);

    }

    private boolean isSameDay(int curDayOfYear) {
        return getPrewDayOfYear() != curDayOfYear;
    }

    private int getPrewDayOfYear() {
        int dayOfYear = sp.getInt(SP_KEY_DAY_OF_YEAR,0);
        return dayOfYear;
    }

    private static float round(double number, int scale) {
        int pow = 10;
        for (int i = 1; i < scale; i++)
            pow *= 10;
        double tmp = number * pow;
        return (float) (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) / pow;
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "GoogleApiClient connection has failed");
    }

    private void saveDistanceAndDate(int distance, int dayOfYear) {
        spEditor = sp.edit();
        spEditor.putInt(SP_KEY_DAY_OF_YEAR, dayOfYear);
        spEditor.putInt(SP_KEY_DISTANCE, distance);
        spEditor.commit();
    }


    private void saveLatLng(double lat, double lng){
        spEditor = sp.edit();
        spEditor.putString(SP_LAT, lat + "");
        spEditor.putString(SP_LNG, lng + "");
        spEditor.commit();
    }

    private int getDistancePref() {
        return sp.getInt(SP_KEY_DISTANCE, 0);
    }


    private static void sendMessageToActivity(double lat, double lng, int distance) {
        Intent intent = new Intent("GPSLocationUpdates");
        // You can also include some extra data.
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        intent.putExtra("distance", distance);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

}
