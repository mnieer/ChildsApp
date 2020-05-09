package com.childs.activity.posts;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.childs.childsapp.MainActivity;
import com.childs.childsapp.R;
import com.childs.operations.GeneralFunctions;
import com.childs.session.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;

public class ShowPost extends AppCompatActivity {

    private ImageView singelImage;
    private TextView singleTitle, singleDesc;
    String post_key = null;
    private DatabaseReference mDatabase;
    private Button deleteBtn;
    private FirebaseAuth mAuth;
    private YouTubePlayerView youTubePlayerView1;
    private YouTubePlayerView youTubePlayerView2;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_show_post_ui);
        sessionManager = new SessionManager(this);
        singelImage = (ImageView) findViewById(R.id.singleImageview);
        singleTitle = (TextView) findViewById(R.id.singleTitle);
        singleDesc = (TextView) findViewById(R.id.singleDesc);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("posts");
        post_key = getIntent().getExtras().getString("PostID");
        deleteBtn = (Button) findViewById(R.id.deleteBtn);
        youTubePlayerView1 = (YouTubePlayerView) findViewById(R.id.youtubePlayerView1);
        youTubePlayerView2 = (YouTubePlayerView) findViewById(R.id.youtubePlayerView2);

        mAuth = FirebaseAuth.getInstance();
        deleteBtn.setVisibility(View.INVISIBLE);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDatabase.child(post_key).removeValue();

                Intent mainintent = new Intent(ShowPost.this, MainActivity.class);
                startActivity(mainintent);
            }
        });


        mDatabase.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {

                    String post_title = (String) dataSnapshot.child(sessionManager.getStringValue("app_lang").equals("ar") ? "ar_title" : "en_title").getValue();
                    String post_desc = (String) dataSnapshot.child(sessionManager.getStringValue("app_lang").equals("ar") ? "ar_desc" : "en_desc").getValue();
                    final String youtubeLink1 = (String) dataSnapshot.child("youtube_link1").getValue();
                    final String youtubeLink2 = (String) dataSnapshot.child("youtube_link2").getValue();
                    //String post_image = (String) dataSnapshot.child("imageUrl").getValue();

                    String post_uid = (String) dataSnapshot.child("uid").getValue();
                    GeneralFunctions.populateToastMsg(getApplicationContext(),youtubeLink1+" "+youtubeLink2,true);
                    if (youtubeLink1!=null && !youtubeLink1.equals("")) {
                        youTubePlayerView1.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                            @Override
                            public void onReady(YouTubePlayer youTubePlayer) {
                                super.onReady(youTubePlayer);
                                youTubePlayer.loadVideo(youtubeLink1, 0);
                            }
                        });
                    }
                    else
                    {
                        youTubePlayerView1.setVisibility(View.GONE);
                    }

                    if (youtubeLink2!=null && !youtubeLink2.equals("")) {
                        youTubePlayerView2.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                            @Override
                            public void onReady(YouTubePlayer youTubePlayer) {
                                super.onReady(youTubePlayer);
                                youTubePlayer.loadVideo(youtubeLink1, 0);
                            }
                        });
                    }
                    else
                    {
                        youTubePlayerView2.setVisibility(View.GONE);
                    }


                    singleTitle.setText(post_title);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        singleDesc.setText(Html.fromHtml(post_desc, Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        singleDesc.setText(Html.fromHtml(post_desc));
                    }

                    Picasso.with(ShowPost.this).load("https://images.pexels.com/photos/35537/child-children-girl-happy.jpg?cs=srgb&dl=person-love-people-summer-35537.jpg").into(singelImage);
                    if (mAuth.getCurrentUser().getUid().equals(post_uid)) {

                        deleteBtn.setVisibility(View.VISIBLE);
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}