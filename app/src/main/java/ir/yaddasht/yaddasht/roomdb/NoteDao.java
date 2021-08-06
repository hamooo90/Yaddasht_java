package ir.yaddasht.yaddasht.roomdb;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ir.yaddasht.yaddasht.model.roomdb.Note;

@Dao
public interface NoteDao {

    @Insert
    void insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    @Query("DELETE FROM note_table")
    void deleteAllNotes();

    @Query("SELECT * FROM note_table ORDER BY creationTime DESC")
    LiveData<List<Note>> getAllNotes();

    @Query("SELECT * FROM note_table WHERE timeReminder > 0 AND NOT deleted ORDER BY creationTime DESC")
    LiveData<List<Note>> getAllReminders();


    @Query("SELECT * FROM note_table WHERE isCheckList AND NOT deleted ORDER BY creationTime DESC")
    LiveData<List<Note>> getAllCheckLists();

    @Query("SELECT * FROM note_table WHERE NOT deleted ORDER BY creationTime DESC")
    LiveData<List<Note>> getAllVisibleNotes();

    @Query("SELECT * FROM note_table WHERE NOT deleted AND NOT pinned ORDER BY creationTime DESC")
    LiveData<List<Note>> getAllVisibleNotPinnedNotes();

    @Query("SELECT * FROM note_table WHERE NOT deleted AND pinned ORDER BY creationTime DESC")////
    LiveData<List<Note>> getAllVisiblePinnedNotes();////

    @Query("SELECT * FROM note_table WHERE NOT isSynced ")
    List<Note> getUnSyncedNotes();


    ///////////
    @Query("SELECT * FROM note_table WHERE title LIKE :text_search OR content LIKE :text_search")
    LiveData<List<Note>> getSearchNotes(String text_search);

    @Query("SELECT * FROM note_table WHERE reqCode = :request_code LIMIT 1")
    Note getSingleNoteWithReqCode(int request_code);

}
