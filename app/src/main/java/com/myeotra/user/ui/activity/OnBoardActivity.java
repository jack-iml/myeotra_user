package com.myeotra.user.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.myeotra.user.R;
import com.myeotra.user.base.BaseActivity;
import com.myeotra.user.ui.activity.login.PasswordActivity;
import com.myeotra.user.ui.activity.register.RegisterActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OnBoardActivity extends BaseActivity {

    @BindView(R.id.sign_in)
    Button sign_in;
    @BindView(R.id.sign_up)
    RelativeLayout sign_up;

    @BindView(R.id.txtCreateAccount)
    TextView txtCreateAccount;


    @Override
    public int getLayoutId() {
        return R.layout.activity_on_board;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }


    @OnClick({R.id.sign_in, R.id.txtCreateAccount})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sign_in:
                sign_in.setEnabled(false);
                startActivity(new Intent(this, PasswordActivity.class));
                break;
            case R.id.txtCreateAccount:

                txtCreateAccount.setEnabled(false);
                startActivity(new Intent(this, RegisterActivity.class));
                break;
        }
    }


    @Override
    public void onResume() {
        sign_in.setEnabled(true);
        txtCreateAccount.setEnabled(true);
        super.onResume();
    }
}
