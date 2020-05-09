package com.childs.childsapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.childs.activity.posts.ChildFeedingPosts;
import com.childs.activity.posts.NewPostUI;
import com.childs.activity.report_child_health.ReportChildHeathListUI;
import com.childs.activity.vaccination.VaccinationsUI;
import com.childs.activity.posts.PostsListUI;
import com.childs.operations.FirebaseLoadSpinners;
import com.childs.chatbot.ChatBotUI;
import com.childs.dialogs.LanguageDialog;
import com.childs.operations.GeneralFunctions;
import com.childs.operations.LocaleManager;
import com.childs.session.SessionManager;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    GridLayout mainGrid;
    SessionManager sessionManager;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //set title
        setTitle(getResources().getString(R.string.main_screen_lbl));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initilize session
        sessionManager = new SessionManager(this);
        mAuth = FirebaseAuth.getInstance();

        //check if user choose language
        if(sessionManager.getStringValue("app_lang")!=null && !sessionManager.getStringValue("app_lang").equals("-1"))
            GeneralFunctions.changeLocale(MainActivity.this,sessionManager.getStringValue("app_lang"));
        else {
            LanguageDialog dialog = new LanguageDialog(this);
            dialog.show();
        }




        //fill database with spinners
        FirebaseLoadSpinners object = new FirebaseLoadSpinners(this);
        object.insertVaccinationTypes();

        //get icon grids
        mainGrid = (GridLayout) findViewById(R.id.mainGrid);
        //Set Event
        setSingleEvent(mainGrid);
        //setToggleEvent(mainGrid);
    }

    private void setToggleEvent(GridLayout mainGrid) {
        //Loop all child item of Main Grid
        for (int i = 0; i < mainGrid.getChildCount(); i++) {
            //You can see , all child item is CardView , so we just cast object to CardView
            final CardView cardView = (CardView) mainGrid.getChildAt(i);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cardView.getCardBackgroundColor().getDefaultColor() == -1) {
                        //Change background color
                        cardView.setCardBackgroundColor(Color.parseColor("#FF6F00"));
                        Toast.makeText(MainActivity.this, "State : True", Toast.LENGTH_SHORT).show();

                    } else {
                        //Change background color
                        cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                        Toast.makeText(MainActivity.this, "State : False", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void setSingleEvent(GridLayout mainGrid) {
        //Loop all child item of Main Grid
        for (int i = 0; i < mainGrid.getChildCount(); i++) {
            //You can see , all child item is CardView , so we just cast object to CardView
            CardView cardView = (CardView) mainGrid.getChildAt(i);
            final int finalIndex = i;

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = null;
                    switch (finalIndex)
                    {
                        case 0 :
                            //Nuts Activity
                            intent = new Intent(MainActivity.this, PostsListUI.class);
                            intent.putExtra("postCategory","2");
                            startActivity(intent);
                            break;
                        case 1 :
                            //Nuts Activity
                            intent = new Intent(MainActivity.this, ChildFeedingPosts.class);
                            intent.putExtra("postCategory","1");
                            startActivity(intent);
                         break;

                        case 2 :
                            //Education Activity
                            intent = new Intent(MainActivity.this, VaccinationsUI.class);
                            intent.putExtra("postCategory","2");
                            startActivity(intent);
                            break;

                        case 3 :
                            intent = new Intent(MainActivity.this, ChatBotUI.class);
                            intent.putExtra("user","");
                            startActivity(intent);
                            break;

                        case 4:
                            intent = new Intent(MainActivity.this, ReportChildHeathListUI.class);
                            intent.putExtra("key","");
                            startActivity(intent);
                            break;
                    }
                }
            });
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
            Intent intent = new Intent(MainActivity.this, SettingsPreferencesActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.add_new_post) {
            startActivity(new Intent(MainActivity.this, NewPostUI.class));
        } else if (id == R.id.exit_item) {

            mAuth.signOut();
            Intent logouIntent = new Intent(MainActivity.this, LoginUI.class);
            logouIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(logouIntent);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context base) {
        sessionManager = new SessionManager(base);
        super.attachBaseContext(LocaleManager.setLocale(base,sessionManager.getStringValue("app_lang")));
    }


}
