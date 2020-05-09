package com.childs.childsapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.childs.operations.LocaleManager;
import com.childs.session.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegisterNewUserUI extends AppCompatActivity {
    private Button registerBtn;
    private EditText emailField, usernameField, passwordField, birthDateField;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView loginTxtView;
    private SessionManager sessionManager;
    private final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_new_user_ui);

        loginTxtView = (TextView) findViewById(R.id.loginTxtView);
        registerBtn = (Button) findViewById(R.id.registerBtn);
        emailField = (EditText) findViewById(R.id.emailField);
        usernameField = (EditText) findViewById(R.id.usernameField);
        passwordField = (EditText) findViewById(R.id.passwordField);
        birthDateField = (EditText) findViewById(R.id.birthDateField);
        sessionManager = new SessionManager(this);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateLabel();
            }

        };

        //on click date field
        birthDateField.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(RegisterNewUserUI.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        loginTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterNewUserUI.this, LoginUI.class));
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RegisterNewUserUI.this, getResources().getString(R.string.loading_lbl), Toast.LENGTH_LONG).show();
                final String username = usernameField.getText().toString().trim();
                final String email = emailField.getText().toString().trim();
                final String password = passwordField.getText().toString().trim();
                final String birthDate = birthDateField.getText().toString().trim();
                if (!TextUtils.isEmpty(birthDate) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            try
                            {
                                String user_id = mAuth.getCurrentUser().getUid();
                                DatabaseReference current_user_db = mDatabase.child(user_id);
                                current_user_db.child("Username").setValue(username);
                                current_user_db.child("Image").setValue("Default");
                                current_user_db.child("BirthDate").setValue(birthDate);
                                Toast.makeText(RegisterNewUserUI.this, getResources().getString(R.string.registration_success_msg), Toast.LENGTH_LONG).show();
                                Intent regIntent = new Intent(RegisterNewUserUI.this, ProfileUI.class);
                                regIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(regIntent);
                            }
                            catch (Exception ex)
                            {
                                Toast.makeText(RegisterNewUserUI.this, getResources().getString(R.string.process_failed_msg)+" "+ ex.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {

                    Toast.makeText(RegisterNewUserUI.this, getResources().getString(R.string.fill_all_fields_lbl), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateDateLabel() {
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        birthDateField.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    protected void attachBaseContext(Context base) {
        sessionManager = new SessionManager(base);
        super.attachBaseContext(LocaleManager.setLocale(base,sessionManager.getStringValue("app_lang")));
    }

}