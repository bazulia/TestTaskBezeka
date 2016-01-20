package com.example.bezeka.testtaskbezeka.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.bezeka.testtaskbezeka.R;
import com.example.bezeka.testtaskbezeka.activity.ImageActivity;
import com.example.bezeka.testtaskbezeka.model.Image;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Igor on 25.11.2015.
 */
public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder>{

    private ArrayList<Image> images;
    private Context context;

    public ImagesAdapter(Context context, ArrayList<Image> images) {
        this.context = context;
        this.images = images;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.rowImage);
        }
    }
    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_item, viewGroup, false);
        final ViewHolder holder = new ViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Image image = images.get(holder.getAdapterPosition());
                Intent intent = new Intent(context, ImageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("path", image.getPath());
                intent.putExtra("tag", image.getTag());
                context.startActivity(intent);
            }
        });

        return holder;
    }

    Transformation transformImage(final ViewHolder holder, ImageView imgView){

        Transformation transformation = new Transformation() {

            @Override public Bitmap transform(Bitmap source) {
                int targetWidth = holder.img.getWidth();

                double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                int targetHeight = (int) (targetWidth * aspectRatio);
                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                if (result != source) {
                    // Same bitmap is returned if sizes are the same
                    source.recycle();
                }


                return result;
            }

            @Override public String key() {
                return "transformation" + " desiredWidth";
            }
        };
        return transformation;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final Image image = images.get(i);

        Picasso.with(context)
                .load(new File(image.getPath()))
                .transform(transformImage(viewHolder,viewHolder.img))
                .fit()
                .centerCrop()
                .into(viewHolder.img, new Callback() {
                    @Override
                    public void onSuccess() {
                        System.out.println("onSuccess -> "+image.getPath());
                    }

                    @Override
                    public void onError() {
                        System.out.println("onError -> "+image.getPath());
                    }
                });

    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }


    @Override
    public int getItemCount() {
        return images.size();
    }


}
