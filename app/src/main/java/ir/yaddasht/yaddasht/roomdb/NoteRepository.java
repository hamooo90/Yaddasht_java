package ir.yaddasht.yaddasht.roomdb;

import android.app.Application;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import ir.yaddasht.yaddasht.model.roomdb.Note;

public class NoteRepository {
    private NoteDao noteDao;
    private LiveData<List<Note>> allNotes;
    private LiveData<List<Note>> allVisibleNotes;
    private LiveData<List<Note>> allRemindersNotes;
    private LiveData<List<Note>> allCheckListsNotes;
    private LiveData<List<Note>> allVisiblePinnedNotes;///////
    private LiveData<List<Note>> allVisibleNotPinnedNotes;

    private LiveData<List<Note>> searchNotes;

    private static MutableLiveData<Integer> sync;


    ///
    static long newId = -1;
    private static List<Note> n;
    private static Note requestedNote;
    private static CountDownLatch mLatch;
    ///


    public NoteRepository(Application application) {
        NoteDatabase database = NoteDatabase.getInstance(application);
        noteDao = database.noteDao();
        allNotes = noteDao.getAllNotes();
        allVisibleNotes = noteDao.getAllVisibleNotes();
        allRemindersNotes = noteDao.getAllReminders();
        allCheckListsNotes = noteDao.getAllCheckLists();
        allVisiblePinnedNotes = noteDao.getAllVisiblePinnedNotes();//////////
        allVisibleNotPinnedNotes = noteDao.getAllVisibleNotPinnedNotes();//////

        sync = new MutableLiveData<>();

    }

    public void insert(Note note){
        new InsertNoteAsyncTask(noteDao).execute(note);
    }

    public void update(Note note){
        new UpdateNoteAsyncTask(noteDao).execute(note);
    }

    public void delete(Note note){
        new DeleteNoteAsyncTask(noteDao).execute(note);
    }

    public void deleteAllNotes(){
        new DeleteAllNoteAsyncTask(noteDao).execute();
    }

    public LiveData<List<Note>> getAllNotes(){
        return allNotes;
    }
    public LiveData<List<Note>> getAllVISIBLENotes(){
        return allVisibleNotes;
    }

    ////////////////////////
    public LiveData<List<Note>> getSearchNotes(String text_search){
        return noteDao.getSearchNotes(text_search);
    }

    public LiveData<List<Note>> getAllReminders(){
        return allRemindersNotes;
    }

    public LiveData<List<Note>> getAllCheckLists(){
        return allCheckListsNotes;
    }

    public LiveData<List<Note>> getAllVisiblePinnedNotes(){
        return allVisiblePinnedNotes;//////////////
    }//////////

    public LiveData<List<Note>> getAllVisibleNotPinnedNotes(){
        return allVisibleNotPinnedNotes;//////////////
    }//////////




    public List<Note> getUnSyncedNotes() {
        mLatch = new CountDownLatch(1);

        new GetUnSyncedAsyncTask(noteDao).execute();
        try {
            mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return n;
    }

    public Note getSingleNoteWithReqCode(int request_code){
        mLatch = new CountDownLatch(1);

        new GetSingleNoteReqTask(noteDao).execute(request_code);
        try {
            mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return requestedNote;
    }



    public static class GetUnSyncedAsyncTask extends AsyncTask<Void,Void,Void>{
        private NoteDao noteDao;
        private GetUnSyncedAsyncTask(NoteDao noteDao) { this.noteDao = noteDao; }
        @Override
        protected Void doInBackground(Void... voids) {
            n = noteDao.getUnSyncedNotes();
            mLatch.countDown();
            return null;
        }
    }

    public static class GetSingleNoteReqTask extends AsyncTask<Integer,Void,Void>{
        private NoteDao noteDao;
        private GetSingleNoteReqTask(NoteDao noteDao) {this.noteDao = noteDao;}

        @Override
        protected Void doInBackground(Integer... integers) {
            requestedNote = noteDao.getSingleNoteWithReqCode(integers[0]);
            mLatch.countDown();
            return null;
        }
    }


    private static class InsertNoteAsyncTask extends AsyncTask<Note,Void,Void>{
        private NoteDao noteDao;

        private InsertNoteAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.insert(notes[0]);
            return null;
        }
    }

    private static class UpdateNoteAsyncTask extends AsyncTask<Note,Void,Void>{
        private NoteDao noteDao;

        private UpdateNoteAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.update(notes[0]);
            return null;
        }
    }

    private static class DeleteNoteAsyncTask extends AsyncTask<Note,Void,Void>{
        private NoteDao noteDao;

        private DeleteNoteAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.delete(notes[0]);
            return null;
        }
    }

    private static class DeleteAllNoteAsyncTask extends AsyncTask<Void,Void,Void>{
        private NoteDao noteDao;

        private DeleteAllNoteAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Void... Voids) {
            noteDao.deleteAllNotes();
            return null;
        }
    }



    public List<Note> getN() {
        return n;
    }

    public void setN(List<Note> n) {
        this.n = n;
    }
}
