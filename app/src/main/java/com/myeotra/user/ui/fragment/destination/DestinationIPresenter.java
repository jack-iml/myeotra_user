package com.myeotra.user.ui.fragment.destination;


import com.myeotra.user.base.MvpPresenter;

interface DestinationIPresenter<V extends DestinationIView> extends MvpPresenter<V> {

    public void getUserInfo();
}