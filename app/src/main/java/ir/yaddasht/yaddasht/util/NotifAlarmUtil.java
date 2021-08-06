package ir.yaddasht.yaddasht.util;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.Random;

import static android.content.Context.ALARM_SERVICE;

public class NotifAlarmUtil {
    TinyDB tinyDB;
    Context context;

    public NotifAlarmUtil(Context context) {
        tinyDB = new TinyDB(context);
        this.context = context;
    }

    public void addReminder(int reqCode, String title, String content, long reminderTime) {
//        int unique_int = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
//        tinyDB.putInt();
//        String keyTitle = reqCode+"t";
//        tinyDB.clear();
        int tinyCount = 0;
        if(tinyDB.getAll().size()>0){
            tinyCount = tinyDB.getAll().size()/5;
        }
        tinyDB.putInt(tinyCount+"r",reqCode);
        tinyDB.putInt(reqCode+"id",tinyCount);
        tinyDB.putString(reqCode+"t",title);
        tinyDB.putString(reqCode+"c",content);
        tinyDB.putLong(reqCode+"a",reminderTime);
//        tinyDB.f

//        tinyDB.putInt();
        Intent i = new Intent(context, ReminderBroadcast.class);
        i.putExtra("content", content);
        i.putExtra("title", title);
        //
        i.putExtra("reqcode",reqCode);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, reqCode, i, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.d("TAGTAG", "onAdd: " + reqCode);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Random rnd = new Random();
        Log.d("TAGTAG", "reminer: " + reminderTime);

        reminderTime = reminderTime + (rnd.nextInt(1000) + 1);
        Log.d("TAGTAG", "reminer Add: " + reminderTime);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
        }

    }

    public void updateReminder(int reqCode, String title, String content, long reminderTime) {

        //////////////////////
//        int tinyCount = 0;
//        if(tinyDB.getAll().size()>0){
//            tinyCount = tinyDB.getAll().size()/4;
//        }
//        tinyDB.putInt(tinyCount+1+"r",reqCode);
        tinyDB.putString(reqCode+"t",title);
        tinyDB.putString(reqCode+"c",content);
        tinyDB.putLong(reqCode+"a",reminderTime);
        //////////////

        Intent i = new Intent(context, ReminderBroadcast.class);
        i.putExtra("content", content);
        i.putExtra("title", title);
        //
        i.putExtra("reqcode",reqCode);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, reqCode, i, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntent.cancel();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        pendingIntent = PendingIntent.getBroadcast(context,
                reqCode, i, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.d("TAGTAG", "onUpdate: " + reqCode);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
        }

    }

    public void deleteReminder(int reqCode, String title, String content) {

        ///////////////
        int tinyCount = 0;
        if(tinyDB.getAll().size()>0){
            tinyCount = tinyDB.getAll().size()/4;
        }
//        tinyDB.remove(reqCode+"r");//reqCode
//        tinyDB.remove(reqCode+"t");//title
//        tinyDB.remove(reqCode+"c");//content
//        tinyDB.remove(reqCode+"a");//alertTime

        tinyDB.remove(tinyDB.getInt(reqCode+"id")+"r");

        tinyDB.remove(reqCode+"id");//id
        tinyDB.remove(reqCode+"t");//title
        tinyDB.remove(reqCode+"c");//content
        tinyDB.remove(reqCode+"a");//alertTime

//        tinyDB.putInt(tinyDB.getInt(reqCode+"id")+"r",reqCode);
//        tinyDB.putInt(reqCode+"id",reqCode);
//        tinyDB.putString(reqCode+"t",title);
//        tinyDB.putString(reqCode+"c",content);
//        tinyDB.putLong(reqCode+"a",reminderTime);
        ///////////////
//
        Intent i = new Intent(context, ReminderBroadcast.class);
        i.putExtra("content", content);
        i.putExtra("title", title);
        //
        i.putExtra("reqcode",reqCode);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, reqCode, i, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntent.cancel();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);


    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Yaddasht";
            String description = "this is description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyme", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }
}
