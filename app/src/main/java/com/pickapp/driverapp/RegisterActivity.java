package com.pickapp.driverapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.IDN;

import model.model.backend.Backend;
import model.model.datasource.BackendFactory;
import model.model.datasource.Firebase_DBManager;
import model.model.entities.Driver;

import static com.pickapp.driverapp.LoginActivity.Email;
import static com.pickapp.driverapp.LoginActivity.Password;

public class RegisterActivity extends AppCompatActivity {

    private Backend backend;
    private SharedPreferences sharedPreferences;

    private EditText lastName;
    private EditText firtName;
    private EditText id;
    private EditText phoneNumber;
    private EditText email;
    private EditText password;
    private EditText creditCard;

    private Button registerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        backend = BackendFactory.getInstance();
        findViews();

    }

    private void findViews() {

        lastName = (EditText) findViewById(R.id.lastName);
        firtName = (EditText) findViewById(R.id.firstName);
        id = (EditText) findViewById(R.id.Id);
        phoneNumber = (EditText) findViewById(R.id.PhoneNumber);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.Password);
        creditCard = (EditText) findViewById(R.id.CreditCard);

        registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkFieldsInput(v);
                try {
                    register(v);
                } catch (Exception e) {

                }
            }
        });
    }

    public void register(View v) throws Exception {

        try {
            String et_fName = firtName.getText().toString();
            String et_lName = lastName.getText().toString();
            Long et_phoneNumber = Long.parseLong(phoneNumber.getText().toString());
            Long et_id = Long.parseLong(id.getText().toString());
            Long et_creditCard = Long.parseLong(creditCard.getText().toString());

            String et_email = email.getText().toString();
            String et_password = password.getText().toString();

            // TODO: register in fireBase to.
            Driver myDriver = new Driver(et_lName, et_fName, et_id, et_phoneNumber, et_email, et_password, et_creditCard);

            backend.addDriver(myDriver, new Firebase_DBManager.Action<Long>() {
                @Override
                public void onSuccess(Long obj) {
                    Toast.makeText(getBaseContext(), "successfully addded you to the database", Toast.LENGTH_LONG).show();
                    resetView();
                }

                @Override
                public void onFailure(Exception exception) {
                    Toast.makeText(getBaseContext(), "Error \n" + exception.getMessage(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onProgress(String status, double percent) {

                }
            });

            // Save info in the shared prefrences
            //TODO actually make these in the register activity

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Email, et_email);
            editor.putString(Password, et_password);
            editor.commit();
            Toast.makeText(getApplicationContext(), "Your information was saved succesfully!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Error ", Toast.LENGTH_LONG).show();
        }
    }

    private void checkFieldsInput(View v) {

        // Reset errors.
        lastName.setError(null);
        firtName.setError(null);
        id.setError(null);
        phoneNumber.setError(null);
        email.setError(null);
        password.setError(null);
        creditCard.setError(null);

        // Store values at the time of the login attempt.
        String et_fName = firtName.getText().toString();
        String et_lName = lastName.getText().toString();

        //  Long et_phoneNumber = Long.valueOf(phoneNumber.getText().toString()).longValue();
        //  Long et_id = Long.valueOf(id.getText().toString()).longValue();
        //Long et_creditCard = Long.valueOf(creditCard.getText().toString()).longValue();

        Long et_phoneNumber = Long.parseLong(phoneNumber.getText().toString());
        Long et_id = Long.parseLong(id.getText().toString());
       Long et_creditCard = Long.parseLong(creditCard.getText().toString());

        String et_email = email.getText().toString();
        String et_password = password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a last name, if the user entered one.
        if (!TextUtils.isEmpty(et_lName)) {
            lastName.setError("enter last name");
            focusView = lastName;
            cancel = true;
        }

        // Check for a first name, if the user entered one.
        if (!TextUtils.isEmpty(et_fName)) {
            firtName.setError("enter first name");
            focusView = firtName;
            cancel = true;
        }

        // Check for a id, if the user entered one.
        if (!TextUtils.isEmpty(et_id.toString())) {
            id.setError("enter id");
            focusView = id;
            cancel = true;
        }

        // Check for a id, if its to short.
        if (!(et_id.toString().length() <= 9)) {
            id.setError("id is to short");
            focusView = id;
            cancel = true;
        }

        // Check for a phone number, if the user entered one.
        if (!TextUtils.isEmpty(et_phoneNumber.toString())) {
            phoneNumber.setError("enter phone number");
            focusView = phoneNumber;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(et_password) && !isPasswordValid(et_password)) {
            password.setError(getString(R.string.error_invalid_password));
            focusView = password;
            cancel = true;
        }

        // Check for a valid email address.
        if (!TextUtils.isEmpty(et_email)) {
            email.setError(getString(R.string.error_field_required));
            focusView = email;
            cancel = true;
        } else if (!isEmailValid(et_email)) {
            email.setError(getString(R.string.error_invalid_email));
            focusView = email;
            cancel = true;
        }

        // Check for a credit card, if the user entered one.
        if (!TextUtils.isEmpty(et_creditCard.toString())) {
            creditCard.setError("enter a valid credit card number");
            focusView = creditCard;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            try {
                register(v);
            } catch (Exception e) {

            }
        }
    }

    private boolean isEmailValid(String email) {
        String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(EMAIL_REGEX);
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private void resetView() {
        lastName.setText("");
        firtName.setText("");
        id.setText("");
        phoneNumber.setText("");
        email.setText("");
        password.setText("");
        creditCard.setText("");
    }

}
