package com.myeotra.user.ui.activity.forgot_password;

import com.myeotra.user.base.MvpView;


public interface ForgotPasswordIView extends MvpView {
    void onSuccess(Object object);

    void onError(Throwable e);
}
