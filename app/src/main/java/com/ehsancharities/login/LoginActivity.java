package com.ehsancharities.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.ehsancharities.R;
import com.ehsancharities.firebase.FirebaseServices;
import com.ehsancharities.sign_up.SignUpActivity;
import com.ehsancharities.utils.Tools;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private AppCompatEditText email, password;
    private Button loginBtn;
    private TextView signUpText;
    private ProgressBar loginLoading;
    private FirebaseServices firebaseServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initWidgets();

    }

    private void initWidgets() {

        firebaseServices = FirebaseServices.getFirebaseServicesInstance();
        firebaseServices.setContext(LoginActivity.this);

        Tools.setSystemBarColor(this, R.color.blue_grey_900);

        email = findViewById(R.id.email_edit_text);
        email.addTextChangedListener(new RealTimeTextWatcher(email));

        password = findViewById(R.id.password_edit_text);
        password.addTextChangedListener(new RealTimeTextWatcher(password));

        loginBtn = findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(this);

        signUpText = findViewById(R.id.sign_up_text);
        signUpText.setOnClickListener(this);

        loginLoading = findViewById(R.id.login_loading);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.sign_up_text:

                goToSignUp();

                break;

            case R.id.login_btn:

                login();

                break;
        }

    }

    private void login() {
        if (!validateEmail() || !validatePassword()) {

        } else {
            Tools.hideKeyboard(LoginActivity.this);
            loginLoading.setVisibility(View.VISIBLE);
            firebaseServices.login(email.getText().toString(), password.getText().toString(), loginLoading);
        }
    }

    private void goToSignUp() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
        LoginActivity.this.finish();
    }

    private boolean validateEmail() {

        boolean valid = true;

        String inputEmail = email.getText().toString();
        if (inputEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches()) {
            email.setError(getString(R.string.valid_email));
            valid = false;
        } // end if from email
        else {
            email.setError(null);
        } // end else

        return valid;

    }

    private boolean validatePassword() {
        boolean valid = true;

        String inputPassword = password.getText().toString();
        if (inputPassword.isEmpty() || inputPassword.length() < 6) {
            password.setError(getString(R.string.at_least_six_character));
            valid = false;
        } else {
            password.setError(null);
        } // end else

        return valid;
    }

    public class RealTimeTextWatcher implements TextWatcher {

        private View view;

        public RealTimeTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

            switch (view.getId()) {

                case R.id.email_edit_text:
                    validateEmail();
                    break;

                case R.id.password_edit_text:
                    validatePassword();
                    break;

            }
        }
    }
}
