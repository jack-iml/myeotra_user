package com.myeotra.user.ui.fragment.lost_item;

import com.myeotra.user.base.MvpView;


public interface LostIView extends MvpView {
    void onSuccess(Object object);

    void onError(Throwable e);
}
