package com.myeotra.user.ui.activity.pick_multiple_location;

import com.myeotra.user.base.MvpPresenter;

public interface MultiLocationPickIPresenter<V extends MultiLocationPickIView> extends MvpPresenter<V> {
    void address();
}
