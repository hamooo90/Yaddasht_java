package ir.yaddasht.yaddasht.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.alirezaafkar.sundatepicker.interfaces.DateSetListener;

import ir.yaddasht.yaddasht.R;
import ir.yaddasht.yaddasht.view.customView.CustomSpinner;
import ir.yaddasht.yaddasht.model.roomdb.Note;
import ir.yaddasht.yaddasht.roomdb.NoteRepository;
import ir.yaddasht.yaddasht.viewmodel.NoteViewModel;
import ir.yaddasht.yaddasht.retrofit.ApiMethode;
import ir.yaddasht.yaddasht.util.CaptureAct;
import ir.yaddasht.yaddasht.util.FormatHelper;
import ir.yaddasht.yaddasht.util.NotifAlarmUtil;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import ir.huri.jcal.JalaliCalendar;

public class AddEditActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_ID = "ir.yaddasht.yaddasht.EXTRA_ID";
    public static final String EXTRA_TITLE = "ir.yaddasht.yaddasht.EXTRA_TITLE";
    public static final String EXTRA_CONTENT = "ir.yaddasht.yaddasht.EXTRA_CONTENT";
    public static final String EXTRA_COLOR = "ir.yaddasht.yaddasht.EXTRA_COLOR";
    public static final String EXTRA_TIME_ADD_EDIT = "ir.yaddasht.yaddasht.EXTRA_TIME_ADD_EDIT";
    public static final String EXTRA_TIME_REMINDER = "ir.yaddasht.yaddasht.EXTRA_TIME_REMINDER";

    public static final String EXTRA_MONGOID = "ir.yaddasht.yaddasht.EXTRA_MONGOID";
    public static final String EXTRA_ISSYNCED = "ir.yaddasht.yaddasht.EXTRA_ISSYNCED";
    public static final String EXTRA_DELETED = "ir.yaddasht.yaddasht.EXTRA_DELETED";

    public static final String EXTRA_VER = "ir.yaddasht.yaddasht.EXTRA_VER";
    public static final String EXTRA_REQCODE = "ir.yaddasht.yaddasht.EXTRA_REQCODE";

    public static final String EXTRA_CHECKED = "ir.yaddasht.yaddasht.EXTRA_CHECKED";
    public static final String EXTRA_CHECKLIST_TICK = "ir.yaddasht.yaddasht.EXTRA_CHECKLIST_TICK";

    public static final String EXTRA_PINNED = "ir.yaddasht.yaddasht.EXTRA_PINNED";


    ///////////
    public static final String EXTRA_NOTE = "ir.yaddasht.yaddasht.EXTRA_NOTE";
    public static final String EXTRA_DELETE_TYPE = "ir.yaddasht.yaddasht.EXTRA_DELETE_TYPE";


    private Note toUpdateNote;
    private int _id = -1, color =1;
    private String mongoId, title, content;
    private long timeAddEdit, timeReminder;
    private boolean isSynced;
    private boolean deleted, isCheckList;
    private int ver =1;
    private int reqCode=1;

    private String checklist_tick;

    private boolean isPinned;
    private long creationTime;

    /////////////////////
    private EditText edit_title;
    private EditText edit_content;

    private LinearLayout checkList_layout,checkList_inner_layout;

    private Button checklist_newLine_button;
    private int indexCheckList, checklist_count;

    private boolean pinnedStateNow;//not from intent


    private boolean firstNewLineCheckList = true;

    ArrayList<String> checklist_content = new ArrayList<>();
    ArrayList<Boolean> checklist_isChecked = new ArrayList<>();


    private NoteViewModel noteViewModel;

    ApiMethode apiMethode;

    ///////////////////
//    LinearLayout fr1;//////////////////////////////////////////////////////////////////////
    ///////////////////
    private CoordinatorLayout addEditActivity;
    private Toolbar myToolbar;
    private Toolbar bottomToolbar;
    private View bottomSheetLeft;
    private View bottomSheetRight;

    private LinearLayout delete_btn_sheet_right;
    private LinearLayout share_btn_sheet_right;
    ///////////////////
    TextView time_text;
    ImageView btn_sheet_left, btn_sheet_right;
    private BottomSheetBehavior mBottomSheetBehaviorLeft, mBottomSheetBehaviorRight;
    TextView txtAlert;
    //////////////////////////
    ////sheet left buttons////
    LinearLayout checklist_btn_sheet_left, barcode_btn_sheet_left;
    //////////////////////////
    private Date date = new Date();
    private long datetime = date.getTime();

    List<String> dateArray;
    List<String> timeArray;
    ArrayAdapter<String> adapterSpinnerDate;
    ArrayAdapter<String> adapterSpinnerTime;
    /////////////
    int dialogOpenFirstTime;//////////////for fixing date picker reminder bug
    int spinnerTimeFirstTime = 1; ////////////fixing spinnerTime bug when item 4 is selected initially///++ Delete this later++
    Calendar timeNow, alertTime;
    JalaliCalendar jalaliCalendar = new JalaliCalendar();
    Long reminderTimeLong;///
    DatePickerDialog.OnDateSetListener datePicker;
    ////////////////////
    private int palletColor = 1;
    private int palletTable[] = {0, R.id.note_color_1, R.id.note_color_2, R.id.note_color_3,
            R.id.note_color_4, R.id.note_color_5, R.id.note_color_6, R.id.note_color_7,
            R.id.note_color_8, R.id.note_color_9, R.id.note_color_10};
    private int colorTable[] = {0, R.color.pallet1, R.color.pallet2, R.color.pallet3, R.color.pallet4,
            R.color.pallet5, R.color.pallet6, R.color.pallet7, R.color.pallet8,
            R.color.pallet9, R.color.pallet10};
    private TextView colorPalletView1, colorPalletView2, colorPalletView3, colorPalletView4,
            colorPalletView5, colorPalletView6, colorPalletView7, colorPalletView8,
            colorPalletView9, colorPalletView10;

    private CardView alertView, alertViewRoot;

//    private

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        forceRTLIfSupported();

        apiMethode = new ApiMethode(getApplication());

        addEditActivity = findViewById(R.id.add_edit_activity);
        //////////////////
        bottomToolbar = findViewById(R.id.add_edit_bottom_toolbar);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);

        alertView = findViewById(R.id.alert_view);
        alertViewRoot = findViewById(R.id.alert_view_root);
        //////////////////click on alert view////////////
        alertViewRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                alertDialogShow();
            }
        });

        //////convert date time milli to readable
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm");
        String dateString = formatter.format(new Date(datetime));

        noteViewModel = new NoteViewModel(getApplication());

        edit_title = findViewById(R.id.edit_title);
        edit_content = findViewById(R.id.edit_content);

        checkList_layout = findViewById(R.id.check_list_layout);
        checkList_inner_layout = findViewById(R.id.checklist_inner_layout);
        checklist_newLine_button = findViewById(R.id.new_checklist_line_button);

        /////////multiline text with keyboard next///////
        edit_title.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        edit_title.setRawInputType(InputType.TYPE_CLASS_TEXT);
        /////////////////////

        ///////////time in bottom toolbar//////
        time_text = findViewById(R.id.text_view_bottom_time);
        String datePerNum = FormatHelper.toPersianNumber(dateString);
        time_text.setText("ویرایش در " + datePerNum);

        ////////////Edit note////////
        Intent intent = getIntent();
        int tmp_req = intent.getIntExtra("reqcode",0);

        ///activity opened from notification
        if(tmp_req>0){
            NoteRepository nr = new NoteRepository(getApplication());
            toUpdateNote = nr.getSingleNoteWithReqCode(tmp_req);

            ///////dismiss notification
            int tmp_unique_int = intent.getIntExtra("unique_int",0);
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(tmp_unique_int);

        } else {
            toUpdateNote = (Note) intent.getSerializableExtra(EXTRA_NOTE);
        }
        if (toUpdateNote != null) {
            _id=toUpdateNote.getId();
            isCheckList = toUpdateNote.isCheckList();

        } else {
            isCheckList = intent.getBooleanExtra(EXTRA_CHECKED, false);
        }
        ////
        if(isCheckList){
            TextView tmp_checklist_btn_text = findViewById(R.id.sheet_left_checklist_text);
            tmp_checklist_btn_text.setText("پاک کردن چک لیست");
        }
        //
//        _id = intent.getIntExtra(EXTRA_ID, -1);
        if (_id == -1) {
            edit_content.requestFocus();
            showKeyboard(AddEditActivity.this);
//            openKeyboard(edit_content);

        } else {

            title = toUpdateNote.getTitle();
            content = toUpdateNote.getContent();
            timeAddEdit = toUpdateNote.getTimeAddEdit();
            timeReminder = toUpdateNote.getTimeReminder();
            color = toUpdateNote.getColor();
            mongoId = toUpdateNote.getMongoId();
            isSynced = toUpdateNote.getSynced();
            deleted = toUpdateNote.getDeleted();
            ver = toUpdateNote.getVer();
            reqCode = toUpdateNote.getReqCode();

            isCheckList = toUpdateNote.isCheckList();
//            mongoId = intent.getStringExtra(EXTRA_MONGOID);

            checklist_tick = toUpdateNote.getChecklistTick();
            isPinned = toUpdateNote.isPinned();

            creationTime = toUpdateNote.getCreationTime();
            pinnedStateNow = isPinned;


            edit_title.setText(title);
            edit_content.setText(content);
            palletColor = color;
            Calendar tmp = Calendar.getInstance();
            tmp.setTimeInMillis(timeAddEdit);
            time_text.setText("ویرایش در "+customFormatTimeDateForBottomToolbar(tmp));

            if (isCheckList) {
                edit_content.setVisibility(View.GONE);
                checkList_layout.setVisibility(View.VISIBLE);
                populateCheckLists(content, checklist_tick);

                edit_title.requestFocus();

            }
            time_text.requestFocus();


        }

        if (_id == -1 && isCheckList) {
            edit_content.setVisibility(View.GONE);
            checkList_layout.setVisibility(View.VISIBLE);
            insertCheckList(checkList_inner_layout, "", false);


        }
        //////////////////
        /////////////get long reminder time from intent//// if no reminder it will be 0/////////////
        reminderTimeLong = timeReminder;
        if (reminderTimeLong == 0) {
            alertViewRoot.setVisibility(View.INVISIBLE);
        }
        txtAlert = findViewById(R.id.alert_view_text);
        Calendar tmpAlert = Calendar.getInstance();
        tmpAlert.setTimeInMillis(reminderTimeLong);
        txtAlert.setText(customFormatTimeDate(tmpAlert));///////////////////////////////////////////////////////////////////////////////////////////
        if (reminderTimeLong < new Date().getTime()) {
            txtAlert.setTextColor(Color.GRAY);
            txtAlert.setPaintFlags(txtAlert.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            ImageView ig = findViewById(R.id.alert_view_image);
            ig.setImageResource(R.drawable.ic_alarm_gray);
            alertViewRoot.setCardBackgroundColor(Color.GRAY);

        }

        ////////////////////////////////////////////////////////
        dateArray = new ArrayList<String>();
        timeArray = new ArrayList<String>();
        dateArray.add(0, getResources().getStringArray(R.array.spinnerDate)[0]);
        dateArray.add(1, getResources().getStringArray(R.array.spinnerDate)[1]);
        dateArray.add(2, getResources().getStringArray(R.array.spinnerDate)[2]);
        dateArray.add(3, getResources().getStringArray(R.array.spinnerDate)[3]);
        ////
        timeArray.add(0, getResources().getStringArray(R.array.spinnerTime)[0]);
        timeArray.add(1, getResources().getStringArray(R.array.spinnerTime)[1]);
        timeArray.add(2, getResources().getStringArray(R.array.spinnerTime)[2]);
        timeArray.add(3, getResources().getStringArray(R.array.spinnerTime)[3]);
        timeArray.add(4, getResources().getStringArray(R.array.spinnerTime)[4]);
        /////////
        /////////
        btn_sheet_left = findViewById(R.id.btm1);
        btn_sheet_right = findViewById(R.id.btm2);

        bottomSheetLeft = findViewById(R.id.bottom_sheet_left);
        bottomSheetRight = findViewById(R.id.bottom_sheet_right);

        delete_btn_sheet_right = findViewById(R.id.sheet_right_delete_button);
        share_btn_sheet_right = findViewById(R.id.sheet_right_share_button);

        checklist_btn_sheet_left = findViewById(R.id.sheet_left_checklist_button);
        barcode_btn_sheet_left = findViewById(R.id.sheet_left_barcode_button);

        mBottomSheetBehaviorLeft = BottomSheetBehavior.from(bottomSheetLeft);
        mBottomSheetBehaviorRight = BottomSheetBehavior.from(bottomSheetRight);

        btn_sheet_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mBottomSheetBehaviorRight.setState(BottomSheetBehavior.STATE_COLLAPSED);
                ////
                closeKeyboard();
                mBottomSheetBehaviorRight.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if (mBottomSheetBehaviorLeft.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetBehaviorLeft.setState(BottomSheetBehavior.STATE_EXPANDED);

                    ////////////////fixing some bugs
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mBottomSheetBehaviorLeft.setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                    }, 200);
                } else {
                    mBottomSheetBehaviorLeft.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }


            }
        });
        btn_sheet_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                closeKeyboard();

                mBottomSheetBehaviorLeft.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if (mBottomSheetBehaviorRight.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetBehaviorRight.setState(BottomSheetBehavior.STATE_EXPANDED);
                    ////////////////fixing some bugs
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mBottomSheetBehaviorRight.setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                    }, 200);
                } else {
                    mBottomSheetBehaviorRight.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }

            }
        });


        checklist_newLine_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indexCheckList++;

                if (isCheckList && _id > -1 && content.isEmpty() && firstNewLineCheckList) {
                    firstNewLineCheckList = false;
                    indexCheckList = 0;
                }

                insertCheckList(checkList_inner_layout, "", false);
                showKeyboard(AddEditActivity.this);
            }
        });

        if (mBottomSheetBehaviorLeft.getState() == BottomSheetBehavior.STATE_EXPANDED || mBottomSheetBehaviorRight.getState() == BottomSheetBehavior.STATE_EXPANDED) {
//            bottomToolbar.setElevation(4);
        } else {
//            bottomToolbar.setElevation(0);
        }

        ////////////datepicker///////
        alertTime = Calendar.getInstance();
        /////////////////if there's a reminder set
        if (reminderTimeLong != 0) {
            alertTime.setTimeInMillis(reminderTimeLong);//////////////////////////////////////////////////////////////////////////////////////////////////
            jalaliCalendar.fromGregorian(new GregorianCalendar(alertTime.get(Calendar.YEAR),
                    alertTime.get(Calendar.MONTH), alertTime.get(Calendar.DAY_OF_MONTH)));

            dateArray.set(3, FormatHelper.toPersianNumber(jalaliCalendar.getDay() + "") + " " + jalaliCalendar.getMonthString());
        }

        timeArray.set(4, FormatHelper.formatTimeToPersian(alertTime));

        timeNow = Calendar.getInstance();
        datePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                alertTime.set(Calendar.YEAR, year);
                alertTime.set(Calendar.MONTH, month);
                alertTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                dateArray.set(3, alertTime.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) +
                        " " + alertTime.get(Calendar.DAY_OF_MONTH));
                adapterSpinnerDate.notifyDataSetChanged();
            }
        };
        ////////////////////////////
        delete_btn_sheet_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehaviorRight.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if (_id == -1) {
                    finish();
                } else {


                    Note deletedNote = new Note(title, content, timeAddEdit, color);
                    deletedNote.setMongoId(mongoId);
                    deletedNote.setId(_id);
                    deletedNote.setTimeReminder(timeReminder);
                    deletedNote.setReqCode(reqCode);
                    deletedNote.setPinned(pinnedStateNow);///

                    deletedNote.setSynced(false);
                    deletedNote.setDeleted(true);

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(EXTRA_NOTE, toUpdateNote);

                    if (deletedNote.getMongoId().isEmpty()) {
                        resultIntent.putExtra(EXTRA_DELETE_TYPE, true);//
                        noteViewModel.delete(deletedNote);
                    }
                    // else delete it after deleting from mongo
                    else {
                        resultIntent.putExtra(EXTRA_DELETE_TYPE, false);//
                        noteViewModel.update(deletedNote);
                    }
                    /// if reminder time is not passed
                    if (deletedNote.getTimeReminder() >= 0) {
                        NotifAlarmUtil notifAlarmUtil = new NotifAlarmUtil(AddEditActivity.this);

                        notifAlarmUtil.deleteReminder(deletedNote.getReqCode(), deletedNote.getTitle(), deletedNote.getContent());
                    }

                    ////send result to mainactivity that yaddasht deleted
                    setResult(Activity.RESULT_OK, resultIntent);//
                    finish();
                }
            }
        });

        share_btn_sheet_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String shareSub = edit_content.getText().toString();
                if (isCheckList) {
                    for (int i = 0; i < checklist_content.size(); i++) {
                        if (!(checklist_content.get(i) == null)) {
                            if (checklist_isChecked.get(i)) {
                                shareSub = shareSub + " [✓] ";
                            } else {
                                shareSub = shareSub + " [ ] ";
                            }
                            shareSub = shareSub + checklist_content.get(i) + "\n";
                        }
                    }
                }
//                myIntent.putExtra(Intent.EXTRA_SUBJECT, shareBody);
                myIntent.putExtra(Intent.EXTRA_TEXT, shareSub);
                startActivity(Intent.createChooser(myIntent, "همرسانی"));
                mBottomSheetBehaviorRight.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

         // start barcode scanner
        barcode_btn_sheet_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanBarcode();
            }
        });

        checklist_btn_sheet_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehaviorLeft.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if (!isCheckList) {
                    isCheckList = true;
                    if (!edit_content.getText().toString().isEmpty()) {
                        indexCheckList = 0;
                        checkList_inner_layout.removeAllViews();
                        String temp_content = "\n" + edit_content.getText().toString();
                        String[] temp_content_lines = edit_content.getText().toString().split("\n");
                        String temp_tick = "";
                        for (int i = 0; i < temp_content_lines.length; i++) {
                            temp_tick = temp_tick + "\nfalse";
                        }
                        populateCheckLists(temp_content, temp_tick);
                    } else {
                        insertCheckList(checkList_inner_layout, "", false);
                    }
                    edit_content.setVisibility(View.GONE);
                    checkList_layout.setVisibility(View.VISIBLE);
                    TextView checklist_btn_text = findViewById(R.id.sheet_left_checklist_text);
                    checklist_btn_text.setText("پاک کردن چک لیست");
                } else {
                    isCheckList = false;
                    String temp_content = "";
                    for (int i = 0; i < checklist_content.size(); i++) {
                        if (i == checklist_content.size() - 1) {
                            temp_content = temp_content + checklist_content.get(i);
                        } else {
                            temp_content = temp_content + checklist_content.get(i) + "\n";

                        }
                    }
                    edit_content.setText(temp_content);
                    edit_content.setVisibility(View.VISIBLE);
                    checkList_layout.setVisibility(View.GONE);
                    TextView checklist_btn_text = findViewById(R.id.sheet_left_checklist_text);
                    checklist_btn_text.setText("افزودن چک لیست");
                }

            }
        });

        ////////////////////
        colorPalletView1 = findViewById(R.id.note_color_1);
        colorPalletView2 = findViewById(R.id.note_color_2);
        colorPalletView3 = findViewById(R.id.note_color_3);
        colorPalletView4 = findViewById(R.id.note_color_4);
        colorPalletView5 = findViewById(R.id.note_color_5);
        colorPalletView6 = findViewById(R.id.note_color_6);
        colorPalletView7 = findViewById(R.id.note_color_7);
        colorPalletView8 = findViewById(R.id.note_color_8);
        colorPalletView9 = findViewById(R.id.note_color_9);
        colorPalletView10 = findViewById(R.id.note_color_10);

        colorPalletView1.setOnClickListener(this);
        colorPalletView2.setOnClickListener(this);
        colorPalletView3.setOnClickListener(this);
        colorPalletView4.setOnClickListener(this);
        colorPalletView5.setOnClickListener(this);
        colorPalletView6.setOnClickListener(this);
        colorPalletView7.setOnClickListener(this);
        colorPalletView8.setOnClickListener(this);
        colorPalletView9.setOnClickListener(this);
        colorPalletView10.setOnClickListener(this);

        //////////color of layout////////////////
        colorChange(palletColor);
    }


    //////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_edit_menu_top, menu);
        if (isPinned) {
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_pin_filled));
        }
        return true;
    }

    ////////////////
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_add_edit_menu_alert:
                closeKeyboard();
                alertDialogShow();
                return true;
            case R.id.item_add_edit_menu_pin:
                if (!pinnedStateNow) {
                    pinnedStateNow = true;
                    item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_pin_filled));
                } else {
                    pinnedStateNow = false;
                    item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_pin_outline));
                }
//                item.setIcon(ContextCompat.getDrawable(this,R.drawable.ic_pin_filled));
                return true;
            case android.R.id.home:///////On appbar back click
                insert();
                closeKeyboard();
                finish();
//                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
//            case R.id.item_add_edit_menu_archive:
////                edit_content.setVisibility(View.VISIBLE);
////                checkList_layout.setVisibility(View.GONE);
//                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    ////////////
    @Override
    public void onBackPressed() {
//        TinyDB tinyDB = new TinyDB(this);
//        tinyDB.putString("qwe","haaamed");
        insert();
        super.onBackPressed();

//        finish();
    }
    ////////////


    @Override
    protected void onPause() {
        super.onPause();
        /// collapse bottom sheet when activity paused
        mBottomSheetBehaviorRight.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehaviorLeft.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    /////////////
    private void insert() {
        String mTitle = edit_title.getText().toString().trim();
        String mContent = edit_content.getText().toString().trim();
        NotifAlarmUtil notifAlarmUtil = new NotifAlarmUtil(this);

        boolean checkListIsEmpty = true;
//        try {
        if (isCheckList) {
            mContent = "";
            for (int i = 0; i < checklist_content.size(); i++) {

                try {
                    if (!(checklist_content.get(i).isEmpty())) {
                        checkListIsEmpty = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
//        } catch (Exception e) {
////            e.printStackTrace();
//        }


        if (!mTitle.isEmpty() || !mContent.isEmpty() || !checkListIsEmpty || (_id > 0 && pinnedStateNow != isPinned)) {
            Note newNote = new Note(mTitle, mContent, datetime, palletColor);
            String tick_check = "";
            if (reminderTimeLong != 0) {///add reminder time if it is not zero

                newNote.setTimeReminder(reminderTimeLong);
                //////////////////////
//                notifAlarmUtil.createNotificationChannel();
                //////////////////////
            }
            if (_id != -1) {/////update note
                if (isCheckList) {
                    mContent = "";
                    for (int i = 0; i < checklist_content.size(); i++) {
                        if (!(checklist_content.get(i) == null)) {
                            mContent = mContent + "\n" + checklist_content.get(i);
                            tick_check = tick_check + "\n" + checklist_isChecked.get(i);
                        }
                    }
                    newNote.setContent(mContent);
                    newNote.setChecklistTick(tick_check);
                    newNote.setCheckList(true);
                } else {
                    mContent = edit_content.getText().toString().trim();
                }
                if (!title.equals(mTitle) || !content.equals(mContent) ||
                        timeReminder != reminderTimeLong || color != palletColor ||
                        !checklist_tick.equals(tick_check) || pinnedStateNow != isPinned) {
                    newNote.setId(_id);
                    newNote.setMongoId(mongoId);
                    newNote.setReqCode(reqCode);
                    if (!isSynced) {
                        newNote.setVer(ver);
                    } else {
                        newNote.setVer(ver + 1);
                    }
                    newNote.setPinned(pinnedStateNow);//
                    newNote.setCreationTime(creationTime);
                    noteViewModel.update(newNote);
//                    Log.d("TAGTAG", "reqCode: " + newNote.getReqCode());
                    long timeNow = new Date().getTime();

                    if (reminderTimeLong > timeNow) {
                        notifAlarmUtil.updateReminder(newNote.getReqCode(), newNote.getTitle(), newNote.getContent(), newNote.getTimeReminder());
                    } else {
                        if (timeReminder != 0) {
                            notifAlarmUtil.deleteReminder(newNote.getReqCode(), newNote.getTitle(), newNote.getContent());
                        }
                    }

                }

            } else {////////add new note

                // normal note
                newNote.setPinned(pinnedStateNow);///
                newNote.setCreationTime(new Date().getTime());
                if (!isCheckList) {
                    noteViewModel.insert(newNote);
                }
                // reminder note
                else {
                    String content = "";
                    for (int i = 0; i < checklist_content.size(); i++) {
                        if (!(checklist_content.get(i) == null)) {
                            content = content + "\n" + checklist_content.get(i);
                            tick_check = tick_check + "\n" + checklist_isChecked.get(i);
                        }
                    }

                    newNote.setContent(content);
                    newNote.setCheckList(true);
                    newNote.setChecklistTick(tick_check);
                    noteViewModel.insert(newNote);

                }
                long timeNow = new Date().getTime();

                if (reminderTimeLong > timeNow) {
                    notifAlarmUtil.addReminder(newNote.getReqCode(), newNote.getTitle(), newNote.getContent(), newNote.getTimeReminder());
                }

            }
        } else {
            if (_id == -1) {
                setResult(1, null);//

            } else {
                setResult(1, null);//

            }
        }
    }

    /////////////////
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void openKeyboard(final EditText editText) {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private static void showKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
//        imm.showSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    /////////////////////////////////
    private void alertDialogShow() {
        final Date dateAlert = new Date();
        long datetimeAlert;
        alertTime.setTimeInMillis(reminderTimeLong);
        dialogOpenFirstTime = 0;

        MaterialAlertDialogBuilder mBuilder = new MaterialAlertDialogBuilder(AddEditActivity.this,R.style.AlertDialogCustomRTL);//android.R.style.Theme_Material_Dialog_Alert

        mBuilder.setTitle("افزودن یادآور");
        View mView = getLayoutInflater().inflate(R.layout.alert_dialog, null);
        CustomSpinner spinnerTime = mView.findViewById(R.id.alert_spinner_time);
        final CustomSpinner spinnerDate = mView.findViewById(R.id.alert_spinner_date);

        adapterSpinnerTime = new ArrayAdapter<String>(AddEditActivity.this,
                R.layout.simple_spinner_dropdown_item_rtl,
                timeArray) {

            /////////disable spinner items if time is passed
            @Override
            public boolean isEnabled(int position) {
                if (spinnerDate.getSelectedItemPosition() == 0) {
                    if (position == 0 && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 8) {
//                        spinnerTime.setSelection(1);
                        return false;
                    } else if (position == 1 && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 13) {
//                        spinnerTime.setSelection(2);

                        return false;
                    } else if (position == 2 && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 18) {
//                        spinnerTime.setSelection(3);

                        return false;
                    } else if (position == 3 && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 20) {
//                        spinnerDate.setSelection(4);
                        return false;
                    }
                }
                return true;
            }

            /////////grey disabled items
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    // Androids orginal spinner view item
                    v = vi.inflate(R.layout.simple_spinner_dropdown_item_rtl, null);
                }
                // The text view of the spinner list view
                TextView tv = (TextView) v.findViewById(android.R.id.text1);
                String val = timeArray.get(position);
                // remove the extra text here
                tv.setText(val.replace(":False", ""));

                boolean disabled = !isEnabled(position);
                if (disabled) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }

                return v;
            }
        };


        adapterSpinnerDate = new ArrayAdapter<String>(AddEditActivity.this,
                R.layout.simple_spinner_dropdown_item_rtl,
                dateArray);
        adapterSpinnerTime.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_rtl);
        adapterSpinnerDate.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_rtl);
        spinnerTime.setAdapter(adapterSpinnerTime);
        spinnerDate.setAdapter(adapterSpinnerDate);
        if (reminderTimeLong != 0) {
            spinnerDate.setSelection(3);/////////////////////////////////////////////////////////////////////////////////////////////////////
            spinnerTime.setSelection(4);
        } else {

//            //////// spinnerTime inital Item selection
//            int spinnerTimeInitSelection = 1;///0 make app crash!!!! dont know why?!?!?
            if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 20) {
//                spinnerTimeFirstTime = 0;////////////////////////
                spinnerDate.setSelection(1);
//                spinnerTimeInitSelection = 4;
            } else if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 18) {
                spinnerTime.setSelection(3);
//                spinnerTimeInitSelection = 3;
            } else if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 13) {
                spinnerTime.setSelection(2);
//                spinnerTimeInitSelection = 2;
            } else if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 8) {
                spinnerTime.setSelection(1);
//                spinnerTimeInitSelection = 1;
            }
//            timeArray.set(3,"asdasdasd");
//            spinnerTime.setSelection(spinnerTimeInitSelection);

        }

        mBuilder.setPositiveButton("ذخیره", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /////////////////\\\\\\\\\\\\\\\\\\\\\\\\\////////////////
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy, hh:mm a");
                String tmpDateTime = sdf.format(alertTime.getTime());////////
                reminderTimeLong = alertTime.getTimeInMillis();
                timeArray.set(4, FormatHelper.formatTimeToPersian(alertTime));


                txtAlert.setText(customFormatTimeDate(alertTime));
                if (alertTime.getTimeInMillis() < new Date().getTime()) {
                    txtAlert.setTextColor(Color.GRAY);
                    txtAlert.setPaintFlags(txtAlert.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    ImageView ig = findViewById(R.id.alert_view_image);
                    ig.setImageResource(R.drawable.ic_alarm_gray);
                    alertViewRoot.setCardBackgroundColor(Color.GRAY);
                } else {
                    txtAlert.setTextColor(Color.WHITE);
                    txtAlert.setPaintFlags(txtAlert.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    ImageView ig = findViewById(R.id.alert_view_image);
                    ig.setImageResource(R.drawable.ic_alarm);
                    alertViewRoot.setCardBackgroundColor(Color.WHITE);

                }
                alertViewRoot.setVisibility(View.VISIBLE);

            }
        });
        mBuilder.setNegativeButton("رد", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        if (reminderTimeLong != 0) {
            mBuilder.setNeutralButton("پاک کردن", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    reminderTimeLong = 0L;
                    alertTime = Calendar.getInstance();
                    alertViewRoot.setVisibility(View.INVISIBLE);
                }
            });
        }
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.yellowB));


            }
        });
        //////////
        dialog.show();

        //////////spinnerDate listen for open and close/////
        spinnerDate.setSpinnerEventsListener(new CustomSpinner.OnSpinnerEventsListener() {
            @Override
            public void onSpinnerOpened(Spinner spin) {
                dateArray.set(0, getResources().getStringArray(R.array.spinnerDate)[0]);
                dateArray.set(1, getResources().getStringArray(R.array.spinnerDate)[1]);
//                jalaliCalendar.fromGregorian(new GregorianCalendar(alertTime.get(Calendar.YEAR),
//                        alertTime.get(Calendar.MONTH), alertTime.get(Calendar.DAY_OF_MONTH)));

                jalaliCalendar = new JalaliCalendar();

                dateArray.set(2, FormatHelper.toPersianNumber(jalaliCalendar.getDay() + "") + " " + jalaliCalendar.getMonthString());
                dateArray.set(2, jalaliCalendar.getDayOfWeekString() + " " + getResources().getStringArray(R.array.spinnerDate)[2]);
//                dateArray.set(2, getResources().getStringArray(R.array.spinnerDate)[2] +
//                        " " + timeNow.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()));
                dateArray.set(3, getResources().getStringArray(R.array.spinnerDate)[3]);
            }

            @Override
            public void onSpinnerClosed(Spinner spin) {

            }
        });
        //////////spinnerDate item select//////////////
        spinnerDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                long today = dateAlert.getTime();
                SimpleDateFormat formatter1 = new SimpleDateFormat("MMMM d");
                Date currTime = Calendar.getInstance().getTime();


                if (position == 0) {

                    jalaliCalendar.fromGregorian(new GregorianCalendar(timeNow.get(Calendar.YEAR),
                            timeNow.get(Calendar.MONTH), timeNow.get(Calendar.DAY_OF_MONTH)));
                    dateArray.set(0, FormatHelper.toPersianNumber(jalaliCalendar.getDay() + "") + " " + jalaliCalendar.getMonthString());
//                    dateArray.set(0, timeNow.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) +
//                            " " + timeNow.get(Calendar.DAY_OF_MONTH));
                    alertTime.set(Calendar.YEAR, timeNow.get(Calendar.YEAR));
                    alertTime.set(Calendar.MONTH, timeNow.get(Calendar.MONTH));
                    alertTime.set(Calendar.DAY_OF_MONTH, timeNow.get(Calendar.DAY_OF_MONTH));
                } else if (position == 1) {
//                    alertTime = Calendar.getInstance();
//                    alertTime.set(Calendar.DAY_OF_MONTH, alertTime.get(Calendar.DAY_OF_MONTH) + 1);

                    alertTime.set(Calendar.YEAR, timeNow.get(Calendar.YEAR));
                    alertTime.set(Calendar.MONTH, timeNow.get(Calendar.MONTH));
                    alertTime.set(Calendar.DAY_OF_MONTH, timeNow.get(Calendar.DAY_OF_MONTH) + 1);

                    jalaliCalendar.fromGregorian(new GregorianCalendar(alertTime.get(Calendar.YEAR),
                            alertTime.get(Calendar.MONTH), alertTime.get(Calendar.DAY_OF_MONTH)));
                    dateArray.set(1, FormatHelper.toPersianNumber(jalaliCalendar.getDay() + "") + " " + jalaliCalendar.getMonthString());

//                    dateArray.set(1, alertTime.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) +
//                            " " + alertTime.get(Calendar.DAY_OF_MONTH));

                } else if (position == 2) {
                    timeNow = Calendar.getInstance();
                    alertTime.set(Calendar.YEAR, timeNow.get(Calendar.YEAR));
                    alertTime.set(Calendar.DAY_OF_YEAR, timeNow.get(Calendar.DAY_OF_YEAR) + 7);
                    jalaliCalendar.fromGregorian(new GregorianCalendar(alertTime.get(Calendar.YEAR),
                            alertTime.get(Calendar.MONTH), alertTime.get(Calendar.DAY_OF_MONTH)));
                    dateArray.set(2, FormatHelper.toPersianNumber(jalaliCalendar.getDay() + "") + " " + jalaliCalendar.getMonthString());

                } else if (position == 3) {
                    if (dialogOpenFirstTime == 0 && reminderTimeLong != 0) {
                        dialogOpenFirstTime = 1;
                    } else {
                        new com.alirezaafkar.sundatepicker.DatePicker.Builder()
                                .id(1424344454)
                                .theme(R.style.PersianDatePickerTheme)
                                .date(alertTime)
                                .minDate(Calendar.getInstance())
                                .build(new DateSetListener() {
                                    @Override
                                    public void onDateSet(int id, @Nullable Calendar calendar, int day, int month, int year) {
                                        alertTime.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
                                        alertTime.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
                                        alertTime.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));


                                        jalaliCalendar.fromGregorian(new GregorianCalendar(alertTime.get(Calendar.YEAR),
                                                alertTime.get(Calendar.MONTH), alertTime.get(Calendar.DAY_OF_MONTH)));

                                        dateArray.set(3, FormatHelper.toPersianNumber(jalaliCalendar.getDay() + "") + " " + jalaliCalendar.getMonthString());
                                        adapterSpinnerDate.notifyDataSetChanged();
                                    }
                                })
                                .show(getSupportFragmentManager(), "");
                    }

                    jalaliCalendar.fromGregorian(new GregorianCalendar(alertTime.get(Calendar.YEAR),
                            alertTime.get(Calendar.MONTH), alertTime.get(Calendar.DAY_OF_MONTH)));

                    dateArray.set(3, FormatHelper.toPersianNumber(jalaliCalendar.getDay() + "") + " " + jalaliCalendar.getMonthString());

                }
                adapterSpinnerDate.notifyDataSetChanged();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //////////spinnerTime listen for open and close///////
        spinnerTime.setSpinnerEventsListener(new CustomSpinner.OnSpinnerEventsListener() {
            @Override
            public void onSpinnerOpened(Spinner spin) {
                timeArray.set(4, getResources().getStringArray(R.array.spinnerTime)[4]);
            }

            @Override
            public void onSpinnerClosed(Spinner spin) {

            }
        });
        //////////spinnerTime /////////////////////////
        spinnerTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    alertTime.set(Calendar.HOUR_OF_DAY, 8);
                    alertTime.set(Calendar.MINUTE, 0);
                } else if (position == 1) {
                    alertTime.set(Calendar.HOUR_OF_DAY, 13);
                    alertTime.set(Calendar.MINUTE, 0);
                } else if (position == 2) {
                    alertTime.set(Calendar.HOUR_OF_DAY, 18);
                    alertTime.set(Calendar.MINUTE, 0);
                } else if (position == 3) {
                    alertTime.set(Calendar.HOUR_OF_DAY, 20);
                    alertTime.set(Calendar.MINUTE, 0);
                } else if (position == 4) {
                    if (spinnerTimeFirstTime == 0) {
                        spinnerTimeFirstTime = 1;
                        alertTime.setTime(Calendar.getInstance().getTime());
                    } else if (dialogOpenFirstTime == 1 && reminderTimeLong != 0) {
                        dialogOpenFirstTime = 2;

                    } else {
                        Calendar c = Calendar.getInstance();
                        int mHour = c.get(Calendar.HOUR_OF_DAY);
                        int mMinute = c.get(Calendar.MINUTE);
                        TimePickerDialog dialog =
                                new TimePickerDialog(AddEditActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        alertTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        alertTime.set(Calendar.MINUTE, minute);
                                        timeArray.set(4, FormatHelper.formatTimeToPersian(alertTime));
                                        adapterSpinnerTime.notifyDataSetChanged();
                                    }
                                }, mHour, mMinute, true);
                        dialog.show();
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    ////////////////////color click//////////////
    @Override
    public void onClick(View v) {
        TextView txt = findViewById(palletTable[palletColor]);
        txt.setText("");
        switch (v.getId()) {
            case R.id.note_color_1:
                colorPalletView1.setText("✓");
                palletColor = 1;
//                colorChange(palletColor);

                break;
            case R.id.note_color_2:
                colorPalletView2.setText("✓");
                palletColor = 2;
//                colorChange(palletColor);

                break;
            case R.id.note_color_3:
                colorPalletView3.setText("✓");
                palletColor = 3;
//                colorChange(palletColor);

                break;
            case R.id.note_color_4:
                colorPalletView4.setText("✓");
                palletColor = 4;
//                colorChange(palletColor);

                break;
            case R.id.note_color_5:
                colorPalletView5.setText("✓");
                palletColor = 5;
//                colorChange(palletColor);

                break;
            case R.id.note_color_6:
                colorPalletView6.setText("✓");
                palletColor = 6;
//                colorChange(palletColor);

                break;
            case R.id.note_color_7:
                colorPalletView7.setText("✓");
                palletColor = 7;
//                colorChange(palletColor);

                break;
            case R.id.note_color_8:
                colorPalletView8.setText("✓");
                palletColor = 8;
//                colorChange(palletColor);

                break;
            case R.id.note_color_9:
                colorPalletView9.setText("✓");
                palletColor = 9;
//                colorChange(palletColor);

                break;
            case R.id.note_color_10:
                colorPalletView10.setText("✓");
                palletColor = 10;
//                colorChange(palletColor);

                break;

        }
        colorChange(palletColor);

    }

    void colorChange(int palletColor) {
        alertView.setCardBackgroundColor(getResources().getColor(colorTable[palletColor]));

        addEditActivity.setBackgroundColor(getResources().getColor(colorTable[palletColor]));
        bottomToolbar.setBackgroundColor(getResources().getColor(colorTable[palletColor]));
        myToolbar.setBackgroundColor(getResources().getColor(colorTable[palletColor]));
        bottomSheetLeft.setBackgroundColor(getResources().getColor(colorTable[palletColor]));
        bottomSheetRight.setBackgroundColor(getResources().getColor(colorTable[palletColor]));
    }

    String customFormatTimeDate(Calendar alert) {
        ///////////////////Miladi////////////////
//        Calendar tNow = Calendar.getInstance();
//        String out;
//        if (alert.get(Calendar.DAY_OF_YEAR) == tNow.get(Calendar.DAY_OF_YEAR)) {
//            out = "Today, ";
//        } else if (alert.get(Calendar.DAY_OF_YEAR) == tNow.get(Calendar.DAY_OF_YEAR) + 1) {
//            out = "Tomorrow, ";
//        } else {
//            out = alert.get(Calendar.DAY_OF_MONTH) + " " + alert.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
//            if (alert.get(Calendar.YEAR) != tNow.get(Calendar.YEAR)) {
//                out = out + " " + alert.get(Calendar.YEAR);
//            }
//            out = out + ", ";
//        }
//        Date tt = alert.getTime();
//        SimpleDateFormat cFormat = new SimpleDateFormat("HH:mm");
//        out = out + cFormat.format(tt);
//        return out;
        //////////////////Miladi/////////////////////
        /////////////////////////////////
        Calendar tNow = Calendar.getInstance();
        JalaliCalendar alertJalali = new JalaliCalendar();
        JalaliCalendar tNowJalali = new JalaliCalendar();
        alertJalali.fromGregorian(new GregorianCalendar(alert.get(Calendar.YEAR),
                alert.get(Calendar.MONTH), alert.get(Calendar.DAY_OF_MONTH)));
        tNowJalali.fromGregorian(new GregorianCalendar(tNow.get(Calendar.YEAR),
                tNow.get(Calendar.MONTH), tNow.get(Calendar.DAY_OF_MONTH)));
        String out;
        if (alertJalali.getYear() == tNowJalali.getYear() && alert.get(Calendar.DAY_OF_YEAR) == tNow.get(Calendar.DAY_OF_YEAR)) {
            out = "امروز، ";
        } else if (alertJalali.getYear() == tNowJalali.getYear() && alert.get(Calendar.DAY_OF_YEAR) == tNow.get(Calendar.DAY_OF_YEAR) + 1) {
            out = "فردا، ";
        } else {
            out = alertJalali.getDay() + " " + alertJalali.getMonthString();
            if (alertJalali.getYear() != tNowJalali.getYear()) {
                out = out + " " + alertJalali.getYear();
            }
            out = out + "، ";

        }
        Date tt = alert.getTime();
        SimpleDateFormat cFormat = new SimpleDateFormat("HH:mm");
        out = out + cFormat.format(tt);
        out = FormatHelper.toPersianNumber(out);
        return out;
    }

    String customFormatTimeDateForBottomToolbar(Calendar editTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm");
        Calendar tNow = Calendar.getInstance();
        JalaliCalendar timeJalali = new JalaliCalendar();
        timeJalali.fromGregorian(new GregorianCalendar(editTime.get(Calendar.YEAR),
                editTime.get(Calendar.MONTH), editTime.get(Calendar.DAY_OF_MONTH)));
        String out;
        if(editTime.get(Calendar.YEAR) == tNow.get(Calendar.YEAR) && editTime.get(Calendar.DAY_OF_YEAR) == tNow.get(Calendar.DAY_OF_YEAR)){
            out = formatter.format(new Date(editTime.getTimeInMillis()));
        } else if(editTime.get(Calendar.YEAR) == tNow.get(Calendar.YEAR)){
            out = timeJalali.getDay() +" "+ timeJalali.getMonthString();
        } else {
            out = timeJalali.getDay() +" "+ timeJalali.getMonthString() +" "+ timeJalali.getYear();
        }

        out = FormatHelper.toPersianNumber(out);
        return out;
    }

    /////////////

    private void insertCheckList(LinearLayout root, String line, boolean ticked) {
        int i = indexCheckList;
        checklist_count++;
        checklist_content.add("");
        checklist_isChecked.add(false);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        CheckBox c = new CheckBox(this);
        EditText e = new EditText(this);
        ImageButton imgB = new ImageButton(this);
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_enabled}, //enabled
                },
                new int[]{Color.WHITE}
        );
        c.setButtonTintList(colorStateList);

        imgB.setId(3000 + indexCheckList);
//        imgB.setVisibility(View.INVISIBLE);
        imgB.setImageResource(R.drawable.ic_close);
        imgB.setBackgroundColor(Color.TRANSPARENT);
        imgB.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );
        imgB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String u = String.valueOf(Integer.valueOf(v.getId()) + 1000);
                int resID = getResources().getIdentifier(u, "id", "ir.yaddasht.yaddasht");
                LinearLayout ll = findViewById(resID);
                ll.setVisibility(View.GONE);
                checklist_content.set(i, null);
                checklist_isChecked.set(i, null);
                checklist_count--;
            }
        });

        e.setId(2000 + indexCheckList);
        e.setText(line);
        e.setBackgroundColor(Color.TRANSPARENT);
        e.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        3f
                )
        );
        e.setSingleLine(true);
        e.setMaxLines(Integer.MAX_VALUE);
        e.setHorizontallyScrolling(false);
//        e.setInputType(InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE);
        e.requestFocus();
        e.setTextColor(Color.WHITE);
        e.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        if (ticked) {
            e.setTextColor(Color.GRAY);
            e.setPaintFlags(e.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        e.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
//            String u = String.valueOf(Integer.valueOf(v.getId()) + 1000);
//            int resID = getResources().getIdentifier(u, "id", "com.hamedvakhide.testpan");
//            ImageButton img = findViewById(resID);
////            Toast.makeText(MainActivity.this, hasFocus+"", Toast.LENGTH_SHORT).show();
//            if(hasFocus){
//                img.setVisibility(View.VISIBLE);
//            } else {
//                img.setVisibility(View.INVISIBLE);
//            }
            }
        });

        e.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // Identifier of the action. This will be either the identifier you supplied,
                // or EditorInfo.IME_NULL if being called due to the enter key being pressed.

                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_NEXT
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    indexCheckList++;
                    insertCheckList(checkList_inner_layout, "", false);
                    return true;
                }
                // Return true if you have consumed the action, else false.
                return false;
            }
        });
        e.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checklist_content.set(i, s.toString());
                int n = 0;
            }
        });

        c.setId(1000 + indexCheckList);
        c.setChecked(ticked);
        c.setFocusable(false);
        c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String u = String.valueOf(Integer.valueOf(buttonView.getId()) + 1000);
                int resID = getResources().getIdentifier(u, "id", "ir.yaddasht.yaddasht");
                EditText et = findViewById(resID);
                checklist_isChecked.set(i, isChecked);
                if (isChecked) {
                    et.setTextColor(Color.GRAY);
                    et.setPaintFlags(et.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    et.setTextColor(Color.WHITE);
                    et.setPaintFlags(et.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

                }
            }
        });

        LinearLayout l = new LinearLayout(this);
        l.setId(4000 + indexCheckList);
        l.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        l.addView(c);
        l.addView(e);
        l.addView(imgB);
        root.addView(l, p);
    }

    /////////////
    private void populateCheckLists(String items_list, String checked_list) {
        String[] str;//= items_list.split("\n");
        String[] chk = checked_list.split("\n");
        checklist_content.clear();
        checklist_isChecked.clear();
        if (items_list.equals("\n")) {
            str = new String[2];
            str[0] = "";
            str[1] = "";
        } else {
            str = items_list.split("\n");
        }
//        indexCheckList++if
//        checklist_content.;
//        checklist_isChecked.clear();
        for (int i = 1; i < chk.length; i++) {
//            checklist_content.set(i-1,str[i]);
            if (chk[i].equals("true")) {
                if (i < str.length) {
                    insertCheckList(checkList_inner_layout, str[i], true);
                    checklist_content.set(i - 1, str[i]);
                } else {
                    insertCheckList(checkList_inner_layout, "", true);
                    checklist_content.set(i - 1, "");
                }
                checklist_isChecked.set(i - 1, true);
            } else {
                if (i < str.length) {
                    insertCheckList(checkList_inner_layout, str[i], false);
                    checklist_content.set(i - 1, str[i]);
                } else {
                    insertCheckList(checkList_inner_layout, "", false);
                    checklist_content.set(i - 1, "");
                }
                checklist_isChecked.set(i - 1, false);

            }
            if (i != chk.length - 1) {
                indexCheckList++;
            }

        }
    }

    /////////////
    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "MyReminderChannel";
            String description = "this is description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyme", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            notificationManager.createNotificationChannel(channel);

        }
    }

    /////
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void forceRTLIfSupported() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

    private void scanBarcode() {
        IntentIntegrator integrator = new IntentIntegrator(AddEditActivity.this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("در حال اسکن بارکد");
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                Snackbar.make(findViewById(R.id.add_edit_activity), result.getContents(), Snackbar.LENGTH_SHORT)
                        .setAnchorView(bottomToolbar)
                        .show();
                if (!isCheckList) {
                    if (edit_content.getText().toString().trim().isEmpty()) {
                        edit_content.append(result.getContents());
                    } else {
                        edit_content.append("\n" + result.getContents());
                    }
                } else {
                    String u = String.valueOf(indexCheckList + 2000);
                    int resID = getResources().getIdentifier(u, "id", "ir.yaddasht.yaddasht");
                    EditText et = findViewById(resID);
//                    et.setText(result.getContents());
                    if (et.getText().toString().isEmpty()) {
                        et.setText(result.getContents());
                    } else {
                        checklist_newLine_button.performClick();

                        u = String.valueOf(indexCheckList + 2000);
                        resID = getResources().getIdentifier(u, "id", "ir.yaddasht.yaddasht");
                        et = findViewById(resID);

                        et.setText(result.getContents());

                    }

                }
            } else {
                Snackbar.make(findViewById(R.id.add_edit_activity), "هیچ بارکدی یافت نشد!", Snackbar.LENGTH_SHORT)
                        .setAnchorView(bottomToolbar)
                        .show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
