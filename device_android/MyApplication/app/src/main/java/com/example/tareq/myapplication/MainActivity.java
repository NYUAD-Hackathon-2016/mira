package com.example.tareq.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private static final int VID = 0x2a03;
    private static final int PID = 0x0043;
    public static UsbController sUsbController;
    TextView textView;
    byte bytes[] = new byte[3];
    CarouselView carouselView;
    int[] sampleImages = {R.drawable.image1, R.drawable.image2};
    int counter = 0;
    private Firebase ref;
    private Firebase help;

    byte eventTracker = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        carouselView = (CarouselView) findViewById(R.id.carouselView);
        carouselView.setPageCount(sampleImages.length);
        carouselView.setOrientation(1);
        ImageListener imageListener = new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                imageView.setImageResource(sampleImages[position]);
            }
        };
        carouselView.setImageListener(imageListener);

        Firebase.setAndroidContext(this);

        ref = new Firebase("https://miraapp.firebaseio.com/android/saving-data/miraPills");

        help = new Firebase("https://miraapp.firebaseio.com/android/saving-data/miraHelp");

        Query queryRef = ref.limitToLast(1);

        queryRef.addChildEventListener(new ChildEventListener() {

            ArrayList<Medecine> listMedecines = new ArrayList<Medecine>();

            boolean dataRetrieved = false;
            //  int childCount =0 ;

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                startActivity(new Intent(MainActivity.this, AlertActivity.class));
                //  if (!dataRetrieved) {
                // childCount ++ ;
                Medecine changedMedecine = (Medecine) dataSnapshot.getValue(Medecine.class);
                listMedecines.add(changedMedecine);
                ref.removeValue();
                //    if(childCount==(int)dataSnapshot.getChildrenCount())
                Log.v("dataChanged", changedMedecine.getName());
                //    dataRetrieved = true;

                //}
                //Log.v("name", dataSnapshot.getValue().toString());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        //  startActivity(new Intent(MainActivity.this, AlertActivity.class));


        textView = (TextView) (findViewById(R.id.textView));
        if (sUsbController == null) {
            sUsbController = new UsbController(this, mConnectionHandler, VID, PID, this, textView);
        }
        sUsbController.owner = this;

    /*    new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (MainActivity.sUsbController != null)
                        eventTracker = MainActivity.sUsbController.eventTracker;
                    try {
                        if (eventTracker == 1 && !sUsbController.eventFired) {
                            sUsbController.eventFired = true;
                            startActivity(new Intent(MainActivity.this, AlertActivity.class));
                            break;
                        }

                        Thread.sleep(10);
                    } catch (Exception e) {
                    }
                }
            }
        }).start();*/

    }

    void sendData(byte... bytes) {
        if (sUsbController != null) {
            sUsbController.send(bytes);
        }
    }

    @Override
    protected void onDestroy() {
        sUsbController.owner = null;
        super.onDestroy();
    }

    private final IUsbConnectionHandler mConnectionHandler = new IUsbConnectionHandler() {
        @Override
        public void onUsbStopped() {
            L.e("Usb stopped!");
        }

        @Override
        public void onErrorLooperRunningAlready() {
            L.e("Looper already running!");
        }

        @Override
        public void onDeviceNotFound() {
            if (sUsbController != null) {
                sUsbController.stop();
                sUsbController = null;
            }
        }
    };
}


