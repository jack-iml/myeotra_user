package com.myeotra.user.ui.fragment.searching;

import com.myeotra.user.base.MvpPresenter;

import java.util.HashMap;

import retrofit2.http.FieldMap;


public interface SearchingIPresenter<V extends SearchingIView> extends MvpPresenter<V> {
    void cancelRequest(@FieldMap HashMap<String, Object> params);
}
