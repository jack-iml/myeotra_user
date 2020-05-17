package com.myeotra.user.ui.fragment.source;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.myeotra.user.R;
import com.myeotra.user.base.BaseFragment;
import com.myeotra.user.common.RecyclerItemClickListener;
import com.myeotra.user.ui.activity.location_pick.LocationPickedActivity;
import com.myeotra.user.ui.adapter.PlacesAutoCompleteAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.myeotra.user.MvpApplication.RIDE_REQUEST;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.SRC_ADD;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.SRC_LAT;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.SRC_LONG;

public class SourceFragment extends BaseFragment implements SourceIView {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "SourceFragment";
    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(new LatLng(-0, 0), new LatLng(0, 0));
    public static boolean SOURCEVISIBLE = false;
    protected GoogleApiClient mGoogleApiClient;
    @BindView(R.id.pickup)
    EditText pickup;


    @BindView(R.id.locations_rv)
    RecyclerView locations_rv;
    private SourcePresenter<SourceFragment> presenter = new SourcePresenter<>();
    private String mParam1;
    private String mParam2;
    private PlacesClient placesClient;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private Boolean isEditable = true;
    private boolean isLocationRvClick = false;
    private boolean isSettingLocationClick = false;
    private LinearLayoutManager mLinearLayoutManager;
    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
            if (isEditable) {
                if (!TextUtils.isEmpty(s) && s.toString().trim().length() != 0) {
                    mAutoCompleteAdapter.getFilter().filter(s.toString());
                    locations_rv.getRecycledViewPool().clear();
                    locations_rv.setVisibility(View.VISIBLE);
                } else {
                    mAutoCompleteAdapter.clear();
                    locations_rv.setVisibility(View.GONE);
                }
            }

        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };


    /*public static SourceFragment newInstance(String param1, String param2) {
        SourceFragment fragment = new SourceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/


    public SourceFragment() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_source;
    }

    @Override
    protected View initView(View view) {

        ButterKnife.bind(this, view);
        presenter.attachView(this);


        if (!Places.isInitialized()) {
            Places.initialize(getActivity(), getString(R.string.google_map_key));
        }
        placesClient = Places.createClient(getActivity());
        if (RIDE_REQUEST.get(SRC_ADD) != null) {
            pickup.setText("" + Objects.requireNonNull(RIDE_REQUEST.get(SRC_ADD)).toString());
            openLocationPicked();

        }

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        locations_rv.setLayoutManager(mLinearLayoutManager);

        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(getActivity(), R.layout.list_item_location, placesClient, BOUNDS_INDIA);
        locations_rv.setAdapter(mAutoCompleteAdapter);


        locations_rv.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mAutoCompleteAdapter.getItemCount() != 0) {
                    PlacesAutoCompleteAdapter.PlaceAutocomplete item = mAutoCompleteAdapter.getItem(position);
                    final String placeId = String.valueOf(item.placeId);

                    List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);

                    FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                            .build();
                    isSettingLocationClick = true;
                    placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                        Place place = response.getPlace();
                        Log.i(TAG, "Place found: " + place.getName());
                        LatLng latLng = place.getLatLng();
//                        isLocationRvClick = true;
//                        isSettingLocationClick = true;


                        setLocationText(String.valueOf(item.address), latLng,
                                isLocationRvClick, isSettingLocationClick);


                    }).addOnFailureListener((exception) -> {
                        if (exception instanceof ApiException) {
                            ApiException apiException = (ApiException) exception;
                            int statusCode = apiException.getStatusCode();
                            // Handle error with given status code.
                            Log.e(TAG, "Place not found: " + exception.getMessage());
                        }
                    });
                }
            }
        }));
        pickup.addTextChangedListener(filterTextWatcher);
        return view;
    }

    private void setLocationText(String address, LatLng latLng, boolean isLocationRvClick,
                                 boolean isSettingLocationClick) {
        if (address != null && latLng != null) {

            Log.e(TAG, "insertlocation == ");
            isEditable = false;
            pickup.setText(address);
            isEditable = true;

            RIDE_REQUEST.put(SRC_ADD, address);
            RIDE_REQUEST.put(SRC_LAT, latLng.latitude);
            RIDE_REQUEST.put(SRC_LONG, latLng.longitude);

            if (pickup.getText().toString() != null && pickup.getText().toString().trim().length() > 0) {
                openLocationPicked();
            }
        } else {
            Log.e(TAG, "deletelocation == ");

            isEditable = false;
            pickup.setText("");
            locations_rv.setVisibility(View.GONE);
            isEditable = true;

            RIDE_REQUEST.remove(SRC_ADD);
            RIDE_REQUEST.remove(SRC_LAT);
            RIDE_REQUEST.remove(SRC_LONG);
        }

        if (isSettingLocationClick) {
            hideKeyboard();
            locations_rv.setVisibility(View.GONE);
        }
    }

    private void openLocationPicked() {
        Intent intent = new Intent(getActivity(), LocationPickedActivity.class);
        startActivity(intent);
        pickup.setText("");

    }


    /*private class DoneOnEditorActionListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i == EditorInfo.IME_ACTION_DONE) {
                if (pickup.getText().toString().length() == 0) {
                    Toast.makeText(getActivity(), "Enter Source", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getActivity(), LocationPickedActivity.class);
                    startActivity(intent);


                }
                return true;
            }
            return false;
        }
    }*/

    /*@OnClick({R.id.ivnext})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivnext:


               *//* RIDE_REQUEST.put(SRC_ADD, "Devi Multiplex, Makarpura, Naroda, Ahmedabad, Gujarat, India");
                RIDE_REQUEST.put(SRC_LAT, 23.0725464);
                RIDE_REQUEST.put(SRC_LONG, 72.6499798);*//*


                Intent intent = new Intent(getActivity(), LocationPickedActivity.class);
                startActivity(intent);


                break;
        }
    }
*/
    @Override
    public void onResume() {
        super.onResume();


        if (presenter.getMvpView() == null) {
            presenter = new SourcePresenter<>();
            presenter.attachView(this);
        }

        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
            Log.v("Google API", "Connecting");
            mGoogleApiClient.connect();
        }

        SOURCEVISIBLE = true;
    }

    @Override
    public void onDestroyView() {
        presenter.onDetach();
        super.onDestroyView();
        SOURCEVISIBLE = false;

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
