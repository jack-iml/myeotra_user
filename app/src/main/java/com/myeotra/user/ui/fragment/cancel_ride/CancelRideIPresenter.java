package com.myeotra.user.ui.fragment.cancel_ride;

import com.myeotra.user.base.MvpPresenter;

import java.util.HashMap;


public interface CancelRideIPresenter<V extends CancelRideIView> extends MvpPresenter<V> {
    void cancelRequest(HashMap<String, Object> params);

    void getReasons();
}
