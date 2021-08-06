package ir.yaddasht.yaddasht.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.MutableLiveData;

import ir.yaddasht.yaddasht.view.AddEditActivity;
import ir.yaddasht.yaddasht.view.MainActivity;
import ir.yaddasht.yaddasht.R;

import java.util.Date;
import java.util.Random;

public class ReminderBroadcast extends BroadcastReceiver {
     public static MutableLiveData<Integer> notifFired = new MutableLiveData<>();



    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyme", "reminder", importance);
            channel.setDescription("desc");

//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }

//        Log.d("TAGTAG", "onReceive: ");
        String message = intent.getStringExtra("content");
        String title = intent.getStringExtra("title");

        //
        int reqcode = intent.getIntExtra("reqcode",0);
//        int notifId = intent.getIntExtra("Id",0);

        notifFired.setValue(reqcode);


        long[] pattern = {500,500};
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyme")
                .setSmallIcon(R.drawable.ic_alarm)
//                .setContentInfo("info")
                .setLights(Color.BLUE,500,500)
                .setVibrate(pattern)
                .setSound(uri)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if(!title.isEmpty()){
            builder.setContentTitle(title);
        }
        if(!message.isEmpty()){
            builder.setContentText(message);

        }

        Random rnd = new Random();
        int unique_int = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        unique_int = unique_int + rnd.nextInt(59);
        Log.d("TAGTAG", "unique_int: "+ unique_int);


        ////////
        Intent backIntent = new Intent(context, MainActivity.class);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Intent i = new Intent(context, AddEditActivity.class);
        i.putExtra("reqcode",reqcode);///
        i.putExtra("unique_int",unique_int);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivities(context,reqcode,new Intent[]{backIntent,i},PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(contentIntent);

//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
//        notificationManager.notify(200, builder.build());

        notificationManager.notify(unique_int, builder.build());
    }

    public MutableLiveData<Integer> getNotifFired(){
        return notifFired;
    }

}
