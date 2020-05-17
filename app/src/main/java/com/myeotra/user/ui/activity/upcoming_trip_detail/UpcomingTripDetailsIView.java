package com.myeotra.user.ui.activity.upcoming_trip_detail;

import com.myeotra.user.base.MvpView;
import com.myeotra.user.data.network.model.Datum;

import java.util.List;

public interface UpcomingTripDetailsIView extends MvpView {

    void onSuccess(List<Datum> upcomingTripDetails);

    void onError(Throwable e);
}
