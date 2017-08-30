package com.favbites.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.CheckBox;
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

public class RegistrationActivity extends BaseActivity implements View.OnClickListener{

    EditText editFirstName, editLastName, editEmail, editPassword, editConfirmPassword;
    TextView tvSignUp;
    CheckBox cbTerms;
    KProgressHUD pd;
    private CoordinatorLayout coordinatorLayout;
    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initViews();
    }

    public void initViews() {
        pd = Utils.showDialog(this);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);

        editFirstName = (EditText) findViewById(R.id.editFirstName);
        editLastName = (EditText) findViewById(R.id.editLastName);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
        editConfirmPassword = (EditText) findViewById(R.id.editConfirmPassword);

        tvSignUp = (TextView) findViewById(R.id.tvSignUp);
        cbTerms = (CheckBox) findViewById(R.id.cbTerms);
        imgBack = (ImageView) findViewById(R.id.imgBack);

        tvSignUp.setOnClickListener(this);
        imgBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.tvSignUp:
                registerUser();
                break;

            case R.id.imgBack:
                finish();
                break;
        }
    }

    public void registerUser() {
        String firstName = editFirstName.getText().toString();
        String lastName = editLastName.getText().toString();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String confirmPassword = editConfirmPassword.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
                || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show();
        } else if(!password.equals(confirmPassword)) {
            Toast.makeText(this, "Password didn't match", Toast.LENGTH_SHORT).show();
        } else if (password.length() < 6) {
            Toast.makeText(this, "Password must contains at least 6 letters.", Toast.LENGTH_SHORT).show();
        } else if(!Utils.emailValidator(email)){
            Toast.makeText(this, "Please enter the valid email address.", Toast.LENGTH_SHORT).show();
        }else if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Please accept all the terms and conditions.", Toast.LENGTH_SHORT).show();
        } else {
            pd.show();

            ModelManager.getInstance().getRegistrationManager().registerUser(
                    Operations.getRegistrationParams(firstName, lastName, email, password, "token", "A"));
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
    public void onEvent(Event event){
        switch (event.getKey()) {
            case Constants.REGISTRATION_SUCCESS:
                pd.dismiss();
                Toast.makeText(this, ""+event.getValue(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                break;

            case Constants.REGISTRATION_FAILED:
                pd.dismiss();
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, ""+event.getValue(), Snackbar.LENGTH_LONG);
                snackbar.show();
               // Toast.makeText(this, ""+event.getValue(), Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
