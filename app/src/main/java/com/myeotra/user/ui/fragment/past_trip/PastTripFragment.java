package com.myeotra.user.ui.fragment.past_trip;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.myeotra.user.MvpApplication;
import com.myeotra.user.R;
import com.myeotra.user.base.BaseFragment;
import com.myeotra.user.data.network.model.Datum;
import com.myeotra.user.data.network.model.Payment;
import com.myeotra.user.ui.activity.past_trip_detail.PastTripDetailActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PastTripFragment extends BaseFragment implements PastTripIView {

    @BindView(R.id.past_trip_rv)
    RecyclerView pastTripRv;
    @BindView(R.id.error_layout)
    LinearLayout errorLayout;
    @BindView(R.id.progress_bar)
    ImageView progress_bar;
    /*@BindView(R.id.progress_bar)
    LottieAnimationView progressBar;*/
    @BindView(R.id.tv_error)
    TextView error;
    Unbinder unbinder;

    List<Datum> list = new ArrayList<>();
    TripAdapter adapter;

    private PastTripPresenter<PastTripFragment> presenter = new PastTripPresenter<>();

    public PastTripFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_past_trip;
    }

    @Override
    public View initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        presenter.attachView(this);
        error.setText(getString(R.string.no_past_found));
        adapter = new TripAdapter(list);
        Glide.with(this)
                .load(R.drawable.loader)
                .into(progress_bar);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager
                (getActivity(), LinearLayoutManager.VERTICAL, false);
        pastTripRv.setLayoutManager(mLayoutManager);
        pastTripRv.setItemAnimator(new DefaultItemAnimator());
        pastTripRv.setAdapter(adapter);
        progress_bar.setVisibility(View.VISIBLE);
        presenter.pastTrip();
        return view;
    }

    @Override
    public void onSuccess(List<Datum> datumList) {
        progress_bar.setVisibility(View.GONE);

        list.clear();
        list.addAll(datumList);
        Log.e("AAAA", "onSuccess: " + new Gson().toJson(datumList));
        adapter.notifyDataSetChanged();

        if (list.isEmpty()) errorLayout.setVisibility(View.VISIBLE);
        else errorLayout.setVisibility(View.GONE);
    }

    @Override
    public void onError(Throwable e) {
        progress_bar.setVisibility(View.GONE);
        handleError(e);
    }

    @Override
    public void onDestroyView() {
        presenter.onDetach();
        super.onDestroyView();
    }

    private class TripAdapter extends RecyclerView.Adapter<TripAdapter.MyViewHolder> {

        private final String TAG = "tripadapter";
        private List<Datum> list;
        private Context mContext;

        private TripAdapter(List<Datum> list) {
            this.list = list;


            Log.e(TAG, "TripAdapter: " + new Gson().toJson(list));


        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            return new MyViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_past_trip, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Datum datum = list.get(position);
            Date inputdate = null;
            String outputDate = "";
//            String latEiffelTower = "48.858235";
//            String lngEiffelTower = "2.294571";
//            String url = "http://maps.google.com/maps/api/staticmap?center=" + latEiffelTower + "," + lngEiffelTower + "&zoom=15&size=200x200&sensor=false&key=YOUR_API_KEY"

            ;

            StringTokenizer tk = new StringTokenizer(datum.getFinishedAt());


            @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");


            try {
                inputdate = inputFormat.parse(tk.nextToken());
                outputDate = outputFormat.format(inputdate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.finishedAt.setText(outputDate);
            holder.bookingId.setText(datum.getBookingId());


            Glide.with(baseActivity()).load(datum.getStaticMap()).apply(RequestOptions.
                    placeholderOf(R.drawable.ic_launcher_background).dontAnimate().
                    error(R.drawable.ic_launcher_background)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)).into(holder.staticMap);

            /*Glide.with(Objects.requireNonNull(getActivity()))
                    .load(datum.getStaticMap())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_background)
                            .dontAnimate().
                                    error(R.drawable.ic_launcher_background).diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(holder.staticMap);*/


            Payment payment = datum.getPayment();
            if (payment != null) {
                String s = getNewNumberFormat(payment.getTotal());
//                holder.payable.setText(SharedHelper.getKey(mContext, "currency") + " " + s);
                holder.payable.setText(" " + s);
            }

            holder.rating.setRating((float) datum.getRating().getProviderRating());

            if (datum.getPayment().getPaymentMode().equalsIgnoreCase("CARD")) {
                holder.paymenttype.setText(datum.getPayment().getPaymentMode());
                Glide.with(baseActivity())
                        .load(R.drawable.ic_card)
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_card)
                                .dontAnimate()
                                .error(R.drawable.ic_card).diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(holder.avatar);
            } else {

                Glide.with(baseActivity())
                        .load(R.drawable.ic_cash)
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_cash)
                                .dontAnimate()
                                .error(R.drawable.ic_cash).diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(holder.avatar);

                holder.paymenttype.setText(datum.getPayment().getPaymentMode());
            }

            holder.serviceType.setText(datum.getServiceType().getName());


            /*ServiceType mService = ;
            if (mService != null) {*/
                /*Glide.with(baseActivity())
                        .load(R.drawable.ic_cash)
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_cash)
                                .dontAnimate()
                                .error(R.drawable.ic_cash).diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(holder.avatar);
            }*/


            //  holder.payable.setText(payment.getTotal().toString());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private CardView itemView;
            private TextView bookingId, payable, finishedAt, serviceType, paymenttype;
            private ImageView staticMap, avatar;

            RatingBar rating;

            MyViewHolder(View view) {
                super(view);
                itemView = view.findViewById(R.id.item_view);
                bookingId = view.findViewById(R.id.booking_id);
                payable = view.findViewById(R.id.payable);
                finishedAt = view.findViewById(R.id.finished_at);
                staticMap = view.findViewById(R.id.static_map);
                serviceType = view.findViewById(R.id.serviceType);
                avatar = view.findViewById(R.id.avatar);
                rating = view.findViewById(R.id.rating);
                paymenttype = view.findViewById(R.id.paymenttype);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                if (view.getId() == R.id.item_view) {
                    MvpApplication.DATUM = list.get(position);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation
                            (baseActivity(), staticMap, ViewCompat.getTransitionName(staticMap));
                    Intent intent = new Intent(getActivity(), PastTripDetailActivity.class);
                    startActivity(intent, options.toBundle());
                }
            }
        }
    }
}
