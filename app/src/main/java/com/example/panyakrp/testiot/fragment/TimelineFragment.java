package com.example.panyakrp.testiot.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.util.Map;


public class TimelineFragment extends Fragment {

    private LineChart mChart;
    private LineChart mChart2;
    private Thread thread;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference myDevice;
    DatabaseReference getStatus;

    public TimelineFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);

        mChart = (LineChart) view.findViewById(R.id.chart1);
        mChart2 = (LineChart) view.findViewById(R.id.chart2);

        new Components().set_Chart(mChart);
        new Components().set_Chart(mChart2);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myDevice = mFirebaseDatabase.getReference().child("Loggas");
        getStatus = mFirebaseDatabase.getReference().child("Gas");

        myDevice.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                collectLpg((Map<String, Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void collectLpg(Map<String, Object> log) {

        for (Map.Entry<String, Object> entry : log.entrySet()) {
            Map singleUser = (Map) entry.getValue();

            feedMultiple(String.valueOf(singleUser.get("lpg")));

            if (String.valueOf(singleUser.get("alert")).equals("1")) {
                feedMultiple2("500");
            } else {
                feedMultiple2("1");
            }


        }

        ((HomeActivity) getActivity()).progressBar.setVisibility(View.INVISIBLE);

        getStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long alert = (Long) dataSnapshot.child("alert").getValue();
                Long lpg = (Long) dataSnapshot.child("lpg").getValue();

                feedMultiple(String.valueOf(lpg));

                if (String.valueOf(alert).equals("1")) {
                    feedMultiple2("500");
                } else {
                    feedMultiple2("1");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

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
        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set_h = data.getDataSetByIndex(0);

            if (set_h == null) {
                set_h = new Components().createSet();
                data.addDataSet(set_h);
            }

            data.addEntry(new Entry(set_h.getEntryCount(), Integer.parseInt(value)), 0);
            data.notifyDataChanged();
            mChart.notifyDataSetChanged();
            mChart.setVisibleXRangeMaximum(20);
            mChart.moveViewToX(data.getEntryCount());

        }
    }

    private void feedMultiple2(final String value) {

        if (thread != null)
            thread.interrupt();

        final Runnable runnable = new Runnable() {

            @Override
            public void run() {
                addEntry2(value);
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

    private void addEntry2(String value) {
        LineData data = mChart2.getData();
        //float gas = Float.parseFloat(value);
        if (data != null) {

            ILineDataSet set_h = data.getDataSetByIndex(0);

            if (set_h == null) {
                set_h = new Components().createSet2();
                data.addDataSet(set_h);
            }

            data.addEntry(new Entry(set_h.getEntryCount(), Integer.parseInt(value)), 0);
            data.notifyDataChanged();
            mChart2.notifyDataSetChanged();
            mChart2.setVisibleXRangeMaximum(20);
            mChart2.moveViewToX(data.getEntryCount());

        }
    }
}
