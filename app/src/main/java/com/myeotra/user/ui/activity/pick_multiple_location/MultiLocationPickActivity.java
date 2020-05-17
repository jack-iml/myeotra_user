package com.myeotra.user.ui.activity.pick_multiple_location;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.myeotra.user.R;
import com.myeotra.user.base.BaseActivity;
import com.myeotra.user.common.RecyclerItemClickListener;
import com.myeotra.user.data.network.model.AddressResponse;
import com.myeotra.user.ui.adapter.PlacesAutoCompleteAdapter;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.myeotra.user.MvpApplication.RIDE_REQUEST;
import static com.myeotra.user.common.Constants.LocationActions.CHOOSADDARESSFROM;
import static com.myeotra.user.common.Constants.LocationActions.DESTINATIONLOCATION;
import static com.myeotra.user.common.Constants.LocationActions.SOURCELOCATION;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.DEST_ADD;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.DEST_LAT;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.DEST_LONG;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.SRC_ADD;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.SRC_LAT;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.SRC_LONG;


public class MultiLocationPickActivity extends BaseActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        MultiLocationPickIView {

    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(new LatLng(-0, 0), new LatLng(0, 0));
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    protected GoogleApiClient mGoogleApiClient;
    @BindView(R.id.destination)
    EditText destination;
    @BindView(R.id.locations_rv)
    RecyclerView locations_rv;
    @BindView(R.id.menu)
    ImageView menu;
    String addresspickfrom;
    String src, dest;
    private Location mLastKnownLocation;
    private MultiLocationPickPresenter<MultiLocationPickActivity> presenter = new MultiLocationPickPresenter<>();
    private String TAG = "Locationpick";
    private String s_address;
    private Double s_latitude;
    private Double s_longitude;
    private EditText selectedEditText;
    private PlacesClient placesClient;
    private boolean isLocationRvClick = false;
    private boolean isSettingLocationClick = false;
    private Boolean isEditable = true;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
            if (isEditable)
                if (!s.toString().equals("")) {
                    locations_rv.setVisibility(View.VISIBLE);
                    mAutoCompleteAdapter.getFilter().filter(s.toString());
                } else if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
                }
            if (s.toString().equals("")) locations_rv.setVisibility(View.GONE);
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_multi_location_pick;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        presenter.attachView(this);

        Intent intent = getIntent();
        if (intent != null) {
            addresspickfrom = intent.getStringExtra(CHOOSADDARESSFROM);

            Toast.makeText(this, "addresspickfrom : " + addresspickfrom, Toast.LENGTH_SHORT).show();
        }

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_map_key));
        }
        placesClient = Places.createClient(this);


//        locations_rv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this, R.layout.list_item_location, placesClient, BOUNDS_INDIA);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        locations_rv.setLayoutManager(mLinearLayoutManager);

        destination.addTextChangedListener(filterTextWatcher);

        locations_rv.setAdapter(mAutoCompleteAdapter);

        destination.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) selectedEditText = destination;
        });


        locations_rv.addOnItemTouchListener(new RecyclerItemClickListener(this, (view, position) -> {
                    if (mAutoCompleteAdapter.getItemCount() == 0) return;
                    final PlacesAutoCompleteAdapter.PlaceAutocomplete item = mAutoCompleteAdapter.getItem(position);
                    final String placeId = String.valueOf(item.placeId);
                    Log.i("LocationPickedActivity", "Autocomplete item selected: " + item.address);


                    List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);

                    FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                            .build();

                    placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                        Place place = response.getPlace();
                        Log.i(TAG, "Place found: " + place.getName());
                        LatLng latLng = place.getLatLng();
                        isLocationRvClick = true;
                        isSettingLocationClick = true;
//                        setLocationText(String.valueOf(item.address), latLng,
//                                isLocationRvClick, isSettingLocationClick);


                        if (addresspickfrom.equalsIgnoreCase(DESTINATIONLOCATION) || addresspickfrom.equalsIgnoreCase(SOURCELOCATION)) {
                            Intent data = new Intent();
                            data.putExtra("address", String.valueOf(item.address));
                            data.putExtra("latitude", Double.toString(latLng.latitude));
                            data.putExtra("longitude", Double.toString(latLng.longitude));
                            data.putExtra(CHOOSADDARESSFROM, addresspickfrom);
                            setResult(13, data);
                        } else {
                            Intent data = new Intent();
                            data.putExtra("address", String.valueOf(item.address));
                            data.putExtra("latitude", Double.toString(latLng.latitude));
                            data.putExtra("longitude", Double.toString(latLng.longitude));
                            setResult(12, data);
                        }


                        finish();
                    }).addOnFailureListener((exception) -> {
                        if (exception instanceof ApiException) {
                            ApiException apiException = (ApiException) exception;
                            int statusCode = apiException.getStatusCode();
                            // Handle error with given status code.
                            Log.e(TAG, "Place not found: " + exception.getMessage());
                        }
                    });
                })
        );
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onSuccess(AddressResponse address) {

    }

    @OnClick({R.id.menu})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.menu:
                finish();
                break;
        }

    }

    private void setLocationText(String address, LatLng latLng, boolean isLocationRvClick,
                                 boolean isSettingLocationClick) {
        if (address != null && latLng != null) {
            isEditable = false;
            destination.setText(address);
            isEditable = true;

            if (destination.getTag().equals("destination")) {
                s_address = address;
                s_latitude = latLng.latitude;
                s_longitude = latLng.longitude;
                RIDE_REQUEST.put(SRC_ADD, address);
                RIDE_REQUEST.put(SRC_LAT, latLng.latitude);
                RIDE_REQUEST.put(SRC_LONG, latLng.longitude);
            }

            if (selectedEditText.getTag().equals("destination")) {
                RIDE_REQUEST.put(DEST_ADD, address);
                RIDE_REQUEST.put(DEST_LAT, latLng.latitude);
                RIDE_REQUEST.put(DEST_LONG, latLng.longitude);

                /*if (isLocationRvClick) {
                    //  Done functionality called...
                    setResult(Activity.RESULT_OK, new Intent());
                    finish();
                }*/
            }
        } else {
            isEditable = false;
            selectedEditText.setText("");
            locations_rv.setVisibility(View.GONE);
            isEditable = true;

            if (selectedEditText.getTag().equals("destination")) {
                RIDE_REQUEST.remove(DEST_ADD);
                RIDE_REQUEST.remove(DEST_LAT);
                RIDE_REQUEST.remove(DEST_LONG);
            }
        }

        if (isSettingLocationClick) {
            hideKeyboard();
            locations_rv.setVisibility(View.GONE);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
            Log.v("Google API", "Connecting");
            mGoogleApiClient.connect();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Log.v("Google API", "Dis-Connecting");
            mGoogleApiClient.disconnect();
        }
    }
}
