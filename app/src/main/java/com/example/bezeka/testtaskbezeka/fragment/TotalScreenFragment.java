package com.example.bezeka.testtaskbezeka.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bezeka.testtaskbezeka.R;
import com.example.bezeka.testtaskbezeka.adapter.ImagesAdapter;
import com.example.bezeka.testtaskbezeka.helper.DatabaseHandler;
import com.example.bezeka.testtaskbezeka.model.Image;
import com.example.bezeka.testtaskbezeka.service.LocationService;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Bezeka on 18.01.2016.
 */
public class TotalScreenFragment extends Fragment {

    private ImagesAdapter adapter;
    private RecyclerView rvImages;
    private RecyclerView.LayoutManager manager;
    private TextView tvDistance;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.total_screen_fragment, container, false);

        tvDistance = (TextView)view.findViewById(R.id.tvDistance);

        int distance = getActivity()
                .getSharedPreferences(LocationService.SP_KEY, Context.MODE_PRIVATE)
                .getInt(LocationService.SP_KEY_DISTANCE, 0);

        if(distance!=0){
            if(distance>1000) {
                tvDistance.setText("Passed: " + String.valueOf(distance/1000.000)+" KM.");
            } else {
                tvDistance.setText("Passed: " + String.valueOf(distance+" M."));
            }
        }

        rvImages = (RecyclerView) view.findViewById(R.id.rvImages);

        DatabaseHandler db = new DatabaseHandler(getActivity());

        ArrayList<Image> images = (ArrayList)db.getAllImagesByDay();

        adapter = new ImagesAdapter(getActivity(),images);

        manager = new GridLayoutManager(getActivity(),3, LinearLayoutManager.VERTICAL, false);

        rvImages.setLayoutManager(manager);

        rvImages.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                distanceReceiver, new IntentFilter("GPSLocationUpdates"));
    }

    private BroadcastReceiver distanceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int distance = intent.getIntExtra("distance", 0);
            if(distance!=0){
                if(distance>1000) {
                    tvDistance.setText("Passed: " + String.valueOf(distance/1000.000)+" KM.");
                } else {
                    tvDistance.setText("Passed: " + String.valueOf(distance+" M."));
                }
            }

        }
    };


}
