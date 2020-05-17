package com.myeotra.user.ui.activity.wallet;

import android.util.Log;

import com.google.gson.Gson;
import com.myeotra.user.base.BasePresenter;
import com.myeotra.user.data.network.APIClient;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class WalletPresenter<V extends WalletIView> extends BasePresenter<V> implements WalletIPresenter<V> {

    private String TAG = "AAAA";

    @Override
    public void addMoney(HashMap<String, Object> obj) {

        Log.e(TAG, "addMoney req: " + new Gson().toJson(obj));
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .addMoney(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }

    @Override
    public void addMoneyPaytm(HashMap<String, Object> obj) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .addMoneyPaytm(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }


    @Override
    public void getBrainTreeToken() {
        getCompositeDisposable().add(APIClient.getAPIClient().getBraintreeToken()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> getMvpView().onSuccess(response),
                        throwable -> getMvpView().onError(throwable)));
    }


}
