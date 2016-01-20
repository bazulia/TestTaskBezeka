package com.example.bezeka.testtaskbezeka.dialog_fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.bezeka.testtaskbezeka.R;
import com.example.bezeka.testtaskbezeka.app.AppConfig;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Bezeka on 19.01.2016.
 */
public class DownloadImageDialogFragment extends DialogFragment {

    private Button btnDownload;
    private ProgressBar pb;
    private EditText etURL;



    @Override
    public void onStart() {
        super.onStart();
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        int height = (display.getHeight())/2;
        int width = (display.getWidth())-((display.getWidth())/10);

        getDialog().getWindow().setLayout(width, height);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.download_image_dialog_fragment,container,false);

        etURL = (EditText) view.findViewById(R.id.etURL);

        btnDownload = (Button) view.findViewById(R.id.btnStartDownload);
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = etURL.getText().toString();

                new DownloadFileFromURL().execute(url);
            }
        });

        pb = (ProgressBar) view.findViewById(R.id.progressDownload);


        return view;
    }

    private void sendResult(int resultCode){
        if(getTargetFragment()==null)
            return;

        Intent i = new Intent();

        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, i);
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setMax(100);
            pb.setProgress(0);

            setCancelable(false);
            btnDownload.setEnabled(false);
            etURL.setEnabled(false);
        }

        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());

                // Output stream
                OutputStream output = new FileOutputStream(AppConfig.IMAGE_PATH);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    System.out.println("" + (int) ((total * 100) / lenghtOfFile));
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pb.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            setCancelable(true);
            btnDownload.setEnabled(true);
            etURL.setEnabled(true);
            if (isImageFile(AppConfig.IMAGE_PATH)){
                sendResult(Activity.RESULT_OK);

            } else {
                sendResult(Activity.RESULT_CANCELED);
            }
            getDialog().cancel();
        }

    }

    private boolean isImageFile(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        return options.outWidth != -1 && options.outHeight != -1;
    }
}
