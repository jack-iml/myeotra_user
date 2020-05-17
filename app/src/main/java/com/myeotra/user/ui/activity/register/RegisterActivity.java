package com.myeotra.user.ui.activity.register;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.PhoneNumber;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.myeotra.user.BuildConfig;
import com.myeotra.user.R;
import com.myeotra.user.base.BaseActivity;
import com.myeotra.user.data.SharedHelper;
import com.myeotra.user.data.network.model.RegisterResponse;
import com.myeotra.user.data.network.model.SettingsResponse;
import com.myeotra.user.ui.activity.main.MainActivity;
import com.myeotra.user.ui.countrypicker.Country;
import com.myeotra.user.ui.countrypicker.CountryPicker;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.myeotra.user.common.Constant.MULTIPLE_PERMISSION;
import static com.myeotra.user.common.Constant.RC_MULTIPLE_PERMISSION_CODE;

public class RegisterActivity extends BaseActivity implements RegisterIView {


    @BindView(R.id.imgProfile)
    CircleImageView imgProfile;

    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.icBack)
    ImageView icBack;
    @BindView(R.id.first_name)
    EditText firstName;
    @BindView(R.id.last_name)
    EditText lastName;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.password_confirmation)
    EditText passwordConfirmation;
    @BindView(R.id.chkTerms)
    CheckBox chkTerms;
    @BindView(R.id.countryImage)
    ImageView countryImage;
    @BindView(R.id.countryNumber)
    TextView countryNumber;
    @BindView(R.id.phoneNumber)
    EditText phoneNumber;

    private String countryDialCode = "+91";
    private CountryPicker mCountryPicker;

    private RegisterPresenter<RegisterActivity> registerPresenter = new RegisterPresenter();
    private boolean isEmailAvailable = true;
    private boolean isPhoneAvailable = true;
    private String TAG = "AAAA";

    private File imgFile = null;


    @Override
    public int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        registerPresenter.attachView(this);
        // Activity title will be updated after the locale has changed in Runtime
//        setTitle(getString(R.string.register));

        //        call for dynamic navigation drawer menu
        registerPresenter.getSettings();

//        clickFunctions();

        setCountryList();

            /*if (BuildConfig.DEBUG) {
                email.setText("stevejobs@yopmail.com");
                firstName.setText("steve");
                lastName.setText("jobs");
                phoneNumber.setText("9003440134");
                password.setText("112233");
                passwordConfirmation.setText("112233");
            }*/


        email.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

                isEmailAvailable = true;
                if (!TextUtils.isEmpty(email.getText().toString()))
                    if (Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                        registerPresenter.verifyEmail(email.getText().toString().trim());
                    }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });


        phoneNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

                isPhoneAvailable = true;
                if (!TextUtils.isEmpty(phoneNumber.getText().toString())) {
                    registerPresenter.verifyCredentials(phoneNumber.getText().toString(), countryDialCode);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });
    }

    private void setCountryList() {
        mCountryPicker = CountryPicker.newInstance("Select Country");
        List<Country> countryList = Country.getAllCountries();
        Collections.sort(countryList, (s1, s2) -> s1.getName().compareToIgnoreCase(s2.getName()));
        mCountryPicker.setCountriesList(countryList);

        setListener();
    }


    private void setListener() {
        mCountryPicker.setListener((name, code, dialCode, flagDrawableResID) -> {
            countryNumber.setText(dialCode);
            countryDialCode = dialCode;
            countryImage.setImageResource(flagDrawableResID);
            mCountryPicker.dismiss();
        });

        countryImage.setOnClickListener(v -> mCountryPicker.show(getSupportFragmentManager(), "COUNTRY_PICKER"));

        countryNumber.setOnClickListener(v -> mCountryPicker.show(getSupportFragmentManager(), "COUNTRY_PICKER"));

        getUserCountryInfo();
    }

    private void getUserCountryInfo() {
        Country country = getDeviceCountry(RegisterActivity.this);
        countryImage.setImageResource(country.getFlag());
        countryNumber.setText(country.getDialCode());
        countryDialCode = country.getDialCode();
    }

    private void clickFunctions() {
        email.setOnFocusChangeListener((v, hasFocus) -> {
            isEmailAvailable = true;
            if (!hasFocus && !TextUtils.isEmpty(email.getText().toString()))
                if (Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches())
                    registerPresenter.verifyEmail(email.getText().toString().trim());
        });

        phoneNumber.setOnFocusChangeListener((v, hasFocus) -> {
            isPhoneAvailable = true;
            if (!hasFocus && !TextUtils.isEmpty(phoneNumber.getText().toString()))
                registerPresenter.verifyCredentials(phoneNumber.getText().toString(), countryDialCode);
        });
    }

    @OnClick({R.id.next, R.id.chkTerms, R.id.icBack, R.id.imgProfile})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.next:
                if (validate()) {
                   /* Country mCountry = getDeviceCountry(this);
                    fbOtpVerify(mCountry.getCode(), mCountry.getDialCode(), phoneNumber.getText().toString());*/

                    register();
                }
                break;
            case R.id.chkTerms:
                showTermsConditionsDialog();
                break;

            case R.id.icBack:
                onBackPressed();
                break;

            case R.id.imgProfile:
                MultiplePermissionTask();
                break;
        }
    }

    private boolean hasMultiplePermission() {
        return EasyPermissions.hasPermissions(this, MULTIPLE_PERMISSION);
    }

    @AfterPermissionGranted(RC_MULTIPLE_PERMISSION_CODE)
    void MultiplePermissionTask() {
        if (hasMultiplePermission()) pickImage();
        else EasyPermissions.requestPermissions(
                this, getString(R.string.please_accept_permission),
                RC_MULTIPLE_PERMISSION_CODE,
                MULTIPLE_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void showTermsConditionsDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getText(R.string.terms_and_conditions));

        WebView wv = new WebView(this);
        wv.loadUrl(BuildConfig.TERMS_CONDITIONS);
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        alert.setView(wv);
        alert.setNegativeButton("Close", (dialog, id) -> dialog.dismiss());
        alert.show();
    }

    private void register() {
        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("first_name", RequestBody.create(MediaType.parse("text/plain"), firstName.getText().toString()));
        map.put("last_name", RequestBody.create(MediaType.parse("text/plain"), lastName.getText().toString()));
        map.put("email", RequestBody.create(MediaType.parse("text/plain"), email.getText().toString()));
        map.put("password", RequestBody.create(MediaType.parse("text/plain"), password.getText().toString()));
        map.put("password_confirmation", RequestBody.create(MediaType.parse("text/plain"), passwordConfirmation.getText().toString()));
        map.put("device_token", RequestBody.create(MediaType.parse("text/plain"), SharedHelper.getKey(this, "device_token")));
        map.put("device_id", RequestBody.create(MediaType.parse("text/plain"), SharedHelper.getKey(this, "device_id")));
        map.put("mobile", RequestBody.create(MediaType.parse("text/plain"), phoneNumber.getText().toString()));
        map.put("country_code", RequestBody.create(MediaType.parse("text/plain"), countryNumber.getText().toString()));
        map.put("device_type", RequestBody.create(MediaType.parse("text/plain"), BuildConfig.DEVICE_TYPE));
        map.put("login_by", RequestBody.create(MediaType.parse("text/plain"), "manual"));


        MultipartBody.Part filePart = null;
        if (imgFile != null)
            try {
                File compressedImageFile = new Compressor(this).compressToFile(imgFile);
                filePart = MultipartBody.Part.createFormData("picture", compressedImageFile.getName(),
                        RequestBody.create(MediaType.parse("image*//*"), compressedImageFile));
            } catch (IOException e) {
                e.printStackTrace();
            }

        showLoading();

//        profilePresenter.update(map, filePart);
        registerPresenter.register(map, filePart);
    }

    private boolean validate() {
        if (email.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            Toast.makeText(this, getString(R.string.invalid_email_address), Toast.LENGTH_SHORT).show();
            return false;
        } else if (firstName.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_first_name), Toast.LENGTH_SHORT).show();
            return false;
        } else if (lastName.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_last_name), Toast.LENGTH_SHORT).show();
            return false;
        } else if (phoneNumber.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_phone_number), Toast.LENGTH_SHORT).show();
            return false;
        } else if (phoneNumber.getText().toString().trim().length() < 6) {
            Toast.makeText(this, getString(R.string.valid_phone_number), Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_password), Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.getText().toString().length() < 6) {
            Toast.makeText(this, getString(R.string.invalid_password_length), Toast.LENGTH_SHORT).show();
            return false;
        } else if (passwordConfirmation.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_confirm_password), Toast.LENGTH_SHORT).show();
            return false;
        } else if (!password.getText().toString().equals(passwordConfirmation.getText().toString())) {
            Toast.makeText(this, getString(R.string.password_should_be_same), Toast.LENGTH_SHORT).show();
            return false;
        } else if (SharedHelper.getKey(this, "device_token").isEmpty()) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.w("RegisterActivity", "getInstanceId failed", task.getException());
                    return;
                }
                Log.d("FCM_TOKEN", task.getResult().getToken());
                SharedHelper.putKey(RegisterActivity.this, "device_token", task.getResult().getToken());
            });
            return false;
        } else if (SharedHelper.getKey(this, "device_id").isEmpty()) {
            String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            SharedHelper.putKey(this, "device_id", deviceId);
            Toast.makeText(this, getString(R.string.invalid_device_id), Toast.LENGTH_SHORT).show();
            return false;
        } else if (!chkTerms.isChecked()) {
            Toast.makeText(this, getString(R.string.please_accept_terms_condition), Toast.LENGTH_SHORT).show();
            return false;
        } else if (isEmailAvailable) {
            showErrorMessage(email, getString(R.string.email_already_exist));
            /*if (BuildConfig.DEBUG) {
                password.setText(null);
                passwordConfirmation.setText(null);
            }*/
            return false;
        } else if (isPhoneAvailable) {
            showErrorMessage(phoneNumber, getString(R.string.mobile_already_exist));
            /*if (BuildConfig.DEBUG) {
                .setText(null);
                passwordConfirmation.setText(null);
            }*/
            return false;
        } else if (imgFile == null) {
            showErrorMessage(phoneNumber, getString(R.string.profile_pic_upload));
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onSuccess(RegisterResponse response) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        if (response.getStatus() == 0) {
            Toasty.error(this, response.getMessage(), Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, " : res : signup " + new Gson().toJson(response));

            Toast.makeText(this, getString(R.string.you_have_been_successfully_registered), Toast.LENGTH_SHORT).show();
            SharedHelper.putKey(this, "access_token", "Bearer " + response.getAccessToken());
            SharedHelper.putKey(this, "logged_in", true);
            finishAffinity();
            startActivity(new Intent(this, MainActivity.class));
        }


    }

    @Override
    public void onSuccess(Object object) {

        Log.e(TAG, "response : verifyEmail -- " + new Gson().toJson(object));

        isEmailAvailable = false;
    }

    @Override
    public void onSuccessPhoneNumber(Object object) {

        isPhoneAvailable = false;

        Log.e(TAG, "response : verifyCredentials -- " + new Gson().toJson(object));

    }

    @Override
    public void onVerifyPhoneNumberError(Throwable e) {
        Log.e(TAG, "onVerifyPhoneNumberError: " + e.getMessage());
        isPhoneAvailable = true;
        showErrorMessage(phoneNumber, getString(R.string.phone_number_already_exists));
    }

    @Override
    public void onError(Throwable e) {

        Log.e(TAG, "rgstr onError: " + e.getMessage());
        handleError(e);
    }

    @Override
    public void onVerifyEmailError(Throwable e) {

        Log.e(TAG, "onVerifyEmailError: " + e.getMessage());
        isEmailAvailable = true;
        showErrorMessage(email, getString(R.string.email_already_exist));
    }

    private void showErrorMessage(EditText view, String message) {
        Toasty.error(this, message, Toast.LENGTH_SHORT).show();
//        view.requestFocus();
//        view.setText(null);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, RegisterActivity.this,
                new DefaultCallback() {
                    @Override
                    public void onImagesPicked(@NonNull List<File> imageFiles,
                                               EasyImage.ImageSource source, int type) {
                        imgFile = imageFiles.get(0);
                        Glide.with(RegisterActivity.this)
                                .load(Uri.fromFile(imgFile))
                                .apply(RequestOptions
                                        .placeholderOf(R.drawable.ic_user_placeholder)
                                        .dontAnimate()
                                        .error(R.drawable.ic_user_placeholder))
                                .into(imgProfile);
                    }
                });


        if (requestCode == REQUEST_ACCOUNT_KIT && data != null && resultCode == RESULT_OK) {
            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(Account account) {
                    Log.d("AccountKit", "onSuccess: Account Kit" + AccountKit.getCurrentAccessToken().getToken());
                    if (AccountKit.getCurrentAccessToken().getToken() != null) {
                        PhoneNumber phoneNumber = account.getPhoneNumber();
                        SharedHelper.putKey(RegisterActivity.this, "countryCode", "+" + phoneNumber.getCountryCode());
                        SharedHelper.putKey(RegisterActivity.this, "mobile", phoneNumber.getPhoneNumber());
//                        register();
                    }
                }

                @Override
                public void onError(AccountKitError accountKitError) {
                    Log.e("AccountKit", "onError: Account Kit" + accountKitError);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        registerPresenter.onDetach();
        super.onDestroy();
    }

    @Override
    public void onSuccess(SettingsResponse response) {
        Log.e(TAG, "response : getSettings -- " + new Gson().toJson(response));


//        lnrReferralCode.setVisibility(response.getReferral().getReferral().equalsIgnoreCase("1") ? View.VISIBLE : View.GONE);
    }
}
