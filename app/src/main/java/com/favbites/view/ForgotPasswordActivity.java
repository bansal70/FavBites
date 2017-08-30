package com.favbites.view;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.favbites.R;
import com.favbites.controller.ModelManager;
import com.favbites.model.Constants;
import com.favbites.model.Event;
import com.favbites.model.Operations;
import com.favbites.model.Utils;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ForgotPasswordActivity extends BaseActivity implements View.OnClickListener {

    private EditText editEmail;
    TextView tvSubmit;
    KProgressHUD pd;
    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initViews();
    }

    public void initViews() {
        pd = Utils.showDialog(this);
        editEmail = (EditText) findViewById(R.id.editEmail);
        tvSubmit = (TextView) findViewById(R.id.tvSubmit);
        imgBack = (ImageView) findViewById(R.id.imgBack);

        tvSubmit.setOnClickListener(this);
        imgBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvSubmit:
                recoverPassword();
                break;

            case R.id.imgBack:
                finish();
                break;
        }
    }

    public void recoverPassword() {
        String email = editEmail.getText().toString();
        if (email.isEmpty() || !Utils.emailValidator(email)) {
            Toast.makeText(this, "Please enter the valid email address", Toast.LENGTH_SHORT).show();
        } else {
            pd.show();
            ModelManager.getInstance().getForgotPasswordManager()
                    .forgotPassword(Operations.getForgotPasswordParams(email));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(Event event) {
        switch (event.getKey()) {

            case Constants.FORGOT_PASSWORD_SUCCESS:
                pd.dismiss();
                Toast.makeText(this, ""+event.getValue(), Toast.LENGTH_SHORT).show();
                break;

            case Constants.FORGOT_PASSWORD_FAILED:
                pd.dismiss();
                Toast.makeText(this, ""+event.getValue(), Toast.LENGTH_SHORT).show();
                break;
        }
    }
}