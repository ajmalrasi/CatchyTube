package catchytube.com.rasi.ajmal;


import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.netcompss.ffmpeg4android.GeneralUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import catchytube.com.rasi.ajmal.dialog.FormatList;
import catchytube.com.rasi.ajmal.fragment.DownloadManager;
import catchytube.com.rasi.ajmal.fragment.VideoPage;
import catchytube.com.rasi.ajmal.interfaces.Communicate;
import catchytube.com.rasi.ajmal.network.DownloadReceiver;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Communicate {

    private static final int SEARCH = 0;
    private static final int DOWNLOADS = 1;
    private static final String TAG = "MainActivity";
    Fragment fragment;
    public ViewPager mViewPager;
    public static Context ct;
    private String youtubeLink;

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
//        Log.i(TAG, "Fragment created " + fragment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ct = MainActivity.this;
        if (getIntent()!=null&&Intent.ACTION_SEND.equals(getIntent().getAction())) {
            getYoutubeIntent(savedInstanceState, this);
        }
        GeneralUtils.checkForPermissionsMAndAbove(this,false);
        setContentView(R.layout.activity_main_nav_bar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        //Toolbar scroll settings
        final AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL |
                AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP |
                AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED);

        //Disable Actionbar Title
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Create the catchytube.com.ajmal.rasi.com.rasi.catchytube.com.ajmal.rasi.adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter;
        FragmentManager fragmentManager = getSupportFragmentManager();
        mSectionsPagerAdapter = new SectionsPagerAdapter(fragmentManager);

        // Set up the ViewPager with the sections catchytube.com.ajmal.rasi.com.rasi.catchytube.com.ajmal.rasi.adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //Find TabLayout and setup with Viewpager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
//      mViewPager.setOffscreenPageLimit(position);

        //Find Floating action bar.
        //Set onClickListener for FAB
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "App is under development.", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });

        //Setup Navigation Bar
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Animated FAB
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switch (position) {
                    case 0:
                        fab.show();
                        break;
                    case 1:
                        fab.hide();
                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (mViewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
        } else if (id == R.id.nav_slideshow) {
        } else if (id == R.id.nav_manage) {
        } else if (id == R.id.nav_share) {
        } else if (id == R.id.nav_send) {
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void sendData(String videoId,String aUrl, String aFl) {
        try{
            FragmentManager fragmentManager = getSupportFragmentManager();
            DownloadManager downloadManager;
            downloadManager = (DownloadManager) fragmentManager.getFragments().get(1);
            mViewPager.setCurrentItem(1, true);
            downloadManager.startDownload(videoId,aFl, aUrl);
        }catch(ClassCastException e){
            Log.e(TAG, "sendData: Class Cast Exception");
            FragmentManager fragmentManager = getSupportFragmentManager();
            DownloadManager downloadManager;
            downloadManager = (DownloadManager) fragmentManager.getFragments().get(2);
            mViewPager.setCurrentItem(1, true);
            downloadManager.startDownload(videoId,aFl, aUrl);
        }
    }

    @Override
    public void sendData(String videoId,String vUrl, String vFl, String aUrl, String aFl) {

        try{
            FragmentManager fragmentManager = getSupportFragmentManager();
            DownloadManager downloadManager;
            downloadManager = (DownloadManager) fragmentManager.getFragments().get(1);
            mViewPager.setCurrentItem(1, true);
            downloadManager.startDashDownload(videoId,vUrl, vFl, aUrl, aFl);
        }catch (ClassCastException e){
            Log.e(TAG, "sendData: Class Cast Exception");
            FragmentManager fragmentManager = getSupportFragmentManager();
            DownloadManager downloadManager;
            downloadManager = (DownloadManager) fragmentManager.getFragments().get(2);
            mViewPager.setCurrentItem(1, true);
            downloadManager.startDashDownload(videoId,vUrl, vFl, aUrl, aFl);
        }

    }

    @Override
    public void transcodeFiles(String vLoc, String aLoc,int type) {
        Intent i = new Intent(this, DownloadReceiver.class);
        if (vLoc!=null&aLoc!=null){
            i.putExtra("Case",type);
            i.putExtra("Video", vLoc);
            i.putExtra("Audio", aLoc);
            i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startService(i);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "This app needs storage access for downloading files", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {


        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
//            Log.i(TAG, "item called is " + position);
            switch (position) {
                case SEARCH:
                    fragment = new VideoPage();
                    return fragment;
                case DOWNLOADS:
                    fragment = new DownloadManager();
                    return fragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
//            Log.i(TAG,"getCount is called");
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case SEARCH:
                    return "Trending";
                case DOWNLOADS:
                    return "Downloads";
            }
            return null;
        }
    }

    public void sendIntent(String videoId) {
        FragmentManager fm = getSupportFragmentManager();
        FormatList dialogFragment = new FormatList();
        Bundle bundle = new Bundle();
        bundle.putString("id", videoId);
        dialogFragment.setArguments(bundle);
        dialogFragment.setCommunicator((Communicate) MainActivity.ct);
        dialogFragment.show(fm, "DownloadDialog");
    }

    public static String getVideoId(String ytLink) {
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu\\x2Ebe\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(ytLink);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public void getYoutubeIntent(Bundle savedInstanceState, Activity activity){
        // Check how it was started and if we can get the youtube link
        if (savedInstanceState == null && Intent.ACTION_SEND.equals(activity.getIntent().getAction())
                && activity.getIntent().getType() != null && "text/plain".equals(activity.getIntent().getType())) {
            String ytLink = activity.getIntent().getStringExtra(Intent.EXTRA_TEXT);
            if (ytLink != null && (ytLink.contains("://youtu.be/") || ytLink.contains("youtube.com/watch?v="))) {
                youtubeLink = ytLink;
                // We have a valid link
                String videoId = getVideoId(ytLink);
                if (videoId != null) {
                    sendIntent(videoId);
                }
            } else {
                Toast.makeText(activity, R.string.error_no_yt_link, Toast.LENGTH_SHORT).show();
                activity.finish();
            }
        } else if (savedInstanceState != null && youtubeLink != null) {
            String videoId = getVideoId(youtubeLink);
            if (videoId != null) {
                sendIntent(videoId);
            }
        } else {
            activity.finish();
        }
    }

}
