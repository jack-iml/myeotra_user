package com.myeotra.user.ui.activity.profile;

import android.util.Log;

import com.google.gson.Gson;
import com.myeotra.user.base.BasePresenter;
import com.myeotra.user.data.network.APIClient;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Part;

public class ProfilePresenter<V extends ProfileIView> extends BasePresenter<V> implements ProfileIPresenter<V> {

    private static final String TAG = "AAAA";

    @Override
    public void update(HashMap<String, RequestBody> obj, @Part MultipartBody.Part filename) {

        Log.e(TAG, " update: req : " + new Gson().toJson(obj));
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .editProfile(obj, filename)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onUpdateSuccess, getMvpView()::onError));
    }

    @Override
    public void profile() {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .profile()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }

    @Override
    public void verifyCredentials(String number, String countryCode) {

        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .verifyCredentials(number, countryCode)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccessPhoneNumber, getMvpView()::onVerifyPhoneNumberError));
    }
}
