package com.myeotra.user.ui.fragment.service_flow;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.model.LatLng;
import com.myeotra.user.R;
import com.myeotra.user.base.BaseFragment;
import com.myeotra.user.chat.ChatActivity;
import com.myeotra.user.common.CancelRequestInterface;
import com.myeotra.user.data.SharedHelper;
import com.myeotra.user.data.network.model.Datum;
import com.myeotra.user.data.network.model.Provider;
import com.myeotra.user.data.network.model.ProviderService;
import com.myeotra.user.data.network.model.ServiceType;
import com.myeotra.user.ui.activity.location_pick.LocationPickedActivity;
import com.myeotra.user.ui.fragment.cancel_ride.CancelRideDialogFragment;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.myeotra.user.MvpApplication.DATUM;
import static com.myeotra.user.MvpApplication.RIDE_REQUEST;
import static com.myeotra.user.MvpApplication.showOTP;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.SRC_LAT;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.SRC_LONG;
import static com.myeotra.user.common.Constants.Status.ARRIVED;
import static com.myeotra.user.common.Constants.Status.PICKED_UP;
import static com.myeotra.user.common.Constants.Status.STARTED;
import static com.myeotra.user.data.SharedHelper.key.SOS_NUMBER;

public class ServiceFlowFragment extends BaseFragment
        implements ServiceFlowIView, CancelRequestInterface {

    public int PERMISSIONS_REQUEST_PHONE = 4;
    @BindView(R.id.otp)
    TextView otp;
    @BindView(R.id.avatar)
    CircleImageView avatar;
    @BindView(R.id.first_name)
    TextView firstName;
    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.rating)
    RatingBar rating;
    @BindView(R.id.cancel)
    Button cancel;
    @BindView(R.id.share_ride)
    Button sharedRide;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.service_type_name)
    TextView serviceTypeName;
    @BindView(R.id.service_number)
    TextView serviceNumber;
    @BindView(R.id.service_model)
    TextView serviceModel;
    @BindView(R.id.call)
    Button call;
    @BindView(R.id.chat)
    ImageView chat;
    @BindView(R.id.provider_eta)
    TextView providerEta;
    private Runnable runnable;
    private Handler handler;
    private int delay = 2 * 60 * 1000;
    private String providerPhoneNumber = null;
    private String shareRideText = "";
    private ServiceFlowPresenter<ServiceFlowFragment> presenter = new ServiceFlowPresenter<>();
    private CancelRequestInterface callback;
    private String TAG = "ServiceFlowFragment";

    public ServiceFlowFragment() {
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_service_flow;
    }

    @Override
    public View initView(View view) {
        ButterKnife.bind(this, view);
        callback = this;
        presenter.attachView(this);

        if (DATUM != null) initView(DATUM);
        return view;
    }

    @Override
    public void onDestroyView() {
        presenter.onDetach();
        super.onDestroyView();
    }

    @OnClick({R.id.sos, R.id.cancel, R.id.share_ride, R.id.call, R.id.chat})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sos:
                sos();
                break;
            case R.id.cancel:
                CancelRideDialogFragment cancelRideFragment = new CancelRideDialogFragment(callback);
                cancelRideFragment.show(baseActivity().getSupportFragmentManager(), cancelRideFragment.getTag());
                break;
            case R.id.share_ride:
                sharedRide();
                break;
            case R.id.call:
                callPhoneNumber(providerPhoneNumber);
                break;
            case R.id.chat:
                if (DATUM != null) {
                    Intent i = new Intent(baseActivity(), ChatActivity.class);
                    i.putExtra("request_id", String.valueOf(DATUM.getId()));
                    startActivity(i);
                }
                break;
        }
    }

    @SuppressLint({"StringFormatInvalid", "RestrictedApi"})
    private void initView(Datum datum) {
        Provider provider = datum.getProvider();
        if (provider != null) {
            firstName.setText(String.format("%s %s", provider.getFirstName(), provider.getLastName()));
            rating.setRating(Float.parseFloat(provider.getRating()));
            Glide.with(baseActivity())
                    .load(provider.getAvatar())
                    .apply(RequestOptions
                            .placeholderOf(R.drawable.ic_user_placeholder)
                            .dontAnimate()
                            .error(R.drawable.ic_user_placeholder))
                    .into(avatar);
            providerPhoneNumber = provider.getMobile();
        }

        ServiceType serviceType = datum.getServiceType();
        if (serviceType != null) {
            serviceTypeName.setText(serviceType.getName());
            Glide.with(baseActivity())
                    .load(serviceType.getImage())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_car)
                            .dontAnimate()
                            .error(R.drawable.ic_car))
                    .into(image);
        }

        chat.setVisibility(PICKED_UP.equalsIgnoreCase(datum.getStatus()) ? View.GONE : View.VISIBLE);

        ProviderService providerService = datum.getProviderService();
        if (providerService != null) {
            serviceNumber.setText(providerService.getServiceNumber());
            serviceModel.setText(providerService.getServiceModel());
        }

        otp.setText(getString(R.string.otp_, datum.getOtp()));
        otp.setVisibility(showOTP ? View.VISIBLE : View.GONE);

        shareRideText = getString(R.string.app_name) + ": "
                + datum.getUser().getFirstName() + " " + datum.getUser().getLastName() + " is riding in "
                + datum.getServiceType().getName() + " would like to share his ride "
                + "http://maps.google.com/maps?q=loc:" + datum.getDLatitude() + "," + datum.getDLongitude();

        switch (datum.getStatus()) {
            case STARTED:
                status.setText(R.string.driver_accepted_your_request);
                break;
            case ARRIVED:
                status.setText(R.string.driver_has_arrived_your_location);
                break;
            case PICKED_UP:
                status.setText(R.string.you_are_on_ride);
                cancel.setVisibility(View.GONE);
                sharedRide.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }

        if (STARTED.equalsIgnoreCase(datum.getStatus())) {
            LatLng source = new LatLng(datum.getProvider().getLatitude(), datum.getProvider().getLongitude());
            LatLng destination = new LatLng(datum.getSLatitude(), datum.getSLongitude());
            ((LocationPickedActivity) Objects.requireNonNull(getActivity())).drawRoute(source, destination);

        } else {
            LatLng origin = new LatLng(datum.getSLatitude(), datum.getSLongitude());
            LatLng destination = new LatLng(datum.getDLatitude(), datum.getDLongitude());
            ((LocationPickedActivity) Objects.requireNonNull(getActivity())).drawRoute(origin, destination);

        }

    }

    private void sos() {
        new AlertDialog.Builder(getContext())
                .setTitle(getContext().getResources().getString(R.string.sos_alert))
                .setMessage(R.string.are_sure_you_want_to_emergency_alert)
                .setCancelable(true)
                .setPositiveButton(getContext().getResources().getString(R.string.yes), (dialog, which) -> callPhoneNumber(SharedHelper.getKey(getContext(), SOS_NUMBER)))
                .setNegativeButton(getContext().getResources().getString(R.string.no), (dialog, which) -> dialog.cancel())
                .show();
    }

    private void callPhoneNumber(String mobileNumber) {
        if (mobileNumber != null && !mobileNumber.isEmpty()) {
            if (ActivityCompat.checkSelfPermission(baseActivity(), Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED)
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobileNumber)));
            else ActivityCompat.requestPermissions(baseActivity(),
                    new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_PHONE);
        }
    }

    private void sharedRide() {
        try {
            String appName = getString(R.string.app_name) + " " + getString(R.string.share_ride);
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareRideText);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, appName);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } catch (Exception e) {
            Toast.makeText(baseActivity(), "applications not found!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_PHONE)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(baseActivity(), "Permission Granted. Try Again!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void cancelRequestMethod() {
    }

    @Override
    public void onDestroy() {
        presenter.onDetach();
        if (handler != null) handler.removeCallbacks(runnable);
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (handler != null) handler.removeCallbacks(runnable);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (presenter.getMvpView() == null) {
            presenter.attachView(this);
        }

        handler = new Handler();
        runnable = () -> {
            try {
                LatLng src = null;
                LatLng des = null;
                Log.e(TAG, "****: " + DATUM.getStatus());

                if (DATUM.getStatus().equalsIgnoreCase(STARTED) || DATUM.getStatus().equalsIgnoreCase(ARRIVED)) {
                    Log.e(TAG, "if: ");
                    src = new LatLng((Double) RIDE_REQUEST.get(SRC_LAT), (Double) RIDE_REQUEST.get(SRC_LONG));
                    des = SharedHelper.getCurrentLocation(getContext());
                } else if (DATUM.getStatus().equalsIgnoreCase(PICKED_UP)) {
                    Log.e(TAG, "else: ");
                    src = SharedHelper.getCurrentLocation(getContext());
                    des = new LatLng(DATUM.getDLatitude(), DATUM.getDLongitude());
                }

                Log.e(TAG, "****: " + src + " dest = " + des);


                GoogleDirection
                        .withServerKey(getString(R.string.google_map_key))
                        .from(src)
                        .to(des)
                        .transportMode(TransportMode.DRIVING)
                        .execute(new DirectionCallback() {
                            @Override
                            public void onDirectionSuccess(Direction direction, String rawBody) {
                                if (direction.isOK()) {
                                    Route route = direction.getRouteList().get(0);
                                    if (!route.getLegList().isEmpty()) {

                                        providerEta.setVisibility(View.VISIBLE);
                                        Leg leg = route.getLegList().get(0);
//                                        providerEta.setVisibility(View.VISIBLE);
//                                        Log.e("android", "onDirectionSuccess: " + new Gson().toJson(route.getLegList().get(0)));
//                                        String arrivalTime = String.valueOf(leg.getDuration().getText());
                                        String arrivalTime = leg.getDuration().getText().toString();

                                        Log.e(TAG, "onDirectionSuccess : " + arrivalTime);

                                        if (arrivalTime.contains("hours"))
                                            arrivalTime = arrivalTime.replace("hours", "h\n");
                                        else if (arrivalTime.contains("hour"))
                                            arrivalTime = arrivalTime.replace("hour", "h\n");
                                        if (arrivalTime.contains("mins"))
                                            arrivalTime = arrivalTime.replace("mins", "min");
//                                        providerEta.setText(String.format("ETA : %s", arrivalTime));
                                        providerEta.setText("ETA : " + arrivalTime);
                                    }
                                } else {
//                                    Toast.makeText(baseActivity(), "ETA : " + direction.getErrorMessage(), Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "onDirectionSuccess: 2  " + direction.getErrorMessage());
                                    providerEta.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onDirectionFailure(Throwable t) {
                                t.printStackTrace();
                                Log.e(TAG, "onDirectionFailure  ---: " + t.getMessage());
                                providerEta.setVisibility(View.GONE);
                            }
                        });
                handler.postDelayed(runnable, delay);
            } catch (Exception e) {

                Log.e(TAG, "****: onResume: " + e.getMessage());
                handler.postDelayed(runnable, 100);
                e.printStackTrace();
            }
        };
        handler.postDelayed(runnable, 100);
    }
}
