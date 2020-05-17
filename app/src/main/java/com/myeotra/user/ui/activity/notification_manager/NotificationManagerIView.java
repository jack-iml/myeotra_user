package com.myeotra.user.ui.activity.notification_manager;

import com.myeotra.user.base.MvpView;
import com.myeotra.user.data.network.model.NotificationManager;

import java.util.List;

public interface NotificationManagerIView extends MvpView {

    void onSuccess(List<NotificationManager> notificationManager);

    void onError(Throwable e);

}