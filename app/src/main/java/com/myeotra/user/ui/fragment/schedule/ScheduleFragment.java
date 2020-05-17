package com.myeotra.user.ui.fragment.schedule;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.myeotra.user.R;
import com.myeotra.user.base.BaseFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

import static com.myeotra.user.MvpApplication.RIDE_REQUEST;
import static com.myeotra.user.common.Constant.updateData;
import static com.myeotra.user.common.Constants.BroadcastReceiver.INTENT_FILTER;

public class ScheduleFragment extends BaseFragment implements ScheduleIView {

    DatePickerDialog.OnDateSetListener dateSetListener;
    TimePickerDialog.OnTimeSetListener timeSetListener;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.time)
    TextView time;
    String TAG = "ScheduleFragment";
    private String selectedScheduledTime;
    private String selectedScheduledHour;
    private String AM_PM;
    private String selectedTime;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SchedulePresenter<ScheduleFragment> presenter = new SchedulePresenter<>();

    public ScheduleFragment() {

        dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            Calendar myCalendar = Calendar.getInstance();
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            date.setText(simpleDateFormat.format(myCalendar.getTime()));
        };

        timeSetListener = (timePicker, selectedHour, selectedMinute) -> {
            selectedScheduledTime = selectedMinute < 10 ? "0" + selectedMinute : String.valueOf(selectedMinute);
            selectedScheduledHour = selectedHour < 10 ? "0" + selectedHour : String.valueOf(selectedHour);
            selectedTime = selectedScheduledHour + ":" + selectedScheduledTime;
            AM_PM = selectedHour < 12 ? "AM" : "PM";
            time.setText(String.format("%s %s", selectedTime, AM_PM));
        };
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_schedule;
    }

    @Override
    public View initView(View view) {
        ButterKnife.bind(this, view);
        presenter.attachView(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnClick({R.id.date, R.id.time, R.id.schedule_request})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.date:
                datePicker(dateSetListener);
                break;
            case R.id.time:
                timePicker(timeSetListener);
                break;
            case R.id.schedule_request:

                String dates = date.getText().toString().trim();
                String times = time.getText().toString().trim();

                if (dates.equalsIgnoreCase(getActivity().getResources().getString(R.string.choose))) {
                    Toast.makeText(baseActivity(), R.string.please_select_date_time, Toast.LENGTH_SHORT).show();
                } else if (times.equalsIgnoreCase(getActivity().getResources().getString(R.string.choose))) {
                    Toast.makeText(baseActivity(), R.string.please_select_date_time, Toast.LENGTH_SHORT).show();
                } else {
                    sendRequest();
                }

                break;
        }
    }

    private void sendRequest() {

        HashMap<String, Object> map = new HashMap<>(RIDE_REQUEST);
        map.put("schedule_date", date.getText().toString());
        map.put("schedule_time", selectedTime);
        showLoading();
        Log.e(TAG, "sendRequest--: " + new Gson().toJson(map));
        presenter.sendRequest(map);
    }

    @Override
    public void onSuccess(Object object) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        Toasty.success(Objects.requireNonNull(getActivity()), getString(R.string.success_schedule_ride_created), Toast.LENGTH_SHORT).show();
        updateData = true;
        baseActivity().sendBroadcast(new Intent(INTENT_FILTER));

//        ((LocationPickedActivity) Objects.requireNonNull(getContext())).changeFlow(EMPTY);

    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    public void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }
}
