package com.myeotra.user.ui.fragment.rate;

import com.myeotra.user.base.MvpView;
import com.myeotra.user.data.network.model.RateCommentResponse;


public interface RatingIView extends MvpView {
    void onSuccess(Object object);

    void onSuccess(RateCommentResponse object);

    void onError(Throwable e);
}
