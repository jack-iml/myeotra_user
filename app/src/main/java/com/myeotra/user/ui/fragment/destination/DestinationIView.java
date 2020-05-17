package com.myeotra.user.ui.fragment.destination;

import com.myeotra.user.base.MvpView;
import com.myeotra.user.data.network.model.User;

public interface DestinationIView extends MvpView {

    void onSuccess(User user);

    void onError(Throwable e);
}
