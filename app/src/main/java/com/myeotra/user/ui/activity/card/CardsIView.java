package com.myeotra.user.ui.activity.card;

import com.myeotra.user.base.MvpView;
import com.myeotra.user.data.network.model.Card;

import java.util.List;


public interface CardsIView extends MvpView {
    void onSuccess(List<Card> cardList);

    void onError(Throwable e);
}
