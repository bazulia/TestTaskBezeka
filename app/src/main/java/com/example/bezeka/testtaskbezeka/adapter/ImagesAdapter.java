package com.example.bezeka.testtaskbezeka.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bezeka.igor.mobilegidkiev.R;
import com.bezeka.igor.mobilegidkiev.activity.DetailActivity;
import com.bezeka.igor.mobilegidkiev.model.Place;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Igor on 25.11.2015.
 */
public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> implements Filterable{

    private ArrayList<Place> places = new ArrayList<>();
    private ArrayList<Place> filteredPlaces;
    private Context context;

    boolean isShowDistance = false;

    public ImagesAdapter(Context context, ArrayList<Place> places) {
        this.places = places;
        this.context = context;
        this.filteredPlaces = new ArrayList<>();
    }

    @Override
    public Filter getFilter() {
        return new PlacesFilter(this,places);
    }

    public static class PlacesFilter extends Filter{

        ImagesAdapter placesAdapter;

        ArrayList<Place> originalPlaces;
        ArrayList<Place> filteredPlaces;

        public PlacesFilter(ImagesAdapter adapter,ArrayList<Place> originalPlaces){
            super();
            this.placesAdapter = adapter;
            this.originalPlaces = new ArrayList<>(originalPlaces);
            this.filteredPlaces = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            System.out.println("Filter KEY : "+constraint);

            filteredPlaces.clear();
            final FilterResults results = new FilterResults();

            if (constraint.length() == 0) {
                filteredPlaces.addAll(originalPlaces);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();

                for (final Place place : originalPlaces) {
                    if (filterPattern.contains("всі")){
                        filteredPlaces.add(place);
                    } else
                    if (place.getTitle().toLowerCase().contains(filterPattern)
                            || place.getAddress().toLowerCase().contains(filterPattern)
                            || place.getName().toLowerCase().contains(filterPattern)) {
                        filteredPlaces.add(place);
                    }
                }
            }
            results.values = filteredPlaces;
            results.count = filteredPlaces.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            placesAdapter.filteredPlaces.clear();
            placesAdapter.filteredPlaces.addAll((ArrayList<Place>) results.values);
            placesAdapter.notifyDataSetChanged();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView description;
        private ImageView img;
        private RatingBar rating;
        private TextView tvDistance;
        private TextView tvCommentsCount;
        private TextView tvAVGRating;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            img = (ImageView) itemView.findViewById(R.id.img);
            rating = (RatingBar) itemView.findViewById(R.id.ratingbar);
            tvDistance = (TextView) itemView.findViewById(R.id.tvDistance);
            tvCommentsCount = (TextView) itemView.findViewById(R.id.tvCommentsCount);
            tvAVGRating = (TextView) itemView.findViewById(R.id.tvRatingAvg);
        }
    }
    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, viewGroup, false);
        final ViewHolder holder = new ViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Place place = filteredPlaces.get((holder.getPosition()));
                Intent intent = new Intent(context,DetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("placeId", place.getId());
                intent.putExtra("title",place.getTitle());
                intent.putExtra("description",place.getDescription());
                intent.putExtra("img",place.getImgLink());
                intent.putExtra("rating",place.getRating());
                intent.putExtra("address",place.getAddress());
                intent.putExtra("distance",place.getDistance());
                intent.putExtra("count_comments",place.getCountComments());
                context.startActivity(intent);
            }
        });

        return holder;
    }

    public void sortByRating(){
        Collections.sort(filteredPlaces,new CustomComparatorRating());
        isShowDistance = false;
        this.notifyDataSetChanged();
    }

    public void sortByAlphabet(){
        Collections.sort(filteredPlaces,new CustomComparatorAlphabet());
        isShowDistance = false;
        this.notifyDataSetChanged();
    }

    public void sortByDistance(){
        Collections.sort(filteredPlaces,new CustomComparatorDistance());
        isShowDistance = true;
        this.notifyDataSetChanged();
    }

    public class CustomComparatorAlphabet implements Comparator<Place> {
        @Override
        public int compare(Place p1, Place p2) {
            return p2.getTitle().compareTo(p1.getTitle());
        }
    }

    public class CustomComparatorRating implements Comparator<Place> {
        @Override
        public int compare(Place p1, Place p2) {
            if(p2.getRating() < p1.getRating())
                return -1;
            else
                return 1;
        }
    }

    public class CustomComparatorDistance implements Comparator<Place> {
        @Override
        public int compare(Place p1, Place p2) {
            if(p2.getDistance() < p1.getDistance())
                return 1;
            else
                return -1;
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {

        Place place = filteredPlaces.get(i);
        viewHolder.title.setText(place.getTitle());
        viewHolder.description.setText(place.getDescription());
        viewHolder.rating.setRating(place.getRating());

        if(place.getDistance()>1000){
            viewHolder.tvDistance.setText(((place.getDistance())/1000)+" (km.)");
        }else {
            viewHolder.tvDistance.setText(place.getDistance()+" (m.)");
        }

        viewHolder.tvAVGRating.setText(place.getRating() + "");
        viewHolder.tvCommentsCount.setText(place.getCountComments());

        if(isShowDistance){
            viewHolder.tvDistance.setVisibility(View.VISIBLE);
            viewHolder.tvAVGRating.setVisibility(View.GONE);
            viewHolder.rating.setVisibility(View.GONE);
        } else {
            viewHolder.tvDistance.setVisibility(View.GONE);
            viewHolder.tvAVGRating.setVisibility(View.VISIBLE);
            viewHolder.rating.setVisibility(View.VISIBLE);
        }

        Picasso.with(context)
                .load(place.getImgLink())
                .into(viewHolder.img);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }


    @Override
    public int getItemCount() {
        return filteredPlaces.size();
    }


}
