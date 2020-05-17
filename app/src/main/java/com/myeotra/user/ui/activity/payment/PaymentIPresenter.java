package com.myeotra.user.ui.activity.payment;

import com.myeotra.user.base.MvpPresenter;

public interface PaymentIPresenter<V extends PaymentIView> extends MvpPresenter<V> {
    void deleteCard(String cardId);

    void card();

    void addCard(String cardId);

    //    void payuMoneyChecksum(String request_id,String user_address_id,String paymentmode);
    void payuMoneyChecksum();

    void paytmCheckSum(String request_id, String paymentmode);

    void getBrainTreeToken();
}
