package com.myeotra.user.ui.fragment.upcoming_trip;

import com.myeotra.user.base.MvpView;
import com.myeotra.user.data.network.model.Datum;

import java.util.List;


public interface UpcomingTripIView extends MvpView {
    void onSuccess(List<Datum> datumList);

    void onSuccess(Object object);

    void onError(Throwable e);
}
