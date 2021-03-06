package com.cs443.timetable.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.cs443.timetable.Contract;
import com.cs443.timetable.Helper;
import com.cs443.timetable.R;
import com.cs443.timetable.Utility;

import java.util.HashSet;
import java.util.Set;

public class EditorActivity extends AppCompatActivity {
    Spinner spinner;
    int page, selectedPos;
    String selection;
    String sqlArray[];
    EditText room;
    SQLiteDatabase mysubdatabase;
    AutoCompleteTextView subjet;
    private String name[];
    Cursor cursor;
    boolean isFirstEntry = false;

    Set<String> subjects = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        name = getResources().getStringArray(R.array.Days);
        page = MainActivity.mainActivity.currentPage;
        setTitle(name[page].toUpperCase());

        spinner = (Spinner) findViewById(R.id.spinner);
        subjet = (AutoCompleteTextView) findViewById(R.id.subject);
        room = (EditText) findViewById(R.id.room);
        sqlArray = getResources().getStringArray(R.array.TimeSQL);

        mysubdatabase = openOrCreateDatabase("DB", MODE_PRIVATE, null);
        mysubdatabase.execSQL("CREATE TABLE IF NOT EXISTS " +
                "subject(_id INTEGER PRIMARY KEY AUTOINCREMENT,subjects TEXT,attended INTEGER DEFAULT 0,total INTEGER DEFAULT 0);");

        String attQuery = "SELECT * FROM subject";
        Cursor subjectCursor = mysubdatabase.rawQuery(attQuery, null);

        Utility.getResults(subjectCursor);

        if (subjectCursor.getCount() > 0) {
            try {
                subjectCursor.moveToFirst();
                String currSub = subjectCursor.getString(subjectCursor.getColumnIndex("subjects"));
                Log.e(currSub, "Current Subject");
                subjects.add(currSub);
                while (subjectCursor.moveToNext()) {
                    currSub = subjectCursor.getString(subjectCursor.getColumnIndex("subjects"));
                    Log.e(currSub, "Current Subject");
                    subjects.add(currSub);
                }
            } catch (Exception e) {
                // Table empty
                isFirstEntry = true;
            }
        }
        String suggest[] = new String[subjects.size()];
        int i = 0;
        for (String sub : subjects) {
            suggest[i++] = sub;
            Log.e(sub, "Set element");
        }


        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, suggest);
        subjet.setAdapter(adapter);
        sqlArray = getResources().getStringArray(R.array.TimeSQL);


        setupspinner();

        if (getIntent() != null) {
            String subjectSt = getIntent().getStringExtra("subject") + "";
            String roomSt = getIntent().getStringExtra("room") + "";
            if (subjectSt != null && roomSt != null && !subjectSt.equals("====") && !roomSt.equals("====") &&
                    !subjectSt.equals("null") && !roomSt.equals("null")) {
                subjet.setText(subjectSt);
                room.setText(roomSt);
            }
            //Toast.makeText(getApplicationContext(),getIntent().getIntExtra("position",0)+"",Toast.LENGTH_SHORT).show();
            spinner.setSelection(getIntent().getIntExtra("position", 0), true);
        }

        Button save = findViewById(R.id.btn_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean done = save();
                if (done) {
                    Intent intent = new Intent(EditorActivity.this, MainActivity.class);
                    intent.putExtra("page", page);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                boolean done = save();
                if (done) {
                    item.setIcon(R.drawable.ic_action_name);
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("page", page);
                    startActivity(intent);
                } else
                    item.setIcon(R.drawable.ic_notdone);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private boolean save() {
        String selectQuery = "SELECT  * FROM " + Contract.Entry.TABLE_NAME + " WHERE "
                + Contract.Entry.COLUMN_DAY + " = " + "\"" + name[page].toLowerCase() + "\"";

        Log.e("TAG", "save: " + selectQuery);

        if (isFirstEntry) {
            addNewSubj();
        }

        String currSub = subjet.getText().toString();
        if (!subjects.contains(currSub)) {
            addNewSubj();
        }

        Helper helper = (new Helper(MainActivity.getContext()));
        Cursor cursor = helper.getReadableDatabase().rawQuery(selectQuery, null);
        ContentValues values = new ContentValues();
        boolean done = false;
        if (cursor != null && cursor.moveToFirst()) {
            int tempColumnIndex = cursor.getColumnIndex(sqlArray[selectedPos]);
            String tempString = cursor.getString(tempColumnIndex);
            String arg[] = {name[page]};
            //String id[] = {"monday", "8AM - 9AM"};
            String id[] = {"monday"};

            String subjectSt = subjet.getText().toString().trim();
            String roomSt = room.getText().toString().trim();
            if (subjectSt != null && roomSt != null && !subjectSt.equals("") && !roomSt.equals("") && !subjectSt.contains("null") && !roomSt.contains("null")) {
                values.put(sqlArray[selectedPos], subjectSt + "-" + roomSt);
                getContentResolver().update(Contract.Entry.CONTENT_URI, values, "day = ?", arg);

                done = true;
                // Toast.makeText(getApplicationContext(), "1st", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Enter Valid Data", Toast.LENGTH_SHORT).show();
            }
        } else {
            values.put(Contract.Entry.COLUMN_DAY, name[page]);
            String subjectSt = subjet.getText().toString().trim();
            String roomSt = room.getText().toString().trim();
            if (subjectSt != null && roomSt != null && !subjectSt.equals("") && !roomSt.equals("") &&
                    !subjectSt.contains("null") && !roomSt.contains("null")) {
                values.put(sqlArray[selectedPos], subjet.getText().toString() + "-" + room.getText().toString());
                getContentResolver().insert(Contract.Entry.CONTENT_URI, values);
                done = true;
                // Toast.makeText(getApplicationContext(), "insert", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Enter Valid Data", Toast.LENGTH_SHORT).show();
            }
        }
        return done;
    }

    private void addNewSubj() {
        if (isFirstEntry)
            Log.e("First", "First");
        String newSub = subjet.getText().toString();
        subjects.add(newSub);
        ContentValues values = new ContentValues();
        values.put("subjects", newSub);
        mysubdatabase.insert("subject", null, values);
        //  Toast.makeText(getApplicationContext(),"Enter Data",Toast.LENGTH_SHORT).show();
        //TODO Make a dialog for enterring  data
    }


    private void setupspinner() {
        ArrayAdapter SpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.Time, android.R.layout.simple_spinner_item);
        SpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(SpinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selection = (String) parent.getItemAtPosition(position);
                selectedPos = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selection = "8AM TO 9AM";
            }
        });

    }
}
