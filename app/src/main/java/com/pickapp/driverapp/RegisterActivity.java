package com.pickapp.driverapp;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import model.model.backend.Backend;
import model.model.datasource.BackendFactory;
import model.model.datasource.Firebase_DBManager;
import model.model.entities.Driver;

public class RegisterActivity extends AppCompatActivity {

    private Backend backend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        backend = BackendFactory.getInstance();

    }

    public void register(View v) throws Exception {

        try {



            // TODO: register in fireBase to.
            Driver myDriver = new Driver(et_fName, et_lName, longPassword, et_phoneNumber, et_email, et_creditCard);

            backend.addDriver(myDriver, new Firebase_DBManager.Action<Long>() {
                @Override
                public void onSuccess(Long obj) {
                    Toast.makeText(getBaseContext(), "successfully addded you to the database", Toast.LENGTH_LONG).show();
                    resetView();
                }

                @Override
                public void onFailure(Exception exception) {
                    Toast.makeText(getBaseContext(), "Error \n" + exception.getMessage(), Toast.LENGTH_LONG).show();
                    //resetView();
                }

                @Override
                public void onProgress(String status, double percent) {

                }
            });
          /*
          // Save info in the shared prefrences
            //TODO actually make these in the register activity
            String et_fName = "john";
            String et_lName = "Smith";
            long et_phoneNumber = 34565432;
            long et_creditCard = 123456789;

            String et_email = mEmailView.getText().toString();
            String et_password = mPasswordView.getText().toString();
            long longPassword = Long.parseLong(et_password);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Email, et_email);
            editor.putString(Password, et_password);
            editor.commit();
            Toast.makeText(getApplicationContext(), "Your information was saved succesfully!", Toast.LENGTH_SHORT).show();
            */
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Error ", Toast.LENGTH_LONG).show();
        }
    }

    private void resetView() {

    }

}
