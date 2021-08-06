package ir.yaddasht.yaddasht.roomdb;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import ir.yaddasht.yaddasht.model.roomdb.Note;

@Database(entities = Note.class,version = 1)
public abstract class NoteDatabase extends RoomDatabase {

    private static NoteDatabase instance;
    public abstract NoteDao noteDao();
    public static synchronized NoteDatabase getInstance(Context context){
        if(instance == null){
            instance= Room.databaseBuilder(context.getApplicationContext(),
                    NoteDatabase.class,"yaddasht_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
