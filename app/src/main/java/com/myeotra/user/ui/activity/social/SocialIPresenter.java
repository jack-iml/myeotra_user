package com.myeotra.user.ui.activity.social;

import com.myeotra.user.base.MvpPresenter;

import java.util.HashMap;


public interface SocialIPresenter<V extends SocialIView> extends MvpPresenter<V> {
    void loginGoogle(HashMap<String, Object> obj);

    void loginFacebook(HashMap<String, Object> obj);
}
