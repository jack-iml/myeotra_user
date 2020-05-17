package com.myeotra.user.ui.activity.add_card;

import com.myeotra.user.base.MvpView;


public interface AddCardIView extends MvpView {
    void onSuccess(Object card);

    void onError(Throwable e);
}
