package com.myeotra.user.ui.fragment.lost_item;

import com.myeotra.user.base.MvpPresenter;

import java.util.HashMap;


public interface LostIPresenter<V extends LostIView> extends MvpPresenter<V> {
    void postLostItem(HashMap<String, Object> obj);
}
