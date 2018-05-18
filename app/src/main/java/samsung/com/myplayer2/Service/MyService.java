package samsung.com.myplayer2.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import samsung.com.myplayer2.Activities.MainActivity;
import samsung.com.myplayer2.Class.Constants;
import samsung.com.myplayer2.Class.Function;
import samsung.com.myplayer2.Class.PlaylistFunction;
import samsung.com.myplayer2.Handler.MusicStore;
import samsung.com.myplayer2.Model.Song;
import samsung.com.myplayer2.R;

/*
 * This is demo code to accompany the Mobiletuts+ series:
 * Android SDK: Creating a Music Player
 *
 * Sue Smith - February 2014
 */

public class MyService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_REWIND = "action_rewind";
    public static final String ACTION_FAST_FORWARD = "action_fast_foward";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PREVIOUS = "action_previous";
    public static final String ACTION_STOP = "action_stop";

    //media player1
    public static MediaPlayer player;

    MediaSession mediaSession;
    android.media.session.MediaController mController;

    Function function = new Function();

    public static final MusicStore musicStore = new MusicStore();

    //song list to play
    private ArrayList<Song> songs;

    //Number of song list
    private String ListType = "";
    //LastKeyword when search
    private String LastKeyword = "";
    //LastGenres choosen
    private String LastGenres = "";
    //Last Playlist choosen
    private Long LastPlaylistId = Long.valueOf(1);
    //Number of song list in Fragments
    private int ListNumberFrag = 1;
    //current position
    private int songPosn;
    //binder
    private final IBinder musicBind = new MusicBinder();
    //title of current song
    private String songTitle = "";
    //artist of current song
    private String songArtist = "";
    //notification id
    private static final int NOTIFY_ID = 27;
    //shuffle flag and random
    private boolean shuffle = false;
    private boolean repeat = false;
    private Random rand;

    public static boolean bothRun = true;

    private boolean recent = false;

    public Context context;

    SharedPreferences shared;
    SharedPreferences.Editor editor;


    public void onCreate() {
        //create the service
        super.onCreate();
        //initialize position
        songPosn = 0;
        //random
        rand = new Random();
        //create player
        player = new MediaPlayer();

        context = this;

        IntentFilter svintent = new IntentFilter(Constants.TO_SERVICE);
        svintent.addAction(Constants.SV_PLAY_PAUSE);
        svintent.addAction(Constants.SV_PLAYONE);
        svintent.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(myServBroadcast, svintent);

        //initialize
        initMusicPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //String action = intent.getAction();

        if (mediaSession == null) {
            initSession();
        }

//        if (action != null) {
//
//            switch (action) {
//
//                case Constants.ACTION.PAUSE_ACTION:
//                    pausePlayer();
//                    showNoti(2);
//
//                    Intent intent1 = new Intent("ToActivity");
//                    intent1.setAction("PlayPause");
//                    intent1.putExtra("key", "pause");
//                    sendBroadcast(intent1);
//
//                    progressHandler.removeCallbacks(run);
//
//                    break;
//
//                case Constants.ACTION.PLAY_ACTION:
//
//                    go();
//                    showNoti(1);
//
//                    Intent intent2 = new Intent("ToActivity");
//                    intent2.setAction("PlayPause");
//                    intent2.putExtra("key", "play");
//                    sendBroadcast(intent2);
//
//                    updateProgress();
//
//                    break;
//
//                case Constants.ACTION.NEXT_ACTION:
//                    showNoti(1);
//                    playNext();
//                    break;
//
//                case Constants.ACTION.PREV_ACTION:
//                    showNoti(1);
//                    playPrev();
//                    break;
//
//                case Constants.ACTION.EXIT_ACTION:
//                    if (!player.isPlaying()) {
//                        if (bothRun = true) {
//                            stopForeground(true);
//                            checkBothRun();
//                        } else if (bothRun = false) {
//                            stopForeground(true);
//                            unregisterReceiver(myServBroadcast);
//                            stopSelf();
//                        }
//                    }
//                    break;
//            }
//        }

        handleIntent(intent);

        return START_NOT_STICKY;
    }


    public void initMusicPlayer() {

        //set player properties
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //set listeners
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    //binder
    public class MusicBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

    //activity will bind to service
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    //release resources when unbind
    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }

    //play a song
    public void playSong(String type, boolean recent) {
        //play
        player.reset();

        //get song
        SetListByType(type);

        Song playSong = songs.get(songPosn);
        //get title
        songTitle = playSong.getTitle();
        //get artist
        songArtist = playSong.getArtist();
        //get id
        long currSong = playSong.getID();

        //set uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);

        //set the data source
        try {
            player.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        try {
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //player.prepareAsync();

        //Send broadcast to MainActivity to set infomation of the song playing
        Intent setup = new Intent(Constants.TO_ACTIVITY);
        setup.setAction(Constants.START_PLAY);
        setup.putExtra(Constants.SONG_TITLE, songTitle);
        setup.putExtra(Constants.ARTIST, songArtist);
        setup.putExtra(Constants.SONG_PATH, playSong.getData());
        sendBroadcast(setup);

        if(!recent) {
            //Store infomation of playing song and playing list in Internal Storage, to resume the song and the list song after stop service and kill app
            //And we do not store info of Recent songs and list here
            shared = PreferenceManager.getDefaultSharedPreferences(context);
            editor = shared.edit();
            //editor.clear();   //Luu y la sharedPreference tu dong xoa cap key-value truoc do bi trung
            editor.putInt(Constants.LAST_POSITION, songPosn);
            editor.putString(Constants.LAST_SONG_TITLE, songs.get(songPosn).getTitle());
            editor.putString(Constants.LAST_ARTIST, songs.get(songPosn).getArtist());
            editor.putString(Constants.LAST_PATH, songs.get(songPosn).getData());
            editor.putString(Constants.LAST_ALBUMID, Long.toString(songs.get(songPosn).getAlbumid()));
            editor.putString(Constants.LAST_TYPE, ListType);
            editor.putString(Constants.LAST_KEYWORD, LastKeyword);
            editor.putString(Constants.LAST_GENRES, LastGenres);
            editor.putLong(Constants.LAST_PLAYLIST_ID, LastPlaylistId);
            editor.apply();
        }

        //Prepare
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                updateProgress();
                buildNotificationPlay(generateAction(R.drawable.ic_pause_black_24dp, "Pause", ACTION_PAUSE));
            }
        });
    }

    private void getLastList(String type) {

        switch (type) {
            case Constants.ALBUM_TYPE:
                setSongListOfAlbum(function.getLastSongList(context, "album_id LIKE '" + getLastString(Constants.LAST_ALBUMID) + "'"));
                break;
            case Constants.ARTIST_TYPE:
                setSongListOfArtist(function.getLastSongList(context, "artist LIKE '" + getLastString(Constants.LAST_ARTIST) + "'"));
                break;
            case Constants.GENRES_TYPE:
                setSongListOfGenres(function.getLastSongListOfGenres(context, getLastString(Constants.LAST_GENRES)));
                break;
            case Constants.PLAYLIST_TYPE:
                setSongListOfPlaylist(PlaylistFunction.getLastSongsInPlaylist(context, getLastLongId(Constants.LAST_PLAYLIST_ID)));
                break;
            case Constants.SEARCH_TYPE:
                setSongListOfSearch(function.getLastSongList(context, "title LIKE '" + getLastString(Constants.LAST_KEYWORD) + "%'"));
                break;
        }

        SetListByType(type);

        songPosn = PreferenceManager.getDefaultSharedPreferences(context).getInt(Constants.LAST_POSITION, 0);
    }


    private String getLastString(String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, "0");
    }

    private Long getLastLongId(String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(key, 1);
    }

    private void SetListByType(String type) {
        if (type != null) {
            switch (type) {
                case Constants.SONG_TYPE:
                    setList(musicStore.getAllsongs());
                    break;
                case Constants.ALBUM_TYPE:
                    setList(musicStore.getSongListOfAlbum());
                    break;
                case Constants.ARTIST_TYPE:
                    setList(musicStore.getSongListOfArtist());
                    break;
                case Constants.PLAYLIST_TYPE:
                    setList(musicStore.getSongListOfPlaylist());
                    break;
                case Constants.GENRES_TYPE:
                    setList(musicStore.getSongListOfGenres());
                    break;
                case Constants.SEARCH_TYPE:
                    setList(musicStore.getSongListOfSearch());
                    break;
                case Constants.RECENT_TYPE:
                    setList(musicStore.getSongListOfRecent());
            }
        }
    }

    //set the song postion in list to play
    public void setSong(int songIndex) {
        songPosn = songIndex;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.v("MUSIC PLAYER", "Playback Error");
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        mp.start();
        updateProgress();
        //showNoti(1);
    }


    public void showNoti(int casenum) {
        //notification
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0, notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pauseIntent = new Intent(this, MyService.class);
        pauseIntent.setAction(Constants.ACTION.PAUSE_ACTION);
        PendingIntent ppauseIntent = PendingIntent.getService(this, 0, pauseIntent, 0);

        Intent playIntent = new Intent(this, MyService.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0, playIntent, 0);

        Intent nextIntent = new Intent(this, MyService.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0, nextIntent, 0);

        Intent prevIntent = new Intent(this, MyService.class);
        prevIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent pprevIntent = PendingIntent.getService(this, 0, prevIntent, 0);

        Intent closeIntent = new Intent(this, MyService.class);
        closeIntent.setAction(Constants.ACTION.EXIT_ACTION);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0, closeIntent, 0);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.ic_play_circle_outline_white_24dp)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Now Playing")
                .setContentText(songTitle)
                .setAutoCancel(true)
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_STOP))
                .setVisibility(Notification.VISIBILITY_PUBLIC)
        ;
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notice_layout);
        switch (casenum) {
            case 1:
                contentView.setImageViewResource(R.id.notice_play, R.drawable.ic_pause_black_24dp);
                contentView.setOnClickPendingIntent(R.id.notice_play, ppauseIntent);
                break;
            case 2:
                contentView.setOnClickPendingIntent(R.id.notice_play, pplayIntent);
                contentView.setImageViewResource(R.id.notice_play, R.drawable.ic_play_arrow_black_24dp);
                break;
        }


        contentView.setTextViewText(R.id.song_name, songTitle);
        contentView.setTextViewText(R.id.song_artist, songArtist);
        contentView.setOnClickPendingIntent(R.id.notice_next, pnextIntent);
        contentView.setOnClickPendingIntent(R.id.notice_prev, pprevIntent);
        contentView.setOnClickPendingIntent(R.id.exit_notice, pcloseIntent);


        Notification noti = builder.build();
        noti.contentView = contentView;

        startForeground(NOTIFY_ID, noti);
    }

    //playback methods
    public int getPosn() {
        return player.getCurrentPosition();
    }

    public int getDur() {
        return player.getDuration();
    }

    public boolean isPng() {
        return player.isPlaying();
    }

    public void pausePlayer() {
        player.pause();
    }

    public void seek(int posn) {
        player.seekTo(posn);
    }

    public void go() {
        player.start();
    }

    public String getSongTitle() {
        return songTitle;
    }

    public String getSongArtist() {
        return songArtist;
    }

    //skip to previous track
    public void playPrev() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                if (songs.size() == 0) {
                    setListType(getLastString(Constants.LAST_TYPE));
                    setLastGenres(getLastString(Constants.LAST_GENRES));
                    setLastKeyword(getLastString(Constants.LAST_KEYWORD));
                    setLastPlaylistId(getLastLongId(Constants.LAST_PLAYLIST_ID));
                    getLastList(ListType);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try {
                    progressHandler.removeCallbacks(run);
                    if (shuffle) {
                        int newSong = songPosn;
                        while (newSong == songPosn) {
                            newSong = rand.nextInt(songs.size());
                        }
                        songPosn = newSong;
                    } else {
                        songPosn--;
                        if (songPosn < 0) songPosn = songs.size() - 1;
                    }

                    if (new File(songs.get(songPosn).getData()).exists()) {
                        playSong(ListType, recent);
                        updateProgress();
                    } else {
                        playPrev();
                    }
                    Toast.makeText(getApplicationContext(), "Jumped to index " + Integer.toString(songPosn), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Not found audio index" + Integer.toString(songPosn), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    //skip to next
    public void playNext() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                if (songs.size() == 0) {
                    setListType(getLastString(Constants.LAST_TYPE));
                    setLastGenres(getLastString(Constants.LAST_GENRES));
                    setLastKeyword(getLastString(Constants.LAST_KEYWORD));
                    setLastPlaylistId(getLastLongId(Constants.LAST_PLAYLIST_ID));
                    getLastList(ListType);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try {
                    progressHandler.removeCallbacks(run);
                    if (!repeat) {
                        if (shuffle) {
                            int newSong = songPosn;
                            while (newSong == songPosn) {
                                newSong = rand.nextInt(songs.size());
                            }
                            songPosn = newSong;
                        } else {
                            songPosn++;
                            if (songPosn >= songs.size()) songPosn = 0;
                        }
                    }

                    if (new File(songs.get(songPosn).getData()).exists()) {
                        playSong(ListType, recent);
                        updateProgress();
                    } else {
                        playNext();
                    }
                    Toast.makeText(getApplicationContext(), "Jumped to index " + Integer.toString(songPosn), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Not found audio index" + Integer.toString(songPosn), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    //toggle shuffle
    public void setShuffle() {
        if (shuffle) {
            shuffle = false;
            Toast.makeText(getApplicationContext(), "Shufle is Off", Toast.LENGTH_SHORT).show();
        } else {
            shuffle = true;
            Toast.makeText(getApplicationContext(), "Shuffle is On", Toast.LENGTH_SHORT).show();
        }
    }

    public void setRepeat() {
        if (repeat) {
            repeat = false;
            Toast.makeText(getApplicationContext(), "Repeat is Off", Toast.LENGTH_SHORT).show();
        } else {
            repeat = true;
            Toast.makeText(getApplicationContext(), "Repeat is On", Toast.LENGTH_SHORT).show();
        }
    }

    public void setRecent() {
        if (recent) {
            recent = false;
        } else {
            recent = true;
        }
    }

    public boolean checkBothRun() {
        if (bothRun) return false;
        else
            return true;
    }

    BroadcastReceiver myServBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().toString().equals(Constants.SV_PLAY_PAUSE)) {
                if (intent.getStringExtra(Constants.KEY).equals(Constants.PAUSE)) {
                    //String songlen = Integer.toString(PreferenceManager.getDefaultSharedPreferences(context).getInt("Position", 0));
                    //Toast.makeText(getApplicationContext(), songlen, Toast.LENGTH_SHORT).show();

                    mController.getTransportControls().pause();

                    progressHandler.removeCallbacks(run);

                } else if (intent.getStringExtra(Constants.KEY).equals(Constants.PLAY)) {
                    //String songlen = Integer.toString(PreferenceManager.getDefaultSharedPreferences(context).getInt("Position", 0));
                    //Toast.makeText(getApplicationContext(), songlen, Toast.LENGTH_SHORT).show();

                    if (songs.size() == 0) {
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... voids) {
                                setListType(getLastString(Constants.LAST_TYPE));
                                setLastGenres(getLastString(Constants.LAST_GENRES));
                                setLastKeyword(getLastString(Constants.LAST_KEYWORD));
                                setLastPlaylistId(getLastLongId(Constants.LAST_PLAYLIST_ID));
                                getLastList(ListType);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                try {
                                    playSong(ListType, recent);
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), "Audio not found or List songs is null", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        //Toast.makeText(context, PreferenceManager.getDefaultSharedPreferences(context).getString(Constants.LAST_TYPE, "0"), Toast.LENGTH_SHORT).show();
                    } else {
                        mController.getTransportControls().play();

                        updateProgress();
                    }

                }
            } else if (intent.getAction().toString().equals(Constants.SV_PLAYONE)) {

                //Toast.makeText(getApplicationContext(), "Service received: Play One", Toast.LENGTH_SHORT).show();

                Integer posn = intent.getIntExtra(Constants.POSITION, 0);
                String getType = intent.getStringExtra(Constants.TYPE_NAME);

                if(getType.equals(Constants.RECENT_TYPE)) {
                    recent = true;
                }else {
                    setListType(getType);
                    recent = false;
                }

                if (SizeList() <= posn)
                    songs = musicStore.getAllsongs();

                setSong(posn);

                progressHandler.removeCallbacks(run);
                playSong(getType, recent);

                Intent intent4 = new Intent(Constants.TO_ACTIVITY);
                intent4.setAction(Constants.PLAY_PAUSE);
                intent4.putExtra(Constants.KEY, Constants.PLAY);
                sendBroadcast(intent4);

            } else if (intent.getAction().compareTo(AudioManager.ACTION_AUDIO_BECOMING_NOISY) == 0) {

                mController.getTransportControls().pause();

                progressHandler.removeCallbacks(run);

            }
        }
    };

    public void updateProgress() {
        try {
            progressHandler.postDelayed(run, 100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static Handler progressHandler = new Handler();

    Runnable run = new Runnable() {
        @Override
        public void run() {
            //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");

            try {

                Intent intent1 = new Intent(Constants.TO_ACTIVITY);
                intent1.setAction(Constants.TIME_TOTAL);
                intent1.putExtra(Constants.KEY, player.getDuration());
                sendBroadcast(intent1);

                //seekPro.get().setMax(player.getDuration());
                //txtTotal.get().setText(simpleDateFormat.format(player.getDuration()));

                Intent intent2 = new Intent(Constants.TO_ACTIVITY);
                intent2.setAction(Constants.TIME_SONG);
                intent2.putExtra(Constants.KEY, player.getCurrentPosition());
                sendBroadcast(intent2);
                //txtCurTime.get().setText(simpleDateFormat.format(player.getCurrentPosition()));

                //Intent intent3 = new Intent("ToActivity");
                //intent3.setAction("seekbar");
                //intent3.putExtra("key", player.getCurrentPosition());
                //sendBroadcast(intent3);
                //seekPro.get().setProgress(player.getCurrentPosition());
                progressHandler.postDelayed(this, 100);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    @Override
    public void onCompletion(MediaPlayer mp) {
        //check if playback has reached the end of a track
        Intent intent3 = new Intent(Constants.TO_ACTIVITY);
        intent3.setAction(Constants.SEEKBAR);
        //intent3.putExtra("key", 0);
        sendBroadcast(intent3);

        progressHandler.removeCallbacks(run);

        Intent intent4 = new Intent(Constants.TO_ACTIVITY);
        intent4.setAction(Constants.PLAY_PAUSE);
        intent4.putExtra(Constants.KEY, Constants.COMPLETE);
        sendBroadcast(intent4);
        //btnPayPause.get().setImageResource(R.drawable.ic_play_circle_outline_white_24dp);
        if (player.getCurrentPosition() > 0) {
            mp.reset();
            playNext();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        progressHandler.removeCallbacks(run);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        progressHandler.removeCallbacks(run);
        seek(seekBar.getProgress());
        updateProgress();
    }

    public int SizeList() {
        return songs.size();
    }

    public String GetSongPath() {
        Song playSong = songs.get(songPosn);
        return playSong.getData();
    }

    //set list of all songs
    public void setAllSongs(ArrayList<Song> listAll) {
        musicStore.setAllsongs(listAll);
    }

    //pass song list
    public void setList(ArrayList<Song> theSongs) {
        songs = theSongs;
    }

    public void setSongListOfAlbum(ArrayList<Song> songListOfAlbum) {
        musicStore.setSongListOfAlbum(songListOfAlbum);
    }

    public void setSongListOfArtist(ArrayList<Song> songListOfArtist) {
        musicStore.setSongListOfArtist(songListOfArtist);
    }

    public void setSongListOfPlaylist(ArrayList<Song> songListOfPlaylist) {
        musicStore.setSongListOfPlaylist(songListOfPlaylist);
    }

    public void setSongListOfGenres(ArrayList<Song> songListOfGenres) {
        musicStore.setSongListOfGenres(songListOfGenres);
    }

    public void setSongListOfSearch(ArrayList<Song> songListOfSearch) {
        musicStore.setSongListOfSearch(songListOfSearch);
    }

    public void setSongListOfRecent(ArrayList<Song> songListOfRecent) {
        musicStore.setSongListOfRecent(songListOfRecent);
    }

    public void setListType(String type) {
        ListType = type;
    }

    public void setLastKeyword(String key) {
        LastKeyword = key;
    }

    public void setLastGenres(String key) {
        LastGenres = key;
    }

    public void setLastPlaylistId(Long Id) {
        LastPlaylistId = Id;
    }

    public Song getCurSong() {
        try {
            return songs.get(songPosn);
        } catch (Exception e) {
            return musicStore.getAllsongs().get(0);
        }
    }

    private void handleIntent(Intent intent) {
        if (intent == null || intent.getAction() == null)
            return;

        String action = intent.getAction();

        if (action.equalsIgnoreCase(ACTION_PLAY)) {
            mController.getTransportControls().play();
        } else if (action.equalsIgnoreCase(ACTION_PAUSE)) {
            mController.getTransportControls().pause();
        } else if (action.equalsIgnoreCase(ACTION_FAST_FORWARD)) {
            mController.getTransportControls().fastForward();
        } else if (action.equalsIgnoreCase(ACTION_REWIND)) {
            mController.getTransportControls().rewind();
        } else if (action.equalsIgnoreCase(ACTION_PREVIOUS)) {
            mController.getTransportControls().skipToPrevious();
        } else if (action.equalsIgnoreCase(ACTION_NEXT)) {
            mController.getTransportControls().skipToNext();
        } else if (action.equalsIgnoreCase(ACTION_STOP)) {
            mController.getTransportControls().stop();
        }
    }

    private Notification.Action generateAction(int icon, String title, String intentAction) {
        Intent intent = new Intent(getApplicationContext(), MyService.class);
        intent.setAction(intentAction);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        return new Notification.Action.Builder(icon, title, pendingIntent).build();
    }

    private void buildNotificationPlay(Notification.Action action) {
        Notification.MediaStyle style = new Notification.MediaStyle();


        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0, notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent = new Intent(getApplicationContext(), MyService.class);
        intent.setAction(ACTION_STOP);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);

        Notification.Builder builder = new Notification.Builder(this)
                .setContentIntent(pendInt)
                .setContentTitle(songTitle)
                .setContentText(songArtist)
                .setSmallIcon(R.drawable.ic_audiotrack)
                .setLargeIcon(loadLargeIcon(getCurSong().getData()))
                .setAutoCancel(true)
                .setOngoing(false)
                .setDeleteIntent(pendingIntent)
                .setStyle(style)
                .setVisibility(Notification.VISIBILITY_PUBLIC);

        builder.addAction(generateAction(R.drawable.ic_skip_previous_black_24dpsmall, "Previous", ACTION_PREVIOUS));
        builder.addAction(action);
        builder.addAction(generateAction(R.drawable.ic_skip_next_black_24dpsmall, "Next", ACTION_NEXT));
        style.setShowActionsInCompactView(0, 1, 2, 3, 4);

        Notification notice = builder.build();

        startForeground(NOTIFY_ID, notice);
    }

    private void buildNotificationPause(Notification.Action action) {
        Notification.MediaStyle style = new Notification.MediaStyle();

        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0, notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent = new Intent(getApplicationContext(), MyService.class);
        intent.setAction(ACTION_STOP);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);

        Notification.Builder builder = new Notification.Builder(this)
                .setContentIntent(pendInt)
                .setContentTitle(songTitle)
                .setContentText(songArtist)
                .setSmallIcon(R.drawable.ic_audiotrack)
                .setLargeIcon(loadLargeIcon(getCurSong().getData()))
                .setAutoCancel(true)
                .setOngoing(false)
                .setDeleteIntent(pendingIntent)
                .setStyle(style)
                .setVisibility(Notification.VISIBILITY_PUBLIC);

        builder.addAction(generateAction(R.drawable.ic_skip_previous_black_24dpsmall, "Previous", ACTION_PREVIOUS));
        builder.addAction(action);
        builder.addAction(generateAction(R.drawable.ic_skip_next_black_24dpsmall, "Next", ACTION_NEXT));
        style.setShowActionsInCompactView(0, 1, 2, 3, 4);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notice = builder.build();

        stopForeground(true);

        notificationManager.notify(NOTIFY_ID, notice);
    }

    private void initSession() {
        mediaSession = new MediaSession(this, "MySession");
        mController = new android.media.session.MediaController(this, mediaSession.getSessionToken());

        mediaSession.setCallback(new MediaSession.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();

                go();
                Intent intent2 = new Intent(Constants.TO_ACTIVITY);
                intent2.setAction(Constants.PLAY_PAUSE);
                intent2.putExtra(Constants.KEY, Constants.PLAY);
                sendBroadcast(intent2);

                updateProgress();

                Log.e("MediaPlayerService", "onPlay");
                buildNotificationPlay(generateAction(R.drawable.ic_pause_black_24dp, "Pause", ACTION_PAUSE));
            }

            @Override
            public void onPause() {
                super.onPause();

                pausePlayer();
                Intent intent1 = new Intent(Constants.TO_ACTIVITY);
                intent1.setAction(Constants.PLAY_PAUSE);
                intent1.putExtra(Constants.KEY, Constants.PAUSE);
                sendBroadcast(intent1);

                progressHandler.removeCallbacks(run);

                Log.e("MediaPlayerService", "onPause");
                buildNotificationPause(generateAction(R.drawable.ic_play_arrow_black_24dp, "Play", ACTION_PLAY));
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                playNext();
                Log.e("MediaPlayerService", "onSkipToNext");
                //Change media here
                buildNotificationPlay(generateAction(R.drawable.ic_pause_black_24dp, "Pause", ACTION_PAUSE));
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                playPrev();
                Log.e("MediaPlayerService", "onSkipToPrevious");
                //Change media here
                buildNotificationPlay(generateAction(R.drawable.ic_pause_black_24dp, "Pause", ACTION_PAUSE));
            }

            @Override
            public void onStop() {
                super.onStop();
                Log.e("MediaPlayerService", "onStop");
                //Stop media player here
                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(NOTIFY_ID);
                Intent intent = new Intent(getApplicationContext(), MyService.class);
                stopService(intent);
            }

        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaSession.release();
        if (player != null) {
            player.stop();
            player.release();
        }
        unregisterReceiver(myServBroadcast);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if (!player.isPlaying()) {
            stopForeground(true);

            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(NOTIFY_ID);

            //unregisterReceiver(myServBroadcast);
            stopSelf();
            Log.i("Thong bao", "Da dung lai Service ");
        } else {
            checkBothRun();
        }

        Log.i("Thong bao", "Da kill task xxxxxxxxxxxxxxxxx ");

        super.onTaskRemoved(rootIntent);

    }

    private Bitmap loadLargeIcon(String path) {
        Bitmap largeIcon;

        try {
            byte[] bitmapByte = function.GetBitMapByte(path);
            largeIcon = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);
        } catch (Exception e) {
            largeIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.noteicon);
        }

        return largeIcon;
    }

    public int getAudioSessionId() {
        try {
            return player.getAudioSessionId();
        } catch (Exception e) {
            return 0;
        }
    }
}
