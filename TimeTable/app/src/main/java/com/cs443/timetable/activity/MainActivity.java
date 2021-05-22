package com.cs443.timetable.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.github.clans.fab.FloatingActionButton;
import com.cs443.timetable.fragments.FridayFragment;
import com.cs443.timetable.fragments.MondayFragment;
import com.cs443.timetable.R;
import com.cs443.timetable.fragments.ThursdayFragment;
import com.cs443.timetable.fragments.TuesdayFragment;
import com.cs443.timetable.fragments.WednesdayFragment;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.io.File;

public class MainActivity extends FragmentActivity {

    private static final String FOR_FIRST_TIME = "for first time";
    public int currentPage = 0;
    private static Context context;

    FloatingActionButton fab = null;
    ListView leftlist;
    static MainActivity mainActivity;

    public static AlarmManager alarmManager;
    static PendingIntent pendingIntent;

    private DrawerLayout mDrawerLayout;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);


        leftlist = (ListView) findViewById(R.id.leftlist);
        mainActivity = this;
        context = this;
        String navigationSt[] = new String[]{"Projects", "Attendence Manager"};
        int navigationImg[] = new int[]{R.mipmap.ic_proj, R.mipmap.ic_att};
        MyDrawerAdapter navigationAdapter = new MyDrawerAdapter(getApplicationContext(), navigationSt
                , navigationImg);
        leftlist.setAdapter(navigationAdapter);

        leftlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectFromDrawer(position);
                mDrawerLayout.closeDrawers();
            }
        });
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("MONDAY", MondayFragment.class)
                .add("TUESDAY", TuesdayFragment.class)
                .add("WEDNESDAY", WednesdayFragment.class)
                .add("THURSDAY", ThursdayFragment.class)
                .add("FRIDAY", FridayFragment.class)
                .create());

        // Calendar c = Calendar.getInstance();


        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);

        if (getIntent() != null) {
            viewPager.setCurrentItem(getIntent().getIntExtra("page", 0));
        }

        viewPagerTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
//                  Toast.makeText(getApplicationContext(),""+currentPage,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        /** Setup the shared preference listener **/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (prefs.getBoolean(FOR_FIRST_TIME, true)) {
            //showTut();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(FOR_FIRST_TIME, false);
            editor.apply();
        }

    }


    public void selectFromDrawer(int position) {
        switch (position) {
            case 0:
                Intent intent1 = new Intent(MainActivity.this, ProjectShowActivity.class);
                startActivity(intent1);
                break;
            case 2:
                Intent in = new Intent(this, AttendenceActivity.class);
                startActivity(in);
                break;
        }
    }


    public static Context getContext() {
        return MainActivity.context;
    }

    public class MyDrawerAdapter extends BaseAdapter {

        private Context context;
        private String[] titles;
        private int[] images;
        private LayoutInflater inflater;

        public MyDrawerAdapter(Context context, String[] titles, int[] images) {
            this.context = context;
            this.titles = titles;
            this.images = images;
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
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder mViewHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.singlenavigation_view, null);
                mViewHolder = new ViewHolder();
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (ViewHolder) convertView.getTag();
            }

            mViewHolder.tvTitle = (TextView) convertView
                    .findViewById(R.id.text_navigation);
            mViewHolder.ivIcon = (ImageView) convertView
                    .findViewById(R.id.image_navigation);

            mViewHolder.tvTitle.setText(titles[position]);
            mViewHolder.ivIcon.setImageResource(images[position]);

            return convertView;
        }

        private class ViewHolder {
            TextView tvTitle;
            ImageView ivIcon;
        }
    }

    private Intent createShareForecastIntent() {
        String path = "/sdcard/mytxt.txt";
        File file = new File(path);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/*");
        try {
            sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
        } catch (Exception e) {

        }
        startActivity(Intent.createChooser(sharingIntent, "share file with"));
        return sharingIntent;
    }


}