package com.myeotra.user.ui.activity.register;

import android.util.Log;

import com.google.gson.Gson;
import com.myeotra.user.base.BasePresenter;
import com.myeotra.user.data.network.APIClient;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class RegisterPresenter<V extends RegisterIView>
        extends BasePresenter<V>
        implements RegisterIPresenter<V> {

    String TAG = "AAAA";

    @Override
    public void register(HashMap<String, RequestBody> obj, MultipartBody.Part filePart) {
        Log.e(TAG, " register : req " + new Gson().toJson(obj));
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .register(obj, filePart)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }

    /*@Override
    public void register(HashMap<String, Object> obj) {
        Log.e(TAG, " : req : register : " + new Gson().toJson(obj));
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .register(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }*/

    @Override
    public void verifyEmail(String email) {

        Log.e(TAG, "verifyEmail : req" + email);

        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .verifyEmail(email)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onVerifyEmailError));
    }

    @Override
    public void verifyCredentials(String phoneNumber, String countryCode) {

        Log.e(TAG, " : req : verifyCredentials : phoneNumber" + phoneNumber + " countryCode " + countryCode);

        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .verifyCredentials(phoneNumber, countryCode)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccessPhoneNumber, getMvpView()::onVerifyPhoneNumberError));
    }

    @Override
    public void getSettings() {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .getSettings()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }
}
