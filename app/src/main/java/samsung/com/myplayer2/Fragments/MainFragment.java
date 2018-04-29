package samsung.com.myplayer2.Fragments;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

import samsung.com.myplayer2.R;
import samsung.com.myplayer2.Service.MyService;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    private ViewPager viewPager;

    View tabcontainer;
    //View coloredBackgroundView;
    Toolbar toolbar;

    MyService myService;
    private boolean musicBound = false;
    private Intent playintent;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        //tabcontainer = rootView.findViewById(R.id.tabcontainer);
        //coloredBackgroundView = rootView.findViewById(R.id.colored_background_view);

        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
            viewPager.setOffscreenPageLimit(4);
        }

        SmartTabLayout tabLayout = (SmartTabLayout) rootView.findViewById(R.id.viewpagertab);
        tabLayout.setViewPager(viewPager);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (musicBound) {
                    switch (position) {
                        case 0:
                            //myService.setListNumber(1);
                            //myService.setListNumberFrag(1);
                            //songAdapter.setOnPlaylist(0);
                            break;
                        case 1:
                            //myService.setListNumber(2);
                            //myService.setListNumberFrag(2);
                            //songAdapter.setOnPlaylist(0);
                            break;
                        case 2:
                            //myService.setListNumber(3);
                            //myService.setListNumberFrag(3);
                            //songAdapter.setOnPlaylist(0);
                            break;
                        case 3:
                            //myService.setListNumber(4);
                            //myService.setListNumberFrag(4);
                            //songAdapter.setOnPlaylist(1);
                            break;
                        case 4:
                            //myService.setListNumber(5);
                            //myService.setListNumberFrag(5);
                            //songAdapter.setOnPlaylist(0);
                            break;
                    }
                }
                //tabcontainer.clearAnimation();
//                tabcontainer
//                        .animate()
//                        .translationY(0)
//                        .start();
                //coloredBackgroundView.setTranslationY(0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return rootView;
    }

    private void setupViewPager(ViewPager viewPager) {
        FragmentAdapter adapter = new FragmentAdapter(getChildFragmentManager());
        adapter.addFragment(new SongListFragment(), this.getString(R.string.allSong));
        adapter.addFragment(new AlbumFragment(), this.getString(R.string.albums));
        adapter.addFragment(new ArtistFragment(), this.getString(R.string.artist));
        //adapter.addFragment(new PlaylistFragment(), this.getString(R.string.playlist));
        adapter.addFragment(new GenresFragment(), this.getString(R.string.genres));
        viewPager.setAdapter(adapter);
    }

    static class FragmentAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            MyService.MusicBinder binder = (MyService.MusicBinder) service;
            //get service
            myService = binder.getService();
            //pass list

            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicBound = false;
        }
    };

    @Override
    public void onStart() {
        super.onStart();

        playintent = new Intent(getActivity(), MyService.class);
        getActivity().startService(playintent);
        getActivity().bindService(playintent, musicConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (musicBound) {
            getActivity().unbindService(musicConnection);
            musicBound = false;
        }
    }

}

