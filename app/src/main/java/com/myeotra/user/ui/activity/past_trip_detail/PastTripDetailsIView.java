package com.myeotra.user.ui.activity.past_trip_detail;

import com.myeotra.user.base.MvpView;
import com.myeotra.user.data.network.model.Datum;

import java.util.List;

public interface PastTripDetailsIView extends MvpView {

    void onSuccess(List<Datum> pastTripDetails);

    void onError(Throwable e);
}
