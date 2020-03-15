package com.childs.childsapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.childs.activity.VaccinationsUI;
import com.childs.blog.ChildNutsBlog;
import com.childs.operations.FirebaseLoadSpinners;
import com.childs.chatbot.ChatBotUI;
import com.childs.dialogs.LanguageDialog;
import com.childs.operations.GeneralFunctions;
import com.childs.operations.LocaleManager;
import com.childs.session.SessionManager;

public class MainActivity extends AppCompatActivity {

    GridLayout mainGrid;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initilize session
        sessionManager = new SessionManager(this);

        //check if user choose language
        if(sessionManager.getStringValue("app_lang")!=null && !sessionManager.getStringValue("app_lang").equals("-1"))
            GeneralFunctions.changeLocale(MainActivity.this,sessionManager.getStringValue("app_lang"));
        else {
            LanguageDialog dialog = new LanguageDialog(this);
            dialog.show();
        }

        setContentView(R.layout.activity_main);

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
                        case 1 :
                            intent = new Intent(MainActivity.this, ChildNutsBlog.class);
                            intent.putExtra("user","");
                            startActivity(intent);
                         break;

                        case 3 :
                            intent = new Intent(MainActivity.this, ChatBotUI.class);
                            intent.putExtra("user","");
                            startActivity(intent);
                            break;

                        case 4:
                            intent = new Intent(MainActivity.this, VaccinationsUI.class);
                            intent.putExtra("user","");
                            startActivity(intent);
                            break;
                    }


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
