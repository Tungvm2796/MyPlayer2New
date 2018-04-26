package samsung.com.myplayer2.Activities;

import android.Manifest;
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
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.bumptech.glide.Glide;
import com.musixmatch.lyrics.musiXmatchLyricsConnector;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import samsung.com.myplayer2.Class.Constants;
import samsung.com.myplayer2.Class.Function;
import samsung.com.myplayer2.Fragments.AlbumSongFragment;
import samsung.com.myplayer2.Fragments.ArtistSongFragment;
import samsung.com.myplayer2.Fragments.BlankFragment;
import samsung.com.myplayer2.Fragments.LyricsFragment;
import samsung.com.myplayer2.Fragments.MainFragment;
import samsung.com.myplayer2.Fragments.SearchFragment;
import samsung.com.myplayer2.Model.Song;
import samsung.com.myplayer2.R;
import samsung.com.myplayer2.Service.MyService;

public class MainActivity extends BaseActivity {

    //private ArrayList<Suggestion> mSuggestion = new ArrayList<>();

    Function function;

    Context context;

    SlidingUpPanelLayout slidingLayout;
    FloatingSearchView searchView;

    MyService myService;
    private boolean musicBound = false;
    private Intent playintent;

    Button btnHide;

    TextView txtTitle;
    TextView txtArtist;
    public ImageButton btnPlayPause;
    ImageButton next;
    ImageButton prev;
    ImageButton shuffle;
    ImageButton repeat;
    public TextView txtTimeSong;
    public TextView txtTotal;
    public SeekBar seekBar;
    ImageView imgDisc;

    String SongPath;

    private DrawerLayout drawerLayout;
    NavigationView navigationView;

    private musiXmatchLyricsConnector mLyricsPlugin = null;

    private String action;
    private Runnable runnable;
    private Map<String, Runnable> navigationMap = new HashMap<String, Runnable>();
    private Handler navDrawerRunnable = new Handler();

    private Runnable navigateLibrary = new Runnable() {
        public void run() {
            navigationView.getMenu().findItem(R.id.nav_library).setChecked(true);
            Fragment fragment = new MainFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment).commitAllowingStateLoss();

        }
    };

    private Runnable navigatePlaylist = new Runnable() {
        public void run() {
            navigationView.getMenu().findItem(R.id.nav_playlist).setChecked(true);
            Fragment fragment = new BlankFragment();            //<-------------------------------<< thay no bang playlist fragment
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(getSupportFragmentManager().findFragmentById(R.id.fragment_container));
            transaction.replace(R.id.fragment_container, fragment).commit();

        }
    };

    private Runnable navigateLyrics = new Runnable() {
        public void run() {
            Fragment fragment = new LyricsFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();

            Bundle args = new Bundle();
            args.putString(Constants.SONG_PATH, myService.GetSongPath());
            args.putString(Constants.SONG_TITLE, myService.getSongTitle());
            args.putString(Constants.ARTIST, myService.getSongArtist());
            fragment.setArguments(args);

            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
        }
    };

    private Runnable navigateAlbum = new Runnable() {
        public void run() {
            long albumID = getIntent().getExtras().getLong(Constants.ALBUM_ID);
            Fragment fragment = AlbumSongFragment.getFragment(albumID);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment).commit();
        }
    };

    private Runnable navigateArtist = new Runnable() {
        public void run() {
            String artist_name = getIntent().getExtras().getString(Constants.ARTIST);
            Fragment fragment = ArtistSongFragment.getFragment(artist_name);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment).commit();
        }
    };

    private Runnable navigateSearch = new Runnable() {
        public void run() {
            Fragment fragment = new SearchFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        action = getIntent().getAction();

        super.onCreate(savedInstanceState);

        initPermission();

        setContentView(R.layout.activity_main);

        navigationMap.put(Constants.NAVIGATE_LIBRARY, navigateLibrary);
        navigationMap.put(Constants.NAVIGATE_PLAYLIST, navigatePlaylist);
        navigationMap.put(Constants.NAVIGATE_LYRICS, navigateLyrics);
        navigationMap.put(Constants.NAVIGATE_ALBUM, navigateAlbum);
        navigationMap.put(Constants.NAVIGATE_ARTIST, navigateArtist);
        navigationMap.put(Constants.NAVIGATE_SEARCH, navigateSearch);

        context = this;
        function = new Function();


        IntentFilter toActivity = new IntentFilter("ToActivity");
        toActivity.addAction("PlayPause");
        toActivity.addAction("StartPlay");
        toActivity.addAction("timeSong");
        toActivity.addAction("timeTotal");
        toActivity.addAction("seekbar");
        registerReceiver(myMainBroadcast, toActivity);

        initView();

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


        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myService.isPng()) {
                    //myService.pausePlayer();
                    btnPlayPause.setImageResource(R.drawable.ic_play_circle_outline_white_24dp);
                    Intent pauseIntent = new Intent("ToService");
                    pauseIntent.setAction("SvPlayPause");
                    pauseIntent.putExtra("key", "pause");
                    sendBroadcast(pauseIntent);

                } else {
                    //myService.go();
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

        mLyricsPlugin = new musiXmatchLyricsConnector(this);
        mLyricsPlugin.setLoadingMessage("Your custom title", "Your custom message");

//        Button btnhide = findViewById(R.id.btn_hide);
//        btnhide.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    mLyricsPlugin.startLyricsActivity(myService.getSongArtist(), myService.getSongTitle());
//                } catch (MissingPluginException e) {
//                    mLyricsPlugin.downloadLyricsPlugin();
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        btnHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //NavigationHelper.goToLyrics(MainActivity.this);
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                navigateLyrics.run();
            }
        });

        navDrawerRunnable.postDelayed(new Runnable() {
            @Override
            public void run() {
                setupDrawerContent(navigationView);
            }
        }, 700);

        addBackstackListener();

        loadEverything();

        //END onCreate() ---------------------------------------------------------------
    }

    private void initPermission() {
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

    private void initView() {
        txtArtist = findViewById(R.id.artist);

        txtTitle = findViewById(R.id.title);

        txtTimeSong = findViewById(R.id.time_song);

        txtTotal = findViewById(R.id.time_total);

        seekBar = findViewById(R.id.seekbar_song);

        btnPlayPause = findViewById(R.id.btn_play_pause);
        next = findViewById(R.id.btn_next);
        prev = findViewById(R.id.btn_prev);
        shuffle = findViewById(R.id.btn_shuffle);
        repeat = findViewById(R.id.btn_repeat);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        navigationView = findViewById(R.id.nav_view);

        searchView = findViewById(R.id.floating_search_view);

        //set layout slide listener
        slidingLayout = findViewById(R.id.sliding_layout);
        slidingLayout.getChildAt(1).setOnClickListener(null);
        //slidingLayout.setDragView(findViewById(R.id.dragview));

        imgDisc = findViewById(R.id.imageViewDisc);

        btnHide = findViewById(R.id.btn_hide);
    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            MyService.MusicBinder binder = (MyService.MusicBinder) service;
            //get service
            myService = binder.getService();

            //pass list
            //Check if list playing song is null, pass a song list with length = 0, to prevent Null List;
            int sizeList;
            try {
                sizeList = myService.SizeList();
            } catch (Exception e) {
                sizeList = 0;
            }

            if (sizeList == 0) {
                ArrayList<Song> ZeroList = new ArrayList<>();
                myService.setList(ZeroList);
            }

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
        editor.putString("curArtist", myService.getSongArtist());
        editor.putString("curSongName", myService.getSongTitle());
        editor.putString("curSongImg", SongPath);
        editor.apply();*/
    }

    BroadcastReceiver myMainBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
            String action = intent.getAction();
            if (action != null) {
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
        } else if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                if (isNavigatingMain()) {
                    drawerLayout.openDrawer(GravityCompat.START);
                } else super.onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(final MenuItem menuItem) {
                        updatePosition(menuItem);
                        return true;

                    }
                });
    }

    private void updatePosition(final MenuItem menuItem) {
        runnable = null;

        switch (menuItem.getItemId()) {
            case R.id.nav_library:
                runnable = navigateLibrary;

                break;

            case R.id.nav_playlist:
                runnable = navigatePlaylist;

                break;

            case R.id.nav_equalizer:
                Intent intent = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                if ((intent.resolveActivity(getPackageManager()) != null)) {
                    startActivityForResult(intent, RESULT_OK);
                } else {
                    // No equalizer found
                }
                break;
        }

        if (runnable != null) {
            menuItem.setChecked(true);
            drawerLayout.closeDrawers();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    runnable.run();
                }
            }, 350);
        }
    }

    private void addBackstackListener() {
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                getSupportFragmentManager().findFragmentById(R.id.fragment_container).onResume();
            }
        });
    }

    private void loadEverything() {
        Runnable navigation = navigationMap.get(action);
        if (navigation != null) {
            navigation.run();
        } else {
            navigateLibrary.run();
        }
    }

    private boolean isNavigatingMain() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        return (currentFragment instanceof MainFragment);
    }

}
