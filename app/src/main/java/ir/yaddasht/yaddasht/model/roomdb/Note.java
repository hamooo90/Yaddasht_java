package ir.yaddasht.yaddasht.model.roomdb;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "note_table")
public class Note implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String content;
    private long timeAddEdit;//////milli sec time

    private long timeReminder;//////milli sec time



    private long creationTime;

    private int color;

    private String mongoId;
    private boolean isSynced;
    private boolean deleted;

    private int ver;

    private int reqCode;

    private boolean isCheckList;
    private String checklistTick;

    private boolean pinned;//////

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

//    private int isReminder;/////true / false
//    private int repeatRemind;/////no daily weekly .....
//
//    private int isArchived;/// true / false ///
//    private int isTrashed;/// true / false ///
//    private int timeTrashed;//////milli sec time

    public void setTimeAddEdit(long timeAddEdit) {
        this.timeAddEdit = timeAddEdit;
    }

    public Note(Note note) {
        this.id = note.getId();
        this.title = note.getTitle();
        this.content = note.getContent();
        this.timeAddEdit = note.getTimeAddEdit();
        this.timeReminder = note.getTimeReminder();
        this.color = note.getColor();
        this.mongoId = note.getMongoId();
        this.isSynced = note.getSynced();
        this.deleted = note.getDeleted();
        this.ver = note.getVer();
        this.reqCode = note.getReqCode();
        this.isCheckList = note.isCheckList();
        this.checklistTick = note.getChecklistTick();
        this.pinned = note.isPinned();
        this.creationTime = note.getCreationTime();
    }

    public Note(String title, String content, long timeAddEdit, int color) {
        this.title = title;
        this.content = content;
        this.timeAddEdit = timeAddEdit;
        this.color = color;
        this.timeReminder = 0;
//        this.isChecklist=0;//false
        mongoId = "";
        isSynced = false;
        deleted = false;

        ver = 1;
//        Random rnd = new Random();
        reqCode = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
//        reqCode = reqCode+rnd.nextInt(100);

        isCheckList = false;
        checklistTick = "";

        pinned = false;////////
        creationTime = 0;

    }
    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public long getTimeAddEdit() {
        return timeAddEdit;
    }

    public int getColor() {
        return color;
    }

    public void setTimeReminder(long timeInMilli) {
        this.timeReminder = timeInMilli;
    }

    public long getTimeReminder() {
        return timeReminder;
    }

    public String getMongoId() {
        return mongoId;
    }

    public void setMongoId(String mongoId) {
        this.mongoId = mongoId;
    }

    public boolean getSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    public boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public int getVer() {
        return ver;
    }

    public void setVer(int ver) {
        this.ver = ver;
    }

    public int getReqCode() {
        return reqCode;
    }

    public void setReqCode(int reqCode) {
        this.reqCode = reqCode;
    }

    public boolean isCheckList() {
        return isCheckList;
    }

    public void setCheckList(boolean checkList) {
        isCheckList = checkList;
    }

    public String getChecklistTick() {
        return checklistTick;
    }

    public void setChecklistTick(String checklistTick) {
        this.checklistTick = checklistTick;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
