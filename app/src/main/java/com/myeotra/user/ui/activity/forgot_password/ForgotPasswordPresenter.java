package com.myeotra.user.ui.activity.forgot_password;


import android.util.Log;

import com.google.gson.Gson;
import com.myeotra.user.base.BasePresenter;
import com.myeotra.user.data.network.APIClient;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ForgotPasswordPresenter<V extends ForgotPasswordIView> extends BasePresenter<V> implements ForgotPasswordIPresenter<V> {


    private static final String TAG ="AAAA" ;

    @Override
    public void resetPassword(HashMap<String, Object> parms) {

        Log.e(TAG, "resetPass : req : "+new Gson().toJson(parms));

        getCompositeDisposable().add(APIClient.getAPIClient().
                resetPassword(parms)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object -> getMvpView().onSuccess(object),
                        throwable -> getMvpView().onError(throwable)));
    }
}
