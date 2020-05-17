package com.myeotra.user.ui.activity.add_card;

import com.myeotra.user.base.MvpPresenter;


interface AddCardIPresenter<V extends AddCardIView> extends MvpPresenter<V> {
    void card(String stripeToken);
}
