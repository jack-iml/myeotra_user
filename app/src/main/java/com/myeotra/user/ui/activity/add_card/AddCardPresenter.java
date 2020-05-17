package com.myeotra.user.ui.activity.add_card;

import com.myeotra.user.base.BasePresenter;
import com.myeotra.user.data.network.APIClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class AddCardPresenter<V extends AddCardIView> extends BasePresenter<V> implements AddCardIPresenter<V> {
    @Override
    public void card(String cardId) {

        getCompositeDisposable().add(APIClient.getAPIClient().card(cardId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object -> getMvpView().onSuccess(object),
                        throwable -> getMvpView().onError(throwable)));
    }
}
