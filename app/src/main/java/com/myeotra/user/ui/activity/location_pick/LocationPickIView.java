package com.myeotra.user.ui.activity.location_pick;

import com.myeotra.user.base.MvpView;
import com.myeotra.user.data.network.model.AddressResponse;
import com.myeotra.user.data.network.model.DataResponse;
import com.myeotra.user.data.network.model.Provider;
import com.myeotra.user.data.network.model.User;

import java.util.List;

public interface LocationPickIView extends MvpView {

    void onSuccess(User user);

    void onSuccess(DataResponse dataResponse);

    void onDestinationSuccess(Object object);

    void onSuccessLogout(Object object);

    void onSuccess(AddressResponse response);

    void onSuccess(List<Provider> objects);

    void onError(Throwable e);

//    void onSuccess(SettingsResponse response);

//    void onSettingError(Throwable e);

}
