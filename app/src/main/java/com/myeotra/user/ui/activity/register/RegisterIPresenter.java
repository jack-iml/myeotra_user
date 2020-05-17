package com.myeotra.user.ui.activity.register;

import com.myeotra.user.base.MvpPresenter;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public interface RegisterIPresenter<V extends RegisterIView> extends MvpPresenter<V> {

//    void register(HashMap<String, Object> obj, MultipartBody.Part filePart);
    void register(HashMap<String, RequestBody> obj, MultipartBody.Part filePart);

    void getSettings();

    void verifyEmail(String email);

    void verifyCredentials(String phoneNumber, String countryCode);

}
