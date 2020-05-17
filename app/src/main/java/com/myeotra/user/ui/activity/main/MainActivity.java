package com.myeotra.user.ui.activity.main;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
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
import com.google.gson.Gson;
import com.myeotra.user.BuildConfig;
import com.myeotra.user.MvpApplication;
import com.myeotra.user.R;
import com.myeotra.user.base.BaseActivity;
import com.myeotra.user.common.Constants;
import com.myeotra.user.common.InfoWindowData;
import com.myeotra.user.common.LocaleHelper;
import com.myeotra.user.data.SharedHelper;
import com.myeotra.user.data.network.model.AddressResponse;
import com.myeotra.user.data.network.model.DataResponse;
import com.myeotra.user.data.network.model.Provider;
import com.myeotra.user.data.network.model.SettingsResponse;
import com.myeotra.user.data.network.model.User;
import com.myeotra.user.data.network.model.UserAddress;
import com.myeotra.user.ui.activity.coupon.CouponActivity;
import com.myeotra.user.ui.activity.help.HelpActivity;
import com.myeotra.user.ui.activity.location_pick.LocationPickedActivity;
import com.myeotra.user.ui.activity.notification_manager.NotificationManagerActivity;
import com.myeotra.user.ui.activity.passbook.WalletHistoryActivity;
import com.myeotra.user.ui.activity.payment.PaymentActivity;
import com.myeotra.user.ui.activity.privacy_police.PrivacyPolicyActivity;
import com.myeotra.user.ui.activity.profile.ProfileActivity;
import com.myeotra.user.ui.activity.setting.SettingsActivity;
import com.myeotra.user.ui.activity.wallet.WalletActivity;
import com.myeotra.user.ui.activity.your_trips.YourTripActivity;
import com.myeotra.user.ui.fragment.destination.DestinationFragment;
import com.myeotra.user.ui.fragment.select_service.ServiceTypeSlection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.myeotra.user.MvpApplication.RIDE_REQUEST;
import static com.myeotra.user.MvpApplication.isCard;
import static com.myeotra.user.MvpApplication.isCash;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.PAYMENT_MODE;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.SRC_ADD;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.SRC_LAT;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.SRC_LONG;
import static com.myeotra.user.common.Constants.Status.EMPTY;
import static com.myeotra.user.data.SharedHelper.key.PROFILE_IMG;
import static com.myeotra.user.data.SharedHelper.key.SOS_NUMBER;

public class MainActivity extends BaseActivity implements MainIView
        , OnMapReadyCallback, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener, LocationListener {
    //DirectionCallback,
    private static final String TAG = "AAAA";
    private static String CURRENT_STATUS = EMPTY;
    @BindView(R.id.sub_name)
    TextView sub_name;

    @BindView(R.id.name)
    TextView name;

    @BindView(R.id.rlViewProfile)
    RelativeLayout rlViewProfile;

    @BindView(R.id.tv_app_ver)
    TextView tv_app_ver;

    @BindView(R.id.llpayment)
    LinearLayout llpayment;

    @BindView(R.id.gps)
    ImageView gps;

    @BindView(R.id.picture)
    CircleImageView picture;

    DrawerLayout drawerLayout;
    private MainPresenter<MainActivity> mainPresenter = new MainPresenter<>();
    private boolean isDoubleBackPressed = false;
    private PlacesClient placesClient;
    private SupportMapFragment mapFragment;
    private FusedLocationProviderClient mFusedLocation;
    private UserAddress home = null, work = null;
    private boolean isLocationPermissionGranted;
    private Location mLastKnownLocation;
    private GoogleMap mGoogleMap;
    private InfoWindowData destinationLeg;

    private Marker srcMarker, destMarker;

    private ArrayList<LatLng> polyLinePoints;

    private Polyline mPolyline;
    private Runnable r;
    private Handler h;

//    private DatabaseReference mProviderLocation;

    /*
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mainPresenter.checkStatus();
        }
    };
    */
    private int getProviderHitCheck;
    private DataResponse checkStatusResponse = new DataResponse();


    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Places.initialize(getApplicationContext(), getString(R.string.google_map_key));
        placesClient = Places.createClient(this);
        ButterKnife.bind(this);

//        registerReceiver(myReceiver, new IntentFilter(INTENT_FILTER));
        mainPresenter.attachView(this);

        drawerLayout = findViewById(R.id.drawer_layout);

        attachDestinationFragment();


        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            tv_app_ver.setText("App Version : " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_main);
        mapFragment.getMapAsync(this);


        findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checknavigationdrawer();
            }
        });

        /*
        h = new Handler();
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
        h.postDelayed(r, 6000);
        */

    }

    private void attachDestinationFragment() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_main, new DestinationFragment(), "destination");
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.commitAllowingStateLoss();
            }
        }, 1000);
    }


    @Override
    public void onSuccess(User user) {

        Log.e(TAG, "response : profile : " + new Gson().toJson(user));

        String dd = LocaleHelper.getLanguage(this);

        String userLanguage = (user.getLanguage() == null) ? Constants.Language.ENGLISH : user.getLanguage();

        if (!userLanguage.equalsIgnoreCase(dd)) {
            LocaleHelper.setLocale(getApplicationContext(), user.getLanguage());
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        SharedHelper.putKey(this, "lang", user.getLanguage());
        SharedHelper.putKey(this, "stripe_publishable_key", user.getStripePublishableKey());
        SharedHelper.putKey(this, "currency", user.getCurrency());
        SharedHelper.putKey(this, "measurementType", user.getMeasurement());
        SharedHelper.putKey(this, "walletBalance", String.valueOf(user.getWalletBalance()));
        SharedHelper.putKey(this, "username", String.format("%s %s", user.getFirstName(), user.getLastName()));
//        SharedHelper.putKey(this, "userInfo", printJSON(user));

        SharedHelper.putKey(this, "referral_code", user.getReferral_unique_id());
        SharedHelper.putKey(this, "referral_count", user.getReferral_count());
        SharedHelper.putKey(this, "referral_text", user.getReferral_text());
        SharedHelper.putKey(this, "referral_total_text", user.getReferral_total_text());

        name.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
        sub_name.setText(user.getEmail());
        SharedHelper.putKey(MainActivity.this, PROFILE_IMG, user.getPicture());
        Glide.with(MainActivity.this)
                .load(BuildConfig.BASE_IMAGE_URL + user.getPicture())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_user_placeholder)
                        .dontAnimate()
                        .error(R.drawable.ic_user_placeholder))
                .into(picture);
        MvpApplication.showOTP = user.getRide_otp().equals("1");

    }

    @Override
    public void onSuccess(DataResponse dataResponse) {
        Log.e(TAG, "response : checkStatus : " + new Gson().toJson(dataResponse));

        checkStatusResponse = dataResponse;
        updatePaymentEntities();
        SharedHelper.putKey(this, SOS_NUMBER, checkStatusResponse.getSos());


        if (dataResponse.getData() != null && dataResponse.getData().size() != 0) {

            startActivity(new Intent(this, LocationPickedActivity.class));

        }

        /*if (!Objects.requireNonNull(dataResponse.getData()).isEmpty()) {
            if (!CURRENT_STATUS.equals(dataResponse.getData().get(0).getStatus())) {
                DATUM = dataResponse.getData().get(0);
                CURRENT_STATUS = DATUM.getStatus();
            }
        } else if (CURRENT_STATUS.equals(SERVICE)) {
            Log.e("RRR CURRENT_STATUS = ", "" + CURRENT_STATUS);
        } else {
            CURRENT_STATUS = EMPTY;
        }
        if (CURRENT_STATUS.equals(STARTED) || CURRENT_STATUS.equals(ARRIVED) || CURRENT_STATUS.equals(PICKED_UP))
            if (mProviderLocation == null) {
                mProviderLocation = FirebaseDatabase.getInstance().getReference().child("loc_p_" + DATUM.getProvider().getId());
            }
        *//*if (canGoToChatScreen) {
            if (!isChatScreenOpen && DATUM != null) {
                Intent i = new Intent(baseActivity(), ChatActivity.class);
                i.putExtra("request_id", String.valueOf(DATUM.getId()));
                startActivity(i);
            }
            canGoToChatScreen = false;
        }*/
    }

    public void updatePaymentEntities() {
        if (checkStatusResponse != null) {
            isCash = checkStatusResponse.getCash() == 1;
            isCard = checkStatusResponse.getCard() == 1;

            Log.e(TAG, "updatePaymentEntities: " + isCard);

            MvpApplication.isPayumoney = checkStatusResponse.getPayumoney() == 1;
            MvpApplication.isPaypal = checkStatusResponse.getPaypal() == 1;
            MvpApplication.isBraintree = checkStatusResponse.getBraintree() == 1;
            MvpApplication.isPaypalAdaptive = checkStatusResponse.getPaypal_adaptive() == 1;
            MvpApplication.isPaytm = checkStatusResponse.getPaytm() == 1;

            SharedHelper.putKey(this, "currency", checkStatusResponse.getCurrency());
            if (isCash) RIDE_REQUEST.put(PAYMENT_MODE, Constants.PaymentMode.CASH);
            else if (isCard) RIDE_REQUEST.put(PAYMENT_MODE, Constants.PaymentMode.CARD);
        }
    }

    @Override
    public void onDestinationSuccess(Object object) {
        Log.e(TAG, "response : extendTrip : " + new Gson().toJson(object));
    }


    @OnClick({R.id.llpayment, R.id.gps, R.id.llyourtrips, R.id.llpromotion, R.id.llwallet, R.id.llpassbook, R.id.llsettings, R.id.llnotificationmanager, R.id.llhelp, R.id.llshare, R.id.llbecometrasporter, R.id.lllogout, R.id.rlViewProfile, R.id.llprivacypolicy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llpayment:
                startActivity(new Intent(this, PaymentActivity.class));
                break;
            case R.id.llyourtrips:
                startActivity(new Intent(this, YourTripActivity.class));
                break;
            case R.id.llpromotion:
                startActivity(new Intent(this, CouponActivity.class));
                break;
            case R.id.llwallet:
                startActivity(new Intent(this, WalletActivity.class));
                break;
            case R.id.llpassbook:
                startActivity(new Intent(this, WalletHistoryActivity.class));
                break;
            case R.id.llsettings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.llnotificationmanager:
                startActivity(new Intent(this, NotificationManagerActivity.class));
                break;
            case R.id.llhelp:
                startActivity(new Intent(this, HelpActivity.class));
                break;
            case R.id.llshare:
                navigateToShareScreen();
                break;
            case R.id.llbecometrasporter:
                break;
            case R.id.lllogout:
                ShowLogoutPopUp();
                break;

            case R.id.llprivacypolicy:

                startActivity(new Intent(this, PrivacyPolicyActivity.class));

                break;

            case R.id.rlViewProfile:
                startActivity(new Intent(this, ProfileActivity.class));
                break;

            case R.id.gps:
                if (mLastKnownLocation != null) {
                    LatLng currentLatLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM));
                }

                /*if (SOURCEVISIBLE) {

                } else {

                }*/

                break;


        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }


    public void navigateToShareScreen() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey Checkout this app," +
                getString(R.string.app_name) + "\nhttps://play.google.com/store/apps/details?id=" +
                BuildConfig.APPLICATION_ID);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void checknavigationdrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else {
            User user = new Gson().fromJson(SharedHelper.getKey(this, "userInfo"), User.class);
            if (user != null) {
                name.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
                sub_name.setText(user.getEmail());
                SharedHelper.putKey(MainActivity.this, PROFILE_IMG, user.getPicture());
                Glide.with(MainActivity.this)
                        .load(BuildConfig.BASE_IMAGE_URL + user.getPicture())
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_user_placeholder)
                                .dontAnimate()
                                .error(R.drawable.ic_user_placeholder))
                        .into(picture);
            }
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);

        else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            if (getSupportFragmentManager().findFragmentById(R.id.container_main)
                    instanceof ServiceTypeSlection) {
                getSupportFragmentManager().popBackStack();
            }
            getSupportFragmentManager().popBackStack();

            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {

            }
        } else {
            if (isDoubleBackPressed) {
                super.onBackPressed();
                return;
            }
            this.isDoubleBackPressed = true;
            Toast.makeText(this, getString(R.string.please_click_back_again_to_exit), Toast.LENGTH_SHORT).show();
        }

        new Handler().postDelayed(() -> isDoubleBackPressed = false, 2000);
    }


    public void ShowLogoutPopUp() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder
                .setMessage(getString(R.string.are_sure_you_want_to_logout)).setCancelable(false)
                .setPositiveButton(getString(R.string.yes), (dialog, id) -> mainPresenter.logout(SharedHelper.getKey(this, "user_id")))
                .setNegativeButton(getString(R.string.no), (dialog, id) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    @Override
    public void onSuccessLogout(Object object) {

        Log.e(TAG, "response : logout : " + new Gson().toJson(object));

        LogoutApp();
    }

    @Override
    public void onSuccess(AddressResponse response) {

        Log.e(TAG, "response : address : " + new Gson().toJson(response));

        home = (response.getHome().isEmpty()) ? null : response.getHome().get(response.getHome().size() - 1);
        work = (response.getWork().isEmpty()) ? null : response.getWork().get(response.getWork().size() - 1);
    }

    @Override
    public void onSuccess(List<Provider> providerList) {

        Log.e(TAG, "response : getProviders : " + new Gson().toJson(providerList));
//        SharedHelper.putProviders(this, printJSON(providerList));
    }

    @Override
    public void onSuccess(SettingsResponse response) {
        Log.e(TAG, "response : getSettings : " + new Gson().toJson(response));
    }

    @Override
    public void onSettingError(Throwable e) {

    }


    @Override
    public void onResume() {
        super.onResume();
//        registerReceiver(myReceiver, new IntentFilter(INTENT_FILTER));
        mainPresenter.getUserInfo();
        mainPresenter.checkStatus();

        /*HashMap<String, Object> map = new HashMap<>();
        map.put("latitude", 23.0706721);
        map.put("longitude", 72.6677789);
        mainPresenter.getProviders(map);*/


        /*if (CURRENT_STATUS.equalsIgnoreCase(EMPTY)) {
            RIDE_REQUEST.remove(DEST_ADD);
            RIDE_REQUEST.remove(DEST_LAT);
            RIDE_REQUEST.remove(DEST_LONG);
            mainPresenter.getSavedAddress();
        }*/

        getLocationPermission();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("source");

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            attachDestinationFragment();
        } else {

        }


    }


    public void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            isLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        isLocationPermissionGranted = false;
        if (requestCode == REQUEST_ACCESS_FINE_LOCATION)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isLocationPermissionGranted = true;
                updateLocationUI();
                getDeviceLocation();
                displayCurrentAddress();
            }
    }


    void getDeviceLocation() {
        try {
            if (isLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocation.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        mLastKnownLocation = task.getResult();
                        mGoogleMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_current_location))
                                .position(new LatLng(
                                        mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()
                                )));

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


    /*@Override
    public void onDirectionSuccess(Direction direction, String rawBody) {

        if (direction.isOK()) {
            if (!CURRENT_STATUS.equalsIgnoreCase(SERVICE))
                mGoogleMap.clear();
            Route route = direction.getRouteList().get(0);
            if (!route.getLegList().isEmpty()) {

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

                if (CURRENT_STATUS.equalsIgnoreCase(SERVICE))
                    srcMarker = mGoogleMap.addMarker(new MarkerOptions()
                            .position(origin)
                            .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView())));
                else srcMarker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(origin)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_2)));

                if (destMarker != null) destMarker.remove();
                destMarker = mGoogleMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.des_icon))
                        .position(destination));
            }

            polyLinePoints = route.getLegList().get(0).getDirectionPoint();

            if (CURRENT_STATUS.equalsIgnoreCase(SERVICE)) {
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
            } else
                mPolyline = mGoogleMap.addPolyline(DirectionConverter.createPolyline
                        (this, polyLinePoints, 2, getResources().getColor(R.color.colorAccent)));
        } else {
            Log.e(TAG, "onDirectionSuccess: " + direction.getErrorMessage());
            Toast.makeText(this, getString(R.string.root_not_available), Toast.LENGTH_SHORT).show();
        }


    }
*/

    private Bitmap getMarkerBitmapFromView() {

        View mView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.map_custom_infowindow, null);

        TextView tvEtaVal = mView.findViewById(R.id.tvEstimatedFare);
        String arrivalTime = destinationLeg.getArrival_time();
        if (arrivalTime.contains("hours")) arrivalTime = arrivalTime.replace("hours", "h\n");
        else if (arrivalTime.contains("hour")) arrivalTime = arrivalTime.replace("hour", "h\n");
        if (arrivalTime.contains("mins")) arrivalTime = arrivalTime.replace("mins", "min");
        tvEtaVal.setText(arrivalTime);
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

    /*@Override
    public void onDirectionFailure(Throwable t) {

    }*/

    @Override
    public void onLocationChanged(Location location) {

        if (location != null) {
            String cityName = null;
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses.size() > 0) {
                    System.out.println(addresses.get(0).getLocality());
                    cityName = addresses.get(0).getLocality();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            String s = location.getLatitude() + "\n" + location.getLongitude()
                    + "\n\n" + getString(R.string.my_current_city)
                    + cityName;
            Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onCameraIdle() {

    }

    @Override
    public void onCameraMove() {

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

                    RIDE_REQUEST.put(SRC_ADD, likelyPlaces.get(0).getPlace().getAddress());
                    RIDE_REQUEST.put(SRC_LAT, likelyPlaces.get(0).getPlace().getLatLng().latitude);
                    RIDE_REQUEST.put(SRC_LONG, likelyPlaces.get(0).getPlace().getLatLng().longitude);
                } else if (getLastKnownLocation() != null) {
                    mLastKnownLocation = getLastKnownLocation();
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                            mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()
                    ), DEFAULT_ZOOM));

                    SharedHelper.putKey(MainActivity.this, "latitude", String.valueOf(mLastKnownLocation.getLatitude()));
                    SharedHelper.putKey(MainActivity.this, "longitude", String.valueOf(mLastKnownLocation.getLongitude()));
                }
            })).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    // Log.e(getClass().getSimpleName(), "Place not found: " + apiException.getStatusCode());
                }

                if (getLastKnownLocation() != null) {
                    mLastKnownLocation = getLastKnownLocation();
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                            mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()
                    ), DEFAULT_ZOOM));

                    SharedHelper.putKey(MainActivity.this, "latitude", String.valueOf(mLastKnownLocation.getLatitude()));
                    SharedHelper.putKey(MainActivity.this, "longitude", String.valueOf(mLastKnownLocation.getLongitude()));
                }
            });

            if (mLastKnownLocation != null)
//                if (TextUtils.isEmpty(sourceTxt.getText().toString()) || sourceTxt.getText().toString().equals(getText(R.string.pickup_location)))
                if (mLastKnownLocation.getLatitude() > 0 && mLastKnownLocation.getLongitude() > 0) {
                    Location mLocation = getLastKnownLocation();
                    String address = getAddress(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
//                        sourceTxt.setText(address);
                    RIDE_REQUEST.put(SRC_ADD, address);
                    RIDE_REQUEST.put(SRC_LAT, mLastKnownLocation.getLatitude());
                    RIDE_REQUEST.put(SRC_LONG, mLastKnownLocation.getLongitude());
                }
            try {
                hideLoading();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } else getLocationPermission();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapFragment.onDestroy();
        mainPresenter.onDetach();
//        unregisterReceiver(myReceiver);
//        h.removeCallbacks(r);
    }
}
