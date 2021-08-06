package ir.yaddasht.yaddasht.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


import java.util.Date;

public class ReBootBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {


//        createNotificationChannel(context);
//        Log.d("TAGTAG", "starting broadcast receiver ");


        NotifAlarmUtil notifAlarmUtil = new NotifAlarmUtil(context);

        TinyDB tinyDB = new TinyDB(context);
        int count = 0;
        int size = tinyDB.getAll().size();
        if(size>0){
            count = size/5;
        }
        for(int i = 0 ; i<count;i++){
            int reqCode = tinyDB.getInt(i+"r");

            String title = tinyDB.getString(reqCode+"t");
            String content = tinyDB.getString(reqCode+"c");
            long reminderTime = tinyDB.getLong(reqCode+"a");
//            Log.d("TAGTAG", "title:  "+title);
//            Log.d("TAGTAG", "content:  "+content);
//            Log.d("TAGTAG", "reqCode:  "+reqCode);
//            Log.d("TAGTAG", "reminderTime:  "+reminderTime);

            long timeNow = new Date().getTime();
            /// if reminder time is not passed
            if (reminderTime >= timeNow) {

                notifAlarmUtil.addReminder(reqCode, title, content, reminderTime);

                    Log.d("TAGTAG", "alarm add: " + title + " "+ reqCode);
            }

        }


//        Log.d("TAGTAG", "onReceive:  ");
//        Toast.makeText(context,"asdasdasd",Toast.LENGTH_SHORT).show();


    }

//    private void createNotificationChannel(Context context) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = "MyReminderChannel";
//            String description = "this is description";
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel("notifyme", name, importance);
//            channel.setDescription(description);
//
//            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//
//        }
//    }
}
