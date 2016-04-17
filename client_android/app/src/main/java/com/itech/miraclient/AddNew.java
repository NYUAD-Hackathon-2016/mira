package com.itech.miraclient;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TimePicker;

import com.firebase.client.Firebase;

import java.util.Date;

public class AddNew extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    Firebase refPills,help;
    Button repeat;
    EditText numberOfTimes ;
    private int alarmYear, alarmMonth, alarmDay, alarmHour, alarmMinute;
    private TimePickerDialog.OnTimeSetListener timePickerListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int selectedHour,
                                      int selectedMinute) {
                    alarmHour = selectedHour;
                    alarmMinute = selectedMinute;
                    showDialog(0);
                }
            };
    private DatePickerDialog.OnDateSetListener pDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                    alarmYear = selectedYear;
                    alarmMonth = selectedMonth + 1;
                    alarmDay = selectedDay;
                    showDialog(999);
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);
        //final EditText name = (EditText) findViewById(R.id.addNew_medicine_name);
        //numberOfTimes = (EditText) findViewById(R.id.addNew_times);


        Firebase.setAndroidContext(this);

        refPills = new Firebase("https://miraapp.firebaseio.com/android/saving-data/miraPills");


        Button image = (Button) findViewById(R.id.addNew_photo);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 10);
            }
        });

        repeat = (Button) findViewById(R.id.addNew_repeat);
        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(AddNew.this, v);
                popupMenu.setOnMenuItemClickListener(AddNew.this);
                popupMenu.inflate(R.menu.menu_repeat);
                Menu menu;
                menu=popupMenu.getMenu();
                popupMenu.show();
            }
        });

        Button start = (Button) findViewById(R.id.addNew_start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(999);
            }
        });

        Button end = (Button) findViewById(R.id.addNew_end);
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(999);
            }
        });

        Button time = (Button) findViewById(R.id.addNew_time);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(0);
            }
        });

        Button add = (Button) findViewById(R.id.addNew_add);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Medecine medecine = new Medecine();
                medecine.setTokken(false);
                try {

                    medecine.setDose(Integer.parseInt(numberOfTimes.getText().toString()));
                    //medecine.setName(name.getText().toString());
                }

                catch (Exception e ){

                    medecine.setDose(5);
                    medecine.setName("Penicillin");

                }

                medecine.setPillTime(new Date());

                pushData(medecine);

                finish();
            }
        });



    }

    public void pushData(Medecine pillSchedule) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        refPills.push().setValue(pillSchedule);

    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if(item.getItemId()==R.id.menu_daily){
            repeat.setText("Daily");
        }
        else if(item.getItemId()==R.id.menu_weekly){
            repeat.setText("Weekly");
        }
        return false;
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 0:
                TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                        timePickerListener, alarmHour, alarmMinute, false);
                return timePickerDialog;

            case 999:
                DatePickerDialog dialog = new DatePickerDialog(this, pDateSetListener, alarmYear, alarmMonth, alarmDay);
                dialog.getDatePicker().setMinDate(new Date().getTime());
                return dialog;
        }
        return null;
    }
}
