package com.myeotra.user.ui.fragment.schedule;

import com.myeotra.user.base.MvpPresenter;

import java.util.HashMap;


public interface ScheduleIPresenter<V extends ScheduleIView> extends MvpPresenter<V> {

    void sendRequest(HashMap<String, Object> obj);
}
