package com.myeotra.user.ui.activity.social;

import android.util.Log;

import com.google.gson.Gson;
import com.myeotra.user.base.BasePresenter;
import com.myeotra.user.data.network.APIClient;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class SocialPresenter<V extends SocialIView> extends BasePresenter<V> implements SocialIPresenter<V> {
    private String TAG = "AAAA";

    @Override
    public void loginGoogle(HashMap<String, Object> obj) {

        Log.e(TAG, " loginGoogle: req : " + new Gson().toJson(obj));

        getCompositeDisposable().add(APIClient.getAPIClient().loginGoogle(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(token -> getMvpView().onSuccess(token),
                        throwable -> getMvpView().onError(throwable)));
    }

    @Override
    public void loginFacebook(HashMap<String, Object> obj) {

        Log.e(TAG, "loginFacebook: req : " + new Gson().toJson(obj));

        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .loginFacebook(obj)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        trendsResponse -> getMvpView().onSuccess(trendsResponse),
                        throwable -> getMvpView().onError(throwable)
                ));
    }

}
