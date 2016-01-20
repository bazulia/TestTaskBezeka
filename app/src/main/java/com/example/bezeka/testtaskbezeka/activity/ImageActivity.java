package com.example.bezeka.testtaskbezeka.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.bezeka.testtaskbezeka.R;
import com.example.bezeka.testtaskbezeka.service.LocationService;
import com.squareup.picasso.Picasso;

import java.io.File;

public class ImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        String path = getIntent().getStringExtra("path");

        String tag = getIntent().getStringExtra("tag");

        if(!"".equals(tag) && !tag.isEmpty()){
            setTitle(tag);
        }

        ImageView img = (ImageView)findViewById(R.id.imgDetail);

        if(!path.isEmpty() && !"".equals(path)){
            Picasso.with(getApplicationContext())
                    .load(new File(path))
                    .into(img);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            stopLocationService();
            return true;
        }

        if (id == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void stopLocationService() {
        Intent serviceIntent = new Intent(this, LocationService.class);
        stopService(serviceIntent);
    }
}
