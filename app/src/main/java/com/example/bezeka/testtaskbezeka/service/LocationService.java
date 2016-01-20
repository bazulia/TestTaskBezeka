package com.example.bezeka.testtaskbezeka.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
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
    public static final String SP_KEY_UPDATE_TIME = "sp_key_test_update_time";

    private Location mLastLocation;

    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;

    private static int UPDATE_INTERVAL = 10000; // 60 sec
    private static int FASTEST_INTERVAL = 5000; // 30 sec
    private static int DISPLACEMENT = 10; //100 meters

    private static int PRIVATE_MODE = 0;

    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG,"onCreate");

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
        Log.d(TAG, "createLocationRequest");

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi
                .requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi
                .removeLocationUpdates(mGoogleApiClient, this);
    }

    protected void getLastKnownLocation() {
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

        if (mLastLocation != null) {
            Log.i(TAG, "LAST LOCATION IS : lat=" + mLastLocation.getLatitude() + ", lng=" + mLastLocation.getLongitude());

            saveLatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

            float distance = mLastLocation.distanceTo(location);

            Log.i(TAG, "Distance = " + distance);

            distance = round(distance, 3);

            Log.i(TAG, "Round distance = " + distance);

            distance += getDistancePref();

            int resultDistance = (int) distance;

            int hours = getCurDate().getHours();
            int minutes = getCurDate().getMinutes();

            Log.i(TAG, "ResultDistance = " + resultDistance + " , time = " + hours + ":" + minutes);

            if(isSameDay()){
                saveDistanceAndDate(resultDistance, getCurDate().toString());
            } else {                //if new day - start getting new location information
                saveDistanceAndDate(0, getCurDate().toString());
            }
            mLastLocation = location;



            Log.d(TAG, location.getLatitude()+" - "+location.getLongitude());
        } else {
            getLastKnownLocation();
            Log.i(TAG, "LAST LOCATION IS : lat=" + mLastLocation.getLatitude() + ", lng=" + mLastLocation.getLongitude());
        }
        sendMessageToActivity(mLastLocation.getLatitude(),mLastLocation.getLongitude());

    }

    private boolean isSameDay() {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(getCurDate());
        cal2.setTime(getPrewDate());
        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        return sameDay;
    }

    private Date getCurDate() {
        Calendar c = Calendar.getInstance();
        return c.getTime();
    }

    private Date getPrewDate() {
        Calendar c = Calendar.getInstance();
        return new Date(c.getTimeInMillis());
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

    private void saveDistanceAndDate(int distance, String date) {
        spEditor = sp.edit();
        spEditor.putString(SP_KEY_UPDATE_TIME, date);
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


    private static void sendMessageToActivity(double lat, double lng) {
        Intent intent = new Intent("GPSLocationUpdates");
        // You can also include some extra data.
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

}
