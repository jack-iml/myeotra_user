package com.myeotra.user.ui.fragment.book_ride;

import com.myeotra.user.base.MvpView;
import com.myeotra.user.data.network.model.PromoResponse;


public interface BookRideIView extends MvpView {
    void onSuccess(Object object);

    void onError(Throwable e);

    void onSuccessCoupon(PromoResponse promoResponse);
}
