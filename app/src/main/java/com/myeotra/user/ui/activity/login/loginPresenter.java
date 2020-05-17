package com.myeotra.user.ui.activity.login;

import android.util.Log;

import com.google.gson.Gson;
import com.myeotra.user.base.BasePresenter;
import com.myeotra.user.data.network.APIClient;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class loginPresenter<V extends LoginIView> extends BasePresenter<V>
        implements LoginIPresenter<V> {

    String TAG = "AAAA";

    @Override
    public void login(HashMap<String, Object> obj) {
        Log.e(TAG, " login: req: " + new Gson().toJson(obj));
        getCompositeDisposable().add(APIClient.getAPIClient().login(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(token -> getMvpView().onSuccess(token),
                        throwable -> getMvpView().onError(throwable)));
    }

    @Override
    public void forgotPassword(String email) {

        Log.e(TAG, "forgotPassword : req : " + email);

        getCompositeDisposable().add(APIClient.getAPIClient().forgotPassword(email)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }
}
