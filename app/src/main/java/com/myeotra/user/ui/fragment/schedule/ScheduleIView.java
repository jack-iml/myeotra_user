package com.myeotra.user.ui.fragment.schedule;

import com.myeotra.user.base.MvpView;


public interface ScheduleIView extends MvpView {
    void onSuccess(Object object);

    void onError(Throwable e);
}
