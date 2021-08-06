package ir.yaddasht.yaddasht.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.RenderMode;
import ir.yaddasht.yaddasht.R;

import ir.yaddasht.yaddasht.retrofit.ApiMethode;
import ir.yaddasht.yaddasht.model.retro.User;
import com.google.android.material.snackbar.Snackbar;

public class RegisterActivity extends AppCompatActivity {

    EditText name_EditText, email_EditText, password_EditText, password_repeat_EditText;
    ImageButton back_btn;
    Button register_btn;
    LottieAnimationView loadbar;

    ApiMethode apiMethode;

    boolean isKeyboardShowing = false;


    MutableLiveData<Integer> isRegistered;
    MutableLiveData<Boolean> isSignedIn;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TOKEN = "token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        forceRTLIfSupported();

        ConstraintLayout contentView = findViewById(R.id.root_layout);
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                contentView.getWindowVisibleDisplayFrame(r);
                int screenHeight = contentView.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

//                Log.d("DAGDAG", "keypadHeight = " + keypadHeight);
                ImageView imgBackGround = findViewById(R.id.img_background);
                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened
                    if (!isKeyboardShowing) {
                        isKeyboardShowing = true;
                        back_btn.setVisibility(View.INVISIBLE);
                        imgBackGround.setVisibility(View.INVISIBLE);
                    }
                } else {
                    // keyboard is closed
                    if (isKeyboardShowing) {
                        isKeyboardShowing = false;
                        back_btn.setVisibility(View.VISIBLE);
                        imgBackGround.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        apiMethode = new ApiMethode(getApplication());


        TextView btn_policy = findViewById(R.id.privacy_policy_txt_button);
        btn_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, PrivacyActivity.class);
                startActivity(intent);

            }
        });

        name_EditText = findViewById(R.id.name_edit_text);
        email_EditText = findViewById(R.id.email_edit_text);
        password_EditText = findViewById(R.id.pass_edit_text);
        password_repeat_EditText = findViewById(R.id.pass_repeat_edit_text);
        password_EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (password_EditText.getText().toString().isEmpty()) {
                    password_EditText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                } else {
                    password_EditText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

                }
            }
        });

        password_repeat_EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(password_repeat_EditText.getText().toString().isEmpty()){
                    password_repeat_EditText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                } else {
                    password_repeat_EditText.setTextAlignment((View.TEXT_ALIGNMENT_TEXT_START));
                }

            }
        });

        back_btn = findViewById(R.id.back_button);
        register_btn = findViewById(R.id.register_button);

        loadbar = findViewById(R.id.lottie_loadbar);
        loadbar.setRenderMode(RenderMode.SOFTWARE);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        isRegistered = apiMethode.getIsRegistered();
        isRegistered.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer code) {
                if (code == 201) { ///success
                    Intent intent = new Intent(RegisterActivity.this, VerifyUserActivity.class);
                    startActivity(intent);
                    finish();

                } else if (code == 409) {
//                    Toast.makeText(RegisterActivity.this, "email address exist", Toast.LENGTH_SHORT).show();
                    email_EditText.setError("آدرس ایمیل قبلا استفاده شده.");
                    register_btn.setEnabled(true);
                    loadbar.setVisibility(View.INVISIBLE);
                } else if (code == 408) {
//                    Toast.makeText(RegisterActivity.this, "timeout", Toast.LENGTH_SHORT).show();
                    Snackbar.make(contentView,"خطا در ارتباط",Snackbar.LENGTH_SHORT)
                            .setTextColor(Color.BLACK)
                            .setBackgroundTint(ContextCompat.getColor(getApplicationContext(),R.color.snackLight))
                            .show();

                    register_btn.setEnabled(true);
                    loadbar.setVisibility(View.INVISIBLE);
                } else {
//                    Toast.makeText(RegisterActivity.this, "error happened", Toast.LENGTH_SHORT).show();
                    Snackbar.make(contentView,"مشکلی پیش آمد!",Snackbar.LENGTH_SHORT)
                            .setTextColor(Color.BLACK)
                            .setBackgroundTint(ContextCompat.getColor(getApplicationContext(),R.color.snackLight))
                            .show();


                    register_btn.setEnabled(true);
                    loadbar.setVisibility(View.INVISIBLE);
                    //// error
                }
            }
        });


        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();

                if (!validateName() | !validateEmail() | !validatePassword() | !validatePasswordRepeat()) {
                    return;
                }

                User user = new User(name_EditText.getText().toString(), email_EditText.getText().toString(), password_EditText.getText().toString());
                apiMethode.register(user);
                register_btn.setEnabled(false);

                loadbar.setVisibility(View.VISIBLE);


            }

        });
    }



    /////////// Validation

    private Boolean validateName() {
        String val = name_EditText.getText().toString();
        if (val.isEmpty()) {
            name_EditText.setError("نام نمی تواند خالی باشد");
            return false;
        } else {
            email_EditText.setError(null);
            return true;
        }
    }

    private Boolean validateEmail() {
        String val = email_EditText.getText().toString();
        if (val.isEmpty()) {
            email_EditText.setError("آدرس ایمیل نمی تواند خالی باشد");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(val).matches()) {
            email_EditText.setError("ایمیل  نامعتبر است!");
            return false;
        } else {
            email_EditText.setError(null);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = password_EditText.getText().toString();

        if (val.isEmpty()) {
            password_EditText.setError("کلمه عبور نمی تواند خالی باشد.");
            return false;
        } else if (val.length() < 6) {
            password_EditText.setError("کلمه عبور باید بزرگتر از 6 کاراکتر باشد.");
            return false;
        } else {
            password_EditText.setError(null);
            return true;
        }
    }

    private Boolean validatePasswordRepeat() {
        String val = password_repeat_EditText.getText().toString();
        String val2 = password_EditText.getText().toString();


        if (val.isEmpty()) {
            password_repeat_EditText.setError("کلمه عبور نمی تواند خالی باشد.");
            return false;
        } else if(!val.equals(val2)){
            password_repeat_EditText.setError("تکرار کلمه عبور مشابه کلمه عبور نیست!");
            return false;
        } else {
            password_repeat_EditText.setError(null);
            return true;
        }
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