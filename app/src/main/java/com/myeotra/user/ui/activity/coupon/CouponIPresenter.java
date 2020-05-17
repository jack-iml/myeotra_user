package com.myeotra.user.ui.activity.coupon;

import com.myeotra.user.base.MvpPresenter;

public interface CouponIPresenter<V extends CouponIView> extends MvpPresenter<V> {
    void coupon();
}
