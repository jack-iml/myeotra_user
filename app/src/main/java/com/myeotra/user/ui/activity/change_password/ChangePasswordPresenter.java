package com.myeotra.user.ui.activity.change_password;


import android.util.Log;

import com.google.gson.Gson;
import com.myeotra.user.base.BasePresenter;
import com.myeotra.user.data.network.APIClient;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class ChangePasswordPresenter<V extends ChangePasswordIView> extends BasePresenter<V> implements ChangePasswordIPresenter<V> {


    private static final String TAG = "AAAA";

    @Override
    public void changePassword(HashMap<String, Object> parms) {

        Log.e(TAG, "changePassword: req : " + new Gson().toJson(parms));
        getCompositeDisposable().add(APIClient.getAPIClient().changePassword(parms)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object -> getMvpView().onSuccess(object),
                        throwable -> getMvpView().onError(throwable)));
    }
}
