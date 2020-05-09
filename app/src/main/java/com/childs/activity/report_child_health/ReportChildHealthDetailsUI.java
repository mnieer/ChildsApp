package com.childs.activity.report_child_health;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.childs.activity.posts.PostsListUI;
import com.childs.childsapp.R;
import com.childs.operations.GeneralFunctions;
import com.childs.operations.LocaleManager;
import com.childs.session.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReportChildHealthDetailsUI extends AppCompatActivity {

    private EditText child_name, child_age, date, medical_center_phone, medical_center_name,
            symptomes, notes, drName, weight, temprature, height, parentsPhone;
    private RadioButton radioSexButton;
    private RadioGroup radioSexGroup;
    private Button submitBtn, cancelBtn;
    private String gender;
    String key = null;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    SessionManager sessionManager;
    private StorageReference storage;
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;
    private DatabaseReference mDatabaseUsers;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_child_heath_ui);
        sessionManager = new SessionManager(this);

        key = getIntent().getExtras().getString("key");

        child_name = (EditText) findViewById(R.id.child_name_tb);
        child_age = (EditText) findViewById(R.id.child_age_tb);
        date = (EditText) findViewById(R.id.date_tb);
        medical_center_phone = (EditText) findViewById(R.id.medical_center_phone_tb);
        medical_center_name = (EditText) findViewById(R.id.medical_center_name_tb);
        symptomes = (EditText) findViewById(R.id.symptoms_tb);
        notes = (EditText) findViewById(R.id.notes_tb);
        drName = (EditText) findViewById(R.id.dr_name_tb);
        weight = (EditText) findViewById(R.id.weight_tb);
        temprature = (EditText) findViewById(R.id.temprature_tb);
        height = (EditText) findViewById(R.id.height_tb);
        parentsPhone = (EditText) findViewById(R.id.parants_phone_no_tb);
        radioSexGroup = (RadioGroup) findViewById(R.id.child_sex);
        submitBtn = (Button) findViewById(R.id.submitBtn_lbl);
        cancelBtn = (Button) findViewById(R.id.cancel_btn);

        //SET CURRENT DATE
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH);
        String todayString = formatter.format(todayDate);
        date.setText(todayString);

        storage = FirebaseStorage.getInstance().getReference();
        databaseRef = database.getInstance().getReference().child("ChildsHeathReports");
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        //submit data into database
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get gender
                int selectedId = radioSexGroup.getCheckedRadioButtonId();
                radioSexButton = (RadioButton) findViewById(selectedId);
                gender = radioSexButton.getText().toString();

                try {

                    GeneralFunctions.populateToastMsg(getApplicationContext(), getResources().getString(R.string.processing_lbl), true);
                    final String childNameVal = child_name.getText().toString().trim();
                    final String childAgeVal = child_age.getText().toString().trim();
                    final String childGender = gender;
                    final String drNameVal = drName.getText().toString().trim();
                    final String medicalCenterPhoneVal = medical_center_phone.getText().toString().trim();
                    final String medicalCenterNameVal = medical_center_name.getText().toString().trim();
                    final String notesVal = notes.getText().toString().trim();
                    final String weightVal = weight.getText().toString().trim();
                    final String temparatureVal = temprature.getText().toString().trim();
                    final String heightVal = height.getText().toString().trim();
                    final String symptomesVal = symptomes.getText().toString().trim();
                    final String parentsPhoneVal = parentsPhone.getText().toString().trim();
                    final String dateVal = date.getText().toString().trim();

                    // do a check for empty fields
                    //if (!TextUtils.isEmpty(PostDesc) && !TextUtils.isEmpty(PostTitle)) {
                    final DatabaseReference newRecord = databaseRef.push();

                    //adding post contents to database reference
                    mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newRecord.child("child_name").setValue(childNameVal);
                            newRecord.child("child_gender").setValue(childGender);
                            newRecord.child("child_age").setValue(childAgeVal);
                            newRecord.child("date").setValue(dateVal);
                            newRecord.child("medical_center_phone").setValue(medicalCenterPhoneVal);
                            newRecord.child("medical_center_name").setValue(medicalCenterNameVal);
                            newRecord.child("symptomes").setValue(symptomesVal);
                            newRecord.child("notes").setValue(notesVal);
                            newRecord.child("drName").setValue(drNameVal);
                            newRecord.child("weight").setValue(weightVal);
                            newRecord.child("drName").setValue(drNameVal);
                            newRecord.child("temprature").setValue(temparatureVal);
                            newRecord.child("height").setValue(heightVal);
                            newRecord.child("parentsPhone").setValue(parentsPhoneVal);

                            newRecord.child("uid").setValue(mCurrentUser.getUid());
                            newRecord.child("username").setValue(dataSnapshot.child("name").getValue())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Intent intent = new Intent(ReportChildHealthDetailsUI.this, ReportChildHeathListUI.class);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                } catch (Exception ex) {
                    ex.printStackTrace();
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.process_failed_msg), Toast.LENGTH_LONG).show();
                }


            }
        });

        //back button
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportChildHealthDetailsUI.this, ReportChildHeathListUI.class);
                startActivity(intent);
            }
        });

        //if key equal -1 mean add new record
        if (!key.equals("-1")) {

            //get data from tale
            mDatabase = FirebaseDatabase.getInstance().getReference().child("ChildsHeathReports");
            mAuth = FirebaseAuth.getInstance();
            mDatabase.child(key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String child_nameVal = (String) dataSnapshot.child("child_name").getValue();
                    String child_genderVal = (String) dataSnapshot.child("child_gender").getValue();
                    String child_ageVal = (String) dataSnapshot.child("child_age").getValue();
                    String dateVal = (String) dataSnapshot.child("date").getValue();
                    String medical_center_phoneVal = (String) dataSnapshot.child("medical_center_phone").getValue();
                    String medical_center_nameVal = (String) dataSnapshot.child("medical_center_name").getValue();
                    String symptomesVal = (String) dataSnapshot.child("symptomes").getValue();
                    String notesVal = (String) dataSnapshot.child("notes").getValue();
                    String weightVal = (String) dataSnapshot.child("weight").getValue();
                    String drNameVal = (String) dataSnapshot.child("drName").getValue();
                    String tempratureVal = (String) dataSnapshot.child("temprature").getValue();
                    String heightVal = (String) dataSnapshot.child("height").getValue();
                    String parentsPhoneVal = (String) dataSnapshot.child("parentsPhone").getValue();

                    if(getResources().getString(R.string.male_lbl).equals(child_genderVal))
                        radioSexGroup.check(R.id.male);
                    else
                        radioSexGroup.check(R.id.female);

                    child_name.setText(child_nameVal);
                    child_age.setText(child_ageVal);
                    date.setText(dateVal);
                    drName.setText(drNameVal);
                    medical_center_name.setText(medical_center_nameVal);
                    medical_center_phone.setText(medical_center_phoneVal);
                    symptomes.setText(symptomesVal);
                    notes.setText(notesVal);
                    weight.setText(weightVal);
                    temprature.setText(tempratureVal);
                    height.setText(heightVal);
                    parentsPhone.setText(parentsPhoneVal);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        sessionManager = new SessionManager(base);
        super.attachBaseContext(LocaleManager.setLocale(base,sessionManager.getStringValue("app_lang")));
    }
}