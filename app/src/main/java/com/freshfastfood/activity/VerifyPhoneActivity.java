package com.freshfastfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.freshfastfood.R;
import com.freshfastfood.model.LoginUser;
import com.freshfastfood.model.RestResponse;
import com.freshfastfood.model.User;
import com.freshfastfood.retrofit.APIClient;
import com.freshfastfood.retrofit.GetResult;
import com.freshfastfood.utils.CustPrograssbar;
import com.freshfastfood.utils.SessionManager;
import com.freshfastfood.utils.Utiles;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

import static com.freshfastfood.utils.Utiles.isvarification;

public class VerifyPhoneActivity extends AppCompatActivity implements GetResult.MyListener {
    @BindView(R.id.txt_mob)
    TextView txtMob;
    @BindView(R.id.ed_otp1)
    TextInputEditText edOtp1;
    @BindView(R.id.ed_otp2)
    TextInputEditText edOtp2;
    @BindView(R.id.ed_otp3)
    TextInputEditText edOtp3;
    @BindView(R.id.ed_otp4)
    TextInputEditText edOtp4;
    @BindView(R.id.ed_otp5)
    TextInputEditText edOtp5;
    @BindView(R.id.ed_otp6)
    TextInputEditText edOtp6;

    @BindView(R.id.btn_reenter)
    TextView btnReenter;
    @BindView(R.id.btn_timer)
    TextView btnTimer;
    private String verificationId;
    private FirebaseAuth mAuth;
    String phonenumber;
    String phonecode;
    CustPrograssbar custPrograssbar;
    SessionManager sessionManager;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        ButterKnife.bind(this);
        sessionManager = new SessionManager(VerifyPhoneActivity.this);
        custPrograssbar = new CustPrograssbar();
        if (isvarification == 2) {
            user = (User) getIntent().getSerializableExtra("user");
        } else {
            user = sessionManager.getUserDetails("");
        }
        mAuth = FirebaseAuth.getInstance();
        phonenumber = getIntent().getStringExtra("phone");
        phonecode = getIntent().getStringExtra("code");
        sendVerificationCode(phonecode + phonenumber);
        txtMob.setText("We have sent you an SMS on " + phonecode + " " + phonenumber + "\n with 6 digit verification code");
        try {
            new CountDownTimer(60000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
                    btnTimer.setText(seconds + " Secound Wait");
                }

                @Override
                public void onFinish() {
                    btnReenter.setVisibility(View.VISIBLE);
                    btnTimer.setVisibility(View.GONE);
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        switch (isvarification) {
                            case 0:
                                Intent intent = new Intent(VerifyPhoneActivity.this, ChanegPasswordActivity.class);
                                intent.putExtra("phone", phonenumber);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                break;
                            case 1:
                                createUser();
                                break;
                            case 2:
                                updateUser();
                                break;
                            default:
                                break;
                        }
                    } else {
                        Toast.makeText(VerifyPhoneActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void sendVerificationCode(String number) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                edOtp1.setText("" + code.substring(0, 1));
                edOtp2.setText("" + code.substring(1, 2));
                edOtp3.setText("" + code.substring(2, 3));
                edOtp4.setText("" + code.substring(3, 4));
                edOtp5.setText("" + code.substring(4, 5));
                edOtp6.setText("" + code.substring(5, 6));
                verifyCode(code);
            }
        }
        @Override
        public void onVerificationFailed(FirebaseException e) {
            User user1 = new User();
            user1.setId("0");
            user1.setName("User");
            user1.setEmail("user@gmail.com");
            user1.setMobile("+91 8888888888");
            sessionManager.setUserDetails("", user1);
            Toast.makeText(VerifyPhoneActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    };

    @OnClick({R.id.btn_send, R.id.btn_reenter})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                if (validation()) {
                    verifyCode(edOtp1.getText().toString() + "" + edOtp2.getText().toString() + "" + edOtp3.getText().toString() + "" + edOtp4.getText().toString() + "" + edOtp5.getText().toString() + "" + edOtp6.getText().toString());
                }
                break;
            case R.id.btn_reenter:
                btnReenter.setVisibility(View.GONE);
                btnTimer.setVisibility(View.VISIBLE);
                try {
                    new CountDownTimer(60000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
                            btnTimer.setText(seconds + " Secound Wait");
                        }

                        @Override
                        public void onFinish() {
                            btnReenter.setVisibility(View.VISIBLE);
                            btnTimer.setVisibility(View.GONE);
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sendVerificationCode(phonecode + phonenumber);
                break;
            default:
                break;
        }
    }

    private void createUser() {
        custPrograssbar.prograssCreate(VerifyPhoneActivity.this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", user.getName());
            jsonObject.put("email", user.getEmail());
            jsonObject.put("mobile", user.getMobile());
            jsonObject.put("ccode", user.getCcode());
            jsonObject.put("password", user.getPassword());
            jsonObject.put("imei", Utiles.getIMEI(VerifyPhoneActivity.this));
            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().getRegister((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateUser() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", user.getId());
            jsonObject.put("name", user.getName());
            jsonObject.put("email", user.getEmail());
            jsonObject.put("mobile", user.getMobile());
            jsonObject.put("ccode", user.getCcode());
            jsonObject.put("password", user.getPassword());
            jsonObject.put("imei", Utiles.getIMEI(VerifyPhoneActivity.this));
            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().updateProfile((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "2");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            Log.e("response", "--->" + result);
            custPrograssbar.closePrograssBar();
            if (callNo.equalsIgnoreCase("1")) {
                isvarification = -1;
                Gson gson = new Gson();
                RestResponse response = gson.fromJson(result.toString(), RestResponse.class);
                Toast.makeText(VerifyPhoneActivity.this, "" + response.getResponseMsg(), Toast.LENGTH_LONG).show();
                if (response.getResult().equals("true")) {
                    startActivity(new Intent(VerifyPhoneActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();
                }
            } else if (callNo.equalsIgnoreCase("2")) {
                isvarification = -1;
                Gson gson = new Gson();
                LoginUser response = gson.fromJson(result.toString(), LoginUser.class);
                Toast.makeText(VerifyPhoneActivity.this, "" + response.getResponseMsg(), Toast.LENGTH_LONG).show();
                if (response.getResult().equals("true")) {
                    sessionManager.setUserDetails("", response.getUser());
                    startActivity(new Intent(VerifyPhoneActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean validation() {

        if (edOtp1.getText().toString().isEmpty()) {
            edOtp1.setError("");
            return false;
        }
        if (edOtp2.getText().toString().isEmpty()) {
            edOtp2.setError("");
            return false;
        }
        if (edOtp3.getText().toString().isEmpty()) {
            edOtp3.setError("");
            return false;
        }
        if (edOtp4.getText().toString().isEmpty()) {
            edOtp4.setError("");
            return false;
        }
        if (edOtp5.getText().toString().isEmpty()) {
            edOtp5.setError("");
            return false;
        }
        if (edOtp6.getText().toString().isEmpty()) {
            edOtp6.setError("");
            return false;
        }
        return true;
    }
}
