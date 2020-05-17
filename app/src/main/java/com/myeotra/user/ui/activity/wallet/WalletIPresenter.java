package com.myeotra.user.ui.activity.wallet;

import com.myeotra.user.base.MvpPresenter;

import java.util.HashMap;

public interface WalletIPresenter<V extends WalletIView> extends MvpPresenter<V> {
    void addMoney(HashMap<String, Object> obj);

    void addMoneyPaytm(HashMap<String, Object> obj);

    void getBrainTreeToken();
}
