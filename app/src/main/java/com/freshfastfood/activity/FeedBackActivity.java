package com.freshfastfood.activity;

import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.freshfastfood.R;
import com.freshfastfood.model.User;
import com.freshfastfood.retrofit.APIClient;
import com.freshfastfood.retrofit.GetResult;
import com.freshfastfood.utils.CustPrograssbar;
import com.freshfastfood.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

public class FeedBackActivity extends BaseActivity implements GetResult.MyListener {

    @BindView(R.id.ed_feedback)
    TextInputEditText edFeedback;
    @BindView(R.id.btn_submit)
    TextView btnSubmit;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;
    User user;
    SessionManager sessionManager;
    CustPrograssbar custPrograssbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        ButterKnife.bind(this);
        sessionManager = new SessionManager(FeedBackActivity.this);
        user = sessionManager.getUserDetails("");
        custPrograssbar = new CustPrograssbar();
    }
    private void userFeedBack() {
        custPrograssbar.prograssCreate(FeedBackActivity.this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("msg", edFeedback.getText().toString());
            jsonObject.put("rate", String.valueOf(ratingBar.getRating()));
            jsonObject.put("uid", user.getId());
            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().sendFeed((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void callback(JsonObject result, String callNo) {
        custPrograssbar.closePrograssBar();
        try {
            if (result != null) {

                    JSONObject object = new JSONObject(result.toString());
                    Toast.makeText(FeedBackActivity.this, "" + object.getString("ResponseMsg"), Toast.LENGTH_SHORT).show();
                    if (object.getString("Result").equals("true")) {
                        ratingBar.setRating(0.0f);
                        edFeedback.setText("");
                    }

            }
        } catch (Exception e) {
            e.toString();
        }
    }
    @OnClick(R.id.btn_submit)
    public void onViewClicked() {
        if (validation()) {
            userFeedBack();
        }
    }
    private boolean validation() {
        if (edFeedback.getText().toString().isEmpty()) {
            edFeedback.setError("Enter FeedBack");
            return false;
        }
        if (String.valueOf(ratingBar.getRating()).equals("0.0")) {
            Toast.makeText(FeedBackActivity.this, "Give Rating", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
