package ir.yaddasht.yaddasht.retrofit;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import ir.yaddasht.yaddasht.model.retro.NoteR;
import ir.yaddasht.yaddasht.model.retro.User;
import ir.yaddasht.yaddasht.view.WelcomeActivity;
import ir.yaddasht.yaddasht.model.roomdb.Note;
import ir.yaddasht.yaddasht.roomdb.NoteRepository;
import ir.yaddasht.yaddasht.util.TinyDB;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiMethode  {
    private JsonApi jsonApi;
    private NoteRepository noteRepository;

    /////
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TOKEN = "token";

    public static User currentUser;
    TinyDB tinyDB;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    /////
    private MutableLiveData<Boolean> mutableLiveData;
    private MutableLiveData<List<NoteR>> allNotes;
    private MutableLiveData<Integer> syncError;
    ////
    private  MutableLiveData<Integer> isLoggedIn;
    private  MutableLiveData<Integer> isRegistered;
    ///
    private  MutableLiveData<Integer> isVerified;

    private  MutableLiveData<Integer> isResend;


    public ApiMethode(Application application) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("Put your Rest Api Address here")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonApi = retrofit.create(JsonApi.class);
        noteRepository = new NoteRepository(application);
        /////
        mutableLiveData = new MutableLiveData<>();
        allNotes = new MutableLiveData<>();

        isLoggedIn = new MutableLiveData<>();
        isRegistered = new MutableLiveData<>();

        isVerified = new MutableLiveData<>();
        isResend = new MutableLiveData<>();

        //
//        token = new MutableLiveData<>();
        sharedPreferences = application.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        syncError = new MutableLiveData<>();
        tinyDB = new TinyDB(application);

    }

    public void login(User user){
        Call<User> call = jsonApi.login((user));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(!response.isSuccessful()){

                    if (response.code() == 403) {
                        currentUser = user;
                    } else {
                        currentUser = null;
                    }
                    isLoggedIn.setValue(response.code());
                    return;
                }

//                Log.d("DAGDAG", response.body().getToken());
                currentUser = user;

//                Headers headers = response.headers()

                editor.putString(TOKEN,response.headers().get("auth-token"));

                Gson gson = new Gson();
//                Log.d("TAGTAG", "onResponse: "+response.body().getEmail());
                String jsonUser = gson.toJson(response.body());
                editor.putString("currentUser",jsonUser);
                editor.remove(WelcomeActivity.ISOFFLINE);

                editor.apply();

                isLoggedIn.setValue(200);

//                token.setValue(response.body().getToken());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                currentUser =null;
                isLoggedIn.setValue(408);
            }
        });
    }


    public void register(User user){
        Call<User> call = jsonApi.register(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(!response.isSuccessful()){

                    isRegistered.setValue(response.code());
                    currentUser=null;
                    return;
                }
                currentUser = user;
                isRegistered.setValue(201);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                currentUser = null;
                isRegistered.setValue(408);//timeout
            }
        });
    }

    public void verify(User user){
        Call<User> call = jsonApi.verify(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(!response.isSuccessful()){
                    isVerified.setValue(response.code());
                    return;
                }
                //success
                isVerified.setValue(200);
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                isVerified.setValue(408);


            }
        });
    }

    public void resend(User user){
        Call<User> call = jsonApi.resend(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(!response.isSuccessful()){
                    isResend.setValue(response.code());
                    return;
                }
                //success
                isResend.setValue(200);
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                isResend.setValue(408);


            }
        });
    }

    public void logout(){

        editor.putString(TOKEN,"");
        editor.remove(TOKEN);
        editor.remove(WelcomeActivity.ISOFFLINE);
        editor.apply();
        tinyDB.clear();
        isLoggedIn.setValue(0);
    }

    public void getNotes(){
        Call<List<NoteR>> call = jsonApi.getNotes(sharedPreferences.getString(TOKEN,""));
        syncError.setValue(0);
//        Log.d("TAGTAG", "method ran: ");

        call.enqueue(new Callback<List<NoteR>>() {

            @Override
            public void onResponse(Call<List<NoteR>> call, Response<List<NoteR>> response) {
                if(!response.isSuccessful()){

//                    Log.d("TAGTAG", "onResponse: "+response.toString()+"\n"+response.message()+"\n"+response.code());
                    syncError.setValue(response.code());
                    return;
                }
                allNotes.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<NoteR>> call, Throwable t) {
                Log.d("TAGTAG", "onFailure: "+t.getMessage());

                syncError.setValue(504);
                int a=0;
            }
        });
    }

    public void deleteNote(NoteR noteR){
        ////
        mutableLiveData.setValue(false);
        ////
        Call<Void> call = jsonApi.deleteNote(sharedPreferences.getString(TOKEN,""), noteR.getMongoId());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                ////
                mutableLiveData.setValue(true);
                ////
                if(!response.isSuccessful()){
                    return;
                }
                Note deleteNote = new Note(noteR.getTitle(),noteR.getContent(),noteR.getTimeAddEdit(),noteR.getColor());
                deleteNote.setId((int) noteR.getRoomId());

                noteRepository.delete(deleteNote);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    public void updateNote(NoteR noteR, int reqCode){
        Call<NoteR> call = jsonApi.updateNote(sharedPreferences.getString(TOKEN,""), noteR.getMongoId(),noteR);

        call.enqueue(new Callback<NoteR>() {
            @Override
            public void onResponse(Call<NoteR> call, Response<NoteR> response) {
                if(!response.isSuccessful()){
                    return;
                }
                Note updateNote = new Note(noteR.getTitle(),noteR.getContent(),noteR.getTimeAddEdit(),noteR.getColor());
                updateNote.setMongoId(noteR.getMongoId());
                updateNote.setTimeReminder(noteR.getTimeReminder());
                updateNote.setId((int) noteR.getRoomId());
                updateNote.setSynced(true);
                updateNote.setVer(noteR.getVer());
                updateNote.setReqCode(reqCode);
                updateNote.setCheckList(noteR.isCheckList());
                updateNote.setChecklistTick(noteR.getChecklistTick());
                updateNote.setPinned(noteR.isPinned());
                updateNote.setCreationTime(noteR.getCreationTime());
                noteRepository.update(updateNote);
                Log.d("TAGTAG", "afterUp: "+updateNote.getReqCode());

            }

            @Override
            public void onFailure(Call<NoteR> call, Throwable t) {

            }
        });
    }

    public void insertNote(NoteR note,int reqCode){
        Call<NoteR> call = jsonApi.insertNote(sharedPreferences.getString(TOKEN,""), note);

        call.enqueue(new Callback<NoteR>() {
            @Override
            public void onResponse(Call<NoteR> call, Response<NoteR> response) {
                if(!response.isSuccessful()){
                    return;
                }
                Note newNote = new Note(response.body().getTitle(),response.body().getContent(),
                        response.body().getTimeAddEdit(),response.body().getColor());
                newNote.setSynced(true);
                newNote.setMongoId(response.body().getMongoId());
                newNote.setId((int) response.body().getRoomId());
                newNote.setTimeReminder(response.body().getTimeReminder());
                newNote.setVer(note.getVer());
                newNote.setReqCode(reqCode);

                ///////////new////
                newNote.setCheckList(note.isCheckList());
                newNote.setChecklistTick(note.getChecklistTick());
                newNote.setPinned(note.isPinned());
                newNote.setCreationTime(note.getCreationTime());
                ///////////new////

                noteRepository.update(newNote);
            }

            @Override
            public void onFailure(Call<NoteR> call, Throwable t) {
//                int b = 0;
            }
        });
    }
    ////
    public MutableLiveData<Boolean> getMutableLiveData(){
        return mutableLiveData;
    }
    public MutableLiveData<List<NoteR>> getAllNotes() {
        return allNotes;
    }

    public MutableLiveData<Integer> getSyncError(){
        return syncError;
    }

    public MutableLiveData<Integer> getIsLoggedIn() {
        return isLoggedIn;
    }
    public MutableLiveData<Integer> getIsRegistered() { return  isRegistered; }

    ///
    public  MutableLiveData<Integer> getIsVerified(){return isVerified;}
    public  MutableLiveData<Integer> getIsResend(){return isResend;}

}
