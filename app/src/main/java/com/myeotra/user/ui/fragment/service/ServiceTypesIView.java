package com.myeotra.user.ui.fragment.service;

import com.myeotra.user.base.MvpView;
import com.myeotra.user.data.network.model.Service;

import java.util.List;

public interface ServiceTypesIView extends MvpView {

    void onSuccess(List<Service> serviceList);

    void onError(Throwable e);

    void onSuccess(Object object);
}
