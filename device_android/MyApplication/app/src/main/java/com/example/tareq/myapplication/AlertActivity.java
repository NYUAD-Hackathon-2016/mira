package com.example.tareq.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AlertActivity extends Activity {
    byte eventTracker = -1;
    MediaPlayer mPlayer;
    private long lastClickTimestamp;
    private Handler handler = new Handler();
    private Firebase help;
    boolean alarmSetted = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        MainActivity.sUsbController.send(new byte[]{0x00});

        lastClickTimestamp = System.currentTimeMillis();
        alarmSetted =true;
         BackgroundJob job = new BackgroundJob();
        handler.postDelayed(job, 10 * 1000);


        Firebase.setAndroidContext(this);

        help = new Firebase("https://miraapp.firebaseio.com/android/saving-data/miraHelp");

        Log.d("TareqTAG", "The app is running");
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        mPlayer = MediaPlayer.create(AlertActivity.this, R.raw.recordedmessage);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    AlertActivity.this.runOnUiThread(new Runnable() {
                                                         @Override
                                                         public void run() {
                                                             Calendar calendar = GregorianCalendar.getInstance();
                                                             if (calendar.get(Calendar.AM_PM) == Calendar.AM)
                                                                 if (calendar.get(Calendar.MINUTE) < 10)
                                                                     ((TextView) findViewById(R.id.textView)).setText(calendar.get(Calendar.HOUR) + " : 0" + calendar.get(Calendar.MINUTE) + " " + "AM");
                                                                 else
                                                                     ((TextView) findViewById(R.id.textView)).setText(calendar.get(Calendar.HOUR) + " : " + calendar.get(Calendar.MINUTE) + " " + "AM");

                                                             else if (calendar.get(Calendar.MINUTE) < 10)
                                                                 ((TextView) findViewById(R.id.textView)).setText(calendar.get(Calendar.HOUR) + " : 0" + calendar.get(Calendar.MINUTE) + " " + "PM");
                                                             else
                                                                 ((TextView) findViewById(R.id.textView)).setText(calendar.get(Calendar.HOUR) + " : " + calendar.get(Calendar.MINUTE) + " " + "PM");

                                                         }
                                                     }

                    );
                    try

                    {
                        Thread.sleep(1000);
                    } catch (
                            Exception e
                            )

                    {

                    }
                }
            }
        }

        ).start();
        mPlayer.setLooping(true);
        mPlayer.start();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (MainActivity.sUsbController != null)
                        eventTracker = MainActivity.sUsbController.eventTracker;
                    try {
                        if (eventTracker == 1 && !MainActivity.sUsbController.eventFired) {
                            MainActivity.sUsbController.eventFired = true;
                            MainActivity.sUsbController.send(new byte[]{0x00});
                            mPlayer.stop();
                            closeActivity();
                            alarmSetted = false;

                            break;
                        }
                        Thread.sleep(10);
                    } catch (Exception e) {
                    }

                }
            }
        }).start();

    }

    void closeActivity() {
        AlertActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(AlertActivity.this, MainActivity.class));
            }
        });
    }

    public class BackgroundJob implements Runnable {

        private boolean done = false;

        // meanwhile in job:
        public void run() {

            if ((lastClickTimestamp > 0 && System.currentTimeMillis() - lastClickTimestamp > 10 * 1000) && alarmSetted) {
                pushHelp();
            }

            if (!done) {
                // reschedule us to continue working
                handler.postDelayed(this, 10 * 1000);
            }
        }
    }


    public void pushHelp() {
        help.setValue(1);

    }

    public void help(View view) {
        pushHelp();
    }

}
