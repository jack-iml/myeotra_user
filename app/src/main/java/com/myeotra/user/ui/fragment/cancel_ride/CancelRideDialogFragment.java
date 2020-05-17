package com.myeotra.user.ui.fragment.cancel_ride;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.myeotra.user.R;
import com.myeotra.user.base.BaseBottomSheetDialogFragment;
import com.myeotra.user.common.CancelRequestInterface;
import com.myeotra.user.data.network.model.CancelResponse;
import com.myeotra.user.data.network.model.Datum;
import com.myeotra.user.ui.activity.main.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.myeotra.user.MvpApplication.DATUM;
import static com.myeotra.user.common.Constants.BroadcastReceiver.INTENT_FILTER;

public class CancelRideDialogFragment extends BaseBottomSheetDialogFragment implements CancelRideIView, AdapterView.OnItemSelectedListener {

    @BindView(R.id.cancel_reason)
    EditText cancelReason;
    @BindView(R.id.dismiss)
    Button dismiss;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.rcvReason)
    RecyclerView rcvReason;

    @BindView(R.id.spinnerServiceType)
    AppCompatSpinner spinnerServiceType;

    private List<CancelResponse> cancelResponseList = new ArrayList<>();
    //    private ReasonAdapter adapter;
    private int last_selected_location = -1;

    private CancelRidePresenter<CancelRideDialogFragment> presenter = new CancelRidePresenter<>();
    private CancelRequestInterface callback;

    private int selected_pos = -1;

    public CancelRideDialogFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public CancelRideDialogFragment(CancelRequestInterface callback) {
        this.callback = callback;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_cancel_ride_dialog;
    }

    @Override
    public void initView(View view) {

        /*if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }*/

        ButterKnife.bind(this, view);
        presenter.attachView(this);


        /*adapter = new ReasonAdapter(cancelResponseList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager
                (getActivity(), LinearLayoutManager.VERTICAL, false);
        rcvReason.setLayoutManager(mLayoutManager);
        rcvReason.setItemAnimator(new DefaultItemAnimator());
        rcvReason.setAdapter(adapter);*/

        presenter.getReasons();

        spinnerServiceType.setOnItemSelectedListener(this);


    }

    @OnClick({R.id.dismiss, R.id.submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dismiss:
                dismiss();
                break;
            case R.id.submit:
                if (selected_pos == -1) {
                    Toast.makeText(getContext(), getString(R.string.invalid_selection), Toast.LENGTH_SHORT).show();
                    return;
                }
                cancelRequest();
                break;
        }
    }

    private void cancelRequest() {
        if (cancelReason.getText().toString().isEmpty() && (selected_pos == cancelResponseList.size() - 1)) {
            Toast.makeText(getContext(), getString(R.string.please_enter_cancel_reason), Toast.LENGTH_SHORT).show();
            return;
        }

        if (DATUM != null) {
            Datum datum = DATUM;
            HashMap<String, Object> map = new HashMap<>();
            map.put("request_id", datum.getId());
            if ((selected_pos == cancelResponseList.size() - 1))
                map.put("cancel_reason", cancelReason.getText().toString());
            else
                map.put("cancel_reason", cancelResponseList.get(selected_pos).getReason());
            showLoading();
            presenter.cancelRequest(map);
        }
    }

    @Override
    public void onSuccess(Object object) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (DATUM != null)
            FirebaseMessaging.getInstance().unsubscribeFromTopic(String.valueOf(DATUM.getId()));
        getActivity().sendBroadcast(new Intent(INTENT_FILTER));
        callback.cancelRequestMethod();
        getActivity().finishAffinity();
        startActivity(new Intent(getActivity(), MainActivity.class));
        dismiss();
    }

    @Override
    public void onSuccess(List<CancelResponse> response) {
        cancelResponseList.addAll(response);
        addCommonReason();

        setupSpinner(cancelResponseList);
    }

    private void setupSpinner(List<CancelResponse> response) {


        ArrayList<String> lstNames = new ArrayList<>(response != null ? response.size() : 0);
        if (response != null) for (CancelResponse serviceType : response)
            lstNames.add(serviceType.getReason());

//        Log.e(TAG, "setupSpinner: " + new Gson().toJson(lstNames));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.item_spinner, lstNames);
        dataAdapter.setDropDownViewResource(R.layout.item_spinner);
        spinnerServiceType.setAdapter(dataAdapter);
    }


    @Override
    public void onReasonError(Throwable e) {
        addCommonReason();
    }

    private void addCommonReason() {
        //Common reason added here
        CancelResponse commonReason = new CancelResponse();
        commonReason.setReason("Other Reason");
        commonReason.setType("USER");
        commonReason.setStatus(1);
        commonReason.setId(0);
        cancelResponseList.add(commonReason);
        if (cancelResponseList.size() == 1) {
            last_selected_location = 0;
            cancelReason.setVisibility(View.VISIBLE);
        }
//        adapter.notifyDataSetChanged();
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    public void onDestroyView() {
        presenter.onDetach();
        super.onDestroyView();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selected_pos = position;

        if (cancelResponseList.get(selected_pos).getReason().equalsIgnoreCase("Other Reason")) {

            cancelReason.setVisibility(View.VISIBLE);
        } else {
            cancelReason.setVisibility(View.GONE);
        }

//        Toast.makeText(getActivity(), "click" + cancelResponseList.get(selected_pos).getReason(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    /*private class ReasonAdapter extends RecyclerView.Adapter<CancelRideDialogFragment.ReasonAdapter.MyViewHolder> {

        private List<CancelResponse> list;
        private Context mContext;

        private ReasonAdapter(List<CancelResponse> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public CancelRideDialogFragment.ReasonAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            return new CancelRideDialogFragment.ReasonAdapter.MyViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cancel_reasons_inflate, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull CancelRideDialogFragment.ReasonAdapter.MyViewHolder holder, int position) {

            CancelResponse data = list.get(position);
            holder.tvReason.setText(data.getReason());

            if (last_selected_location == position) {
                holder.cbItem.setChecked(true);
            } else {
                holder.cbItem.setChecked(false);
            }

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            LinearLayout llItemView;
            TextView tvReason;
            CheckBox cbItem;

            MyViewHolder(View view) {
                super(view);
                llItemView = view.findViewById(R.id.llItemView);
                tvReason = view.findViewById(R.id.tvReason);
                cbItem = view.findViewById(R.id.cbItem);

                llItemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                if (position == list.size() - 1) {
                    cancelReason.setVisibility(View.VISIBLE);
                } else {
                    cancelReason.setVisibility(View.GONE);
                }
                last_selected_location = position;
                notifyDataSetChanged();
            }
        }
    }*/
}
