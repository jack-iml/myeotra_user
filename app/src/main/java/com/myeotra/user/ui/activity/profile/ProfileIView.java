package com.myeotra.user.ui.activity.profile;

import com.myeotra.user.base.MvpView;
import com.myeotra.user.data.network.model.User;

public interface ProfileIView extends MvpView {

    void onSuccess(User user);

    void onUpdateSuccess(User user);

    void onError(Throwable e);

    void onSuccessPhoneNumber(Object object);

    void onVerifyPhoneNumberError(Throwable e);
}
