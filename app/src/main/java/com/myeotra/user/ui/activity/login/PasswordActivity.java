package com.myeotra.user.ui.activity.login;

import android.content.Intent;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.myeotra.user.BuildConfig;
import com.myeotra.user.R;
import com.myeotra.user.base.BaseActivity;
import com.myeotra.user.data.SharedHelper;
import com.myeotra.user.data.network.model.ForgotResponse;
import com.myeotra.user.data.network.model.Token;
import com.myeotra.user.ui.activity.forgot_password.ForgotPasswordActivity;
import com.myeotra.user.ui.activity.main.MainActivity;
import com.myeotra.user.ui.activity.register.RegisterActivity;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

public class PasswordActivity extends BaseActivity implements LoginIView {

    public static String TAG = "AAAA";
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.icBack)
    ImageView icBack;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.sign_up)
    TextView sign_up;

    private String stringemail;
    private loginPresenter<PasswordActivity> presenter = new loginPresenter();

    @Override
    public int getLayoutId() {
        return R.layout.activity_password;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        presenter.attachView(this);
    }

    private void login() {

        try {
            if (email.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.invalid_password), Toast.LENGTH_SHORT).show();
                return;
            }
            if (SharedHelper.getKey(this, "device_token").isEmpty()) {
                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        SharedHelper.putKey(this, "device_token", task.getResult().getToken());
                        Log.e(TAG, "FCM TOKEN : " + task.getResult().getToken());
                    } else Log.e(TAG, "getInstanceId failed", task.getException());
                });
                return;
            }
            HashMap<String, Object> map = new HashMap<>();
            map.put("grant_type", "password");
            map.put("username", email.getText().toString());
            map.put("password", password.getText().toString());
            map.put("client_secret", BuildConfig.CLIENT_SECRET);
            map.put("client_id", BuildConfig.CLIENT_ID);
            map.put("device_token", SharedHelper.getKey(this, "device_token", "No device"));
            map.put("device_id", SharedHelper.getKey(this, "device_id", "123"));
            map.put("device_type", BuildConfig.DEVICE_TYPE);

            Log.e(TAG, " device token : " + SharedHelper.getKey(this, "device_token", "No device"));

            showLoading();
            presenter.login(map);
        } catch (Exception e) {
            e.printStackTrace();

            Log.e(TAG, " login error: " + e.getMessage());
        }
    }

    @Override
    public void onResume() {
        sign_up.setEnabled(true);
        super.onResume();
    }

    @OnClick({R.id.sign_up, R.id.forgot_password, R.id.next, R.id.icBack})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sign_up:

                sign_up.setEnabled(false);
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.forgot_password:
                if (email.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Please Enter Email Address", Toast.LENGTH_SHORT).show();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                    Toast.makeText(this, getString(R.string.invalid_email_address), Toast.LENGTH_SHORT).show();
                } else {
                    showLoading();
                    presenter.forgotPassword(email.getText().toString());
                }
                break;
            case R.id.next:
                login();
                break;
            case R.id.icBack:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onSuccess(Token token) {
        Log.e(TAG, "response  : login : " + new Gson().toJson(token));

        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        if (token.getStatus() == 0) {
            Toasty.error(this, token.getMessage(), Toast.LENGTH_SHORT).show();
        } else {
            String accessToken = token.getTokenType() + " " + token.getAccessToken();
            SharedHelper.putKey(this, "access_token", accessToken);
            SharedHelper.putKey(this, "refresh_token", token.getRefreshToken());
            SharedHelper.putKey(this, "logged_in", true);
            finishAffinity();
            startActivity(new Intent(this, MainActivity.class));
        }
    }


    @Override
    public void onSuccess(ForgotResponse forgotResponse) {

        Log.e(TAG, "forgotPassword : res : " + new Gson().toJson(forgotResponse));

        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        if (forgotResponse.getStatus() == 0) {
            Toasty.error(this, forgotResponse.getMessage(), Toast.LENGTH_SHORT).show();
        } else {

            Toast.makeText(this, forgotResponse.getMessage(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ForgotPasswordActivity.class);
            intent.putExtra("email", forgotResponse.getUser().getEmail());
            intent.putExtra("otp", Double.toString(forgotResponse.getUser().getOtp()));
            intent.putExtra("id", forgotResponse.getUser().getId());
            startActivity(intent);
        }


    }

    @Override
    public void onError(Throwable e) {

        Log.e(TAG, "paswrd : onError: " + e.getMessage());


        /*try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }*/

//        handleError(e);
//        Toasty.error(this, "UnAuthorised User", Toast.LENGTH_SHORT).show();
        email.setText("");
        password.setText("");

    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }
}
