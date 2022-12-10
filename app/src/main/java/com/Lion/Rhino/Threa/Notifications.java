package com.Lion.Rhino.Threa;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AppCompatActivity;

public class Notifications extends AppCompatActivity {

    //노티 에딧텍스트 추가 하기!
    NotificationCompat.Action replyAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        processInlineReply(getIntent());
        finish();


    }

    public void processInlineReply(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);

        String str = "";

        if (remoteInput != null) {
            CharSequence reply = remoteInput.getCharSequence("key reply");
            assert reply != null;
            str = intent.getStringExtra("Data") + "\n" + reply;
        }

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, str);
        sendIntent.setType("text/plain");
        PendingIntent share = PendingIntent.getActivity(this, 0, Intent.createChooser(sendIntent, str), PendingIntent.FLAG_UPDATE_CURRENT);


        String replyLabel = "Enter";

        //Initialise RemoteInput
        RemoteInput remoteInputs = new RemoteInput.Builder("key reply")
                .setLabel(replyLabel)
                .build();

        //PendingIntent that restarts the current activity instance.
        Intent resultIntent = new Intent(this, Notifications.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        resultIntent.putExtra("Data", str);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 5, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        //Notifications Action with RemoteInput instance added.
        replyAction = new NotificationCompat.Action.Builder(
                android.R.drawable.sym_action_chat, "입력", resultPendingIntent)
                .addRemoteInput(remoteInputs)
                .setAllowGeneratedReplies(true)
                .build();

        //Notifications.Action instance added to Notifications Builder.


        NotificationCompat.Builder notice = new NotificationCompat.Builder(this,"com.Lion.Rhino.Threa");
        notice.setPriority(2);
        notice.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher));
        notice.setSmallIcon(R.mipmap.ic_launcher);
        notice.setVisibility(1);
        notice.setCategory("msg");
        notice.setContentTitle("메모");
        notice.setContentText(str);
        notice.setOngoing(false);
        Intent notint = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        notint.setData(Uri.parse("package:com.Lion.Rhino.Threa"));
        PendingIntent conin = PendingIntent.getActivity(this.getApplicationContext(), 3, notint, 0);
        notice.setContentIntent(conin);
        notice.addAction(replyAction);
        notice.addAction(1, "공유", share);
        notice.addAction(1, "복사", PendingIntent.getActivity
                (this, 0, new Intent(this, copy.class).putExtra("Data", str), PendingIntent.FLAG_UPDATE_CURRENT));
        notice.setStyle(new NotificationCompat.BigTextStyle().bigText(str));
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert mNotificationManager != null;
        mNotificationManager.notify(35, notice.build());

        finish();
    }
}
