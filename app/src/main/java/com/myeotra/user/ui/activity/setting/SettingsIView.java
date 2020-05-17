package com.myeotra.user.ui.activity.setting;

import com.myeotra.user.base.MvpView;
import com.myeotra.user.data.network.model.AddressResponse;

public interface SettingsIView extends MvpView {

    void onSuccessAddress(Object object);

    void onLanguageChanged(Object object);

    void onSuccess(AddressResponse address);

    void onError(Throwable e);
}
