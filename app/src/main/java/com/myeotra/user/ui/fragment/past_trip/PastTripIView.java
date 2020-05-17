package com.myeotra.user.ui.fragment.past_trip;

import com.myeotra.user.base.MvpView;
import com.myeotra.user.data.network.model.Datum;

import java.util.List;


public interface PastTripIView extends MvpView {
    void onSuccess(List<Datum> datumList);

    void onError(Throwable e);
}
