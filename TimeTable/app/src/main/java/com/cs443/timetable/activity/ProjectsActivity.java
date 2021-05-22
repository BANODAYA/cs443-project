package com.cs443.timetable.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;


import com.cs443.timetable.R;


public class ProjectsActivity extends AppCompatActivity {


    DatePicker datePicker;
    SQLiteDatabase mydatabase;
    EditText subject_proj, project_ed;
    int date, month, year;
    Button button;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);

        subject_proj = (EditText) findViewById(R.id.subject_proj);
        project_ed = (EditText) findViewById(R.id.assg);
        button = (Button) findViewById(R.id.set_date);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeLangDialog1();
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
                    Intent intent = new Intent(this, ProjectShowActivity.class);
                    startActivity(intent);
                } else
                    item.setIcon(R.drawable.ic_notdone);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean save() {
        boolean done = false;
        mydatabase = openOrCreateDatabase("chartDB", MODE_PRIVATE, null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS " +
                "project(_id INTEGER PRIMARY KEY AUTOINCREMENT,subjects TEXT,projects TEXT ,date TEXT);");

        String subject = subject_proj.getText().toString().trim();
        String project = project_ed.getText().toString().trim();
        if (subject != null && project != null && !subject.contains("null") && !project.contains("null")
                && !subject.equals("") && !project.equals("")) {
            ContentValues values = new ContentValues();
            values.put("subjects", subject);
            values.put("projects", project);
            values.put("date", date + ":" + month + ":" + year);
            int num = (int) mydatabase.insert("project", null, values);
            done = true;
        } else {
            Toast.makeText(this, "Enter Valid Data", Toast.LENGTH_SHORT).show();
        }
        return done;
    }


    public void showChangeLangDialog1() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialogdate, null);
        dialogBuilder.setView(dialogView);

        final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker);
        java.util.Calendar calendar = java.util.Calendar.getInstance();

        dialogBuilder.setTitle("Select Date");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onClick(DialogInterface dialog, int whichButton) {
                date = datePicker.getDayOfMonth();
                month = datePicker.getMonth();
                year = datePicker.getYear();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

}
