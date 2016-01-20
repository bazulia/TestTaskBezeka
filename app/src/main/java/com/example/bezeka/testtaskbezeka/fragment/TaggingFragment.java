package com.example.bezeka.testtaskbezeka.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bezeka.testtaskbezeka.R;
import com.example.bezeka.testtaskbezeka.activity.MainActivity;
import com.example.bezeka.testtaskbezeka.app.AppConfig;
import com.example.bezeka.testtaskbezeka.dialog_fragment.DownloadImageDialogFragment;
import com.example.bezeka.testtaskbezeka.helper.DatabaseHandler;
import com.example.bezeka.testtaskbezeka.model.Image;
import com.example.bezeka.testtaskbezeka.service.LocationService;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Created by Bezeka on 18.01.2016.
 */
public class TaggingFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = TaggingFragment.class.getSimpleName();

    public static final int REQUSERT_CODE_CAMERA = 100;
    public static final int REQUSERT_CODE_GALERY = 200;
    public static final int REQUSERT_CODE_DOWNLOAD = 300;

    private Button btnPick;
    private Button btnCamera;
    private Button btnDownload;
    private Button btnDone;

    private EditText etTag;

    private ImageView imageView;

    private View cstmView;

    private Uri fileUri;

    private File cameraFile = null;

    private String path;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tagging_fragment,container,false);

        btnPick = (Button) view.findViewById(R.id.btnPick);
        btnCamera = (Button) view.findViewById(R.id.btnCamera);
        btnDownload = (Button) view.findViewById(R.id.btnDownload);
        btnDone = (Button) view.findViewById(R.id.btnDone);

        btnPick.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
        btnDownload.setOnClickListener(this);
        btnDone.setOnClickListener(this);

        etTag = (EditText) view.findViewById(R.id.etTagForImage);

        imageView = (ImageView) view.findViewById(R.id.imgForTagging);

        cstmView = view.findViewById(R.id.cstmView);

        return view;
    }

    @Override
    public void onClick(View v) {
        hideSoftKeyboard();
        switch (v.getId()){
            case R.id.btnPick:
                FromCard();
                break;
            case R.id.btnCamera:
                if (isDeviceSupportCamera()){
                    FromCamera();
                } else {
                    Toast.makeText(getContext(),"This device is not support camera",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btnDownload:
                downloadImage();
                break;
            case R.id.btnDone:
                if(etTag.getText().toString().length()>0) {

                    DatabaseHandler db = new DatabaseHandler(getActivity());

                    double lat = ((MainActivity)getActivity()).actLat;
                    double lng = ((MainActivity)getActivity()).actLng;

                    Image image = new Image();
                    image.setId(new Random().nextInt() + 99999);
                    image.setLat(lat);
                    image.setLng(lng);
                    image.setDateTime(Calendar.getInstance().get(Calendar.DAY_OF_YEAR)+"");
                    image.setTag(etTag.getText().toString());
                    image.setPath(etTag.getText().toString());

                    try {
                        copy(new File(path),new File(image.getPath()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    db.addImage(image);

                    clearUI();

                    Toast.makeText(getActivity(),"Image "+image.getTag()+" added!",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(),"Please enter tag for image",Toast.LENGTH_LONG).show();
                break;
                }
                break;
        }
    }

    private void downloadImage(){
        DownloadImageDialogFragment dialogFragment = new DownloadImageDialogFragment();
        dialogFragment.setTargetFragment(TaggingFragment.this,REQUSERT_CODE_DOWNLOAD);
        dialogFragment.show(getActivity().getSupportFragmentManager(),dialogFragment.getClass().getSimpleName());
    }

    public void FromCamera() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri();

        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        startActivityForResult(takePicture, REQUSERT_CODE_CAMERA);
    }

    public void FromCard() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUSERT_CODE_GALERY);
    }

    private void setVisibleTagElements(boolean visible){
        if(visible ){
            imageView.setVisibility(View.VISIBLE);
            etTag.setVisibility(View.VISIBLE);
            btnDone.setVisibility(View.VISIBLE);
            cstmView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
            etTag.setVisibility(View.GONE);
            btnDone.setVisibility(View.GONE);
            cstmView.setVisibility(View.GONE);
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case REQUSERT_CODE_CAMERA:
                if (resultCode == Activity.RESULT_OK) {

                    path = fileUri.getPath();

                    setVisibleTagElements(true);

                    imageView.setImageURI(fileUri);
                }

                break;
            case REQUSERT_CODE_GALERY:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    path = getRealPathFromUri(getActivity(), selectedImage);

                    setVisibleTagElements(true);

                    imageView.setImageURI(selectedImage);
                }
                break;
            case REQUSERT_CODE_DOWNLOAD:
                if (resultCode == Activity.RESULT_OK) {
                    File file = new File(AppConfig.IMAGE_PATH);

                    path = AppConfig.IMAGE_PATH;

                    Picasso.with(getContext())
                            .load(file)
                            .into(imageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    setVisibleTagElements(true);
                                }

                                @Override
                                public void onError() {
                                    setVisibleTagElements(false);

                                    Toast.makeText(getContext(), "Error opening image", Toast.LENGTH_LONG).show();
                                }
                            });
                }
                break;
            default:
                setVisibleTagElements(false);
                break;
        }
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private boolean isDeviceSupportCamera() {
        // this device has a camera
// no camera on this device
        return getActivity().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("file_uri", fileUri);
    }

    public Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    private File getOutputMediaFile() {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                AppConfig.FOLDER_FOR_IMAGES);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + AppConfig.FOLDER_FOR_IMAGES + " directory");
                return null;
            }
        }

        File mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "download" + ".jpg");


        return mediaFile;
    }

    private void clearUI(){
        etTag.setText("");
        imageView.setImageBitmap(null);
        setVisibleTagElements(false);
    }

    public void copy(File src, File dst) throws IOException {

        System.out.println("Copy file from: "+src.getPath()+", to: "+dst.getPath());

        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }

    private void hideSoftKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
