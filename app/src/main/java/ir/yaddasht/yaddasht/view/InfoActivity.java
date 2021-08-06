package ir.yaddasht.yaddasht.view;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.appbar.MaterialToolbar;

import ir.yaddasht.yaddasht.R;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        forceRTLIfSupported();

        MaterialToolbar topToolbar = findViewById(R.id.topAppBar);
        topToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LinearLayout contactMe = findViewById(R.id.contact_me_ll);
        LinearLayout siteAddress = findViewById(R.id.site_address_ll);
        contactMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Create the Intent */
                final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

                /* Fill it with Data */
                emailIntent.setType("plain/text");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"hamedvakhide@gmail.com"});

                /* Send it off to the Activity-Chooser */
                startActivity(Intent.createChooser(emailIntent, "ارسال ایمیل..."));

            }
        });

        siteAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.yaddashtapp.ir";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void forceRTLIfSupported() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

}
