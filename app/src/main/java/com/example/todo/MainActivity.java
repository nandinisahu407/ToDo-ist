package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN=5000;   //5 sec baad screen khud chale jayegi then dashboard will come

    private static final int NOTIFICATION_ID=100;
    private static final int REQ_CODE=100;
    private static final String  CHANNEL_ID="MY Channel";

    //variables
    Animation topAnim,bottomAnim,slidein;
    ImageView image;
    TextView logo,slogan,name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TO HIDE READYMADE STATUS BAR
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //animation
        topAnim= AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim= AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
        slidein=AnimationUtils.loadAnimation(this,R.anim.slide_in);

        image=findViewById(R.id.imageView);
        logo=findViewById(R.id.textView);
        slogan=findViewById(R.id.textView2);
        name=findViewById(R.id.creator);

        image.setAnimation(topAnim);
        logo.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);
        name.setAnimation(slidein);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(MainActivity.this,SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_SCREEN);

//        Drawable drawable= ResourcesCompat.getDrawable(getResources(),R.drawable.calendar_badge3,null);
//        BitmapDrawable bitmapdrawable=(BitmapDrawable) drawable;
//        Bitmap largeicon=bitmapdrawable.getBitmap();
//
//
//        NotificationManager nm=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        Notification notificataion;
//
//        Intent inotify=new Intent(getApplicationContext(),MainActivity.class);
//        inotify.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        PendingIntent pi=PendingIntent.getActivity(this,REQ_CODE,inotify,PendingIntent.FLAG_UPDATE_CURRENT);
//
//        //big picture notification
//        Notification.BigPictureStyle bigpicturestyle=new Notification.BigPictureStyle()
//                .bigPicture(bitmapdrawable.getBitmap())
//                .bigLargeIcon(largeicon)
//                .setBigContentTitle("Image sent")
//                .setSummaryText("Image message");
//
//
//
//
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
//            notificataion==new Notification.Builder(this)
//                    .setSmallIcon(R.drawable.calendar_badge3)
//                    .setContentText("New Message")
//                    .setSubText("New Message From Nandini")
//                    .setContentIntent(pi)
//                    .setChannelId(CHANNEL_ID)
//                    .build();
//            nm.createNotificationChannel(new NotificationChannel(CHANNEL_ID,"New Channel",NotificationManager.IMPORTANCE_HIGH));
//
//        }
//
//


    }
}