package samsung.com.myplayer2.Activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.audiofx.AudioEffect;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.bumptech.glide.Glide;
import com.musixmatch.lyrics.MissingPluginException;
import com.musixmatch.lyrics.musiXmatchLyricsConnector;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import samsung.com.myplayer2.Adapter.CustomPagerAdapter;
import samsung.com.myplayer2.Adapter.RecyclerAlbumAdapter;
import samsung.com.myplayer2.Adapter.RecyclerArtistAdapter;
import samsung.com.myplayer2.Adapter.RecyclerSongAdapter;
import samsung.com.myplayer2.Class.Album;
import samsung.com.myplayer2.Class.Artist;
import samsung.com.myplayer2.Class.Function;
import samsung.com.myplayer2.Class.KMPSearch;
import samsung.com.myplayer2.Class.Song;
import samsung.com.myplayer2.Class.Suggestion;
import samsung.com.myplayer2.R;
import samsung.com.myplayer2.Service.MyService;

public class MainActivity extends AppCompatActivity implements RecyclerAlbumAdapter.AlbumClickListener, RecyclerArtistAdapter.ArtistClickListener, NavigationView.OnNavigationItemSelectedListener {

    private ArrayList<Song> MainSongList;
    private ArrayList<Song> SongListOfResult;
    private ArrayList<Song> SongListOfInnerResult;

    private ArrayList<Album> AllAlbum;
    private ArrayList<Album> AlbumOfResult;

    private ArrayList<Artist> AllArtist;
    private ArrayList<Artist> ArtistOfResult;

    private ArrayList<Suggestion> mSuggestion = new ArrayList<>();

    Function function;

    Context context;

    SlidingUpPanelLayout slidingLayout;
    FloatingSearchView searchView;

    ViewPager viewPager;
    CustomPagerAdapter customPagerAdapter;

    //TabLayout tabLayout;
    SmartTabLayout smartTabLayout;
    //PagerTabStrip pagerTabStrip;
    MyService myService;
    private boolean musicBound = false;
    private Intent playintent;
    public ImageButton btnPlayPause;
    ImageButton next;
    ImageButton prev;
    ImageButton shuffle;
    ImageButton repeat;
    TextView txtArtist;
    TextView txtTitle;
    public TextView txtTimeSong;
    public TextView txtTotal;
    public SeekBar seekBar;
    ImageView imgDisc;
    String SongPath;

    RecyclerSongAdapter songAdapter;

    RecyclerSongAdapter songAdapterOfResult;
    RecyclerSongAdapter songAdapterOfInnerResult;
    RecyclerAlbumAdapter albumAdapterOfResult;
    RecyclerArtistAdapter artistAdapterOfResult;

    RecyclerView resultAlbum;
    RecyclerView resultArtist;
    RecyclerView resultSong;
    RecyclerView resultInnerSong;

    LinearLayout mainlay1;
    LinearLayout mainlay2;
    LinearLayout mainlay3;

    int index;

    View tabcontainer;
    View coloredBackgroundView;
    Toolbar toolbar;

    private DrawerLayout drawerLayout;

    private musiXmatchLyricsConnector mLyricsPlugin = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initPermission();

        Button mybtn = findViewById(R.id.xemlist);

        context = this;
        function = new Function();

        mybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, Integer.toString(myService.getListNumber()), Toast.LENGTH_SHORT).show();
            }
        });

        IntentFilter toActivity = new IntentFilter("ToActivity");
        toActivity.addAction("PlayPause");
        toActivity.addAction("StartPlay");
        toActivity.addAction("timeSong");
        toActivity.addAction("timeTotal");
        toActivity.addAction("seekbar");
        toActivity.addAction("FragIndex");
        registerReceiver(myMainBroadcast, toActivity);

        initView();

        tabcontainer = findViewById(R.id.tabcontainer);
        coloredBackgroundView = findViewById(R.id.colored_background_view);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        MainSongList = new ArrayList<>();
        SongListOfResult = new ArrayList<>();
        SongListOfInnerResult = new ArrayList<>();

        AllAlbum = new ArrayList<>();
        AlbumOfResult = new ArrayList<>();

        AllArtist = new ArrayList<>();
        ArtistOfResult = new ArrayList<>();

        //function.getSongList(context, MainSongList);
        //function.getAlbumsLists(context, AllAlbum);
        //function.getArtist(context, AllArtist);

        resultAlbum = findViewById(R.id.album_result);
        resultArtist = findViewById(R.id.artist_result);
        resultSong = findViewById(R.id.song_result);
        resultInnerSong = findViewById(R.id.song_inner_result);

        songAdapter = new RecyclerSongAdapter();

        setSuggestion();

        searchView = findViewById(R.id.floating_search_view);

        //set layout slide listener
        slidingLayout = findViewById(R.id.sliding_layout);
        slidingLayout.getChildAt(1).setOnClickListener(null);
        //slidingLayout.setDragView(findViewById(R.id.dragview));

        imgDisc = findViewById(R.id.imageViewDisc);

        slidingLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    if (myService.isPng())
                        btnPlayPause.setImageResource(R.drawable.ic_pause_circle_outline_white_24dp);
                } else if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    if (myService.isPng())
                        btnPlayPause.setImageResource(R.drawable.ic_pause_circle_outline_white_24dp);
                }
            }
        });

        slidingLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (musicBound) {
                    switch (position) {
                        case 0:
                            myService.setListNumber(1);
                            myService.setListNumberFrag(1);
                            //songAdapter.setOnPlaylist(0);
                            break;
                        case 1:
                            myService.setListNumber(2);
                            myService.setListNumberFrag(2);
                            //songAdapter.setOnPlaylist(0);
                            break;
                        case 2:
                            myService.setListNumber(3);
                            myService.setListNumberFrag(3);
                            //songAdapter.setOnPlaylist(0);
                            break;
                        case 3:
                            myService.setListNumber(4);
                            myService.setListNumberFrag(4);
                            //songAdapter.setOnPlaylist(1);
                            break;
                        case 4:
                            myService.setListNumber(5);
                            myService.setListNumberFrag(5);
                            //songAdapter.setOnPlaylist(0);
                            break;
                    }
                }
                tabcontainer.clearAnimation();
                tabcontainer
                        .animate()
                        .translationY(0)
                        .start();
                coloredBackgroundView.setTranslationY(0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        searchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                if (!oldQuery.equals("") && newQuery.equals("")) {
                    searchView.clearSuggestions();
                } else {
                    searchView.showProgress();
                    searchView.swapSuggestions(getSuggestion(newQuery));
                    searchView.hideProgress();
                }
            }
        });

        searchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {
                searchView.showProgress();
                searchView.swapSuggestions(getSuggestion(searchView.getQuery()));
                searchView.hideProgress();
            }

            @Override
            public void onFocusCleared() {

            }
        });

        searchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

                Suggestion sug = (Suggestion) searchSuggestion;

                SetDataForResultView(sug.getBody());

                searchView.clearFocus();
            }

            @Override
            public void onSearchAction(String currentQuery) {

                SetDataForResultView(currentQuery);
            }
        });

        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myService.isPng()) {
                    myService.pausePlayer();
                    btnPlayPause.setImageResource(R.drawable.ic_play_circle_outline_white_24dp);
                    Intent pauseIntent = new Intent("ToService");
                    pauseIntent.setAction("SvPlayPause");
                    pauseIntent.putExtra("key", "pause");
                    sendBroadcast(pauseIntent);

                } else {
                    myService.go();
                    btnPlayPause.setImageResource(R.drawable.ic_pause_circle_outline_white_24dp);
                    Intent playIntent = new Intent("ToService");
                    playIntent.setAction("SvPlayPause");
                    playIntent.putExtra("key", "play");
                    sendBroadcast(playIntent);
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                myService.seek(seekBar.getProgress());
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myService.playNext();
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myService.playPrev();
            }
        });

        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myService.setShuffle();
            }
        });

        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myService.setRepeat();
            }
        });

        Button btn2 = findViewById(R.id.btnmainlay2);
        Button btn3 = findViewById(R.id.btnmainlay3);

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainlay1.setVisibility(View.INVISIBLE);
                mainlay2.setVisibility(View.INVISIBLE);
                mainlay3.setVisibility(View.VISIBLE);
                myService.setListNumber(7);  ///// Sua lai cho nay, dung ra nos la button quay lai
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainlay1.setVisibility(View.INVISIBLE);
                mainlay2.setVisibility(View.VISIBLE);
                mainlay3.setVisibility(View.INVISIBLE);
                myService.setListNumber(6);
            }
        });

        mLyricsPlugin = new musiXmatchLyricsConnector(this);
        mLyricsPlugin.setLoadingMessage("Your custom title", "Your custom message");

        Button btnhide = findViewById(R.id.btn_hide);
        btnhide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mLyricsPlugin.startLyricsActivity(myService.getSongArtist(), myService.getSongTitle());
                } catch (MissingPluginException e) {
                    mLyricsPlugin.downloadLyricsPlugin();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initView() {
        txtArtist = findViewById(R.id.artist);

        txtTitle = findViewById(R.id.title);

        txtTimeSong = findViewById(R.id.time_song);

        txtTotal = findViewById(R.id.time_total);

        seekBar = findViewById(R.id.seekbar_song);

        smartTabLayout = findViewById(R.id.viewpagertab);

        customPagerAdapter = new CustomPagerAdapter(this, getSupportFragmentManager());

        viewPager = findViewById(R.id.viewpager1);

        viewPager.setAdapter(customPagerAdapter);

        viewPager.setOffscreenPageLimit(customPagerAdapter.getCount() - 1);

        smartTabLayout.setViewPager(viewPager);

        //FragmentManager fragmentManager = getSupportFragmentManager();

        //FragmentAdapter fragmentAdapter = new FragmentAdapter(fragmentManager, MainActivity.this);

        btnPlayPause = findViewById(R.id.btn_play_pause);
        next = findViewById(R.id.btn_next);
        prev = findViewById(R.id.btn_prev);
        shuffle = findViewById(R.id.btn_shuffle);
        repeat = findViewById(R.id.btn_repeat);

        mainlay1 = findViewById(R.id.mainlay1);
        mainlay2 = findViewById(R.id.mainlay2);
        mainlay3 = findViewById(R.id.mainlay3);

        mainlay2.setVisibility(View.INVISIBLE);
        mainlay3.setVisibility(View.INVISIBLE);

    }

    public void initPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            //if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            //}
            //if (checkSelfPermission(Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WAKE_LOCK}, 1);
            //}
            //if (checkSelfPermission(Manifest.permission.MEDIA_CONTENT_CONTROL) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.MEDIA_CONTENT_CONTROL}, 1);
            //}
            //if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.INTERNET}, 1);
            //}
        }
    }

    private void setSuggestion() {
        for (Song song : MainSongList) {
            mSuggestion.add(new Suggestion(song.getTitle()));
            mSuggestion.add(new Suggestion(song.getArtist()));
        }
        for (Album album : AllAlbum) {
            mSuggestion.add(new Suggestion(album.getAlbumName()));
        }
        for (Artist artist : AllArtist) {
            mSuggestion.add(new Suggestion(artist.getName()));
        }
    }

    private ArrayList<Suggestion> getSuggestion(String query) {
        ArrayList<Suggestion> suggestions = new ArrayList<>();
        for (Suggestion suggestion : mSuggestion) {
            if (suggestion.getBody().toLowerCase().contains(query.toLowerCase())) {
                suggestions.add(suggestion);
            }
        }
        return suggestions;
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

        playintent = new Intent(this, MyService.class);
        this.startService(playintent);
        this.bindService(playintent, musicConnection, Context.BIND_AUTO_CREATE);
        registerReceiver(myMainBroadcast, new IntentFilter("ToActivity"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (musicBound) {
            this.unbindService(musicConnection);
            musicBound = false;
        }
    }

    @Override
    protected void onResume() {

        IntentFilter toActivity = new IntentFilter("ToActivity");
        toActivity.addAction("PlayPause");
        toActivity.addAction("StartPlay");
        toActivity.addAction("timeSong");
        toActivity.addAction("timeTotal");
        toActivity.addAction("seekbar");
        toActivity.addAction("FragIndex");
        registerReceiver(myMainBroadcast, toActivity);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String saveTitle = settings.getString("Title", "0");
        String saveArtist = settings.getString("Artist", "0");
        String savePath = settings.getString("Path", "0");
        txtTitle.setText(saveTitle);
        txtArtist.setText(saveArtist);
        if (!savePath.equals("0"))
            Glide.with(context).load(function.GetBitMapByte(savePath)).into(imgDisc);

        mLyricsPlugin.doBindService();

        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(myMainBroadcast);

        Intent intent = new Intent("ToPlaylist");
        intent.setAction("Unregister");
        sendBroadcast(intent);

        mLyricsPlugin.doUnbindService();

        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("curSongName", myService.getSongTitle());
        editor.putString("curArtist", myService.getSongArtist());
        editor.putString("curSongImg", SongPath);
        editor.apply();*/
    }

    BroadcastReceiver myMainBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
            String action = intent.getAction();
            if(action != null) {
                switch (action) {

                    case "PlayPause":
                        String od = intent.getStringExtra("key");
                        if (od.equals("pause")) {
                            btnPlayPause.setImageResource(R.drawable.ic_play_circle_outline_white_24dp);
                        } else if (od.equals("play")) {
                            btnPlayPause.setImageResource(R.drawable.ic_pause_circle_outline_white_24dp);
                        } else if (od.equals("complete"))
                            btnPlayPause.setImageResource(R.drawable.ic_play_circle_outline_white_24dp);
                        break;

                    case "StartPlay":
                        txtTitle.setText(intent.getStringExtra("title"));
                        txtArtist.setText(intent.getStringExtra("artist"));
                        btnPlayPause.setImageResource(R.drawable.ic_pause_circle_outline_white_24dp);
                        SongPath = intent.getStringExtra("songpath");
                        Glide.with(context).load(function.GetBitMapByte(SongPath)).into(imgDisc);
                        break;

                    case "FragIndex":
                        index = intent.getIntExtra("key", 0);
                        break;

                    case "timeTotal":
                        int timeTotal = intent.getIntExtra("key", 0);
                        seekBar.setMax(timeTotal);
                        txtTotal.setText(simpleDateFormat.format(timeTotal));
                        break;

                    case "timeSong":
                        int timeSong = intent.getIntExtra("key", 0);
                        seekBar.setProgress(timeSong);
                        txtTimeSong.setText(simpleDateFormat.format(timeSong));
                        break;

                    case "seekbar":
                        seekBar.setProgress(0);
                        break;
                }
            }
        }
    };

    @Override
    public void onBackPressed() {
        //If SlidingUp Panel is show, collapse it
        if (slidingLayout != null &&
                (slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED ||
                        slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
        //If SlingUp Panel is collaped and the inner result is show, close the inner result layout and back to the result
        else if (slidingLayout != null && (slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) &&
                (mainlay3.getVisibility() == View.VISIBLE)) {
            CloseLay3();
            OpenLay2();
            crossFadeAnimation(mainlay2, mainlay3, 270);
            myService.setListNumber(6);
        }
        //If SlidingUp Panel is collapsed and the result is show, close the result layout and back to the main layout, set Listnumber to Fragments
        else if (slidingLayout != null && (slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) &&
                (mainlay2.getVisibility() == View.VISIBLE)) {
            CloseLay2();
            OpenLay1();
            myService.setListNumber(myService.getListNumberFrag()); //Turn back to list content not searchview, so set the listNumber to the fragNumber before
        } else if ((myService.getListNumber() == 2) &&
                (slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED)
                && index == 2) {
            Toast.makeText(context, "close Album", Toast.LENGTH_SHORT).show();
            index = 0;
        } else {
            super.onBackPressed();
        }
    }

    private void SetDataForResultView(String keyword) {

        CloseLay1();
        CloseLay2();
        CloseLay3();

        OpenLay2();

        myService.setListNumber(6);
        SongListOfResult.clear();
        SongListOfInnerResult.clear();
        AlbumOfResult.clear();
        ArtistOfResult.clear();

        //Get Result Albums
        for (int i = 0; i < AllAlbum.size(); i++) {
            KMPSearch kmpSearch1 = new KMPSearch();
            kmpSearch1.Search(keyword.toLowerCase(), AllAlbum.get(i).getAlbumName().toLowerCase());
            if (kmpSearch1.GetSize() != 0 && kmpSearch1.GetFirstIndex() == 0) {
                AlbumOfResult.add(AllAlbum.get(i));
            } else {
                kmpSearch1.Search(" " + keyword.toLowerCase(), AllAlbum.get(i).getAlbumName().toLowerCase());
                if (kmpSearch1.GetSize() != 0 && kmpSearch1.GetFirstIndex() != 0)
                    AlbumOfResult.add(AllAlbum.get(i));
            }
        }

        //Get Result Artists
        for (int i = 0; i < AllArtist.size(); i++) {
            KMPSearch kmpSearch2 = new KMPSearch();
            kmpSearch2.Search(keyword.toLowerCase(), AllArtist.get(i).getName().toLowerCase());
            if (kmpSearch2.GetSize() != 0 && kmpSearch2.GetFirstIndex() == 0) {
                ArtistOfResult.add(AllArtist.get(i));
            } else {
                kmpSearch2.Search(" " + keyword.toLowerCase(), AllArtist.get(i).getName().toLowerCase());
                if (kmpSearch2.GetSize() != 0 && kmpSearch2.GetFirstIndex() != 0)
                    ArtistOfResult.add(AllArtist.get(i));
            }
        }

        //Get Result Songs
        for (int i = 0; i < MainSongList.size(); i++) {
            KMPSearch kmpSearch3 = new KMPSearch();
            kmpSearch3.Search(keyword.toLowerCase(), MainSongList.get(i).getTitle().toLowerCase());
            if (kmpSearch3.GetSize() != 0 && kmpSearch3.GetFirstIndex() == 0) {
                SongListOfResult.add(MainSongList.get(i));
            } else {
                kmpSearch3.Search(" " + keyword.toLowerCase(), MainSongList.get(i).getTitle().toLowerCase());
                if (kmpSearch3.GetSize() != 0 && kmpSearch3.GetFirstIndex() != 0)
                    SongListOfResult.add(MainSongList.get(i));
            }
        }

        //Set Adapter for Result Albums
        albumAdapterOfResult = new RecyclerAlbumAdapter(context, AlbumOfResult);
        albumAdapterOfResult.setAlbumClickListener(this);

        //Set Adapter for Result Artists
        artistAdapterOfResult = new RecyclerArtistAdapter(context, ArtistOfResult);
        artistAdapterOfResult.setArtistClickListener(this);

        //Set Layout for RecyclerView list result Albums
        RecyclerView.LayoutManager mManager1 = new GridLayoutManager(context, 2) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        resultAlbum.setLayoutManager(mManager1);
        resultAlbum.setAdapter(albumAdapterOfResult);

        //Now Set layout for result, all is disable to scroll vertical

        //Set Layout for RecyclerView list result Artists
        RecyclerView.LayoutManager mManager2 = new GridLayoutManager(context, 2) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        resultArtist.setLayoutManager(mManager2);
        resultArtist.setAdapter(artistAdapterOfResult);

        //Set Layout for RecyclerView list result Songs
        RecyclerView.LayoutManager manager1 = new LinearLayoutManager(context) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        songAdapterOfResult = new RecyclerSongAdapter(context, SongListOfResult);
        resultSong.setLayoutManager(manager1);
        resultSong.setAdapter(songAdapterOfResult);

        //Set list song to List show in Result
        myService.setSongListResult(SongListOfResult);
    }

    @Override
    public void onAlbumClick(View view, int position) {

        OpenLay3();
        crossFadeAnimation(mainlay3, mainlay2, 270);

        myService.setListNumber(7);
        SongListOfInnerResult.clear();
        resultInnerSong.setAdapter(null);

        for (int i = 0; i < MainSongList.size(); i++) {
            if (MainSongList.get(i).getAlbumid() == AlbumOfResult.get(position).getId())
                SongListOfInnerResult.add(MainSongList.get(i));
        }

        songAdapterOfInnerResult = new RecyclerSongAdapter(context, SongListOfInnerResult);
        RecyclerView.LayoutManager manager2 = new LinearLayoutManager(context);
        resultInnerSong.setLayoutManager(manager2);
        resultInnerSong.setAdapter(songAdapterOfInnerResult);

        myService.setSongListInnerResult(SongListOfInnerResult);
    }

    @Override
    public void onArtistClick(View view, int position) {

        OpenLay3();
        crossFadeAnimation(mainlay3, mainlay2, 270);

        myService.setListNumber(7);
        SongListOfInnerResult.clear();
        resultInnerSong.setAdapter(null);

        for (int i = 0; i < MainSongList.size(); i++) {
            if (MainSongList.get(i).getArtist().equals(ArtistOfResult.get(position).getName()))
                SongListOfInnerResult.add(MainSongList.get(i));
        }

        songAdapterOfInnerResult = new RecyclerSongAdapter(context, SongListOfInnerResult);
        RecyclerView.LayoutManager manager3 = new LinearLayoutManager(context);
        resultInnerSong.setLayoutManager(manager3);
        resultInnerSong.setAdapter(songAdapterOfInnerResult);

        myService.setSongListInnerResult(SongListOfInnerResult);
    }

    public ArrayList<Song> getAllSong() {
        return MainSongList;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.nav_song:
                viewPager.setCurrentItem(0, true);
                break;

            case R.id.nav_album:
                viewPager.setCurrentItem(1, true);
                break;

            case R.id.nav_artist:
                viewPager.setCurrentItem(2, true);
                break;

            case R.id.nav_playlist:
                viewPager.setCurrentItem(3, true);
                break;

            case R.id.nav_equalizer:
                Intent intent = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                if ((intent.resolveActivity(getPackageManager()) != null)) {
                    startActivityForResult(intent, RESULT_OK);
                } else {
                    // No equalizer found
                }
                break;

            case R.id.nav_online:

        }
        //close navigation drawer
        item.setChecked(true);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void OpenLay1() {
        mainlay1.animate().translationX(0);
        tabcontainer.animate().translationX(0).setDuration(720);
        toolbar.animate().translationX(0).setDuration(900);
        mainlay1.setVisibility(View.VISIBLE);
    }

    public void CloseLay1() {
        mainlay1.clearAnimation();
        toolbar.clearAnimation();
        tabcontainer.clearAnimation();

        mainlay1.animate().translationX(-mainlay1.getWidth()).setDuration(360);
        toolbar.animate().translationX(toolbar.getWidth());
        tabcontainer.animate().translationX(tabcontainer.getWidth());

        //mainlay1.setVisibility(View.INVISIBLE);
    }

    public void OpenLay2() {
        mainlay2.animate().translationX(0);
        mainlay2.setVisibility(View.VISIBLE);
    }

    public void CloseLay2() {
        mainlay2.animate().translationX(-mainlay2.getWidth());
        mainlay2.setVisibility(View.INVISIBLE);
    }

    public void OpenLay3() {
        mainlay3.animate().translationX(0);
        mainlay3.setVisibility(View.VISIBLE);
    }

    public void CloseLay3() {
        mainlay3.animate().translationX(-mainlay3.getWidth());
        mainlay3.setVisibility(View.INVISIBLE);
    }

    private void crossFadeAnimation(final View fadeInTarget, final View fadeOutTarget, long duration) {
        AnimatorSet mAnimationSet = new AnimatorSet();
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(fadeOutTarget, View.ALPHA, 1f, 0f);
        fadeOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        fadeOut.setInterpolator(new LinearInterpolator());

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(fadeInTarget, View.ALPHA, 0f, 1f);
        fadeIn.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                fadeInTarget.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        fadeIn.setInterpolator(new LinearInterpolator());
        mAnimationSet.setDuration(duration);
        mAnimationSet.playTogether(fadeOut, fadeIn);
        mAnimationSet.start();
    }
}
