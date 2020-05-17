package com.myeotra.user.ui.activity.card;

import com.myeotra.user.base.MvpPresenter;


public interface CarsIPresenter<V extends CardsIView> extends MvpPresenter<V> {
    void card();
}
