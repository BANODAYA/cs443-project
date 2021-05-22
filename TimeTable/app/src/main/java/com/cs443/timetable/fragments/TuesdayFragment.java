package com.cs443.timetable.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.cs443.timetable.Contract;
import com.cs443.timetable.Helper;
import com.cs443.timetable.R;
import com.cs443.timetable.activity.EditorActivity;
import com.cs443.timetable.activity.MainActivity;
import com.cs443.timetable.adapter.MyAdapter;


public class TuesdayFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    Cursor mCursor;
    final int LOADER_CODE = 2;
    View view = null;
    ListView list1 = null;
    String sqlArray[];
    String tSQL[];

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tuesday_layout, container, false);
        list1 = (ListView) view.findViewById(R.id.list_view2);
        registerForContextMenu(list1);
        sqlArray = getResources().getStringArray(R.array.TimeSQL);
        getActivity().getSupportLoaderManager().initLoader(LOADER_CODE, null, this);

        list1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.getContext(), EditorActivity.class);
                intent.putExtra("subject", ((TextView) view.findViewById(R.id.subject)).getText().toString());
                intent.putExtra("room", ((TextView) view.findViewById(R.id.room)).getText().toString());
                intent.putExtra("position", position);
                startActivity(intent);
                return true;
            }
        });
        setView(mCursor);
        return view;
    }


    public void setView(Cursor cursor) {
        tSQL = MainActivity.getContext().getResources().getStringArray(R.array.TimeSQL);

        if (cursor != null && cursor.moveToFirst()) {
            MyAdapter adapter = new MyAdapter("tuesday",MainActivity.getContext(), cursor, tSQL);
            list1.setAdapter(adapter);
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String selectQuery = "SELECT  * FROM " + Contract.Entry.TABLE_NAME + " WHERE "
                + Contract.Entry.COLUMN_DAY + " = " + "\"tuesday\"";

        Helper helper = (new Helper(MainActivity.getContext()));
        try {
            mCursor = helper.getReadableDatabase().rawQuery(selectQuery, null);
        } catch (SQLiteException e) {

        }

        return new CursorLoader(getContext(), Contract.Entry.CONTENT_URI,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

}
