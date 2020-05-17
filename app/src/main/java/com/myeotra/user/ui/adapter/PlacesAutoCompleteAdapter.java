package com.myeotra.user.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.myeotra.user.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PlacesAutoCompleteAdapter extends RecyclerView.Adapter<PlacesAutoCompleteAdapter.PredictionHolder> implements Filterable {

    private static final String TAG = "BBBB";


    private ArrayList<PlaceAutocomplete> tempmResultList = new ArrayList<>();

    private List<PlaceAutocomplete> tempResult;
    private LatLngBounds mBounds;
    private PlacesClient placesClient;
    private Activity mContext;
    private int layout;
    private CharacterStyle STYLE_BOLD;
    private CharacterStyle STYLE_NORMAL;

    public PlacesAutoCompleteAdapter(Activity context, int resource, PlacesClient placesClient, LatLngBounds bounds) {

        mContext = context;
        layout = resource;
        mBounds = bounds;
        this.placesClient = placesClient;
        STYLE_BOLD = new StyleSpan(Typeface.BOLD);
        STYLE_NORMAL = new StyleSpan(Typeface.NORMAL);


    }

    public void setBounds(LatLngBounds bounds) {
        mBounds = bounds;
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                Log.e(TAG, " ok: " + constraint.toString());
                FilterResults results = new FilterResults();
                if (!constraint.toString().isEmpty()) {
                    ArrayList<PlaceAutocomplete> mResultList = new ArrayList<>();

                    AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
                    FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
//                            .setCountry("IN")
                            .setTypeFilter(TypeFilter.ADDRESS)
                            .setSessionToken(token)
                            .setQuery(constraint.toString())
                            .build();

                    Task<FindAutocompletePredictionsResponse> autocompletePredictions = placesClient.findAutocompletePredictions(request);


                    try {
                        Tasks.await(autocompletePredictions, 30, TimeUnit.SECONDS);
                    } catch (ExecutionException | InterruptedException | TimeoutException e) {
                        e.printStackTrace();
                    }

                    if (autocompletePredictions.isSuccessful()) {


                        FindAutocompletePredictionsResponse findAutocompletePredictionsResponse = autocompletePredictions.getResult();
                        if (findAutocompletePredictionsResponse != null)
                            for (AutocompletePrediction prediction : findAutocompletePredictionsResponse.getAutocompletePredictions()) {
                                mResultList.add(new PlaceAutocomplete(prediction.getPlaceId(), prediction.getPrimaryText(STYLE_NORMAL).toString(), prediction.getFullText(STYLE_BOLD).toString()));
                            }
                        results.values = mResultList;
                        results.count = mResultList.size();
                        Log.e(TAG, "performFiltering : " + mResultList.size());
                    }
                    return results;
                } else {
                    return null;
                }
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                tempmResultList = (ArrayList<PlaceAutocomplete>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    @NonNull
    @Override
    public PredictionHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = layoutInflater.inflate(layout, viewGroup, false);
        return new PredictionHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull PredictionHolder mPredictionHolder, final int i) {

        if (tempmResultList != null && tempmResultList.size() > 0) {
            mPredictionHolder.area.setText(tempmResultList.get(i).area);
//            mPredictionHolder.address.setText(mResultList.get(i).address);
            mPredictionHolder.address.setText(tempmResultList.get(i).address.toString()
                    .replace(tempmResultList.get(i).area + ", ", ""));
        }

    }

    @Override
    public int getItemCount() {
//        return (mResultList == null) ? 0 : mResultList.size();

        if (tempmResultList != null)
            return tempmResultList.size();
        else
            return 0;
    }

    public PlaceAutocomplete getItem(int position) {
        return tempmResultList.get(position);
    }

    public void clear() {
        tempmResultList.clear();
        notifyDataSetChanged();
    }

    class PredictionHolder extends RecyclerView.ViewHolder {
        private TextView address, area;

        PredictionHolder(View itemView) {
            super(itemView);
            area = itemView.findViewById(R.id.area);
            address = itemView.findViewById(R.id.address);
        }
    }

    public class PlaceAutocomplete {

        public CharSequence placeId;
        public CharSequence address, area;

        PlaceAutocomplete(CharSequence placeId, CharSequence area, CharSequence address) {
            this.placeId = placeId;
            this.area = area;
            this.address = address;
        }

        @Override
        public String toString() {
            return area.toString();
        }
    }
}
