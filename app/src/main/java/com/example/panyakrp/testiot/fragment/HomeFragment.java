package com.example.panyakrp.testiot.fragment;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.panyakrp.testiot.Components;
import com.example.panyakrp.testiot.HomeActivity;
import com.example.panyakrp.testiot.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import az.plainpie.PieView;
import az.plainpie.animation.PieAngleAnimation;

public class HomeFragment extends Fragment {
    PieView pieView;
    LineChart chart;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference status;
    DatabaseReference device_stop;
    Button btnSend;
    EditText editTime, editTime2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ((HomeActivity) getActivity()).progressBar.setVisibility(View.INVISIBLE);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        status = mFirebaseDatabase.getReference().child("Gas");
        device_stop = mFirebaseDatabase.getReference().child("Device_stopped");

        pieView = (PieView) view.findViewById(R.id.pieView);
        chart = (LineChart) view.findViewById(R.id.chart);
        btnSend = (Button) view.findViewById(R.id.send);
        editTime = (EditText) view.findViewById(R.id.editTime);
        editTime2 = (EditText) view.findViewById(R.id.editTime2);
        pieView.setMaxPercentage(1000);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editTime == null || editTime.getText().toString().equals("") || editTime2 == null || editTime2.getText().toString().equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("โปรดกำหนดเวลา");
                    builder.setNegativeButton("ตกลง", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                } else {
                    int mm = Integer.parseInt(editTime.getText().toString());
                    int ss = Integer.parseInt(editTime2.getText().toString());

                    if (mm < 0 || mm >= 60 || ss < 0 || ss >= 60) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("กรุณาใส่เวลาให้ถูกต้อง ");
                        builder.setNegativeButton("ตกลง", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.show();
                    } else if (mm == 0 && ss == 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("โปรดกำหนดเวลา ");
                        builder.setNegativeButton("ตกลง", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.show();
                    } else {
                        final int timeStop = (mm * 60 * 1000) + (ss * 1000);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("กำหนดเวลา " + mm + " นาที " + ss + " วินาที");
                        builder.setPositiveButton("ยืนยัน", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(getActivity(), "อุปกรณ์เริ่มต้นหยุดทำงาน", Toast.LENGTH_LONG).show();
                                device_stop.setValue(timeStop);
                            }
                        });
                        builder.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.show();

                    }
                }

            }
        });

        new Components().set_Chart(chart);

        PieAngleAnimation animation = new PieAngleAnimation(pieView);

        pieView.startAnimation(animation);
        pieView.setInnerText("0");

        status.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Long alert = (Long) dataSnapshot.child("alert").getValue();
                Long lpg = (Long) dataSnapshot.child("lpg").getValue();
                if (alert == 1) {
                    showNotification();
                    status.child("alert").setValue(0);
                }


                if (lpg == 0) {
                    pieView.setPercentage(1);
                    pieView.setInnerText("0");
                } else {
                    pieView.setPercentage(lpg);
                    pieView.setInnerText(String.valueOf(lpg));
                }

                feedMultiple(String.valueOf(lpg));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
    }

    private Thread thread;

    private void feedMultiple(final String value) {

        if (thread != null)
            thread.interrupt();

        final Runnable runnable = new Runnable() {

            @Override
            public void run() {
                addEntry1(value);
            }
        };

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(runnable);
            }
        });

        thread.start();
    }

    private void addEntry1(String value) {
        LineData data = chart.getData();

        if (data != null) {

            ILineDataSet set_h = data.getDataSetByIndex(0);

            if (set_h == null) {
                set_h = new Components().createSet();
                data.addDataSet(set_h);
            }

            data.addEntry(new Entry(set_h.getEntryCount(), Integer.parseInt(value)), 0);
            data.notifyDataChanged();
            chart.notifyDataSetChanged();
            chart.setVisibleXRangeMaximum(20);
            chart.moveViewToX(data.getEntryCount());

        }
    }

    public void showNotification() {
        int color = ContextCompat.getColor(getContext(), R.color.colorPrimary);
        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.logo);

        Intent intent = new Intent(getActivity(), HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);

        Notification notification =
                new NotificationCompat.Builder(getContext())
                        .setSmallIcon(R.mipmap.logo)
                        .setLargeIcon(bitmap)
                        .setContentTitle("แจ้งเตือนก๊าซ LPG รั่วไหลโปรดระวัง")
                        .setContentText("")
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setColor(color)
                        .setVibrate(new long[]{500, 1000, 500})
                        .build();

        NotificationManager notficationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notficationManager.notify(1000, notification);

    }


}
