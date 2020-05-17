package com.myeotra.user.ui.activity.change_password;

import com.myeotra.user.base.MvpView;

public interface ChangePasswordIView extends MvpView {
    void onSuccess(Object object);

    void onError(Throwable e);
}
