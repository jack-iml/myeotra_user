package com.myeotra.user.ui.activity.coupon;

import com.myeotra.user.base.MvpView;
import com.myeotra.user.data.network.model.PromoResponse;

public interface CouponIView extends MvpView {
    void onSuccess(PromoResponse object);

    void onError(Throwable e);
}
