package com.childs.activity.report_child_health;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.childs.activity.posts.NewPostUI;
import com.childs.childsapp.LoginUI;
import com.childs.childsapp.R;
import com.childs.childsapp.RegisterNewUserUI;
import com.childs.objects.ChildHealthReport;
import com.childs.operations.LocaleManager;
import com.childs.session.SessionManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ReportChildHeathListUI extends AppCompatActivity {
    SessionManager sessionManager;
    String age = "";
    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseRecyclerAdapter<ChildHealthReport, ReportChildHeathListUI.ChildsHeathReportsViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_childhealth_list_ui);
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            setTitle(getResources().getString(R.string.report_child_health_form));
            //initialize recyclerview and FIrebase objects
            recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setHasFixedSize(true);
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mAuth = FirebaseAuth.getInstance();
            sessionManager = new SessionManager(this);
            age = getIntent().getExtras().getString("age");

            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (mAuth.getCurrentUser() == null) {
                        Intent loginIntent = new Intent(ReportChildHeathListUI.this, LoginUI.class);
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(loginIntent);
                    }
                }
            };

            //get reports from database
            getReportsList();

        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.process_failed_msg), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.stopListening();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.report_child_health_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add_new_record) {
            Intent addRecordIntent = new Intent(ReportChildHeathListUI.this, ReportChildHealthDetailsUI.class);
            addRecordIntent.putExtra("key", "-1");
            startActivity(addRecordIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    //get reports from database
    private void getReportsList() {
        Query query = mDatabase.child("ChildsHeathReports");//.orderByChild("age").equalTo(age);
        FirebaseRecyclerOptions<ChildHealthReport> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<ChildHealthReport>()
                .setQuery(query, ChildHealthReport.class)
                .build();

        //query.equalTo(age);

        //create firebase recycle adapter
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ChildHealthReport, ReportChildHeathListUI.ChildsHeathReportsViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull  ReportChildHeathListUI.ChildsHeathReportsViewHolder viewHolder, int position, @NonNull ChildHealthReport model) {
                //bind model to item view
                final String key = getRef(position).getKey().toString();
                viewHolder.setSymptomesDesc(model.getSymptomes());
                viewHolder.setStatusDesc(model.getStatus());
                //on click open post
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent singleActivity = new Intent(ReportChildHeathListUI.this, ReportChildHealthDetailsUI.class);
                        singleActivity.putExtra("key", key);
                        startActivity(singleActivity);
                    }
                });
            }

            @Override
            public ReportChildHeathListUI.ChildsHeathReportsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_childhealth_list_item, parent, false);

                return new ReportChildHeathListUI.ChildsHeathReportsViewHolder (view);
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    protected void attachBaseContext(Context base) {
        sessionManager = new SessionManager(base);
        super.attachBaseContext(LocaleManager.setLocale(base, sessionManager.getStringValue("app_lang")));
    }

    public static class ChildsHeathReportsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public ChildsHeathReportsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setSymptomesDesc(String symptomes) {
            TextView symptomesTV = mView.findViewById(R.id.symptomes);
            symptomesTV.setText(symptomes);
        }

        public void setStatusDesc(String status) {
            TextView statusDesc = mView.findViewById(R.id.status);
            statusDesc.setText(status);
        }
    }
}