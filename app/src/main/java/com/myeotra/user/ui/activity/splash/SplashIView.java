package com.myeotra.user.ui.activity.splash;

import com.myeotra.user.base.MvpView;
import com.myeotra.user.data.network.model.CheckVersion;
import com.myeotra.user.data.network.model.Service;

import java.util.List;

public interface SplashIView extends MvpView {

    void onSuccess(List<Service> serviceList);

//    void onSuccess(User user);

    void onError(Throwable e);

    void onSuccess(CheckVersion checkVersion);
}
