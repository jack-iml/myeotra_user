package com.myeotra.user.ui.activity.forgot_password;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.myeotra.user.R;
import com.myeotra.user.base.BaseActivity;
import com.myeotra.user.ui.activity.login.PasswordActivity;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgotPasswordActivity extends BaseActivity implements ForgotPasswordIView {

    private static final String TAG = "AAAA";
    @BindView(R.id.otp)
    EditText otp;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.passwordConfirmation)
    EditText passwordConfirmation;
    @BindView(R.id.submit)
    Button submit;

    @BindView(R.id.icBack)
    ImageView icBack;
    String OTP;
    int Id;

    private ForgotPasswordPresenter<ForgotPasswordActivity> presenter = new ForgotPasswordPresenter<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_forgot_password;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        presenter.attachView(this);
        setTitle(getString(R.string.reset_password));
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Id = extras.getInt("id");
            OTP = extras.getString("otp");
            Log.e(TAG, "initView: " + extras.getString("otp"));
        }

        otp.setText(OTP);
    }

    @OnClick({R.id.submit, R.id.icBack})
    public void onViewClicked(View view) {

        switch (view.getId()) {

            case R.id.submit:
                if (otp.getText().toString().isEmpty()) {
                    Toast.makeText(this, getString(R.string.invalid_otp), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!otp.getText().toString().equals(OTP)) {
                    Toast.makeText(this, getString(R.string.wrong_otp), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.getText().toString().isEmpty()) {
                    Toast.makeText(this, getString(R.string.invalid_password), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.getText().toString().length() < 6) {
                    Toast.makeText(this, getString(R.string.invalid_password_length), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.getText().toString().equals(passwordConfirmation.getText().toString())) {
                    Toast.makeText(this, getString(R.string.password_should_be_same), Toast.LENGTH_SHORT).show();
                    return;
                }

                HashMap<String, Object> map = new HashMap<>();
                map.put("password", password.getText().toString());
                map.put("password_confirmation", passwordConfirmation.getText().toString());
                map.put("id", Id);
                showLoading();
                presenter.resetPassword(map);
                break;

            case R.id.icBack:

                onBackPressed();
                break;

        }


    }

    @Override
    public void onSuccess(Object object) {

        Log.e(TAG, "resetPass : res : " + new Gson().toJson(object));
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        Toast.makeText(this, getString(R.string.password_changed_successfully), Toast.LENGTH_SHORT).show();
        finishAffinity();
        startActivity(new Intent(this, PasswordActivity.class));
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }
}


