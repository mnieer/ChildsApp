package com.childs.activity.posts;


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
import com.childs.childsapp.LoginUI;
import com.childs.childsapp.R;
import com.childs.childsapp.RegisterNewUserUI;
import com.childs.objects.Age;
import com.childs.operations.GeneralFunctions;
import com.childs.operations.LocaleManager;
import com.childs.session.SessionManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ChildFeedingPosts extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseRecyclerAdapter<Age, ChildFeedingPosts.AgesViewHolder> firebaseRecyclerAdapter;
    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feeding_child_ages_list_ui);

        try
        {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            setTitle(getResources().getString(R.string.child_feeding_posts_by_ages_lbl));
            //initialize recyclerview and FIrebase objects
            recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setHasFixedSize(true);
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mAuth = FirebaseAuth.getInstance();
            sessionManager = new SessionManager(this);
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (mAuth.getCurrentUser() == null) {
                        Intent loginIntent = new Intent(ChildFeedingPosts.this, LoginUI.class);
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(loginIntent);
                    }
                }
            };

            //get child ages list
            getChildAgesList();

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

    public static class AgesViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public AgesViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setAge(String ageValue) {
            TextView age = mView.findViewById(R.id.itemDescription);
            age.setText(ageValue);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.add_new_post) {
            startActivity(new Intent(ChildFeedingPosts.this, NewPostUI.class));
        } else if (id == R.id.action_settings) {
            mAuth.signOut();
            Intent logouIntent = new Intent(ChildFeedingPosts.this, RegisterNewUserUI.class);
            logouIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(logouIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    //get postins details
    private void getChildAgesList() {
        Query query = mDatabase.child("child_ages");
        FirebaseRecyclerOptions<Age> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Age>()
                .setQuery(query, Age.class)
                .build();

        //create firebase recycle adapter
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Age, ChildFeedingPosts.AgesViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ChildFeedingPosts.AgesViewHolder viewHolder, int position, @NonNull final Age model) {
                //bind model to item view
                final String key = getRef(position).getKey().toString();

                viewHolder.setAge(sessionManager.getStringValue("app_lang").equals("en") ? model.getEn_name() : model.getAr_name());
                //viewHolder.setId(model.getId());
                //on click open post
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent singleActivity = new Intent(ChildFeedingPosts.this, PostsListUI.class);
                        singleActivity.putExtra("age_id", model.getId());
                        singleActivity.putExtra("postCategory", "1");

                        //GeneralFunctions.populateToastMsg(getApplicationContext(),"Age is "+model.getAge(),true);
                        startActivity(singleActivity);
                    }
                });
            }

            @Override
            public ChildFeedingPosts.AgesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feeding_child_ages_list_item, parent, false);

                return new ChildFeedingPosts.AgesViewHolder(view);
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    protected void attachBaseContext(Context base) {
        sessionManager = new SessionManager(base);
        super.attachBaseContext(LocaleManager.setLocale(base, sessionManager.getStringValue("app_lang")));
    }
}
