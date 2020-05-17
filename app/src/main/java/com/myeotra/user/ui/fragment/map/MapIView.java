package com.myeotra.user.ui.fragment.map;

import com.myeotra.user.base.MvpView;

public interface MapIView extends MvpView {
    void onSuccess(Object object);

    void onError(Throwable e);
}
