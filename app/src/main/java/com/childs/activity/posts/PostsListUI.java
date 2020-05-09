package com.childs.activity.posts;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.childs.objects.Post;
import com.childs.operations.GeneralFunctions;
import com.childs.operations.LocaleManager;
import com.childs.session.SessionManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class PostsListUI extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseRecyclerAdapter<Post, BlogViewHolder> firebaseRecyclerAdapter;
    SessionManager sessionManager;
    String postCategory = "-1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.childs_nuts_blog_main);
        try
        {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            //get post category
            postCategory = getIntent().getExtras().getString("postCategory");

            //sset activity title
            if(postCategory.equals("1"))
                setTitle(getResources().getString(R.string.child_nuts_title));
            else if(postCategory.equals("2"))
                setTitle(getResources().getString(R.string.education_title));

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
                        Intent loginIntent = new Intent(PostsListUI.this, LoginUI.class);
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(loginIntent);
                    }
                }
            };

            //get posts list
            getPosts(postCategory);

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

        if (firebaseRecyclerAdapter!= null) {
            firebaseRecyclerAdapter.stopListening();
        }
    }
    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String title) {
            TextView post_title = mView.findViewById(R.id.post_title_txtview);
            post_title.setText(title);
        }

        public void setDesc(String desc) {
            TextView post_desc = mView.findViewById(R.id.post_desc_txtview);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                post_desc.setText(Html.fromHtml(desc, Html.FROM_HTML_MODE_COMPACT));
            } else {
                post_desc.setText(Html.fromHtml(desc));
            }
        }

        public void setImageUrl(Context ctx, String imageUrl) {
            ImageView post_image = mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(imageUrl).into(post_image);
        }

        public void setUserName(String userName) {
            TextView postUserName = mView.findViewById(R.id.post_user);
            postUserName.setText(userName);
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
            startActivity(new Intent(PostsListUI.this, NewPostUI.class));
        } else if (id == R.id.action_settings) {
            mAuth.signOut();
            Intent logouIntent = new Intent(PostsListUI.this, RegisterNewUserUI.class);
            logouIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(logouIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    //get postins details
    private void getPosts(String postCategory)
    {
        //get all posts which type of nuts
        //1 eqlual nuts posts
        Query query = null;


        if(postCategory.equals("1")) {
            //get post category
            String age_id = getIntent().getExtras().getString("age_id");
            query = mDatabase.child("posts").orderByChild("cat_age").equalTo(postCategory+"_"+age_id);

        }
        else
            query = mDatabase.child("posts").orderByChild("category").equalTo(postCategory);

        FirebaseRecyclerOptions<Post> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .build();

        //create firebase recycle adapter
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, BlogViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull BlogViewHolder viewHolder, int position, @NonNull Post model) {
                //bind model to item view
                final String post_key = getRef(position).getKey().toString();
                viewHolder.setTitle(sessionManager.getStringValue("app_lang").equals("en") ? model.getEn_title() :model.getAr_title());
                viewHolder.setDesc(sessionManager.getStringValue("app_lang").equals("en") ? model.getEn_desc() :model.getAr_desc());
                viewHolder.setImageUrl(getApplicationContext(), model.getPost_photo_url());
                viewHolder.setUserName(model.getUsername());
                //on click open post
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent singleActivity = new Intent(PostsListUI.this, ShowPost.class);
                        singleActivity.putExtra("PostID", post_key);
                        startActivity(singleActivity);
                    }
                });
            }

            @Override
            public BlogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item, parent, false);

                return new BlogViewHolder(view);
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    protected void attachBaseContext(Context base) {
        sessionManager = new SessionManager(base);
        super.attachBaseContext(LocaleManager.setLocale(base,sessionManager.getStringValue("app_lang")));
    }
}