package com.pickapp.driverapp;

import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import model.model.backend.Backend;
import model.model.backend.BackendFactory;
import model.model.entities.Driver;

public class RegisterActivity extends AppCompatActivity {

    private Backend backend;
    private SharedPreferences sharedPreferences;

    private EditText lastName;
    private EditText firstName;
    private EditText id;
    private EditText phoneNumber;
    private EditText email;
    private EditText password;
    private EditText creditCard;

    private Button registerButton;
    public static final String MyPreference = "mypref";
    public static final String Email = "email_add";
    public static final String Password = "password";

    public List<Driver> driverList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        backend = BackendFactory.getInstance();
        findViews();
        registerButton.setEnabled(false);
        
        // GIVES THE OPTION TO GO BACK TO MAIN ACTIVITY
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    // when go back button clicked
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private void findViews() {

        lastName = (EditText) findViewById(R.id.lastName);
        lastName.addTextChangedListener(registerTextWatcher);
        
        firstName = (EditText) findViewById(R.id.firstName);
        firstName.addTextChangedListener(registerTextWatcher);

        id = (EditText) findViewById(R.id.Id);
        id.addTextChangedListener(registerTextWatcher);

        phoneNumber = (EditText) findViewById(R.id.PhoneNumber);
        phoneNumber.addTextChangedListener(registerTextWatcher);

        email = (EditText) findViewById(R.id.email);
        email.addTextChangedListener(registerTextWatcher);

        password = (EditText) findViewById(R.id.Password);
        password.addTextChangedListener(registerTextWatcher);

        creditCard = (EditText) findViewById(R.id.CreditCard);
        creditCard.addTextChangedListener(registerTextWatcher);

        sharedPreferences = getSharedPreferences(MyPreference, Context.MODE_PRIVATE);

        registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkFieldsInput(v);
            }
        });
    }

    public void register(View v) throws Exception {

        try {
            registerButton.setEnabled(false);
            String et_fName = firstName.getText().toString();
            String et_lName = lastName.getText().toString();
            String et_phoneNumber = phoneNumber.getText().toString();
            Long et_id = Long.parseLong(id.getText().toString());
            Long et_creditCard = Long.parseLong(creditCard.getText().toString());

            String et_email = email.getText().toString();
            String et_password = password.getText().toString();

            final Driver myDriver = new Driver(et_lName, et_fName, et_id, et_phoneNumber, et_email, et_password, et_creditCard);

            // checks if there is a person with same id
            new AsyncTask<Driver, Void, Void>() {

                @Override
                protected Void doInBackground(Driver... str ) {
                    backend.isDriverInDataBase(myDriver, new Backend.Action() {
                        @Override
                        public void onSuccess() {
                            // adds new account to database
                            backend.addDriver(myDriver, new Backend.Action() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(getBaseContext(), "successfully added you to the database", Toast.LENGTH_LONG).show();
                                    resetView();
                                }

                                @Override
                                public void onFailure(Exception exception) {
                                    Toast.makeText(getBaseContext(), "Error \n" + exception.getMessage(), Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onProgress(String status, double percent) {}
                            });

                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(getBaseContext(), "Error \n" + e.getMessage(), Toast.LENGTH_LONG).show();

                            registerButton.setEnabled(true);
                        }

                        @Override
                        public void onProgress(String status, double percent) {

                        }
                    });
                    return null;
                }
            }.execute(myDriver);


            // Save info in the shared preferences

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Email, et_email);
            editor.putString(Password, et_password);
            editor.commit();
            finish();

        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Error ", Toast.LENGTH_LONG).show();
        }
    }

    private void checkFieldsInput(View v) {

        // Reset errors.
        lastName.setError(null);
        firstName.setError(null);
        id.setError(null);
        phoneNumber.setError(null);
        email.setError(null);
        password.setError(null);
        creditCard.setError(null);

        // Store values at the time of the login attempt.
        String et_fName = firstName.getText().toString();
        String et_lName = lastName.getText().toString();

        String et_phoneNumber = phoneNumber.getText().toString();
        Long et_id = Long.parseLong(id.getText().toString());
        Long et_creditCard = Long.parseLong(creditCard.getText().toString());

        String et_email = email.getText().toString();
        String et_password = password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a last name, if the user entered one.
        if (TextUtils.isEmpty(et_lName)) {
            lastName.setError("enter last name");
            focusView = lastName;
            cancel = true;
        }

        // Check for a first name, if the user entered one.
        if (TextUtils.isEmpty(et_fName)) {
            firstName.setError("enter first name");
            focusView = firstName;
            cancel = true;
        }

        // Check for a id, if the user entered one.
        if (TextUtils.isEmpty(et_id.toString())) {
            id.setError("enter id");
            focusView = id;
            cancel = true;
        }

        // Check for a id, if its to short.
        if (TextUtils.isEmpty(et_id.toString())) {
            id.setError("enter id");
            focusView = id;
            cancel = true;
        }

        if (et_id.toString().length() < 9) {
            id.setError("id is to short");
            focusView = id;
            cancel = true;
        }

        // Check for a phone number, if the user entered one.
        if (TextUtils.isEmpty(et_phoneNumber.toString())) {
            phoneNumber.setError("enter phone number");
            focusView = phoneNumber;
            cancel = true;
        }
        if (!et_phoneNumber.matches("^[0-9]*$") || !et_phoneNumber.startsWith("05")) {
            phoneNumber.setError("invalid phone number");
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
        if (TextUtils.isEmpty(et_email)) {
            email.setError(getString(R.string.error_field_required));
            focusView = email;
            cancel = true;
        } else if (!isEmailValid(et_email)) {
            email.setError(getString(R.string.error_invalid_email));
            focusView = email;
            cancel = true;
        }

        // Check for a credit card, if the user entered one.
        if (TextUtils.isEmpty(et_creditCard.toString())) {
            creditCard.setError("enter a valid credit card number");
            focusView = creditCard;
            cancel = true;
        }if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            try {
                register(v);
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     * a method that allows to enable order button unless all needed information is entered
     */
    private TextWatcher registerTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String lastNameInput = lastName.getText().toString().trim();
            String firstNameInput = firstName.getText().toString().trim();
            String ideInput = id.getText().toString().trim();
            String phoneNumberInput = phoneNumber.getText().toString().trim();
            String emailInput = email.getText().toString().trim();
            String passwordInput = password.getText().toString().trim();
            String creditCardInput = creditCard.getText().toString().trim();

            // all fields ,ust contain somthing inorder to enable register button
            registerButton.setEnabled(!lastNameInput.isEmpty() && !firstNameInput.isEmpty()
                    && !ideInput.isEmpty() && !phoneNumberInput.isEmpty() && !emailInput.isEmpty()
                    && !passwordInput.isEmpty() && !creditCardInput.isEmpty());

        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
    
    private boolean isEmailValid(String email) {
        String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(EMAIL_REGEX);
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private void resetView() {
        lastName.setText(" ");
        firstName.setText(" ");
        id.setText(" ");
        phoneNumber.setText(" ");
        email.setText(" ");
        password.setText(" ");
        creditCard.setText(" ");
    }

}