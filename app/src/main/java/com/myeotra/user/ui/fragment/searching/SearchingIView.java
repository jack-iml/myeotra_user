package com.myeotra.user.ui.fragment.searching;

import com.myeotra.user.base.MvpView;

public interface SearchingIView extends MvpView {
    void onSuccess(Object object);

    void onError(Throwable e);
}
