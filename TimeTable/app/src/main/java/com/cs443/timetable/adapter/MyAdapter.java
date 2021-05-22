package com.cs443.timetable.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.cs443.timetable.Contract;
import com.cs443.timetable.Helper;
import com.cs443.timetable.R;
import com.cs443.timetable.activity.MainActivity;

public class MyAdapter extends ArrayAdapter<String> {

    private String TimeSQL[];
    String tempSt;
    Cursor cursor;
    String displayTime[];
    Activity activity;
    String day = "";

    public MyAdapter(String day, @NonNull Context context, Cursor cursor, String Time[]) {
        super(context, R.layout.list_single, R.id.list_single_time, Time);
        this.day = day;
        this.TimeSQL = Time;
        this.cursor = cursor;
        displayTime = context.getResources().getStringArray(R.array.Time);
        activity = (Activity) context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        tempSt = cursor.getString(cursor.getColumnIndex(TimeSQL[position]));
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            row = inflater.inflate(R.layout.list_single, parent, false);
        }
        LinearLayout list_single = (LinearLayout) row.findViewById(R.id.list_single);
        CardView cvPerson = (CardView) row.findViewById(R.id.cvPerson);
        TextView timetv = (TextView) row.findViewById(R.id.list_single_time);
        TextView subtv = (TextView) row.findViewById(R.id.subject);
        TextView roomtv = (TextView) row.findViewById(R.id.room);
        Button btn_delete = (Button) row.findViewById(R.id.btn_delete);

        Log.e("TAG", "getView: " + tempSt);
        Log.e("TAG", "getView: " + tempSt);

        if (tempSt != null && !tempSt.contains("null") && !tempSt.equals("")) {
            timetv.setText("Time: " + displayTime[position]);
            subtv.setText("Subject: " + tempSt.split("-")[0]);
            roomtv.setText("Room: " + tempSt.split("-")[1]);
            list_single.setVisibility(View.VISIBLE);
            cvPerson.setVisibility(View.VISIBLE);

        } else {
            timetv.setText("Time: " + displayTime[position]);
            subtv.setText("Subject: -");
            roomtv.setText("Room: -");
            list_single.setVisibility(View.GONE);
            cvPerson.setVisibility(View.GONE);
        }

        btn_delete.setOnClickListener(view -> {
            list_single.setVisibility(View.GONE);
            cvPerson.setVisibility(View.GONE);

            String selectQuery = "UPDATE " + Contract.Entry.TABLE_NAME + " SET "
                    + TimeSQL[position] + " = " + "\"\"" + " WHERE " + Contract.Entry.COLUMN_DAY + " = " + "\"" + day + "\"";

            Helper helper = (new Helper(MainActivity.getContext()));
            try {
                helper.getWritableDatabase().execSQL(selectQuery);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(activity, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            activity.finish();

        });
        return row;
    }
}