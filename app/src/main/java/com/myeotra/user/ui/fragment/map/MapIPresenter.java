package com.myeotra.user.ui.fragment.map;

import com.myeotra.user.base.MvpPresenter;

import java.util.HashMap;


public interface MapIPresenter<V extends MapIView> extends MvpPresenter<V> {

    void sendRequest(HashMap<String, Object> obj);
}
