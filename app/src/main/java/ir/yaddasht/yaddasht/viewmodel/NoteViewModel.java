package ir.yaddasht.yaddasht.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import ir.yaddasht.yaddasht.model.roomdb.Note;
import ir.yaddasht.yaddasht.roomdb.NoteRepository;

public class NoteViewModel extends AndroidViewModel {
    private NoteRepository repository;
    private LiveData<List<Note>> allNotes;
//    private LiveData<List<Note>> allNotes2;

    private LiveData<List<Note>> allReminders;
    private LiveData<List<Note>> allCheckLists;
    private LiveData<List<Note>> allVisiblePinnedNotes;
    private LiveData<List<Note>> allVisibleNotPinnedNotes;


    public NoteViewModel(@NonNull Application application) {
        super(application);
        repository = new NoteRepository(application);
        allNotes = repository.getAllVISIBLENotes();
        allReminders = repository.getAllReminders();
        allCheckLists = repository.getAllCheckLists();
        allVisiblePinnedNotes = repository.getAllVisiblePinnedNotes();
        allVisibleNotPinnedNotes = repository.getAllVisibleNotPinnedNotes();

    }

    public void insert(Note note) {
        repository.insert(note);
    }

    public void update(Note note) {
        repository.update(note);
    }

    public void delete(Note note) {
        repository.delete(note);
    }

    public void deleteAllNotes() {
        repository.deleteAllNotes();
    }

    public LiveData<List<Note>> getAllVisibleNotes() {
        return allNotes;
    }

    public LiveData<List<Note>> getAllReminders() {
        return allReminders;
    }

    public LiveData<List<Note>> getAllCheckLists() {
        return allCheckLists;
    }

    public LiveData<List<Note>> getAllVisiblePinnedNotes() {
        return allVisiblePinnedNotes;
    }

    public LiveData<List<Note>> getAllVisibleNotPinnedNotes() {
        return allVisibleNotPinnedNotes;
    }


    public LiveData<List<Note>> getSearchNotes(String text_search) {
        return repository.getSearchNotes(text_search);
    }
//    public void refreshReminders(){
////        allReminders = null;
////        allReminders = repository.getAllReminders();
//    }
}
