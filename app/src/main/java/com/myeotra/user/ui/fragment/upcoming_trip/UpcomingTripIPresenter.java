package com.myeotra.user.ui.fragment.upcoming_trip;

import com.myeotra.user.base.MvpPresenter;

import java.util.HashMap;


public interface UpcomingTripIPresenter<V extends UpcomingTripIView> extends MvpPresenter<V> {
    void upcomingTrip();

    void cancelRequest(HashMap<String, Object> params);
}
