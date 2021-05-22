package com.cs443.timetable.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.cs443.timetable.R;

import java.util.Hashtable;
import java.util.Set;

public class MyattAdapter extends BaseAdapter {

    private Context context;
    private String[] titles;
    private int[] images;
    private String[] percentage;
    private LayoutInflater inflater;
    Hashtable<String, Boolean> changed;
    Hashtable<String, String> databaseData;

    public MyattAdapter(Context context, String[] titles, double[] percentage, Hashtable<String, String> datab) {
        this.context = context;
        this.databaseData = datab;
        this.titles = titles;
        changed = new Hashtable<>();
        for (int i = 0; i < this.titles.length; i++) {
            changed.put(this.titles[i], false);
        }
        this.percentage = new String[percentage.length];
        if (percentage == null) {
            Log.e("Error ", "null array");
        } else {
            for (int i = 0; i < percentage.length; i++) {
                this.percentage[i] = String.format("%.1f", (float) percentage[i]);
            }
        }


        this.inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final MyattAdapter.ViewHolder mViewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.single_check, null);
            mViewHolder = new MyattAdapter.ViewHolder();
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyattAdapter.ViewHolder) convertView.getTag();
        }

        mViewHolder.tvTitle = (CheckBox) convertView
                .findViewById(R.id.checkBox);
        mViewHolder.per_tv = (TextView) convertView.findViewById(R.id.percentage_id);
        mViewHolder.tvTitle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    String key = mViewHolder.tvTitle.getText().toString();
                    int attended = Integer.valueOf((databaseData.get(key)).split(",")[0]);
                    int total = Integer.valueOf((databaseData.get(key)).split(",")[1]);
                    attended++;
                    total++;
                    String val = attended + "," + total;
                    changed.put(key, true);
                    databaseData.put(key, val);
                    percentage[position] = (attended * 100.0 / total) + "";
                    mViewHolder.per_tv.setText(percentage[position]);
                } else {
                    String key = mViewHolder.tvTitle.getText().toString();
                    int attended = Integer.valueOf((databaseData.get(key)).split(",")[0]);
                    int total = Integer.valueOf((databaseData.get(key)).split(",")[1]);
                    attended--;
                    total--;
                    changed.put(key, false);
                    String val = attended + "," + total;
                    databaseData.put(key, val);
                    if (total == 0)
                        total = 1;
                    percentage[position] = (attended * 100.0 / total) + "";
                    mViewHolder.per_tv.setText(percentage[position]);
                }
            }
        });


        mViewHolder.tvTitle.setText(titles[position]);
        mViewHolder.per_tv.setText(percentage[position]);


        return convertView;
    }

    public Hashtable<String, String> getHashTable() {
        Set<String> keys = changed.keySet();
        for (String key : keys) {
            if (!changed.get(key)) {
                databaseData.put(key, databaseData.get(key).split(",")[0] + "," + (Integer.valueOf(databaseData.get(key).split(",")[1]) + 1));
            }
        }
        return databaseData;
    }

    private class ViewHolder {
        CheckBox tvTitle;
        TextView per_tv;
    }
}
