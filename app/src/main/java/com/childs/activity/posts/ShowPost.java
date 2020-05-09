package com.childs.activity.posts;

import android.content.Context;
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
import com.childs.operations.LocaleManager;
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
    private TextView singleTitle, singleDesc,publish_date;
    String post_key = null;
    private DatabaseReference mDatabase;
    private Button deleteBtn;
    private FirebaseAuth mAuth;
    private YouTubePlayerView youTubePlayerView1;
    private YouTubePlayerView youTubePlayerView2;

    String post_photo_url = "";

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_show_post_ui);

        setTitle(getResources().getString(R.string.post_details_lbl));

        sessionManager = new SessionManager(this);
        singelImage = (ImageView) findViewById(R.id.singleImageview);
        singleTitle = (TextView) findViewById(R.id.singleTitle);
        singleDesc = (TextView) findViewById(R.id.singleDesc);
        publish_date = (TextView) findViewById(R.id.publish_date);

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
                    final String publish_date_val = (String) dataSnapshot.child("date").getValue();
                    post_photo_url = (String) dataSnapshot.child("post_photo_url").getValue();
                    //String post_image = (String) dataSnapshot.child("imageUrl").getValue();

                    String post_uid = (String) dataSnapshot.child("uid").getValue();
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
                    publish_date.setText(getResources().getString(R.string.publish_date_lbl)+" "+publish_date_val);

                    //set description as html
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        singleDesc.setText(Html.fromHtml(post_desc, Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        singleDesc.setText(Html.fromHtml(post_desc));
                    }

                    //get image
                    if(!post_photo_url.equals(""))
                    {
                        Picasso.with(ShowPost.this).load(post_photo_url).into(singelImage);
                    }
                    else {
                        Picasso.with(ShowPost.this).load("https://arabiaparenting.firstcry.com/wp-content/uploads/2019/09/Weight-Gain-Foods-for-Babies-and-Kids-660x330.jpg").into(singelImage);
                    }

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

    @Override
    protected void attachBaseContext(Context base) {
        sessionManager = new SessionManager(base);
        super.attachBaseContext(LocaleManager.setLocale(base, sessionManager.getStringValue("app_lang")));
    }
}