package com.myeotra.user.ui.activity.login;

import com.myeotra.user.base.MvpPresenter;

import java.util.HashMap;


public interface LoginIPresenter<V extends LoginIView> extends MvpPresenter<V> {
    void login(HashMap<String, Object> obj);

    void forgotPassword(String email);
}
