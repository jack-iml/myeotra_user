package com.myeotra.user.ui.fragment.searching;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.myeotra.user.MvpApplication;
import com.myeotra.user.R;
import com.myeotra.user.base.BaseBottomSheetDialogFragment;
import com.myeotra.user.data.network.model.Datum;
import com.myeotra.user.ui.activity.location_pick.LocationPickedActivity;

import java.util.HashMap;
import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.myeotra.user.MvpApplication.DATUM;
import static com.myeotra.user.common.Constants.BroadcastReceiver.INTENT_FILTER;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.DEST_ADD;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.DEST_LAT;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.DEST_LONG;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.SRC_ADD;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.SRC_LAT;
import static com.myeotra.user.common.Constants.RIDE_REQUEST.SRC_LONG;
import static com.myeotra.user.common.Constants.Status.EMPTY;

public class SearchingFragment extends BaseBottomSheetDialogFragment implements SearchingIView {

    private static final String TAG = "AAAA";
    private SearchingPresenter<SearchingFragment> presenter = new SearchingPresenter<>();
    private Dialog dialog;

    public SearchingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_searching;
    }

    @Override
    public void initView(View view) {
        setCancelable(false);
        getDialog().setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            View bottomSheetInternal = d.findViewById(R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        ButterKnife.bind(this, view);
        presenter.attachView(this);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnClick(R.id.cancel)
    public void onViewClicked() {
        alertCancel();
    }

    @Override
    public void onSuccess(Object object) {
        dialog.dismiss();

        Log.e(TAG, " res : cancelRequest :" + new Gson().toJson(object));
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        MvpApplication.RIDE_REQUEST.remove(DEST_ADD);
        MvpApplication.RIDE_REQUEST.remove(DEST_LAT);
        MvpApplication.RIDE_REQUEST.remove(DEST_LONG);


        MvpApplication.RIDE_REQUEST.remove(SRC_ADD);
        MvpApplication.RIDE_REQUEST.remove(SRC_LAT);
        MvpApplication.RIDE_REQUEST.remove(SRC_LONG);


        baseActivity().sendBroadcast(new Intent(INTENT_FILTER));
        ((LocationPickedActivity) Objects.requireNonNull(getContext())).changeFlow(EMPTY);

        dismissAllowingStateLoss();

        getActivity().finish();
    }

    private void alertCancel() {
        /*new AlertDialog.Builder(getContext())
                .setMessage(R.string.are_sure_you_want_to_cancel_the_request)
                .setCancelable(true)
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    if (DATUM != null) {
                        showLoading();
                        Datum datum = DATUM;
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("request_id", datum.getId());
                        presenter.cancelRequest(map);
                    }
                }).setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.cancel())
                .show();*/


        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.setContentView(R.layout.dilg_cancel_confirm);

        TextView txtno, txtyes;

        txtno = dialog.findViewById(R.id.txtno);
        txtyes = dialog.findViewById(R.id.txtyes);


        txtyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DATUM != null) {
                    showLoading();
                    Datum datum = DATUM;
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("request_id", datum.getId());
                    presenter.cancelRequest(map);
                }
            }
        });

        txtno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (DATUM != null) {
                dialog.dismiss();
//                }
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        lp.windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setAttributes(lp);

        dialog.show();
    }

    @Override
    public void onError(Throwable e) {
        dialog.dismiss();
        handleError(e);
        baseActivity().sendBroadcast(new Intent(INTENT_FILTER));
        ((LocationPickedActivity) Objects.requireNonNull(getContext())).changeFlow(EMPTY);
    }
}
