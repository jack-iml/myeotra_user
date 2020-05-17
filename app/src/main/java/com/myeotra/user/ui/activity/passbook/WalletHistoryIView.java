package com.myeotra.user.ui.activity.passbook;

import com.myeotra.user.base.MvpView;
import com.myeotra.user.data.network.model.WalletResponse;

public interface WalletHistoryIView extends MvpView {
    void onSuccess(WalletResponse response);

    void onError(Throwable e);
}
