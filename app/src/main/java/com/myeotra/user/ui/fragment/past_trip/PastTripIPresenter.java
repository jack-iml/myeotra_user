package com.myeotra.user.ui.fragment.past_trip;

import com.myeotra.user.base.MvpPresenter;


public interface PastTripIPresenter<V extends PastTripIView> extends MvpPresenter<V> {
    void pastTrip();
}
