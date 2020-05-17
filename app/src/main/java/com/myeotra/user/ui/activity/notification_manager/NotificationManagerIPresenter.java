package com.myeotra.user.ui.activity.notification_manager;

import com.myeotra.user.base.MvpPresenter;

public interface NotificationManagerIPresenter<V extends NotificationManagerIView> extends MvpPresenter<V> {
    void getNotificationManager();
}
