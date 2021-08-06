package ir.yaddasht.yaddasht.model.retro;

public class NoteR {
    private String _id;
    private String title;
    private String content;

    private long timeAddEdit;
    private long timeReminder;

    private int color;

    private long roomId;
    private boolean isSynced;
    private boolean deleted;

    private int ver;

    private boolean isCheckList;
    private String checklistTick;

    private boolean pinned;//////
    private long creationTime;



    public NoteR(String title, String content){
        this.title = title;
        this.content = content;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public boolean isCheckList() {
        return isCheckList;
    }

    public void setIsCheckList(boolean checkList) {
        isCheckList = checkList;
    }

    public String getChecklistTick() {
        return checklistTick;
    }

    public void setChecklistTick(String checklistTick) {
        this.checklistTick = checklistTick;
    }

    public String getMongoId() {
        return _id;
    }

    public void setMongoId(String mongoId) {
        this._id = mongoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimeAddEdit() {
        return timeAddEdit;
    }

    public void setTimeAddEdit(long timeAddEdit) {
        this.timeAddEdit = timeAddEdit;
    }

    public long getTimeReminder() {
        return timeReminder;
    }

    public void setTimeReminder(long timeReminder) {
        this.timeReminder = timeReminder;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    public boolean isDeleted() {
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
}
