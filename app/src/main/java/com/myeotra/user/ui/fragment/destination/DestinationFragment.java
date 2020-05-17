package com.myeotra.user.ui.fragment.destination;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import com.google.gson.Gson;
import com.myeotra.user.R;
import com.myeotra.user.base.BaseFragment;
import com.myeotra.user.common.RecyclerItemClickListener;
import com.myeotra.user.data.network.model.User;
import com.myeotra.user.ui.adapter.PlacesAutoCompleteAdapter;
import com.myeotra.user.ui.fragment.source.SourceFragment;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.myeotra.user.MvpApplication.RIDE_REQUEST;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.DEST_ADD;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.DEST_LAT;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.DEST_LONG;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.SRC_ADD;


public class DestinationFragment extends BaseFragment implements DestinationIView
        /*,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener*/ {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "AAAA";
    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(new LatLng(-0, 0), new LatLng(0, 0));
    public static boolean DESTVISIBLE = false;
    protected GoogleApiClient mGoogleApiClient;
    @BindView(R.id.tvusername)
    TextView tvusername;

    @BindView(R.id.locations_rv)
    RecyclerView locations_rv;


    @BindView(R.id.whereto)
    EditText whereto;
    private String mParam1;
    private String mParam2;
    private DestinationPresenter<DestinationFragment> presenter = new DestinationPresenter<>();
    private boolean isClick = false;
    private PlacesClient placesClient;
    private Handler mHandler;
    private LinearLayoutManager mLinearLayoutManager;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private Boolean isEditable = true;
    private boolean isLocationRvClick = false;
    private boolean isSettingLocationClick = false;
    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
            if (isEditable)
                if (!TextUtils.isEmpty(s) && s.toString().length() != 0) {
                    mAutoCompleteAdapter.getFilter().filter(s.toString());
                    locations_rv.getRecycledViewPool().clear();
                    locations_rv.setVisibility(View.VISIBLE);
                } else {
//                    mAutoCompleteAdapter.clear();
                    locations_rv.setVisibility(View.GONE);
                }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_destination;
    }

    @Override
    protected View initView(View view) {
        ButterKnife.bind(this, view);
        presenter.attachView(this);
        Places.initialize(getActivity(), getString(R.string.google_map_key));
        placesClient = Places.createClient(getActivity());

//        if (RIDE_REQUEST.get(SRC_ADD) != null) {
//            whereto.setText("" + Objects.requireNonNull(RIDE_REQUEST.get(SRC_ADD)).toString());
//        }

        presenter.getUserInfo();
        mLinearLayoutManager = new LinearLayoutManager(getActivity());


        locations_rv.setLayoutManager(mLinearLayoutManager);
        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(getActivity(), R.layout.list_item_location, placesClient, BOUNDS_INDIA);
        locations_rv.setAdapter(mAutoCompleteAdapter);


        locations_rv.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                if (mAutoCompleteAdapter.getItemCount() != 0) {


                if (mAutoCompleteAdapter.getItemCount() == 0) return;
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
//                }
            }
        }));

        whereto.addTextChangedListener(filterTextWatcher);
        return view;
    }


    /*@OnClick({R.id.ivnext})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivnext:


                *//*RIDE_REQUEST.put(DEST_ADD, "Haridarshan Cross Road, Vasant Vihar 2, Nava Naroda, Ahmedabad, Gujarat");
                RIDE_REQUEST.put(DEST_LAT, 23.0697146);
                RIDE_REQUEST.put(DEST_LONG, 72.6731357);*//*

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.container_main, new SourceFragment());
                ft.commitAllowingStateLoss();

                break;
        }
    }*/

    @Override
    public void onSuccess(User user) {


        Log.e(TAG, "user : res : " + new Gson().toJson(user));
        tvusername.setText("Welcome, " + String.format("%s %s", user.getFirstName(), user.getLastName()));

    }

    private void setLocationText(String address, LatLng latLng, boolean isLocationRvClick,
                                 boolean isSettingLocationClick) {
        if (address != null && latLng != null) {

            isEditable = false;
            whereto.setText(address);
            isEditable = true;

            RIDE_REQUEST.put(DEST_ADD, address);
            RIDE_REQUEST.put(DEST_LAT, latLng.latitude);
            RIDE_REQUEST.put(DEST_LONG, latLng.longitude);

            if (whereto.getText().toString() != null && whereto.getText().toString().trim().length() > 0) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.container_main, new SourceFragment(), "source");
                ft.commit();
                whereto.setText("");
            }


        } else {
            Log.e(TAG, "deletelocation == ");

            isEditable = false;
            whereto.setText("");
            locations_rv.setVisibility(View.GONE);
            isEditable = true;
            RIDE_REQUEST.remove(DEST_ADD);
            RIDE_REQUEST.remove(DEST_LAT);
            RIDE_REQUEST.remove(DEST_LONG);
        }

        if (isSettingLocationClick) {
            hideKeyboard();
            locations_rv.setVisibility(View.GONE);
        }
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onResume() {
        super.onResume();
//        if (presenter.getMvpView() == null) {
        presenter = new DestinationPresenter<>();
        presenter.attachView(this);
        DESTVISIBLE = true;

        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
            Log.v("Google API", "Connecting");
            mGoogleApiClient.connect();
        }

       /* if (isClick) {
            mHandler = new Handler() {
                public void handleMessage(Message msg) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.replace(R.id.map_container, new SourceFragment());
                    ft.addToBackStack(null);
                    ft.commitAllowingStateLoss();

                }
            };

        }*/
    }

    @Override
    public void onDestroyView() {
        presenter.onDetach();
        super.onDestroyView();
        DESTVISIBLE = false;

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Log.v("Google API", "Dis-Connecting");
            mGoogleApiClient.disconnect();
        }
    }

    private class DoneOnEditorActionListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i == EditorInfo.IME_ACTION_DONE) {
                if (whereto.getText().toString().length() == 0) {
                    Toast.makeText(getActivity(), "Enter Destination", Toast.LENGTH_SHORT).show();
                } else {
//                    InputMethodManager imm = (InputMethodManager) textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);


//                    ((MainActivity)(Objects.requireNonNull(getActivity()))).changeFlow("source");
//                    ((MainActivity) (Objects.requireNonNull(getActivity()))).changeFlow("source", getActivity());
//                    ((MainActivity) (Objects.requireNonNull(getActivity()))).changeFlow(new SourceFragment());


                }
                return true;
            }
            return false;
        }
    }


}
