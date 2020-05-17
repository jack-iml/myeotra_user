package com.myeotra.user.ui.fragment.rate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.myeotra.user.R;
import com.myeotra.user.base.BaseBottomSheetDialogFragment;
import com.myeotra.user.data.SharedHelper;
import com.myeotra.user.data.network.model.Datum;
import com.myeotra.user.data.network.model.Provider;
import com.myeotra.user.data.network.model.RateCommentResponse;
import com.myeotra.user.ui.activity.location_pick.LocationPickedActivity;
import com.myeotra.user.ui.activity.main.MainActivity;
import com.myeotra.user.ui.adapter.RateCommentAdapter;
import com.myeotra.user.ui.fragment.invoice.InvoiceFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.myeotra.user.MvpApplication.DATUM;
import static com.myeotra.user.chat.ChatActivity.chatPath;
import static com.myeotra.user.common.Constants.BroadcastReceiver.INTENT_FILTER;
import static com.myeotra.user.common.Constants.Status.EMPTY;

public class RatingDialogFragment extends BaseBottomSheetDialogFragment implements RatingIView, RateCommentAdapter.CommentClickListner {

    private static final String TAG = "AAAA";
    @BindView(R.id.avatar)
    CircleImageView avatar;
    @BindView(R.id.rating)
    RatingBar rating;
    @BindView(R.id.comment)
    EditText comment;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.ratings_name)
    TextView ratingsName;

    @BindView(R.id.rvComment)
    RecyclerView rvComment;
    RecyclerView.LayoutManager layoutManager;
    private RatingPresenter<RatingDialogFragment> presenter = new RatingPresenter<>();
    private RateCommentAdapter adapter;
    private List<RateCommentResponse.RateComment> commentlist;
    ;

    public RatingDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_rating_dialog;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initView(View view) {
        InvoiceFragment.isInvoiceCashToCard = false;
        setCancelable(false);
        getDialog().setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            View bottomSheetInternal = d.findViewById(R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        getDialog().setCanceledOnTouchOutside(false);
        ButterKnife.bind(this, view);
        presenter.attachView(this);
        presenter.getComment();

        if (DATUM != null) {
            Provider provider = DATUM.getProvider();
            if (provider != null) {
                Glide.with(baseActivity()).load(provider.getAvatar()).
                        apply(RequestOptions.placeholderOf(R.drawable.ic_user_placeholder).
                                dontAnimate().error(R.drawable.ic_user_placeholder)).into(avatar);
                ratingsName.setText(SharedHelper.getKey(getActivity(), "username"));

            }
        }
    }

    @Override
    public void onSuccess(Object object) {

        Log.e(TAG, "rate res : " + new Gson().toJson(object));
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        dismiss();
        Objects.requireNonNull(getActivity()).sendBroadcast(new Intent(INTENT_FILTER));
        ((LocationPickedActivity) Objects.requireNonNull(getContext())).changeFlow(EMPTY);
        getActivity().finishAffinity();
        startActivity(new Intent(getActivity(), MainActivity.class));
        if (!TextUtils.isEmpty(chatPath))
            FirebaseDatabase.getInstance().getReference().child(chatPath).removeValue();
    }

    @Override
    public void onSuccess(RateCommentResponse object) {
        Log.e(TAG, "getComment : res : " + new Gson().toJson(object));

        commentlist = new ArrayList<>();
        commentlist = object.getRateComment();

        rvComment.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        if (object.getRateComment() != null && object.getRateComment().size() > 0) {
            adapter = new RateCommentAdapter(getActivity(), object.getRateComment());
            adapter.RegisterInterface(this);
            rvComment.setAdapter(adapter);
        }
    }


    @Override
    public void onError(Throwable e) {

        Log.e(TAG, "rtndlg onError: " + e.getMessage());
        handleError(e);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnClick(R.id.submit)
    public void onViewClicked() {
        if (DATUM != null) {
            Datum datum = DATUM;
            HashMap<String, Object> map = new HashMap<>();
            map.put("request_id", datum.getId());

            if (rating.getRating() == 0.0) {
                map.put("rating", 0.0);
            } else {
                map.put("rating", Math.round(rating.getRating()));
            }
            map.put("comment", comment.getText().toString());
            showLoading();
            presenter.rate(map);

        }
    }

    @Override
    public void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }

    @Override
    public void commentListClick(int position) {
        comment.setText(commentlist.get(position).getComment());
    }
}
