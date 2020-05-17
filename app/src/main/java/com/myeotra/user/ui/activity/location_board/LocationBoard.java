package com.myeotra.user.ui.activity.location_board;

import com.myeotra.user.R;
import com.myeotra.user.base.BaseActivity;

public class LocationBoard extends BaseActivity implements LocationBoardIView {


    LocationBoardPresenter<LocationBoard> presenter = new LocationBoardPresenter<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_location_board;
    }

    @Override
    protected void initView() {
        presenter.attachView(this);

    }


}
