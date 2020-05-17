package com.myeotra.user.ui.fragment.invoice;

import com.appoets.paytmpayment.PaytmObject;
import com.myeotra.user.base.MvpView;
import com.myeotra.user.data.network.model.BrainTreeResponse;
import com.myeotra.user.data.network.model.CheckSumData;
import com.myeotra.user.data.network.model.Message;

public interface InvoiceIView extends MvpView {
    void onSuccess(Message message);

    void onSuccess(Object o);

    void onSuccessPayment(Object o);

    void onError(Throwable e);

    void onSuccess(BrainTreeResponse response);

    void onPayumoneyCheckSumSucess(CheckSumData checkSumData);

    void onPayTmCheckSumSucess(PaytmObject payTmResponse);
}
