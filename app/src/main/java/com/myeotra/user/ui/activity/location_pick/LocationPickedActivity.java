package com.myeotra.user.ui.activity.location_pick;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.myeotra.user.BuildConfig;
import com.myeotra.user.MvpApplication;
import com.myeotra.user.R;
import com.myeotra.user.base.BaseActivity;
import com.myeotra.user.chat.ChatActivity;
import com.myeotra.user.common.Constants;
import com.myeotra.user.common.InfoWindowData;
import com.myeotra.user.common.LocaleHelper;
import com.myeotra.user.common.PolyUtil;
import com.myeotra.user.data.SharedHelper;
import com.myeotra.user.data.network.model.AddressResponse;
import com.myeotra.user.data.network.model.DataResponse;
import com.myeotra.user.data.network.model.LatLngFireBaseDB;
import com.myeotra.user.data.network.model.MultiLocationModel;
import com.myeotra.user.data.network.model.Provider;
import com.myeotra.user.data.network.model.User;
import com.myeotra.user.data.network.model.UserAddress;
import com.myeotra.user.ui.activity.pick_multiple_location.MultiLocationPickActivity;
import com.myeotra.user.ui.adapter.PlacesAutoCompleteAdapter;
import com.myeotra.user.ui.fragment.RateCardFragment;
import com.myeotra.user.ui.fragment.book_ride.BookRideFragment;
import com.myeotra.user.ui.fragment.invoice.InvoiceFragment;
import com.myeotra.user.ui.fragment.rate.RatingDialogFragment;
import com.myeotra.user.ui.fragment.schedule.ScheduleFragment;
import com.myeotra.user.ui.fragment.searching.SearchingFragment;
import com.myeotra.user.ui.fragment.service.ServiceTypesFragment;
import com.myeotra.user.ui.fragment.service_flow.ServiceFlowFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.myeotra.user.MvpApplication.DATUM;
import static com.myeotra.user.MvpApplication.RIDE_REQUEST;
import static com.myeotra.user.MvpApplication.canGoToChatScreen;
import static com.myeotra.user.MvpApplication.isCard;
import static com.myeotra.user.MvpApplication.isCash;
import static com.myeotra.user.MvpApplication.isChatScreenOpen;
import static com.myeotra.user.common.Constant.updateData;
import static com.myeotra.user.common.Constants.BroadcastReceiver.INTENT_FILTER;
import static com.myeotra.user.common.Constants.LocationActions.CHOOSADDARESSFROM;
import static com.myeotra.user.common.Constants.LocationActions.DESTINATIONLOCATION;
import static com.myeotra.user.common.Constants.LocationActions.MULTIPLELOCATION;
import static com.myeotra.user.common.Constants.LocationActions.SOURCELOCATION;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.DEST_ADD;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.DEST_LAT;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.DEST_LONG;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.PAYMENT_MODE;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.SRC_ADD;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.SRC_LAT;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.SRC_LONG;
import static com.myeotra.user.common.Constants.Status.ARRIVED;
import static com.myeotra.user.common.Constants.Status.COMPLETED;
import static com.myeotra.user.common.Constants.Status.DROPPED;
import static com.myeotra.user.common.Constants.Status.EMPTY;
import static com.myeotra.user.common.Constants.Status.PICKED_UP;
import static com.myeotra.user.common.Constants.Status.RATING;
import static com.myeotra.user.common.Constants.Status.SEARCHING;
import static com.myeotra.user.common.Constants.Status.SERVICE;
import static com.myeotra.user.common.Constants.Status.STARTED;
import static com.myeotra.user.data.SharedHelper.key.PROFILE_IMG;
import static com.myeotra.user.data.SharedHelper.key.SOS_NUMBER;
import static com.myeotra.user.ui.activity.payment.PaymentActivity.PICK_PAYMENT_METHOD;

public class LocationPickedActivity extends BaseActivity
        implements OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraIdleListener, DirectionCallback
        , LocationPickIView {

    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(new LatLng(-0, 0), new LatLng(0, 0));
    private static final String TAG = "AAAA";
    public static String arriveTime;
    private final int MULTIPLELOCATIONREQUEST = 10;
    private final int SOURCEDESTINATIONLOCATION = 11;
    protected GoogleApiClient mGoogleApiClient;
    boolean isEnableIdle = false;
    @BindView(R.id.tv_add_stop)
    TextView tv_add_stop;
    @BindView(R.id.edtsource)
    EditText edtsource;
    @BindView(R.id.edtdest)
    EditText edtdest;
    @BindView(R.id.tv_done)
    TextView tv_done;
    @BindView(R.id.ivsrc_close)
    ImageView ivsrc_close;
    @BindView(R.id.ivdestclose)
    ImageView ivdestclose;
    @BindView(R.id.rvMultiStoplist)
    RecyclerView rvMultiStoplist;

    /*@BindView(R.id.locationBsLayout)
    LinearLayout locationBsLayout;*/

    /*@BindView(R.id.locationsRv)
    RecyclerView locationsRv;*/
    @BindView(R.id.inflation_container)
    FrameLayout inflation_container;
    @BindView(R.id.menu)
    ImageView menu;
    String location;
    ArrayList<MultiLocationModel> multilocatonmodelArrayList;
    MultipleStopListAdapter multipleStopListAdapter;
    private Location mLastKnownLocation;
    private LocationPickedPresenter<LocationPickedActivity> mainPresenter = new LocationPickedPresenter<>();
    private boolean isLocationRvClick = false;
    private boolean isSettingLocationClick = false;
    private GoogleMap mGoogleMap;
    private FusedLocationProviderClient mFusedLocation;
    private SupportMapFragment mapFragment;
    private DatabaseReference mProviderLocation;
    private InfoWindowData destinationLeg;
    private boolean isDoubleBackPressed = false;
    private boolean isLocationPermissionGranted;
    private boolean canReRoute = true, canCarAnim = true;
    private int getProviderHitCheck;
    private NavigationView navigationView;
    private BottomSheetBehavior bsBehavior;
    private CircleImageView picture;
    private TextView name;
    private TextView sub_name;
    private HashMap<Integer, Marker> providersMarker;
    private ArrayList<LatLng> polyLinePoints;
    private Marker srcMarker, destMarker;
    private Polyline mPolyline;
    private LatLng start = null, end = null;
    private DataResponse checkStatusResponse = new DataResponse();
    private UserAddress home = null, work = null;
    private PlacesClient placesClient;
    private Runnable r;
    private Handler h;
    private Boolean isEditable = false;
    private String CURRENT_STATUS = EMPTY;
    private EditText selectedEditText;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private BottomSheetBehavior mBottomSheetBehavior;
    private String s_address;
    private Double s_latitude;
    private Double s_longitude;
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mainPresenter.checkStatus();
        }
    };



    /*interface FragmentCallback {
        void onClickButton(Intent data);
    }*/
//    private PaymentInterface mCallback;


    LocationPickedActivity activity;

    @Override
    public int getLayoutId() {
        return R.layout.activity_location_picked;
    }

    /*public LocationPickedActivity(PaymentInterface mCallback) {
        this.mCallback = mCallback;
    }*/

    @Override
    public void initView() {

        activity = LocationPickedActivity.this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ButterKnife.bind(this);
        mainPresenter.attachView(this);


        Places.initialize(getApplicationContext(), getString(R.string.google_map_key));
        placesClient = Places.createClient(this);
        multilocatonmodelArrayList = new ArrayList<>();
        providersMarker = new HashMap<>();

        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        bsBehavior = BottomSheetBehavior.from(inflation_container);
        bsBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, @BottomSheetBehavior.State int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        BottomSheetBehavior.from(inflation_container).setHideable(true);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        BottomSheetBehavior.from(inflation_container).setHideable(true);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        BottomSheetBehavior.from(inflation_container).setHideable(true);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


        rvMultiStoplist.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        multipleStopListAdapter = new MultipleStopListAdapter(baseActivity(), multilocatonmodelArrayList);
        rvMultiStoplist.setAdapter(multipleStopListAdapter);

        Intent intent = getIntent();

        if (intent != null) {
            location = intent.getStringExtra("pickfrom");
        }

        edtsource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LocationPickedActivity.this, MultiLocationPickActivity.class);
                intent.putExtra(CHOOSADDARESSFROM, SOURCELOCATION);
                startActivityForResult(intent, SOURCEDESTINATIONLOCATION);
            }
        });

        edtdest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocationPickedActivity.this, MultiLocationPickActivity.class);
                intent.putExtra(CHOOSADDARESSFROM, DESTINATIONLOCATION);
                startActivityForResult(intent, SOURCEDESTINATIONLOCATION);
            }
        });

        edtsource.setText(RIDE_REQUEST.containsKey(SRC_ADD)
                ? TextUtils.isEmpty(Objects.requireNonNull(RIDE_REQUEST.get(SRC_ADD)).toString())
                ? ""
                : String.valueOf(RIDE_REQUEST.get(SRC_ADD))
                : "");

        edtdest.setText(RIDE_REQUEST.containsKey(DEST_ADD)
                ? TextUtils.isEmpty(Objects.requireNonNull(RIDE_REQUEST.get(DEST_ADD)).toString())
                ? ""
                : String.valueOf(RIDE_REQUEST.get(DEST_ADD))
                : "");


        if (RIDE_REQUEST.get(SRC_LAT) != null && RIDE_REQUEST.get(SRC_LONG) != null && RIDE_REQUEST.get(DEST_LAT) != null && RIDE_REQUEST.get(DEST_LONG) != null) {
            Log.e(TAG, "lctn pic initView: lat " + RIDE_REQUEST.get(SRC_LAT) + " long " + RIDE_REQUEST.get(SRC_LONG));
            LatLng origin = new LatLng((Double) RIDE_REQUEST.get(SRC_LAT), (Double) RIDE_REQUEST.get(SRC_LONG));
            LatLng destination = new LatLng((Double) RIDE_REQUEST.get(DEST_LAT), (Double) RIDE_REQUEST.get(DEST_LONG));
            drawRoute(origin, destination);
        } else {
            Log.e(TAG, "lctn pic initView: lat long null");

        }


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (mLastKnownLocation != null) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("latitude", mLastKnownLocation.getLatitude());
                    map.put("longitude", mLastKnownLocation.getLongitude());
                    Log.e(TAG, "initView: " + new Gson().toJson(map));
                    mainPresenter.getProviders(map);
                }

            }
        }, 300);


        /*h = new Handler();
        r = () -> {
            mainPresenter.checkStatus();
            if (CURRENT_STATUS.equals(SERVICE) || CURRENT_STATUS.equals(EMPTY)) {
                if (getProviderHitCheck % 10 == 0) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("latitude", mLastKnownLocation.getLatitude());
                    map.put("longitude", mLastKnownLocation.getLongitude());
                    mainPresenter.getProviders(map);
                }
            } else if (CURRENT_STATUS.equals(STARTED) || CURRENT_STATUS.equals(ARRIVED)
                    || CURRENT_STATUS.equals(PICKED_UP)) if (getProviderHitCheck % 3 == 0) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (LatLng latLng : polyLinePoints) builder.include(latLng);
                LatLngBounds bounds = builder.build();
                try {
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 250));
                } catch (Exception e) {
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 90));
                }
            }
            getProviderHitCheck++;
            h.postDelayed(r, 6000);
        };
        h.postDelayed(r, 6000);*/

    }


    public void drawRoute(LatLng source, LatLng destination) {
        GoogleDirection
                .withServerKey(getString(R.string.google_map_key))
                .from(source)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
//                .transitMode(TransitMode.BUS)
                .execute(this);
        Log.e(TAG, "drawRoute: source" + source + " : destination :" + destination);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {

        if (direction.isOK()) {
            if (!CURRENT_STATUS.equalsIgnoreCase(SERVICE))
                Log.e(TAG, "direction.isOK: if");
            mGoogleMap.clear();
            Route route = direction.getRouteList().get(0);
            if (!route.getLegList().isEmpty()) {

                Log.e(TAG, "route.getLegList()");

                Leg leg = route.getLegList().get(0);
                InfoWindowData originLeg = new InfoWindowData();
                originLeg.setAddress(leg.getStartAddress());
                originLeg.setArrival_time(null);
                originLeg.setDistance(leg.getDistance().getText());

                destinationLeg = new InfoWindowData();
                destinationLeg.setAddress(leg.getEndAddress());
                destinationLeg.setArrival_time(leg.getDuration().getText());
                destinationLeg.setDistance(leg.getDistance().getText());

                LatLng origin = new LatLng(leg.getStartLocation().getLatitude(), leg.getStartLocation().getLongitude());
                LatLng destination = new LatLng(leg.getEndLocation().getLatitude(), leg.getEndLocation().getLongitude());
                if (CURRENT_STATUS.equalsIgnoreCase(SERVICE)) {
                    srcMarker = mGoogleMap.addMarker(new MarkerOptions()
                            .position(origin)
                            .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView())));
                } else if (CURRENT_STATUS.equalsIgnoreCase(EMPTY)) {
//                    srcMarker.remove();
                    srcMarker = mGoogleMap.addMarker(new MarkerOptions()
                            .position(origin)
                            .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView())));
                } else {
                    srcMarker = mGoogleMap.addMarker(new MarkerOptions()
                            .position(origin)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_2)));
                }


                /*else srcMarker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(origin)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_2)));*/

                if (destMarker != null) destMarker.remove();
                destMarker = mGoogleMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.des_icon))
                        .position(destination));


                CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                        origin, 15);
                mGoogleMap.animateCamera(location);

            } else {
                Log.e(TAG, "route.getLegList() null");
            }

            polyLinePoints = route.getLegList().get(0).getDirectionPoint();

            Log.e(TAG, "polyLinePoints: " + new Gson().toJson(polyLinePoints));

            if (CURRENT_STATUS.equalsIgnoreCase(SERVICE)) {

                Log.e(TAG, "polyLinePoints if");
                if (mPolyline != null) mPolyline.remove();
                mPolyline = mGoogleMap.addPolyline(DirectionConverter.createPolyline
                        (this, polyLinePoints, 2, getResources().getColor(R.color.yellow)));
                LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
                LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
                LatLngBounds bounds = new LatLngBounds(southwest, northeast);
                try {
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 250));
                } catch (Exception e) {
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 90));
                }
            } else {
                Log.e(TAG, "polyLinePoints else");

                mPolyline = mGoogleMap.addPolyline(DirectionConverter.createPolyline
                        (this, polyLinePoints, 4, getResources().getColor(R.color.yellow)));
            }
        } else {
            changeFlow(EMPTY);
            Log.e(TAG, "onDirectionSuccess: " + direction.getErrorMessage());
            Toast.makeText(this, getString(R.string.root_not_available), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onSuccess(User user) {

        String dd = LocaleHelper.getLanguage(this);
        String userLanguage = (user.getLanguage() == null) ? Constants.Language.ENGLISH : user.getLanguage();

        if (!userLanguage.equalsIgnoreCase(dd)) {
            LocaleHelper.setLocale(getApplicationContext(), user.getLanguage());
            Intent intent = new Intent(this, LocationPickedActivity.class);
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK));
        }

        SharedHelper.putKey(this, "lang", user.getLanguage());
        SharedHelper.putKey(this, "stripe_publishable_key", user.getStripePublishableKey());
        SharedHelper.putKey(this, "currency", user.getCurrency());
        SharedHelper.putKey(this, "measurementType", user.getMeasurement());
        SharedHelper.putKey(this, "walletBalance", String.valueOf(user.getWalletBalance()));
        SharedHelper.putKey(this, "userInfo", printJSON(user));
        SharedHelper.putKey(this, "referral_code", user.getReferral_unique_id());
        SharedHelper.putKey(this, "referral_count", user.getReferral_count());
        SharedHelper.putKey(this, "referral_text", user.getReferral_text());
        SharedHelper.putKey(this, "referral_total_text", user.getReferral_total_text());

        name.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
        sub_name.setText(user.getEmail());
        SharedHelper.putKey(LocationPickedActivity.this, PROFILE_IMG, user.getPicture());
        Glide.with(LocationPickedActivity.this)
                .load(BuildConfig.BASE_IMAGE_URL + user.getPicture())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_user_placeholder)
                        .dontAnimate()
                        .error(R.drawable.ic_user_placeholder))
                .into(picture);
        MvpApplication.showOTP = user.getRide_otp().equals("1");

    }

    @Override
    public void onSuccess(DataResponse dataResponse) {

        Log.e(TAG, "lctn : chck status : res : " + new Gson().toJson(dataResponse));

        this.checkStatusResponse = dataResponse;
        updatePaymentEntities();
        SharedHelper.putKey(this, SOS_NUMBER, checkStatusResponse.getSos());
        if (dataResponse.getData() != null && dataResponse.getData().size() > 0) {
            if (!CURRENT_STATUS.equals(dataResponse.getData().get(0).getStatus())) {
                DATUM = dataResponse.getData().get(0);

                if (dataResponse.getData().get(0).getStatus().equalsIgnoreCase("COMPLETED")) {
                    Toast.makeText(activity, "--" + new Gson().toJson(dataResponse.getData().get(0).getPayment()), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "payment not --> ", Toast.LENGTH_SHORT).show();
                }

                Log.e(TAG, "lctn actvt  stats - " + dataResponse.getData().get(0).getStatus());
                Log.e(TAG, "lctn actvt : DATUM - " + new Gson().toJson(dataResponse.getData().get(0)));
                CURRENT_STATUS = DATUM.getStatus();
                Log.e(TAG, "onSuccess : CURRENT_STATUS" + CURRENT_STATUS);
                changeFlow(CURRENT_STATUS);
            }
        } else if (CURRENT_STATUS.equals(SERVICE)) {
            Log.e(TAG, "SERVICE" + CURRENT_STATUS);
        } else {
            CURRENT_STATUS = EMPTY;

            if (dataResponse.getData().size() == 0 && tv_done.getVisibility() == View.GONE) {
                finish();
            } else {
                changeFlow(CURRENT_STATUS);
            }

//            updateData = true;
        }
        if (CURRENT_STATUS.equals(STARTED) || CURRENT_STATUS.equals(ARRIVED) || CURRENT_STATUS.equals(PICKED_UP))
            if (mProviderLocation == null) {
                Log.e(TAG, "mProviderLocation == null: ");
                mProviderLocation = FirebaseDatabase.getInstance().getReference().child("loc_p_" + DATUM.getProvider().getId());
                updateDriverNavigation();

            } else {
                Log.e(TAG, "mProviderLocation != null: ");
            }
        if (canGoToChatScreen) {
            if (!isChatScreenOpen && DATUM != null) {
                Intent i = new Intent(baseActivity(), ChatActivity.class);
                i.putExtra("request_id", String.valueOf(DATUM.getId()));
                startActivity(i);
            }
            canGoToChatScreen = false;
        }

    }

    private void updateDriverNavigation() {
        if (mProviderLocation != null)
            Log.e(TAG, "updateDriverNavigation: if");
        mProviderLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    LatLngFireBaseDB fbData = dataSnapshot.getValue(LatLngFireBaseDB.class);
                    double lat = fbData.getLat();
                    double lng = fbData.getLng();
                    LatLng source = null, destination = null;
                    Log.e(TAG, "onDataChange locationpick: " + lat + " lng " + lng);

                    System.out.println("RRR Lat: " + lat + " Lng: " + lng);
                    if (lng > 0 && lat > 0)

                        if (STARTED.equalsIgnoreCase(CURRENT_STATUS) ||
                                ARRIVED.equalsIgnoreCase(CURRENT_STATUS) ||
                                PICKED_UP.equalsIgnoreCase(CURRENT_STATUS)) {

                            switch (CURRENT_STATUS) {
                                case STARTED:
                                    source = new LatLng(lat, lng);
                                    destination = new LatLng(DATUM.getSLatitude(), DATUM.getSLongitude());
                                    break;
                                case ARRIVED:
                                case PICKED_UP:
                                    source = new LatLng(lat, lng);
                                    destination = new LatLng(DATUM.getDLatitude(), DATUM.getDLongitude());
                                    break;
                            }
                            if (polyLinePoints == null || polyLinePoints.size() < 2 || mPolyline == null)
                                drawRoute(source, destination);
                            else {
                                int index = checkForReRoute(source);
                                if (index < 0) reRoutingDelay(source, destination);
                                else polyLineRerouting(source, polyLinePoints, index);
                            }

                            start = source;
                            if (start != null) {
                                Log.e(TAG, "onDataChange: " + start);
                                SharedHelper.putCurrentLocation(LocationPickedActivity.this, start);
                                end = start;
                            }
//                                if (end != null && canCarAnim) carAnim(srcMarker, end, start);
                        }

                } catch (Exception e) {
                    Log.e(TAG, "onDataChange: Exception " + e.getMessage());
                    Toast.makeText(LocationPickedActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void reRoutingDelay(LatLng source, LatLng destination) {
        if (canReRoute) {
            canReRoute = !canReRoute;
            drawRoute(source, destination);
            new Handler().postDelayed(() -> canReRoute = true, 8000);
        }
    }

    private int checkForReRoute(LatLng point) {
        if (polyLinePoints != null && polyLinePoints.size() > 0) {
            System.out.println("RRR indexOnEdgeOrPath = " +
                    new PolyUtil().indexOnEdgeOrPath(point, polyLinePoints, false, true, 100));
            //      indexOnEdgeOrPath returns -1 if the point is outside the polyline
            //      returns the index position if the point is inside the polyline
            return new PolyUtil().indexOnEdgeOrPath(point, polyLinePoints, false, true, 100);
        } else return -1;
    }

    private void polyLineRerouting(LatLng point, List<LatLng> polyLine, int index) {
        if (index > 0) {
            polyLine.subList(0, index + 1).clear();
            polyLine.add(0, point);
            mPolyline.remove();
            mPolyline = mGoogleMap.addPolyline(DirectionConverter.createPolyline
                    (this, polyLinePoints, 2, getResources().getColor(R.color.yellow)));
            System.out.println("RRR mPolyline = " + polyLine.size());
        } else System.out.println("RRR mPolyline = Failed");
    }

    public void changeFlow(String status) {

        Log.e(TAG, "changeFlow: " + status);

        dismissDialog(SEARCHING);
        dismissDialog(RATING);
        switch (status) {
            case EMPTY:
                tv_done.setVisibility(View.VISIBLE);
                CURRENT_STATUS = EMPTY;

                updateData = true;
//                ivBack.setVisibility(View.GONE);
                menu.setVisibility(View.VISIBLE);
//                pickLocationLayout.setVisibility(View.VISIBLE);

//                addAllProviders(SharedHelper.getProviders(this));
                displayCurrentAddress();
                changeFragment(null);
//                destinationTxt.setText(getString(R.string.where_to));
//                llPickHomeAdd.setVisibility(home != null ? View.VISIBLE : View.INVISIBLE);
//                llPickWorkAdd.setVisibility(work != null ? View.VISIBLE : View.INVISIBLE);

                /*mProviderLocation = null;
                polyLinePoints = null;
                mGoogleMap.clear();
                providersMarker.clear();*/
                break;
            case SERVICE:
//                ivBack.setVisibility(View.VISIBLE);
//                menu.setVisibility(View.GONE);
//                updatePaymentEntities();
                updateData = true;
                tv_done.setVisibility(View.GONE);

                changeFragment(new ServiceTypesFragment());
                break;
            case SEARCHING:
//                pickLocationLayout.setVisibility(View.GONE);

                updateData = true;
                tv_done.setVisibility(View.GONE);
                updatePaymentEntities();
                SearchingFragment searchingFragment = new SearchingFragment();
                searchingFragment.show(getSupportFragmentManager(), SEARCHING);
                break;
            case STARTED:

                updateData = true;
                tv_done.setVisibility(View.GONE);
                mGoogleMap.clear();


//                pickLocationLayout.setVisibility(View.GONE);
//                ivBack.setVisibility(View.GONE);
                menu.setVisibility(View.GONE);

                if (DATUM != null)
                    FirebaseMessaging.getInstance().subscribeToTopic(String.valueOf(DATUM.getId()));
                changeFragment(new ServiceFlowFragment());
                break;
            case ARRIVED:
//                pickLocationLayout.setVisibility(View.GONE);
                updateData = true;
                tv_done.setVisibility(View.GONE);
                changeFragment(new ServiceFlowFragment());
                break;
            case PICKED_UP:
//                pickLocationLayout.setVisibility(View.GONE);
//                llChangeLocation.setVisibility(View.VISIBLE);
//                changeDestination.setText(DATUM.getDAddress());
                updateData = true;
                tv_done.setVisibility(View.GONE);

                changeFragment(new ServiceFlowFragment());
                break;
            case DROPPED:
            case COMPLETED:
//                pickLocationLayout.setVisibility(View.GONE);
//                llChangeLocation.setVisibility(View.GONE);
                updateData = true;
                changeFragment(new InvoiceFragment());
                break;
            case RATING:
                updateData = true;
                changeFragment(null);
                if (DATUM != null) {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(String.valueOf(DATUM.getId()));
                }
                new RatingDialogFragment().show(getSupportFragmentManager(), RATING);
                RIDE_REQUEST.clear();
                mGoogleMap.clear();
                tv_done.setVisibility(View.VISIBLE);

                break;
            default:
                break;
        }
    }

    private void displayCurrentAddress() {
        if (mGoogleMap == null) return;

        if (isLocationPermissionGranted) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
                return;

            // Use fields to define the data types to return.
            List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

            // Use the builder to create a FindCurrentPlaceRequest.
            FindCurrentPlaceRequest request =
                    FindCurrentPlaceRequest.builder(placeFields).build();

            placesClient.findCurrentPlace(request).addOnSuccessListener(((response) -> {
                List<PlaceLikelihood> likelyPlaces = response.getPlaceLikelihoods();
                if (response.getPlaceLikelihoods().size() > 0) {
//                    sourceTxt.setText(likelyPlaces.get(0).getPlace().getAddress());
                    RIDE_REQUEST.put(SRC_ADD, likelyPlaces.get(0).getPlace().getAddress());
                    RIDE_REQUEST.put(SRC_LAT, likelyPlaces.get(0).getPlace().getLatLng().latitude);
                    RIDE_REQUEST.put(SRC_LONG, likelyPlaces.get(0).getPlace().getLatLng().longitude);
                } else if (LocationPickedActivity.this.getLastKnownLocation() != null) {
                    mLastKnownLocation = getLastKnownLocation();
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                            mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()
                    ), DEFAULT_ZOOM));

                    SharedHelper.putKey(LocationPickedActivity.this, "latitude", String.valueOf(mLastKnownLocation.getLatitude()));
                    SharedHelper.putKey(LocationPickedActivity.this, "longitude", String.valueOf(mLastKnownLocation.getLongitude()));
                }
            })).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
//                    Log.e(getClass().getSimpleName(), "Place not found: " + apiException.getStatusCode());
                }

                if (LocationPickedActivity.this.getLastKnownLocation() != null) {
                    mLastKnownLocation = getLastKnownLocation();
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                            mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()
                    ), DEFAULT_ZOOM));

                    SharedHelper.putKey(LocationPickedActivity.this, "latitude", String.valueOf(mLastKnownLocation.getLatitude()));
                    SharedHelper.putKey(LocationPickedActivity.this, "longitude", String.valueOf(mLastKnownLocation.getLongitude()));
                }
            });


            if (mLastKnownLocation != null)
                /*if (TextUtils.isEmpty(edtsource.getText().toString()) || edtsource.getText().toString().equals(getText(R.string.pickup_location)))
                    if (mLastKnownLocation.getLatitude() > 0 && mLastKnownLocation.getLongitude() > 0) {
                        Location mLocation = getLastKnownLocation();
                        String address = getAddress(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
                        edtsource.setText(address);
                        RIDE_REQUEST.put(SRC_ADD, address);
                        RIDE_REQUEST.put(SRC_LAT, mLastKnownLocation.getLatitude());
                        RIDE_REQUEST.put(SRC_LONG, mLastKnownLocation.getLongitude());
                    }*/
                try {
                    hideLoading();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
        } else getLocationPermission();
    }

    public void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) isLocationPermissionGranted = true;
        else
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_ACCESS_FINE_LOCATION);
    }

    private Location getLastKnownLocation() {
        LocationManager mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers)
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location l = mLocationManager.getLastKnownLocation(provider);
                if (l == null) continue;
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy())
                    bestLocation = l;
            }
        return bestLocation;
    }

    private void addAllProviders(List<Provider> providers) {
        Log.e(TAG, "addAllProviders: ");
        if (providers != null) for (Provider provider : providers) {
            if (providersMarker.get(provider.getId()) != null) {
                Marker marker = providersMarker.get(provider.getId());
                LatLng startPos = marker.getPosition();
                LatLng endPos = new LatLng(provider.getLatitude(), provider.getLongitude());
                marker.setPosition(endPos);
//                carAnim(marker, startPos, endPos);
            } else {
                MarkerOptions markerOptions = new MarkerOptions()
                        .anchor(0.5f, 0.5f)
                        .position(new LatLng(provider.getLatitude(), provider.getLongitude()))
                        .rotation(0.0f)
                        .snippet("" + provider.getId())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_2));
                providersMarker.put(provider.getId(), mGoogleMap.addMarker(markerOptions));
            }
        }
    }

    public void changeFragment(Fragment fragment) {
        Log.e(TAG, "changeFragment: " + fragment);
        if (isFinishing()) return;

        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            if (fragment instanceof RateCardFragment)
                fragmentTransaction.addToBackStack(fragment.getTag());
            else if (fragment instanceof ScheduleFragment)
                fragmentTransaction.addToBackStack(fragment.getTag());
            else if (fragment instanceof ServiceTypesFragment)
                fragmentTransaction.addToBackStack(fragment.getTag());
            else if (fragment instanceof BookRideFragment)
                fragmentTransaction.addToBackStack(fragment.getTag());

            try {
//                fragmentTransaction.replace(R.id.container, fragment, fragment.getTag());
                fragmentTransaction.replace(R.id.inflation_container, fragment, fragment.getTag());
                fragmentTransaction.commitAllowingStateLoss();
//                fragmentTransaction.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            for (Fragment fragmentd : getSupportFragmentManager().getFragments()) {
                if (fragmentd instanceof ServiceFlowFragment)
                    getSupportFragmentManager().beginTransaction().remove(fragmentd).commitAllowingStateLoss();
                if (fragmentd instanceof InvoiceFragment)
                    getSupportFragmentManager().beginTransaction().remove(fragmentd).commitAllowingStateLoss();
            }
//            container.removeAllViews();
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public void updatePaymentEntities() {
        if (checkStatusResponse != null) {
            isCash = checkStatusResponse.getCash() == 1;
            isCard = checkStatusResponse.getCard() == 1;

            MvpApplication.isPayumoney = checkStatusResponse.getPayumoney() == 1;
            MvpApplication.isPaypal = checkStatusResponse.getPaypal() == 1;
            MvpApplication.isBraintree = checkStatusResponse.getBraintree() == 1;
            MvpApplication.isPaypalAdaptive = checkStatusResponse.getPaypal_adaptive() == 1;
            MvpApplication.isPaytm = checkStatusResponse.getPaytm() == 1;

            SharedHelper.putKey(this, "currency", checkStatusResponse.getCurrency());
            if (isCash) {
                RIDE_REQUEST.put(PAYMENT_MODE, Constants.PaymentMode.CASH);
            } else if (isCard) {
                RIDE_REQUEST.put(PAYMENT_MODE, Constants.PaymentMode.CARD);
            }
        }
    }

    @Override
    public void onDestinationSuccess(Object object) {

    }

    @Override
    public void onSuccess(AddressResponse response) {

    }

    @Override
    public void onSuccess(List<Provider> providerList) {

        Log.e(TAG, "getProvider Res: " + new Gson().toJson(providerList));
        SharedHelper.putProviders(this, printJSON(providerList));
    }

    /*@Override
    public void onSuccess(SettingsResponse response) {

    }*/

    /*@Override
    public void onSettingError(Throwable e) {

    }*/

    @Override
    public void onError(Throwable e) {
        Log.e(TAG, "onError: locaionpicked : " + e.getMessage());

    }

    void dismissDialog(String tag) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment instanceof SearchingFragment) {
            SearchingFragment df = (SearchingFragment) fragment;
            df.dismissAllowingStateLoss();
        } else if (fragment instanceof RatingDialogFragment) {
            RatingDialogFragment df = (RatingDialogFragment) fragment;
            df.dismissAllowingStateLoss();
        }
    }

    @OnClick({R.id.menu, R.id.tv_add_stop, R.id.ivdestclose, R.id.ivsrc_close, R.id.tv_done})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.menu:
                onBackPressed();
                break;
            case R.id.tv_add_stop:

                if (multilocatonmodelArrayList != null && multilocatonmodelArrayList.size() <= 6) {
                    Intent intent = new Intent(this, MultiLocationPickActivity.class);
                    intent.putExtra(CHOOSADDARESSFROM, MULTIPLELOCATION);
                    startActivityForResult(intent, MULTIPLELOCATIONREQUEST);
                } else {
                    Toast.makeText(this, "You Can not Add more than 7 stops.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.ivsrc_close:

                edtsource.setText("");
                RIDE_REQUEST.remove(SRC_ADD);
                RIDE_REQUEST.remove(SRC_LAT);
                RIDE_REQUEST.remove(SRC_LONG);
                break;
            case R.id.ivdestclose:
                edtdest.setText("");
                RIDE_REQUEST.remove(DEST_ADD);
                RIDE_REQUEST.remove(DEST_LAT);
                RIDE_REQUEST.remove(DEST_LONG);
                break;


            case R.id.tv_done:

                if (edtsource.getText().toString().trim().length() == 0) {

                    Toast.makeText(this, "Enter Source", Toast.LENGTH_SHORT).show();
                } else if (edtdest.getText().toString().trim().length() == 0) {
                    Toast.makeText(this, "Enter Destination", Toast.LENGTH_SHORT).show();

                } else {
                    changeFlow(SERVICE);
                }
                break;


           /* case R.id.edtsource:

                edtsource.setText("");
                *//*RIDE_REQUEST.remove(SRC_ADD);
                RIDE_REQUEST.remove(SRC_LAT);
                RIDE_REQUEST.remove(SRC_LONG);*//*

                Intent intent = new Intent(this, MultiLocationPickActivity.class);
                intent.putExtra(CHOOSADDARESSFROM, SOURCELOCATION);
                startActivityForResult(intent, MULTIPLELOCATIONREQUEST);
                break;
            case R.id.edtdest:
                edtdest.setText("");

                *//*RIDE_REQUEST.remove(DEST_ADD);
                RIDE_REQUEST.remove(DEST_LAT);
                RIDE_REQUEST.remove(DEST_LONG);*//*

                Intent intent2 = new Intent(this, MultiLocationPickActivity.class);
                intent2.putExtra(CHOOSADDARESSFROM, DESTINATIONLOCATION);
                startActivityForResult(intent2, MULTIPLELOCATIONREQUEST);
                break;*/
        }
    }


    @Override
    public void onBackPressed() {
        if (bsBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bsBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        super.onBackPressed();
    }

    private Bitmap getMarkerBitmapFromView() {

        View mView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.map_custom_infowindow, null);

        TextView tvEtaVal = mView.findViewById(R.id.tvEstimatedFare);
        String arrivalTime = destinationLeg.getArrival_time();
        if (arrivalTime.contains("hours")) arrivalTime = arrivalTime.replace("hours", "h\n");
        else if (arrivalTime.contains("hour")) arrivalTime = arrivalTime.replace("hour", "h\n");
        if (arrivalTime.contains("mins")) arrivalTime = arrivalTime.replace("mins", "min");
        tvEtaVal.setText(arrivalTime);
        arriveTime = arrivalTime;
        mView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mView.layout(0, 0, mView.getMeasuredWidth(), mView.getMeasuredHeight());
        mView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(mView.getMeasuredWidth(),
                mView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = mView.getBackground();
        if (drawable != null) drawable.draw(canvas);
        mView.draw(canvas);
        return returnedBitmap;
    }

    @Override
    public void onDirectionFailure(Throwable t) {

    }

    @Override
    public void onCameraIdle() {
        bsBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onCameraMove() {
        if (bsBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            bsBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        this.mGoogleMap = googleMap;

        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
        displayCurrentAddress();
    }

    private void updateLocationUI() {
        if (mGoogleMap == null) return;
        try {
            if (isLocationPermissionGranted) {
                mGoogleMap.setMyLocationEnabled(false);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mGoogleMap.getUiSettings().setCompassEnabled(false);
                mGoogleMap.setOnCameraMoveListener(this);
                mGoogleMap.setOnCameraIdleListener(this);
            } else {
                mGoogleMap.setMyLocationEnabled(false);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    void getDeviceLocation() {
        try {
            if (isLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocation.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        mLastKnownLocation = task.getResult();
                        mGoogleMap.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(new LatLng(
                                        mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()
                                ), DEFAULT_ZOOM));

                        SharedHelper.putKey(this, "latitude", String.valueOf(mLastKnownLocation.getLatitude()));
                        SharedHelper.putKey(this, "longitude", String.valueOf(mLastKnownLocation.getLongitude()));
                    } else {
                        mDefaultLocation = new LatLng(
                                Double.valueOf(SharedHelper.getKey(this, "latitude", "-33.8523341")),
                                Double.valueOf(SharedHelper.getKey(this, "longitude", "151.2106085"))
                        );
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void addSpecificProviders(List<Provider> providers, String key) {
        if (providers != null) {
            Bitmap b;
            BitmapDescriptor bd;
            try {
                /*b = Bitmap.createScaledBitmap(decodeBase64(SharedHelper.getKey
                        (this, key)), 60, 60, false);
                bd = BitmapDescriptorFactory.fromBitmap(b);*/

                bd = BitmapDescriptorFactory.fromResource(R.drawable.car_icon_2);
            } catch (Exception e) {

                Log.e(TAG, "addSpecificProviders: " + e.getMessage());
                bd = BitmapDescriptorFactory.fromResource(R.drawable.car_icon_2);
                e.printStackTrace();
            }
            Iterator it = providersMarker.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                Marker marker = providersMarker.get(pair.getKey());
                marker.remove();
                it.remove();
            }
            for (Provider provider : providers) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .anchor(0.5f, 0.5f)
                        .position(new LatLng(provider.getLatitude(), provider.getLongitude()))
                        .rotation(0.0f)
                        .snippet("" + provider.getId())
                        .icon(bd);
                providersMarker.put(provider.getId(), mGoogleMap.addMarker(markerOptions));
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e(TAG, "onActivityResult: location picked");
        switch (requestCode) {
            case MULTIPLELOCATIONREQUEST:

                if (resultCode == 12) {
                    String addaress = data.getStringExtra("address");
                    String latitude = data.getStringExtra("latitude");
                    String longitude = data.getStringExtra("longitude");

                    /*if (addresspickfrom.equalsIgnoreCase(SOURCELOCATION)) {
                        edtsource.setText(addaress);
                    } else if (addresspickfrom.equalsIgnoreCase(DESTINATIONLOCATION)) {
                        edtdest.setText(addaress);
                    } else {*/
                    MultiLocationModel model = new MultiLocationModel();
                    model.setS_latitude(latitude);
                    model.setS_longitude(longitude);
                    model.setS_address(addaress);

                    multilocatonmodelArrayList.add(model);

                    if (multipleStopListAdapter != null) {
                        multipleStopListAdapter.notifyDataSetChanged();
                    }
//                    }

                    Toast.makeText(this, "addaress : " + addaress + " latitude " + latitude + " longitude " + longitude, Toast.LENGTH_SHORT).show();

                    Log.e(TAG, "onActivityResult: " + addaress + "" + latitude + "" + longitude);
                }
                break;

            case SOURCEDESTINATIONLOCATION:
                if (resultCode == 13) {
                    String addresspickfrom = data.getStringExtra(CHOOSADDARESSFROM);

                    String addaress = data.getStringExtra("address");
                    String latitude = data.getStringExtra("latitude");
                    String longitude = data.getStringExtra("longitude");

                    Toast.makeText(this, "addresspickfrom " + addresspickfrom + " lat : " + latitude + " lo : " + longitude, Toast.LENGTH_SHORT).show();

                    Log.e(TAG, "onActivityResult: " + addresspickfrom + " lat : " + latitude + " lo : " + longitude);

                    if (addresspickfrom.equals(SOURCELOCATION)) {
                        edtsource.setText("" + addaress);
                        RIDE_REQUEST.put(SRC_ADD, addaress);
                        RIDE_REQUEST.put(SRC_LAT, Double.valueOf(latitude));
                        RIDE_REQUEST.put(SRC_LONG, Double.valueOf(longitude));

                        LatLng origin = new LatLng((Double) RIDE_REQUEST.get(SRC_LAT), (Double) RIDE_REQUEST.get(SRC_LONG));
                        LatLng destination = new LatLng((Double) RIDE_REQUEST.get(DEST_LAT), (Double) RIDE_REQUEST.get(DEST_LONG));
                        drawRoute(origin, destination);
                    } else {
                        edtdest.setText("" + addaress);

                        RIDE_REQUEST.put(DEST_ADD, addaress);
                        RIDE_REQUEST.put(DEST_LAT, Double.valueOf(latitude));
                        RIDE_REQUEST.put(DEST_LONG, Double.valueOf(longitude));

                        LatLng origin = new LatLng((Double) RIDE_REQUEST.get(SRC_LAT), (Double) RIDE_REQUEST.get(SRC_LONG));
                        LatLng destination = new LatLng((Double) RIDE_REQUEST.get(DEST_LAT), (Double) RIDE_REQUEST.get(DEST_LONG));
                        drawRoute(origin, destination);
                    }
                }
                break;

            case PICK_PAYMENT_METHOD:


                if (resultCode == Activity.RESULT_OK) {
                    updateData = false;
                    Intent intent = new Intent("KEY");
                    intent.putExtras(data);
                    sendBroadcast(intent);
                } else {
                    updateData = false;
                }
//                Log.e(TAG, "onActivityResult: " + data.getStringExtra("payment_mode"));

                break;

//            if (requestCode == PICK_PAYMENT_METHOD && resultCode == Activity.RESULT_OK) {
        }
    }


    @Override
    public void onResume() {
        Log.e(TAG, "onActivityResult: resume");
        super.onResume();
        mapFragment.onResume();
//        mainPresenter.getUserInfo();

        if (updateData) {
            registerReceiver(myReceiver, new IntentFilter(INTENT_FILTER));
            mainPresenter.checkStatus();
        }
        /*if (CURRENT_STATUS.equalsIgnoreCase(EMPTY)) {
            RIDE_REQUEST.remove(DEST_ADD);
            RIDE_REQUEST.remove(DEST_LAT);
            RIDE_REQUEST.remove(DEST_LONG);
            mainPresenter.getSavedAddress();
        }*/


    }

    public void onLowMemory() {
        super.onLowMemory();
        mapFragment.onLowMemory();
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause: ");
        super.onPause();
        mapFragment.onPause();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapFragment.onDestroy();
        mainPresenter.onDetach();
        unregisterReceiver(myReceiver);
//        H.removeCallbacks(r);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapFragment.onStop();
    }

    private interface LatLngInterface {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements LatLngInterface {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                if (Math.abs(lngDelta) > 180) lngDelta -= Math.signum(lngDelta) * 360;
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }


    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PAYMENT_METHOD && resultCode == Activity.RESULT_OK) {
            RIDE_REQUEST.put(PAYMENT_MODE, data.getStringExtra("payment_mode"));
            paymentMode = data.getStringExtra("payment_mode");
            if (data.getStringExtra("payment_mode").equals("CARD")) {
                RIDE_REQUEST.put(CARD_ID, data.getStringExtra("card_id"));
                RIDE_REQUEST.put(CARD_LAST_FOUR, data.getStringExtra("card_last_four"));
            }
            initPayment(estimatedPaymentMode);
        }
    }*/
}
