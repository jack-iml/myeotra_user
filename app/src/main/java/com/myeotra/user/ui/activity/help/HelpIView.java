package com.myeotra.user.ui.activity.help;

import com.myeotra.user.base.MvpView;
import com.myeotra.user.data.network.model.Help;

public interface HelpIView extends MvpView {

    void onSuccess(Help help);

    void onError(Throwable e);
}
