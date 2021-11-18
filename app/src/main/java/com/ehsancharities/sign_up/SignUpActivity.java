package com.ehsancharities.sign_up;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ehsancharities.MapsActivity;
import com.ehsancharities.R;
import com.ehsancharities.firebase.FirebaseServices;
import com.ehsancharities.login.LoginActivity;
import com.ehsancharities.model.Charity;
import com.ehsancharities.utils.Const;
import com.ehsancharities.utils.Tools;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;


public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton add_loc;
    private EditText charityName, email, password, charityAddress, charityPhoneNumber;
    private TextView login;
    private Button signUpBtn;
    private FirebaseServices firebaseServices;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    private DocumentReference documentReference;
    private ProgressBar signUpLoading;
    private static final String TAG = "SignUpActivity";

    public static LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initWidgets();

    }

    private void initWidgets() {

        firebaseServices = FirebaseServices.getFirebaseServicesInstance();
        firebaseServices.setContext(SignUpActivity.this);

        Tools.setSystemBarColor(this, R.color.blue_grey_900);
        charityName = findViewById(R.id.charity_name_edit_text);
        charityName.addTextChangedListener(new RealTimeTextWatcher(charityName));

        email = findViewById(R.id.email_edit_text);
        email.addTextChangedListener(new RealTimeTextWatcher(email));

        password = findViewById(R.id.password_edit_text);
        password.addTextChangedListener(new RealTimeTextWatcher(password));

        charityAddress = findViewById(R.id.charity_address_edittext);
        charityAddress.addTextChangedListener(new RealTimeTextWatcher(charityAddress));

        charityPhoneNumber = findViewById(R.id.charity_phone_number_edit_text);
        charityPhoneNumber.addTextChangedListener(new RealTimeTextWatcher(charityPhoneNumber));

        login = findViewById(R.id.login_text);
        login.setOnClickListener(this);

        signUpBtn = findViewById(R.id.sign_up_btn);
        signUpBtn.setOnClickListener(this);

        add_loc=findViewById(R.id.add_loc);
        add_loc.setOnClickListener(this);

        signUpLoading = findViewById(R.id.sign_up_loading);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    private boolean validateCharityName() {
        boolean valid = true;

        String inputCharityName = charityName.getText().toString();
        if (inputCharityName.isEmpty() || inputCharityName.length() < 3) {
            charityName.setError(getString(R.string.at_least_three_character));
            valid = false;
        } else {
            charityName.setError(null);
        } // end else

        return valid;
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

    private boolean validateAddress() {
        boolean valid = true;

        String inputAddress = charityAddress.getText().toString();
        if (inputAddress.isEmpty() || inputAddress.length() < 3) {
            charityAddress.setError(getString(R.string.at_least_three_character));
            valid = false;
        } else {
            charityAddress.setError(null);
        } // end else

        return valid;
    }

    private boolean validatePhoneNumber() {

        boolean valid = true;

        String inputPhone = charityPhoneNumber.getText().toString();
        if (inputPhone.isEmpty()) {
            charityPhoneNumber.setError(getString(R.string.cannot_be_empty));
            valid = false;
        } else {
            charityPhoneNumber.setError(null);
        } // end else

        return valid;

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.login_text:

                backToLogin();

                break;

            case R.id.sign_up_btn:

                signUp();

                break;

            case R.id.add_loc:
                selectLocation();
                break;
        }

    }
    private void selectLocation() {

        startActivity(new Intent(getBaseContext() , MapsActivity.class));
    }

    private void backToLogin() {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
        SignUpActivity.this.finish();
    }

    private void signUp() {
        if (!validateCharityName() || !validateEmail() || !validatePassword()
                || !validateAddress() || !validatePhoneNumber()) {

        } else {
            Tools.hideKeyboard(SignUpActivity.this);

            signUpLoading.setVisibility(View.VISIBLE);
            firebaseServices.createCharity(generateCharity(), password.getText().toString(), signUpLoading);
        }
    }

    private Charity generateCharity() {
        Charity charity = new Charity();
        charity.setCharityName(charityName.getText().toString());
        charity.setCharityEmail(email.getText().toString());
        charity.setCharityAddress(charityAddress.getText().toString());
        charity.setCharityPhoneNumber(charityPhoneNumber.getText().toString());
        charity.setLat(latLng.latitude);
        charity.setLng(latLng.longitude);
        charity.setStateAccount(0);
        charity.setAccountType("Charity");
        return charity;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backToLogin();
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

        @SuppressLint("NonConstantResourceId")
        @Override
        public void afterTextChanged(Editable editable) {

            switch (view.getId()) {
                case R.id.charity_name_edit_text:
                    validateCharityName();
                    break;

                case R.id.email_edit_text:
                    validateEmail();
                    break;

                case R.id.password_edit_text:
                    validatePassword();
                    break;

                case R.id.charity_address_edittext:
                    validateAddress();
                    break;


                case R.id.charity_phone_number_edit_text:
                    validatePhoneNumber();
                    break;

            }
        }
    }

}
