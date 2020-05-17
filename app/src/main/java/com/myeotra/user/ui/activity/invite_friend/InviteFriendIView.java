package com.myeotra.user.ui.activity.invite_friend;

import com.myeotra.user.base.MvpView;
import com.myeotra.user.data.network.model.User;

public interface InviteFriendIView extends MvpView {

    void onSuccess(User user);

    void onError(Throwable e);

}
