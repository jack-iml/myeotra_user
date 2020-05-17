package com.myeotra.user.ui.activity.passbook;

import com.myeotra.user.base.MvpPresenter;

public interface WalletHistoryIPresenter<V extends WalletHistoryIView> extends MvpPresenter<V> {
    void wallet();
}
