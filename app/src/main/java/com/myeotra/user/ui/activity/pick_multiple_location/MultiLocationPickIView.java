package com.myeotra.user.ui.activity.pick_multiple_location;

import com.myeotra.user.base.MvpView;
import com.myeotra.user.data.network.model.AddressResponse;


public interface MultiLocationPickIView extends MvpView {

    void onSuccess(AddressResponse address);

    void onError(Throwable e);
}
