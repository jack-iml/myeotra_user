package com.myeotra.user.ui.activity.splash;

import com.myeotra.user.base.BasePresenter;
import com.myeotra.user.data.network.APIClient;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SplashPresenter<V extends SplashIView> extends BasePresenter<V> implements SplashIPresenter<V> {

    String TAG = "AAAA";

    @Override
    public void services() {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .services()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }

    /*@Override
    public void profile() {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .profile()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }*/

    @Override
    public void checkVersion(HashMap<String, Object> map) {

//        Log.e(TAG, getClass().getSimpleName()+" req : " + new Gson().toJson(map));
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .checkversion(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }
}
