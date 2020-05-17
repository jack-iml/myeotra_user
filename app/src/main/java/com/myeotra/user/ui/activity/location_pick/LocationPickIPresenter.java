package com.myeotra.user.ui.activity.location_pick;

import com.myeotra.user.base.MvpPresenter;

import java.util.HashMap;

public interface LocationPickIPresenter<V extends LocationPickIView> extends MvpPresenter<V> {
    void getUserInfo();

    void logout(String id);

    void checkStatus();

    void getSavedAddress();

    void getProviders(HashMap<String, Object> params);

//    void getNavigationSettings();

    void updateDestination(HashMap<String, Object> obj);
}
