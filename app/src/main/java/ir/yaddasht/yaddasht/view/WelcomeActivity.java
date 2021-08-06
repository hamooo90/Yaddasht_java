package ir.yaddasht.yaddasht.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ir.yaddasht.yaddasht.R;

import ir.yaddasht.yaddasht.retrofit.ApiMethode;

public class WelcomeActivity extends AppCompatActivity {
    Button login, register, offline;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TOKEN = "token";
    public static final String ISOFFLINE = "isoffline";

    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        login = findViewById(R.id.login_btn_go);
        register = findViewById(R.id.registe_btn_go);
        offline = findViewById(R.id.offline_btn_go);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        if (!sharedPreferences.getString(TOKEN, "").isEmpty() || sharedPreferences.getBoolean(ISOFFLINE, false)) {
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            ///wake up heroku///
            ApiMethode apiMethode = new ApiMethode(getApplication());
            apiMethode.getNotes();
            ///////////////
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WelcomeActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

        offline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor = sharedPreferences.edit();
                editor.putBoolean(ISOFFLINE, true);
                editor.apply();
                Intent i = new Intent(WelcomeActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(i);
            }
        });


    }
}