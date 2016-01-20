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
import android.widget.TextView;

import com.example.bezeka.testtaskbezeka.R;
import com.example.bezeka.testtaskbezeka.adapter.ImagesAdapter;
import com.example.bezeka.testtaskbezeka.helper.DatabaseHandler;

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
        View view = inflater.inflate(R.layout.total_screen_fragment,container,false);

        tvDistance = (TextView)view.findViewById(R.id.tvDistance);

        rvImages = (RecyclerView) view.findViewById(R.id.rvImages);

        DatabaseHandler db = new DatabaseHandler(getActivity());

        adapter = new ImagesAdapter(getActivity(),(ArrayList)db.getAllImages());

        manager = new GridLayoutManager(getActivity(),3, LinearLayoutManager.VERTICAL, false);

        rvImages.setLayoutManager(manager);

        rvImages.setAdapter(adapter);

        return view;
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
                tvDistance.setText(String.valueOf(distance));
            }

        }
    };


}
