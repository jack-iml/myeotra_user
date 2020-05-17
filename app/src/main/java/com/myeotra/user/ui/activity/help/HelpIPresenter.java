package com.myeotra.user.ui.activity.help;


import com.myeotra.user.base.MvpPresenter;


public interface HelpIPresenter<V extends HelpIView> extends MvpPresenter<V> {
    void help();
}
