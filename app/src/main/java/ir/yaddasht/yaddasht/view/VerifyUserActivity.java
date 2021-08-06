package ir.yaddasht.yaddasht.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import ir.yaddasht.yaddasht.R;

import ir.yaddasht.yaddasht.retrofit.ApiMethode;
import ir.yaddasht.yaddasht.model.retro.User;
import com.google.android.material.snackbar.Snackbar;

public class VerifyUserActivity extends AppCompatActivity {
    ApiMethode apiMethode;
    boolean isKeyboardShowing = false;
    ImageButton back_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_user);
        forceRTLIfSupported();

        apiMethode = new ApiMethode(getApplication());

        LottieAnimationView loadbar = findViewById(R.id.lottie_loadbar);

        EditText verifyCodeTextBox = findViewById(R.id.verification_textBox);
        Button verifyButton = findViewById(R.id.verify_button);
        back_btn = findViewById(R.id.back_button);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        MutableLiveData<Integer> isVerified = apiMethode.getIsVerified();
        isVerified.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer code) {
                verifyButton.setEnabled(true);
                loadbar.setVisibility(View.INVISIBLE);


                if(code == 200){
                    User user = ApiMethode.currentUser;
                    apiMethode.login(user);
                } else if(code == 401){
                    Snackbar.make(findViewById(R.id.root_layout),"کاربر وجود ندارد!",Snackbar.LENGTH_SHORT)
                            .setTextColor(Color.BLACK)
                            .setBackgroundTint(ContextCompat.getColor(getApplicationContext(),R.color.snackLight))
                            .show();
                } else if(code == 406){
                    verifyCodeTextBox.setError("کد تایید اشتباه است");
                } else if(code == 408){
                    Snackbar.make(findViewById(R.id.root_layout),"خطا در ارتباط",Snackbar.LENGTH_SHORT)
                            .setTextColor(Color.BLACK)
                            .setBackgroundTint(ContextCompat.getColor(getApplicationContext(),R.color.snackLight))
                            .show();
                } else {
                    Snackbar.make(findViewById(R.id.root_layout),"مشکلی پیش آمد!",Snackbar.LENGTH_SHORT)
                            .setTextColor(Color.BLACK)
                            .setBackgroundTint(ContextCompat.getColor(getApplicationContext(),R.color.snackLight))
                            .show();
                }
            }
        });


        MutableLiveData<Integer> isSignedIn = apiMethode.getIsLoggedIn();
        isSignedIn.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer code) {

//                loadbar.setVisibility(View.INVISIBLE);
                if (code == 200) {
//                    login_btn.setEnabled(true);
                    Intent intent = new Intent(VerifyUserActivity.this, MainActivity.class);
                    intent.putExtra("FROM_VERIFY",true);////////////
                    // clear activity stack
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }else if(code == 401){
//                    login_btn.setEnabled(true);
                    Snackbar.make(findViewById(R.id.root_layout),"کاربر وجود ندارد!",Snackbar.LENGTH_SHORT)
                            .setTextColor(Color.BLACK)
                            .setBackgroundTint(ContextCompat.getColor(getApplicationContext(),R.color.snackLight))
                            .show();
//                    password_EditText.setError("The email or password you entered is incorrect.");
                }else if(code == 403){
//                    login_btn.setEnabled(true);
//                    Toast.makeText(getApplicationContext(),"verify user",Toast.LENGTH_SHORT).show();
//                    password_EditText.setError("The email or password you entered is incorrect.");
                }else if(code == 408){
//                    login_btn.setEnabled(true);
                    Snackbar.make(findViewById(R.id.root_layout),"خطا در ارتباط",Snackbar.LENGTH_SHORT)
                            .setTextColor(Color.BLACK)
                            .setBackgroundTint(ContextCompat.getColor(getApplicationContext(),R.color.snackLight))
                            .show();
                    //                    password_EditText.setError("The email or password you entered is incorrect.");
                }
                else {
//                    login_btn.setEnabled(true);
                    Snackbar.make(findViewById(R.id.root_layout),"مشکلی پیش آمد!",Snackbar.LENGTH_SHORT)
                            .setTextColor(Color.BLACK)
                            .setBackgroundTint(ContextCompat.getColor(getApplicationContext(),R.color.snackLight))
                            .show();
//                    password_EditText.setError("The email or password you entered is incorrect.");
                }
            }
        });

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!verifyCodeTextBox.getText().toString().isEmpty()){
                    User user = ApiMethode.currentUser;
                    user.setUniqueString(verifyCodeTextBox.getText().toString());
                    apiMethode.verify(user);
                    verifyButton.setEnabled(false);
                    closeKeyboard();
                    loadbar.setVisibility(View.VISIBLE);
                }
            }
        });

        TextView resendButton = findViewById(R.id.resend_code_button);
        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiMethode.resend(ApiMethode.currentUser);
            }
        });
        MutableLiveData<Integer> resend = apiMethode.getIsResend();
        resend.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer code) {
                if(code == 200){
                    Snackbar.make(findViewById(R.id.root_layout),"ارسال شد",Snackbar.LENGTH_SHORT)
//                            .setTextColor(Color.BLACK)
//                            .setBackgroundTint(ContextCompat.getColor(getApplicationContext(),R.color.snackLight))
                            .show();
                } else {
//                    Toast.makeText(getApplicationContext(),"Resend failed",Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void forceRTLIfSupported() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }
}