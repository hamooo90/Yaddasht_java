package ir.yaddasht.yaddasht.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import ir.yaddasht.yaddasht.R;
import ir.yaddasht.yaddasht.view.adapter.NoteAdapter;
import ir.yaddasht.yaddasht.model.roomdb.Note;
import ir.yaddasht.yaddasht.roomdb.NoteRepository;
import ir.yaddasht.yaddasht.viewmodel.NoteViewModel;
import ir.yaddasht.yaddasht.retrofit.ApiMethode;
import ir.yaddasht.yaddasht.model.retro.NoteR;
import ir.yaddasht.yaddasht.model.retro.User;
import ir.yaddasht.yaddasht.util.InternetCheck;
import ir.yaddasht.yaddasht.util.NotifAlarmUtil;
import ir.yaddasht.yaddasht.util.ReminderBroadcast;
import ir.yaddasht.yaddasht.util.TinyDB;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.Date;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    public static final String SHARED_PREFS = "sharedPrefs";
    SharedPreferences.Editor editor;


    public static final int REQ_CODE_FOR_EDIT_RESULT = 1;
    public static final int REQ_CODE_FOR_ADD_RESULT = 2;

    private NoteViewModel noteViewModel;
    NoteRepository noteRepository;

    ApiMethode apiMethode;

    NotifAlarmUtil notifAlarmUtil;
//    Button logoutBtn;

    List<NoteR> allMongoNotes;
    List<Note> allRoomNotes;

    LiveData<List<Note>> notesForRecyclerAdapter;

    NoteAdapter adapter;
    NoteAdapter adapterPinned;

    boolean firstSync;
    boolean isSyncing;
    //    boolean listToShow;//0 = all notes, 1 = reminders
    int allNotesCountInDb;

    SwipeRefreshLayout swipeToRefresh;

    ConstraintLayout emptyState;
    ImageView emptyStateImage;
    TextView emptyStateText;

    TextView recyclerViewTypeTextView;
    TextView recyclerViewPinNoteTextView;
    TextView recyclerViewTextInPinLL;
    LinearLayout pinnedView;

    boolean isOffline;

//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)


    @Override
    protected void onRestart() {
        sync(700);

        SharedPreferences sharedPreferences = getSharedPreferences(WelcomeActivity.SHARED_PREFS, MODE_PRIVATE);
        if (sharedPreferences.getBoolean(WelcomeActivity.ISOFFLINE, false)) {
            isOffline = true;
        } else {
            isOffline = false;
        }
        super.onRestart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQ_CODE_FOR_EDIT_RESULT) {
            if (resultCode == RESULT_OK) {
                Note note = (Note) data.getSerializableExtra(AddEditActivity.EXTRA_NOTE);
                boolean deleteType = data.getBooleanExtra(AddEditActivity.EXTRA_DELETE_TYPE, true);
                showRedoSnackBar(note);
            }
            if (resultCode == 1) {

                Snackbar.make(findViewById(R.id.drawer), "تغییرات اعمال نشد!", Snackbar.LENGTH_SHORT)
                        .setAnchorView(findViewById(R.id.add_button))
                        .show();
            }
        } else if (requestCode == REQ_CODE_FOR_ADD_RESULT) {
            if (resultCode == 1) {
                Snackbar.make(findViewById(R.id.drawer), "یادداشت خالی پاک شد.", Snackbar.LENGTH_SHORT)
                        .setAnchorView(findViewById(R.id.add_button))
                        .show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /////////
        sharedPreferences = getApplication().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        ////////////

        SharedPreferences sharedPreferences = getSharedPreferences(WelcomeActivity.SHARED_PREFS, MODE_PRIVATE);
        if (sharedPreferences.getBoolean(WelcomeActivity.ISOFFLINE, false)) {
            isOffline = true;
        }

        AppBarLayout appBarLayout = findViewById(R.id.appbar_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StateListAnimator stateListAnimator = new StateListAnimator();
            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(appBarLayout, "elevation", 0.1f));
            appBarLayout.setStateListAnimator(stateListAnimator);
        }


        BottomAppBar bottomAppBar = findViewById(R.id.bottomAppBar);
        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_menu_new_checklist:
                        Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
                        intent.putExtra(AddEditActivity.EXTRA_CHECKED, true);
                        startActivityForResult(intent, REQ_CODE_FOR_ADD_RESULT);
                        return true;
                }
                return false;
            }
        });

///////////////

        notifAlarmUtil = new NotifAlarmUtil(this);
///////////////
        boolean firstTimeUse = sharedPreferences.getBoolean("first_use", true);
        if (firstTimeUse) {
            editor.putBoolean("first_use", false);
            editor.apply();
        }
        Intent intenResult = getIntent();
        boolean isFromVerify = intenResult.getBooleanExtra("FROM_VERIFY", false);
        if (isFromVerify||firstTimeUse) {

            MaterialAlertDialogBuilder mBuilder = new MaterialAlertDialogBuilder(MainActivity.this, R.style.AlertDialogCustomRTL)
                    .setTitle("توجه!")
                    .setMessage(R.string.user_agreement)
                    .setPositiveButton("\u200Cتایید", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            AlertDialog dialog = mBuilder.create();
//            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
//                @Override
//                public void onShow(DialogInterface arg) {
//                    dialog.getT
//                    dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.redDialogNeut));
//                }
//            });
            dialog.show();

        }

        //////////////////

        forceRTLIfSupported();

        DrawerLayout mDrawer = findViewById(R.id.drawer);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navDrawerEmail = (TextView) headerView.findViewById(R.id.txt_nav_email);
        TextView navDrawerRegister = (TextView) headerView.findViewById(R.id.txt_nav_register);
        TextView navDrawerLogin = (TextView) headerView.findViewById(R.id.txt_nav_login);
        LinearLayout navDrawerOffline = headerView.findViewById(R.id.nav_offline_layout);


        Gson gson = new Gson();
        String jsonUser = sharedPreferences.getString("currentUser", "");
        User currentUser = gson.fromJson(jsonUser, User.class);
        if (isOffline) {
            navDrawerEmail.setVisibility(View.GONE);
            navDrawerOffline.setVisibility(View.VISIBLE);
        } else {
            navDrawerEmail.setVisibility(View.VISIBLE);
            navDrawerOffline.setVisibility(View.GONE);
            navDrawerEmail.setText(currentUser.getEmail());

        }
        navDrawerRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
        navDrawerLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

/////

        Toolbar topToolBar = (Toolbar) findViewById(R.id.top_toolbar_main);


        topToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.openDrawer(GravityCompat.START);
            }
        });

        final SearchView searchView = findViewById(R.id.edit_text_search);


        emptyState = findViewById(R.id.empty_state_view);
        emptyStateImage = findViewById(R.id.empty_Recycler_Image);
        emptyStateText = findViewById(R.id.empty_Recycler_Text);

        recyclerViewTypeTextView = findViewById(R.id.notes_type_textview);
        recyclerViewPinNoteTextView = findViewById(R.id.notes_pinned_textview);
        recyclerViewTextInPinLL = findViewById(R.id.notes_type_inPin_textview);

        pinnedView = findViewById(R.id.pin_layout);

        FloatingActionButton addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AddEditActivity.class);
//                startActivity(i);
                startActivityForResult(i, REQ_CODE_FOR_ADD_RESULT);
            }
        });

        apiMethode = new ApiMethode(getApplication());
        noteRepository = new NoteRepository(getApplication());


        ////
        ///pull to refresh///
        swipeToRefresh = findViewById(R.id.swipe_to_refresh);
        swipeToRefresh.setProgressViewOffset(true, 120, 300);
        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isOffline) {
                    getNotes();
                    if (InternetCheck.isNetworkConnected(getApplicationContext())) {
                        isSyncing = true;
                    } else {
                        Snackbar.make(mDrawer, "اتصال اینرنت برقرار نیست", Snackbar.LENGTH_SHORT).setAnchorView(addButton).show();
                        swipeToRefresh.setRefreshing(false);
                    }
                } else {
                    swipeToRefresh.setRefreshing(false);
                    Snackbar.make(mDrawer, "برای همگام سازی نیاز به حساب کابری دارید.", BaseTransientBottomBar.LENGTH_LONG)
                            .setAnchorView(addButton)
                            .setAction("ثبت نام", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setActionTextColor(getResources().getColor(R.color.colorAccent))
                            .show();
                }

            }
        });

        //// sync at program start
        if (!isOffline) {
            getNotes();
            if (InternetCheck.isNetworkConnected(getApplicationContext())) {
                swipeToRefresh.setRefreshing(true);
                isSyncing = true;

            } else {
                addButton.post(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(mDrawer, "اتصال اینرنت برقرار نیست", Snackbar.LENGTH_SHORT)
                                .setAnchorView(addButton)
                                .show();
                    }
                });
            }
        }

        MutableLiveData<Boolean> mutableLiveData = apiMethode.getMutableLiveData();
        mutableLiveData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
//                logoutBtn.setEnabled(aBoolean);
            }
        });
//        //////////sync after db insert or update or delete////////////
//        MutableLiveData<Integer> sync = noteRepository.getSync();
//        sync.observe(this, new Observer<Integer>() {
//            @Override
//            public void onChanged(Integer integer) {
//                swipeToRefresh.setRefreshing(true);
//                apiMethode.getNotes();
//            }
//        });
        ////////error on sync/////////
        firstSync = true;
        MutableLiveData<Integer> syncError = apiMethode.getSyncError();
        syncError.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == 503) {
                    swipeToRefresh.setRefreshing(false);
                    isSyncing = false;

                    Snackbar.make(mDrawer, "سرور در دسترس نیست", Snackbar.LENGTH_SHORT).setAnchorView(addButton).show();
                } else if (integer == 504) {
//                    swipeToRefresh.setRefreshing(false);
                    if (firstSync) {
                        getNotes();
                        isSyncing = true;
//                        swipeToRefresh.setRefreshing(true);
                        firstSync = false;
                    } else {
                        Snackbar.make(mDrawer, "امکان ارتباط با سرور وجود ندارد. ", Snackbar.LENGTH_SHORT).setAnchorView(addButton).show();
                        firstSync = true;
                        swipeToRefresh.setRefreshing(false);
                        isSyncing = false;
                    }

                }
            }
        });
        ////////
        MutableLiveData<List<NoteR>> allNotesMongo = apiMethode.getAllNotes();
        allNotesMongo.observe(this, new Observer<List<NoteR>>() {
            @Override
            public void onChanged(List<NoteR> noteRS) {
                swipeToRefresh.setRefreshing(false);


                //// sync from Room => Mongo
                for (Note noteRoom : allRoomNotes) {
                    boolean delete;
                    NoteR newNoteMongo = new NoteR(noteRoom.getTitle(), noteRoom.getContent());
                    newNoteMongo.setColor(noteRoom.getColor());
                    newNoteMongo.setTimeAddEdit(noteRoom.getTimeAddEdit());
                    newNoteMongo.setTimeReminder(noteRoom.getTimeReminder());
                    newNoteMongo.setRoomId(noteRoom.getId());
                    newNoteMongo.setMongoId(noteRoom.getMongoId());
                    newNoteMongo.setVer(noteRoom.getVer());

                    newNoteMongo.setPinned(noteRoom.isPinned());
                    newNoteMongo.setCreationTime(noteRoom.getCreationTime());
                    //// delete from Mongo
                    if (noteRoom.getDeleted()) {
                        apiMethode.deleteNote(newNoteMongo);

//                        long timeNow = new Date().getTime();
                        if (noteRoom.getTimeReminder() >= 0) {
                            notifAlarmUtil.deleteReminder(noteRoom.getReqCode(), noteRoom.getTitle(), noteRoom.getContent());
                        }
                    }

                    //// insert to Mongo
                    else if (noteRoom.getMongoId().isEmpty()) {
                        newNoteMongo.setSynced(true);
                        newNoteMongo.setIsCheckList(noteRoom.isCheckList());
                        newNoteMongo.setChecklistTick(noteRoom.getChecklistTick());
                        newNoteMongo.setCreationTime(noteRoom.getCreationTime());

                        apiMethode.insertNote(newNoteMongo, noteRoom.getReqCode());

                    } else {
                        delete = true;
                        for (NoteR noteMongo : noteRS) {
                            //// found
                            if (noteMongo.getMongoId().equals(noteRoom.getMongoId())) {
                                delete = false;
                                if (noteRoom.getTimeAddEdit() > noteMongo.getTimeAddEdit()) {
                                    //update mongo

                                    newNoteMongo.setSynced(true);
                                    newNoteMongo.setIsCheckList(noteRoom.isCheckList());
                                    newNoteMongo.setChecklistTick(noteRoom.getChecklistTick());
                                    newNoteMongo.setCreationTime(noteRoom.getCreationTime());

                                    apiMethode.updateNote(newNoteMongo, noteRoom.getReqCode());
                                } else if (noteRoom.getTimeAddEdit() < noteMongo.getTimeAddEdit()) {
                                    // update room
                                    Note updateNoteRoom = new Note(noteMongo.getTitle(), noteMongo.getContent(), noteMongo.getTimeAddEdit(), noteMongo.getColor());
                                    updateNoteRoom.setId(noteRoom.getId());
                                    updateNoteRoom.setSynced(true);
                                    updateNoteRoom.setVer(noteMongo.getVer());
                                    updateNoteRoom.setTimeReminder(noteMongo.getTimeReminder());
                                    updateNoteRoom.setMongoId(noteMongo.getMongoId());
                                    updateNoteRoom.setReqCode(noteRoom.getReqCode());
                                    updateNoteRoom.setChecklistTick(noteMongo.getChecklistTick());
                                    updateNoteRoom.setCheckList(noteMongo.isCheckList());
                                    updateNoteRoom.setPinned(noteMongo.isPinned());
                                    updateNoteRoom.setCreationTime(noteMongo.getCreationTime());

                                    noteRepository.update(updateNoteRoom);

                                    long timeNow = new Date().getTime();
                                    /// if reminder time is not passed
                                    if (updateNoteRoom.getTimeReminder() >= timeNow) {
                                        notifAlarmUtil.updateReminder(updateNoteRoom.getReqCode(), updateNoteRoom.getTitle(), updateNoteRoom.getContent(), updateNoteRoom.getTimeReminder());
                                    } else if (updateNoteRoom.getTimeReminder() == 0 && noteRoom.getTimeReminder() != 0) {
                                        notifAlarmUtil.deleteReminder(updateNoteRoom.getReqCode(), updateNoteRoom.getTitle(), updateNoteRoom.getContent());
                                    }
                                }
                                break;
                            }
                        }
                        if (delete) {
                            // delete note from local db
                            noteRepository.delete(noteRoom);
                            if (noteRoom.getTimeReminder() >= 0) {
                                notifAlarmUtil.deleteReminder(noteRoom.getReqCode(), noteRoom.getTitle(), noteRoom.getContent());
                            }
                        }
                    }
                }
                //// sync from Mongo to Room
                boolean found;
                for (NoteR noteMongo : noteRS) {
                    found = false;

                    for (Note noteRoom : allRoomNotes) {
                        if (noteMongo.getMongoId().equals(noteRoom.getMongoId())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        Note newNoteRoom = new Note(noteMongo.getTitle(), noteMongo.getContent(), noteMongo.getTimeAddEdit(), noteMongo.getColor());
                        newNoteRoom.setSynced(true);
                        newNoteRoom.setVer(noteMongo.getVer());
                        newNoteRoom.setTimeReminder(noteMongo.getTimeReminder());
                        newNoteRoom.setMongoId(noteMongo.getMongoId());
                        newNoteRoom.setChecklistTick(noteMongo.getChecklistTick());
                        newNoteRoom.setCheckList(noteMongo.isCheckList());
                        newNoteRoom.setPinned(noteMongo.isPinned());
                        newNoteRoom.setCreationTime(noteMongo.getCreationTime());

                        int rc = (int) (noteMongo.getTimeAddEdit() / 1000);
                        newNoteRoom.setReqCode(rc);
                        noteRepository.insert(newNoteRoom);

                        long timeNow = new Date().getTime();
                        /// if reminder time is not passed
                        if (newNoteRoom.getTimeReminder() >= timeNow) {
                            notifAlarmUtil.addReminder(newNoteRoom.getReqCode(), newNoteRoom.getTitle(), newNoteRoom.getContent(), newNoteRoom.getTimeReminder());
                        }
                    }
                }
                isSyncing = false;
            }
        });
        //////////

        ////////////////set recyclerView adapter////////////
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);
        RecyclerView recyclerViewPin = findViewById(R.id.recycler_view_pin);
        recyclerViewPin.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPin.setHasFixedSize(false);
        recyclerViewPin.setNestedScrollingEnabled(false);


        adapter = new NoteAdapter();
        adapterPinned = new NoteAdapter();

        recyclerView.setAdapter(adapter);
        recyclerViewPin.setAdapter(adapterPinned);


        ////////////////////refresh recyclerView when notif fired////////////////////////////////
        MutableLiveData<Integer> notifListener = ReminderBroadcast.notifFired;
        notifListener.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                recyclerView.setAdapter(null);
                recyclerView.setAdapter(adapter);
                recyclerViewPin.setAdapter(null);
                recyclerViewPin.setAdapter(adapterPinned);
            }
        });

        /////////////////////////////
        /////////////Observe LiveData<List<Note>> for change///////////////////
        /////////////if list is changed set the new data in adapter////////////
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);

        noteViewModel.getAllVisibleNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                if (notes.isEmpty()) {
                    emptyState.setVisibility(View.VISIBLE);
                } else {
                    emptyState.setVisibility(View.GONE);
                }
            }
        });

        noteViewModel.getAllVisibleNotPinnedNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                adapter.submitList(notes);
            }
        });

        noteViewModel.getAllVisiblePinnedNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                adapterPinned.submitList(notes);
                if (notes.isEmpty()) {
                    recyclerViewPin.setVisibility(View.GONE);
                    recyclerViewPinNoteTextView.setVisibility(View.GONE);
                    recyclerViewTextInPinLL.setVisibility(View.GONE);
                } else {
                    recyclerViewPin.setVisibility(View.VISIBLE);
                    recyclerViewPinNoteTextView.setVisibility(View.VISIBLE);
                    recyclerViewTextInPinLL.setVisibility(View.VISIBLE);

                }
            }
        });


        noteRepository.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                allRoomNotes = notes;
            }
        });
        ///////////////////////////////////////////////


        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!searchView.hasFocus()) {
                    Intent ii = new Intent(MainActivity.this, SearchActivity.class);
                    //////
//                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this,searchView, ViewCompat.getTransitionName(searchView));
                    //////
//                    startActivity(ii,options.toBundle());
                    startActivity(ii);
                }
                searchView.clearFocus();
            }
        });


        //////////////on recycler item click////////////

        adapter.setOnItemClickListener(adapterItemCliclListener);
        adapterPinned.setOnItemClickListener(adapterItemCliclListener);



        ////////////////////swipe to delete//////////////
        recyclerViewSwipe(recyclerView, adapter);
        recyclerViewSwipe(recyclerViewPin, adapterPinned);
        /////////////////////////////////////////////////

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.item_drawer_notes) {
                    if (!item.isChecked()) {
                        emptyStateImage.setImageResource(R.drawable.svg_note_white);
                        emptyStateText.setText("یادداشتی وجود ندارد.");

                        recyclerViewTypeTextView.setVisibility(View.GONE);

                        noteViewModel.getAllReminders().removeObservers(MainActivity.this);
                        noteViewModel.getAllCheckLists().removeObservers(MainActivity.this);

                        noteViewModel.getAllVisibleNotPinnedNotes().observe(MainActivity.this, new Observer<List<Note>>() {
                            @Override
                            public void onChanged(List<Note> notes) {
                                adapter.submitList(notes);

                            }
                        });

                        noteViewModel.getAllVisibleNotes().observe(MainActivity.this, new Observer<List<Note>>() {
                            @Override
                            public void onChanged(List<Note> notes) {
                                if (notes.isEmpty()) {
                                    emptyState.setVisibility(View.VISIBLE);
                                } else {
                                    emptyState.setVisibility(View.GONE);
                                }
                            }
                        });
                        pinnedView.setVisibility(View.VISIBLE);

                    }
                    item.setChecked(true);
                    mDrawer.closeDrawer(GravityCompat.START);
                    return true;
                } else if (item.getItemId() == R.id.item_drawer_reminders) {
//                    if(firstTime){
//                        firstTime = false;
//                    }
                    if (!item.isChecked()) {

                        emptyStateImage.setImageResource(R.drawable.svg_bell_white);
                        emptyStateText.setText("یادآوری وجود ندارد.");
                        recyclerViewTypeTextView.setText("یادآور ها");
                        recyclerViewTypeTextView.setVisibility(View.VISIBLE);

                        noteViewModel.getAllVisibleNotPinnedNotes().removeObservers(MainActivity.this);
                        noteViewModel.getAllCheckLists().removeObservers(MainActivity.this);

                        noteViewModel.getAllVisibleNotes().removeObservers(MainActivity.this);

                        noteViewModel.getAllReminders().observe(MainActivity.this, new Observer<List<Note>>() {
                            @Override
                            public void onChanged(List<Note> notes) {
                                adapter.submitList(notes);
                                if (notes.isEmpty()) {
                                    emptyState.setVisibility(View.VISIBLE);
                                } else {
                                    emptyState.setVisibility(View.GONE);
                                }
                            }
                        });
                        pinnedView.setVisibility(View.GONE);

                    }
                    item.setChecked(true);
                    mDrawer.closeDrawer(GravityCompat.START);
                    return true;
                } else if (item.getItemId() == R.id.item_drawer_checklist) {

                    if (!item.isChecked()) {
                        emptyStateImage.setImageResource(R.drawable.svg_checklist_white);
                        emptyStateText.setText("چک\u200Cلیستی وجود ندارد.");
                        recyclerViewTypeTextView.setText("چک\u200Cلیست ها");
                        recyclerViewTypeTextView.setVisibility(View.VISIBLE);


                        noteViewModel.getAllVisibleNotPinnedNotes().removeObservers(MainActivity.this);
                        noteViewModel.getAllReminders().removeObservers(MainActivity.this);
                        noteViewModel.getAllVisibleNotes().removeObservers(MainActivity.this);

                        noteViewModel.getAllCheckLists().observe(MainActivity.this, new Observer<List<Note>>() {
                            @Override
                            public void onChanged(List<Note> notes) {
                                adapter.submitList(notes);
                                if (notes.isEmpty()) {
                                    emptyState.setVisibility(View.VISIBLE);
                                } else {
                                    emptyState.setVisibility(View.GONE);
                                }
                            }
                        });
                        pinnedView.setVisibility(View.GONE);

                    }
                    item.setChecked(true);
                    mDrawer.closeDrawer(GravityCompat.START);
                    return true;
                } else if (item.getItemId() == R.id.item_drawer_logout) {

                    if (isOffline) {
                        MaterialAlertDialogBuilder mBuilder = new MaterialAlertDialogBuilder(MainActivity.this, R.style.AlertDialogCustomRTL)
                                .setTitle("توجه!")
                                .setMessage("شما به صورت آفلاین از برنامه استفاده می\u200Cکنید و در صورت خروج تمام یادداشت ها پاک خواهند شد.")
                                .setNeutralButton("ثبت نام", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mDrawer.closeDrawer(GravityCompat.START);
                                        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton("بازگشت", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mDrawer.closeDrawer(GravityCompat.START);

                                    }
                                })
                                .setPositiveButton("خروج", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mDrawer.closeDrawer(GravityCompat.START);
                                        ////////////////
                                        TinyDB tinyDB = new TinyDB(getApplicationContext());
                                        int count = 0;
                                        int size = tinyDB.getAll().size();
                                        if (size > 0) {
                                            count = size / 5;
                                        }
                                        for (int i = 0; i < count; i++) {
                                            int reqCode = tinyDB.getInt(i + "r");
                                            String title = tinyDB.getString(reqCode + "t");
                                            String content = tinyDB.getString(reqCode + "c");
                                            long reminderTime = tinyDB.getLong(reqCode + "a");
                                            long timeNow = new Date().getTime();
                                            /// if reminder time is not passed
                                            if (reminderTime >= timeNow) {
                                                notifAlarmUtil.deleteReminder(reqCode, title, content);
                                            }
                                        }
                                        ////////////////
                                        noteRepository.deleteAllNotes();
                                        apiMethode.logout();
                                        Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                        AlertDialog dialog = mBuilder.create();
                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface arg) {
                                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.redDialogNeut));
                            }
                        });
                        dialog.show();
                    } else {
                        mDrawer.closeDrawer(GravityCompat.START);
                        ////////////////
                        TinyDB tinyDB = new TinyDB(getApplicationContext());
                        int count = 0;
                        int size = tinyDB.getAll().size();
                        if (size > 0) {
                            count = size / 5;
                        }
                        for (int i = 0; i < count; i++) {
                            int reqCode = tinyDB.getInt(i + "r");
                            String title = tinyDB.getString(reqCode + "t");
                            String content = tinyDB.getString(reqCode + "c");
                            long reminderTime = tinyDB.getLong(reqCode + "a");
                            long timeNow = new Date().getTime();
                            /// if reminder time is not passed
                            if (reminderTime >= timeNow) {
                                notifAlarmUtil.deleteReminder(reqCode, title, content);
                            }
                        }
                        ////////////////
                        noteRepository.deleteAllNotes();
                        apiMethode.logout();
                        Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                        startActivity(intent);
                        finish();

                    }

                    return true;
                } else if (item.getItemId() == R.id.item_drawer_about) {
                    mDrawer.closeDrawer(GravityCompat.START);

                    Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                    startActivity(intent);
                }
                else if (item.getItemId() == R.id.item_drawer_privacy) {
                    mDrawer.closeDrawer(GravityCompat.START);

                    Intent intent = new Intent(MainActivity.this, PrivacyActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });

    }
////////////////////////////////////On create Ended///////////////////////

    //recyclerview On item clock listener//
    NoteAdapter.OnItemClickListener adapterItemCliclListener = new NoteAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(Note note) {
            Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
            intent.putExtra(AddEditActivity.EXTRA_ID, note.getId());
            intent.putExtra(AddEditActivity.EXTRA_TITLE, note.getTitle());
            intent.putExtra(AddEditActivity.EXTRA_CONTENT, note.getContent());
            intent.putExtra(AddEditActivity.EXTRA_TIME_ADD_EDIT, note.getTimeAddEdit());
            intent.putExtra(AddEditActivity.EXTRA_COLOR, note.getColor());
            intent.putExtra(AddEditActivity.EXTRA_TIME_REMINDER, note.getTimeReminder());
            intent.putExtra(AddEditActivity.EXTRA_MONGOID, note.getMongoId());
            intent.putExtra(AddEditActivity.EXTRA_ISSYNCED, note.getSynced());
            intent.putExtra(AddEditActivity.EXTRA_DELETED, note.getDeleted());
            intent.putExtra(AddEditActivity.EXTRA_VER, note.getVer());
            intent.putExtra(AddEditActivity.EXTRA_REQCODE, note.getReqCode());
            intent.putExtra(AddEditActivity.EXTRA_CHECKED, note.isCheckList());
            intent.putExtra(AddEditActivity.EXTRA_CHECKLIST_TICK, note.getChecklistTick());
            intent.putExtra(AddEditActivity.EXTRA_PINNED, note.isPinned());//

            intent.putExtra(AddEditActivity.EXTRA_NOTE, note);

            startActivityForResult(intent, REQ_CODE_FOR_EDIT_RESULT);
        }
    };


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        Note clickedNote;
        Note redoNote;
        switch (item.getItemId()) {
            case 121:
                clickedNote = adapter.getNoteAt(item.getGroupId());
                redoNote = new Note(clickedNote);

                deleteNoteFunc(clickedNote);

                showRedoSnackBar(redoNote);
                return true;
            case 122:
                clickedNote = adapter.getNoteAt(item.getGroupId());
                clickedNote.setPinned(true);
                clickedNote.setSynced(false);
                clickedNote.setTimeAddEdit(clickedNote.getTimeAddEdit() + 1);
                noteRepository.update(clickedNote);
                sync(3500);///
                return true;
            case 221:
                clickedNote = adapterPinned.getNoteAt(item.getGroupId());
                redoNote = new Note(clickedNote);

                deleteNoteFunc(clickedNote);

                showRedoSnackBar(redoNote);

                return true;
            case 222:
                clickedNote = adapterPinned.getNoteAt(item.getGroupId());
                clickedNote.setPinned(false);
                clickedNote.setSynced(false);
                clickedNote.setTimeAddEdit(clickedNote.getTimeAddEdit() + 1);

                noteRepository.update(clickedNote);
                sync(3500);
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void forceRTLIfSupported() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

    //////////
    /////////////////item touch helper for recycler view//////////////////////
    private void recyclerViewSwipe(RecyclerView recyclerView, NoteAdapter adapter) {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.pallet1))
                        .addActionIcon(R.drawable.ic_outline_delete)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Note deletedNote = adapter.getNoteAt(viewHolder.getAbsoluteAdapterPosition());
                Note redoNote = new Note(deletedNote);
                deleteNoteFunc(deletedNote);
                showRedoSnackBar(redoNote);
            }


        }).attachToRecyclerView(recyclerView);
    }

    private void showRedoSnackBar(Note deletedNote) {
        Snackbar.make(findViewById(R.id.drawer), "یادداشت پاک شد", Snackbar.LENGTH_LONG)
                .setAnchorView(findViewById(R.id.add_button))
                .setAction("برگردان", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (deletedNote.getMongoId().isEmpty()) {
                            noteRepository.insert(deletedNote);
                            sync(3500);///
                        }
                        // else delete it after deleting from mongo
                        else {
                            noteRepository.update(deletedNote);
                            sync(3500);///
                        }

                        /// if reminder time is not passed
                        long timeNow = new Date().getTime();
                        if (deletedNote.getTimeReminder() >= timeNow) {
                            notifAlarmUtil.addReminder(deletedNote.getReqCode(), deletedNote.getTitle(), deletedNote.getContent(), deletedNote.getTimeReminder());
                        }

                    }
                })
                .setActionTextColor(getResources().getColor(R.color.colorAccent))
                .show();
    }

    private void deleteNoteFunc(Note note) {
        /// if note does not exist in mongo delete it
        note.setSynced(false);
        note.setDeleted(true);
        if (note.getMongoId().isEmpty()) {
            noteRepository.delete(note);
            sync(3500);//
        }
        // else delete it after deleting from mongo
        else {
            noteRepository.update(note);
            sync(3500);//
        }

        /// if reminder time is not passed
        if (note.getTimeReminder() >= 0) {
            notifAlarmUtil.deleteReminder(note.getReqCode(), note.getTitle(), note.getContent());
        }
    }


    final Handler handler = new Handler(Looper.getMainLooper());
    Runnable rn = new Runnable() {
        @Override
        public void run() {
            isSyncing = true;
//            swipeToRefresh.setRefreshing(true);
            getNotes();
        }
    };
    /// fix some bugs with delay!//
    private void sync(int delay) {

        handler.removeCallbacks(rn);
        if (!isSyncing) {
            handler.postDelayed(rn, delay);
        }

    }

    private void getNotes() {
        if (InternetCheck.isNetworkConnected(getApplicationContext())) {
            apiMethode.getNotes();
        }
    }


}
