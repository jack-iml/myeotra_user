package com.myeotra.user.ui.activity.login;

import com.myeotra.user.base.MvpView;
import com.myeotra.user.data.network.model.ForgotResponse;
import com.myeotra.user.data.network.model.Token;

public interface LoginIView extends MvpView {
    void onSuccess(Token token);

    void onSuccess(ForgotResponse object);

    void onError(Throwable e);
}
