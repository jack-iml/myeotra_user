package com.myeotra.user.ui.fragment.book_ride;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.myeotra.user.R;
import com.myeotra.user.base.BaseFragment;
import com.myeotra.user.common.Constants;
import com.myeotra.user.common.EqualSpacingItemDecoration;
import com.myeotra.user.data.network.model.EstimateFare;
import com.myeotra.user.data.network.model.PromoList;
import com.myeotra.user.data.network.model.PromoResponse;
import com.myeotra.user.data.network.model.Service;
import com.myeotra.user.ui.activity.location_pick.LocationPickedActivity;
import com.myeotra.user.ui.activity.payment.PaymentActivity;
import com.myeotra.user.ui.adapter.CouponAdapter;
import com.myeotra.user.ui.fragment.schedule.ScheduleFragment;
import com.rbrooks.indefinitepagerindicator.IndefinitePagerIndicator;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.myeotra.user.MvpApplication.RIDE_REQUEST;
import static com.myeotra.user.MvpApplication.isCard;
import static com.myeotra.user.MvpApplication.isCash;
import static com.myeotra.user.common.Constants.BroadcastReceiver.INTENT_FILTER;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.CARD_ID;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.CARD_LAST_FOUR;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.DISTANCE_VAL;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.PAYMENT_MODE;
import static com.myeotra.user.ui.activity.payment.PaymentActivity.PICK_PAYMENT_METHOD;

public class BookRideFragment extends BaseFragment implements BookRideIView {

    Unbinder unbinder;
    @BindView(R.id.schedule_ride)
    Button scheduleRide;
    @BindView(R.id.ride_now)
    Button rideNow;
    @BindView(R.id.use_wallet)
    CheckBox useWallet;

    @BindView(R.id.tvEstimateTime)
    TextView tvEstimateTime;
    @BindView(R.id.estimated_image)
    ImageView estimatedImage;
    @BindView(R.id.view_coupons)
    TextView viewCoupons;
    @BindView(R.id.estimated_payment_mode)
    TextView estimatedPaymentMode;
    @BindView(R.id.tvChange)
    TextView tvChange;

    @BindView(R.id.tvEstimateFare)
    TextView tvEstimateFare;

    @BindView(R.id.tvServiceName)
    TextView tvServiceName;


    @BindView(R.id.wallet_balance)
    TextView walletBalance;
    @BindView(R.id.llEstimatedFareContainer)
    RelativeLayout llEstimatedFareContainer;
    private int lastSelectCoupon = 0;
    private String mCouponStatus;
    public String paymentMode;
    private Double estimatedFare;
    private String TAG = "bookride";
    private BookRidePresenter<BookRideFragment> presenter = new BookRidePresenter<>();
    private CouponListener mCouponListener = new CouponListener() {
        @Override
        public void couponClicked(int pos, PromoList promoList, String promoStatus) {
            if (!promoStatus.equalsIgnoreCase(getString(R.string.remove))) {
                lastSelectCoupon = promoList.getId();
                viewCoupons.setText(promoList.getPromoCode());
                viewCoupons.setTextColor(getResources().getColor(R.color.colorAccent));
                viewCoupons.setBackgroundResource(R.drawable.coupon_transparent);
                mCouponStatus = viewCoupons.getText().toString();
                Double discountFare = (estimatedFare * promoList.getPercentage()) / 100;

                if (discountFare > promoList.getMaxAmount()) {
                    tvEstimateFare.setText(String.format("%s",
                            getNewNumberFormat(estimatedFare - promoList.getMaxAmount())));
                    /*tvEstimateFare.setText(String.format("%s %s",
                            SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                            getNewNumberFormat(estimatedFare - promoList.getMaxAmount())));*/
                } else {
                    tvEstimateFare.setText(String.format("%s",
                            getNewNumberFormat(estimatedFare - discountFare)));
                    /*tvEstimateFare.setText(String.format("%s %s",
                            SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                            getNewNumberFormat(estimatedFare - discountFare)));*/
                }
            } else {
                scaleView(viewCoupons, 0f, 0.9f);
                viewCoupons.setText(getString(R.string.view_coupon));
                viewCoupons.setBackgroundResource(R.drawable.button_round_accent);
                viewCoupons.setTextColor(getResources().getColor(R.color.white));
                mCouponStatus = viewCoupons.getText().toString();
                tvEstimateFare.setText(String.format("%s",
                        getNewNumberFormat(estimatedFare)));
                /*tvEstimateFare.setText(String.format("%s %s",
                        SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                        getNewNumberFormat(estimatedFare)));*/
            }
        }
    };




    /*public static BookRideFragment newInstance(Intent data) {

        Log.e("data", "newInstance: " + data.getStringExtra("payment_mode"));
        BookRideFragment fragment = new BookRideFragment();
        Bundle args = new Bundle();
        args.putString("payment_mode", data.getStringExtra("payment_mode"));
        args.putString("card_id", data.getStringExtra("card_id"));
        args.putString("card_last_four", data.getStringExtra("card_last_four"));
        fragment.setArguments(args);
        return fragment;
    }*/


    @Override
    public void onResume() {
        super.onResume();


        initPayment(estimatedPaymentMode);
        tvChange.setVisibility((!isCard && isCash) ? View.GONE : View.VISIBLE);
        if (presenter.getMvpView() == null) {
            presenter = new BookRidePresenter<>();
            presenter.attachView(this);
        }


        getActivity().registerReceiver(mNotificationReceiver, new IntentFilter("KEY"));


    }

    private BroadcastReceiver mNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent data) {

            Log.e(TAG, "onReceive: " + data.getStringExtra("payment_mode"));
            RIDE_REQUEST.put(PAYMENT_MODE, data.getStringExtra("payment_mode"));
            paymentMode = data.getStringExtra("payment_mode");
            if (data.getStringExtra("payment_mode").equals("CARD")) {
                RIDE_REQUEST.put(CARD_ID, data.getStringExtra("card_id"));
                RIDE_REQUEST.put(CARD_LAST_FOUR, data.getStringExtra("card_last_four"));
            }
            initPayment(estimatedPaymentMode);
        }
    };

   /* public void updatePayment(String payment_mode, String card_id, String card_last_four) {
        RIDE_REQUEST.put(PAYMENT_MODE, payment_mode);
//        paymentMode = data.getStringExtra("payment_mode");
        if (payment_mode.equals("CARD")) {
            RIDE_REQUEST.put(CARD_ID, card_id);
            RIDE_REQUEST.put(CARD_LAST_FOUR, card_last_four);
        }
        initPayment(estimatedPaymentMode);
    }*/


    @Override
    public int getLayoutId() {
        return R.layout.fragment_book_ride;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        presenter.attachView(this);
        Bundle args = getArguments();

        /*if (getArguments() != null) {
            Log.e(TAG, "onResume:-- " + getArguments().getString("payment_mode"));
            String payment_mode = getArguments().getString("payment_mode");
            String card_id = getArguments().getString("card_id");
            String card_last_four = getArguments().getString("card_last_four");
            updatePayment(payment_mode, card_id, card_last_four);
        }*/


        if (LocationPickedActivity.arriveTime != null && LocationPickedActivity.arriveTime.trim().length() > 0) {
            tvEstimateTime.setText("" + LocationPickedActivity.arriveTime.trim());
        }
        if (args != null) {
            String serviceName = args.getString("service_name");
            Service service = (Service) args.getSerializable("mService");
            EstimateFare estimateFare = (EstimateFare) args.getSerializable("estimate_fare");
            double walletAmount = Objects.requireNonNull(estimateFare).getWalletBalance();
            if (serviceName != null && !serviceName.isEmpty()) {
                Glide.with(Objects.requireNonNull(getContext()))
                        .load(Objects.requireNonNull(service).getImage())
                        .apply(RequestOptions
                                .placeholderOf(R.drawable.ic_car)
                                .dontAnimate()
                                .override(100, 100)
                                .error(R.drawable.ic_car))
                        .into(estimatedImage);
                estimatedFare = estimateFare.getEstimatedFare();
                tvEstimateFare.setText(" " + getNewNumberFormat(estimatedFare) + " | ");
                tvServiceName.setText("" + serviceName);
                tvEstimateFare.setText("" + estimateFare.getEstimatedFare() + " | ");
                /*tvEstimateFare.setText(SharedHelper.getKey(getContext(), "currency") + " " +
                        getNewNumberFormat(estimatedFare));
*/
                if (walletAmount == 0) {
                    useWallet.setVisibility(View.GONE);
                    walletBalance.setVisibility(View.GONE);
                } else {
                    useWallet.setVisibility(View.VISIBLE);
                    walletBalance.setVisibility(View.VISIBLE);
                    walletBalance.setText(getNewNumberFormat(Double.parseDouble(String.valueOf(walletAmount))));

                }
                RIDE_REQUEST.put(DISTANCE_VAL, estimateFare.getDistance());
            }
        }
        scaleView(viewCoupons, 0f, 0.9f);

        return view;
    }

    public void scaleView(View v, float startScale, float endScale) {
        Animation anim = new ScaleAnimation(
                1f, 1f, // Start and end values for the X axis scaling
                startScale, endScale, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 1f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(1000);
        v.startAnimation(anim);
    }

    @Override
    public void onDestroyView() {
        presenter.onDetach();
        getActivity().unregisterReceiver(mNotificationReceiver);
        super.onDestroyView();

    }

    @OnClick({R.id.schedule_ride, R.id.ride_now, R.id.view_coupons, R.id.tvChange})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.schedule_ride:

                if (Objects.requireNonNull(RIDE_REQUEST.get(PAYMENT_MODE)).toString().equals(Constants.PaymentMode.CARD)) {
                    if (RIDE_REQUEST.containsKey(CARD_LAST_FOUR)) {
                        ((LocationPickedActivity) Objects.requireNonNull(getActivity())).changeFragment(new ScheduleFragment());
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(),
                                getResources().getString(R.string.choose_card), Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    ((LocationPickedActivity) Objects.requireNonNull(getActivity())).changeFragment(new ScheduleFragment());
                }

                break;
            case R.id.ride_now:
                if (Objects.requireNonNull(RIDE_REQUEST.get(PAYMENT_MODE)).toString().equals(Constants.PaymentMode.CARD)) {
                    if (RIDE_REQUEST.containsKey(CARD_LAST_FOUR))
                        sendRequest();
                    else
                        Toast.makeText(getActivity().getApplicationContext(),
                                getResources().getString(R.string.choose_card), Toast.LENGTH_SHORT)
                                .show();
                } else {
                    sendRequest();
                }
                break;
            case R.id.view_coupons:
                showLoading();
                try {
                    presenter.getCouponList();
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        hideLoading();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
                break;
            case R.id.tvChange:
                ((LocationPickedActivity) Objects.requireNonNull(getActivity())).updatePaymentEntities();
                getActivity().startActivityForResult(new Intent(getActivity(), PaymentActivity.class), PICK_PAYMENT_METHOD);
                break;
        }
    }






    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e(TAG, "onActivityResult: Book Ride fragment" );

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

    private Dialog couponDialog(PromoResponse promoResponse) {
        BottomSheetDialog couponDialog = new BottomSheetDialog(Objects.requireNonNull(getContext()), R.style.SheetDialog);
        couponDialog.setCanceledOnTouchOutside(true);
        couponDialog.setCancelable(true);
        couponDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        couponDialog.setContentView(R.layout.activity_coupon_dialog);
        RecyclerView couponView = couponDialog.findViewById(R.id.coupon_rv);
        IndefinitePagerIndicator indicator = couponDialog.findViewById(R.id.recyclerview_pager_indicator);
        List<PromoList> couponList = promoResponse.getPromoList();
        if (couponList != null && !couponList.isEmpty()) {
            CouponAdapter couponAdapter = new CouponAdapter(getActivity(), couponList,
                    mCouponListener, couponDialog, lastSelectCoupon, mCouponStatus);
            assert couponView != null;
            couponView.setLayoutManager(new LinearLayoutManager(getActivity(),
                    LinearLayoutManager.HORIZONTAL, false));
            couponView.setItemAnimator(new DefaultItemAnimator());
            couponView.addItemDecoration(new EqualSpacingItemDecoration(16,
                    EqualSpacingItemDecoration.HORIZONTAL));
            Objects.requireNonNull(indicator).attachToRecyclerView(couponView);
            couponView.setAdapter(couponAdapter);
            couponAdapter.notifyDataSetChanged();
        }
        couponDialog.setOnKeyListener((dialogInterface, keyCode, keyEvent) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                new BottomSheetDialog(getContext()).dismiss();
                Log.d("TAG", "--------- Do Something -----------");
                return true;
            }
            return false;
        });
        Window window = couponDialog.getWindow();
        assert window != null;
        WindowManager.LayoutParams param = window.getAttributes();
        param.gravity = Gravity.CENTER | Gravity.CENTER_HORIZONTAL;
        window.setAttributes(param);
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        couponDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        return couponDialog;
    }

    public void sendRequest() {

        HashMap<String, Object> map = new HashMap<>(RIDE_REQUEST);
        map.put("use_wallet", useWallet.isChecked() ? 1 : 0);
        map.put("promocode_id", lastSelectCoupon);
        if (paymentMode != null && !paymentMode.equalsIgnoreCase("")) {
            map.put("payment_mode", paymentMode);
        } else {
            map.put("payment_mode", "CASH");
        }
        showLoading();
        try {

            presenter.rideNow(map);
        } catch (Exception e) {
            Log.e(TAG, "sendRequest err: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccess(@NonNull Object object) {

        Log.e(TAG, "response : ridenow : " + new Gson().toJson(object));
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
//        Toast.makeText(baseActivity(), "-----"+object.toString(), Toast.LENGTH_SHORT).show();
        baseActivity().sendBroadcast(new Intent(INTENT_FILTER));
    }

    @Override
    public void onError(Throwable e) {
        Log.e(TAG, "onError: " + e.getMessage());
        handleError(e);
    }

    @Override
    public void onSuccessCoupon(PromoResponse promoResponse) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (promoResponse != null && promoResponse.getPromoList() != null
                && !promoResponse.getPromoList().isEmpty()) couponDialog(promoResponse).show();
        else Toast.makeText(baseActivity(), "Coupon is empty", Toast.LENGTH_SHORT).show();
    }




    /*public void updatepyment(Intent data) {
        RIDE_REQUEST.put(PAYMENT_MODE, data.getStringExtra("payment_mode"));
        paymentMode = data.getStringExtra("payment_mode");
        if (data.getStringExtra("payment_mode").equals("CARD")) {
            RIDE_REQUEST.put(CARD_ID, data.getStringExtra("card_id"));
            RIDE_REQUEST.put(CARD_LAST_FOUR, data.getStringExtra("card_last_four"));
        }
        initPayment(estimatedPaymentMode);

    }
*/
//    @Override


    public interface CouponListener {
        void couponClicked(int pos, PromoList promoList, String promoStatus);
    }
}