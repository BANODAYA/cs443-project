package com.cs443.timetable.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.cs443.timetable.Contract;
import com.cs443.timetable.Helper;
import com.cs443.timetable.R;
import com.cs443.timetable.Utility;
import com.cs443.timetable.adapter.MyattAdapter;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

public class AttendenceActivity extends AppCompatActivity {

    Cursor mCursor;
    ListView listView;
    SQLiteDatabase mysubdatabase = null;
    static String tSQL[];
    MyattAdapter adapter;

    static Hashtable<String,String> databaseData = new Hashtable<>();
    static Hashtable<String,String> edittedData = new Hashtable<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence);

        Calendar c = Calendar.getInstance();

        String day = Utility.Day(c.getTime().toString().split(" ")[0]);

        Log.e("day",day);
        listView = (ListView) findViewById(R.id.attendance_list);
        String selectQuery = "SELECT * FROM " + Contract.Entry.TABLE_NAME + " WHERE "
                + Contract.Entry.COLUMN_DAY + " = " + "\""+day+"\"";

        Helper helper = (new Helper(MainActivity.getContext()));
        try {
            mCursor = helper.getReadableDatabase().rawQuery(selectQuery, null);
        }catch (SQLiteException e){

        }


        tSQL = MainActivity.getContext().getResources().getStringArray(R.array.TimeSQL);
        int position = 0;
        ArrayList<String> titles = new ArrayList<>();
        if(mCursor.moveToFirst()){
            String tempSt;
            do {
                tempSt = mCursor.getString(mCursor.getColumnIndex(tSQL[position]));

                if(tempSt != null && !tempSt.equals("") && !tempSt.equals("null") && !tempSt.contains("null")){
                    Log.e("tempSt",tempSt);
                    titles.add(tempSt.split("-")[0]);
                }
                position++;
            }while (position != mCursor.getColumnCount()-2);
        }
        if(titles.size()>0) {
            Log.e("titleSize",titles.size()+"");
            double[] percentage = new double[titles.size()];

            for(int i = 0;i <titles.size();i++){
                mysubdatabase = openOrCreateDatabase("DB", MODE_PRIVATE, null);
                mysubdatabase.execSQL("CREATE TABLE IF NOT EXISTS " +
                        "subject(_id INTEGER PRIMARY KEY AUTOINCREMENT,subjects TEXT,attended INTEGER DEFAULT 0,total INTEGER DEFAULT 0);");

                String query = "SELECT * FROM subject WHERE subjects = " + "\""+titles.get(i)+ "\"";
                String  key = titles.get(i);
                Cursor cursor = mysubdatabase.rawQuery(query, null);

                try {
                    cursor.moveToFirst();
                    String val=cursor.getInt(2)+",";
                    percentage[i] = cursor.getInt(2)*1.0;
                    Log.e("attended",percentage[i]+"");

                    int tot=cursor.getInt(3);
                    val=val+tot;
                    databaseData.put(key,val);
                    if (tot==0)
                        tot=1;
                    percentage[i] /= tot;
                    Log.e("total",percentage[i]+"");
                    percentage[i]*=100;
                }
                catch (Exception e) {
                    Log.e("Exception caught",e.toString());
                    cursor.close();
                }

            }

            Log.e("HashTable ",databaseData.toString());
             adapter = new MyattAdapter(this, titles.toArray(new String[titles.size()]),percentage,databaseData);
            listView.setAdapter(adapter);
        }

    }

    private void savetoDatabase() {
        if (adapter==null)
            return;
        edittedData=adapter.getHashTable();
        databaseData=edittedData;
        Log.e("edittedData",edittedData.toString());
        mysubdatabase = openOrCreateDatabase("DB", MODE_PRIVATE, null);
        mysubdatabase.execSQL("CREATE TABLE IF NOT EXISTS " +
                "subject(_id INTEGER PRIMARY KEY AUTOINCREMENT,subjects TEXT,attended INTEGER DEFAULT 0,total INTEGER DEFAULT 0);");



        Set<String> keys = edittedData.keySet();
        for (String key:keys) {
            //String updateQuery1 = "UPDATE subject SET attended = "+edittedData.get(key).split(",")[0]+" WHERE subjects = \"" + key+"\";";
            //String updateQuery2 = "UPDATE subject SET total = "+edittedData.get(key).split(",")[1]+" WHERE subjects = \"" + key+"\";";

            ContentValues values = new ContentValues();
            values.put("attended",edittedData.get(key).split(",")[0]);
            values.put("total",edittedData.get(key).split(",")[1]);
            mysubdatabase.update("subject",values,"subjects = ?",new String[]{key});
            //mysubdatabase.rawQuery(updateQuery2,null);
        }
        finish();
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
                savetoDatabase();
        }
        return super.onOptionsItemSelected(item);
    }

}


