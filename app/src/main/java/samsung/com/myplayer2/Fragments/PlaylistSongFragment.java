package samsung.com.myplayer2.Fragments;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import samsung.com.myplayer2.Adapter.SongInPlaylistAdapter;
import samsung.com.myplayer2.Class.Constants;
import samsung.com.myplayer2.Class.EditItemTouchHelperCallback;
import samsung.com.myplayer2.Class.Function;
import samsung.com.myplayer2.Class.OnStartDragListener;
import samsung.com.myplayer2.Class.PlaylistFunction;
import samsung.com.myplayer2.Model.Song;
import samsung.com.myplayer2.R;
import samsung.com.myplayer2.Service.MyService;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistSongFragment extends Fragment implements OnStartDragListener {


    public PlaylistSongFragment() {
        // Required empty public constructor
    }

    Long playlistId;
    String playlistName;

    MyService myService;
    private boolean musicBound = false;
    private Intent playintent;

    Toolbar toolbar;

    TextView playlistTxtName;
    ImageView PlaylistImg;
    SongInPlaylistAdapter adapter;
    RecyclerView PlaylistSongView;
    ArrayList<Song> SongOfPlaylist;
    PlaylistFunction playlistFunction = new PlaylistFunction();

    ItemTouchHelper mItemTouchHelper;

    public static PlaylistSongFragment getFragment(Long playlistID, String playlistName) {
        PlaylistSongFragment fragment = new PlaylistSongFragment();

        Bundle args = new Bundle();
        args.putLong(Constants.PLAYLIST_ID, playlistID);
        args.putString(Constants.PLAYLIST_NAME, playlistName);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playlistId = getArguments().getLong(Constants.PLAYLIST_ID);
            playlistName = getArguments().getString(Constants.PLAYLIST_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_playlist_song, container, false);

        IntentFilter toPlaylistSong = new IntentFilter(Constants.TO_PLAYLIST_SONG);
        toPlaylistSong.addAction(Constants.RELOAD_PLAYLIST_SONG);
        getActivity().registerReceiver(PlaylistSongBroadcast, toPlaylistSong);

        toolbar = v.findViewById(R.id.toolbar);
        setupToolbar();

        playlistTxtName = v.findViewById(R.id.playlist_txt_name);
        playlistTxtName.setText(playlistName);

        PlaylistImg = v.findViewById(R.id.playlist_art);

        String art = getArtUriForSongList(playlistId);
        if (art.equals("")) {
            Glide.with(this).load(R.drawable.noteicon)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .skipMemoryCache(true)
                    .into(PlaylistImg);
        } else {
            ImageLoader.getInstance().displayImage(art,
                    PlaylistImg, new DisplayImageOptions.Builder().cacheInMemory(true)
                            .showImageOnLoading(R.drawable.noteicon)
                            .resetViewBeforeLoading(true).build());
        }

        PlaylistSongView = v.findViewById(R.id.song_of_playlist);
        SongOfPlaylist = new ArrayList<>();

        RecyclerView.LayoutManager mManager = new LinearLayoutManager(getContext());
        PlaylistSongView.setLayoutManager(mManager);

        new LoadPlaylistSong().execute();

        return v;
    }

    private void setupToolbar() {

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Playlist");
    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            MyService.MusicBinder binder = (MyService.MusicBinder) service;
            //get service
            myService = binder.getService();

            //pass list
            myService.setSongListOfPlaylist(SongOfPlaylist);
            myService.setLastPlaylistId(playlistId);

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

    BroadcastReceiver PlaylistSongBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                if (intent.getAction().equals(Constants.RELOAD_PLAYLIST_SONG)) {
                    myService.setSongListOfPlaylist(adapter.getSongs());
                } else if (intent.getAction().equals("Unregister")) {
                    getActivity().unregisterReceiver(PlaylistSongBroadcast);
                }
            }
        }
    };

    class LoadPlaylistSong extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            playlistFunction.getSongsInPlaylist(getContext(), playlistId, SongOfPlaylist);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter = new SongInPlaylistAdapter((AppCompatActivity) getActivity(), SongOfPlaylist, true, PlaylistSongFragment.this);

            ItemTouchHelper.Callback callback =
                    new EditItemTouchHelperCallback(adapter);
            mItemTouchHelper = new ItemTouchHelper(callback);
            mItemTouchHelper.attachToRecyclerView(PlaylistSongView);

            adapter.setPlaylistId(playlistId);
            PlaylistSongView.setAdapter(adapter);
        }
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    public String getArtUriForSongList(long id) {
        ArrayList<Song> pl = new ArrayList<>();
        PlaylistFunction.getSongsInPlaylist(getActivity(), id, pl);

        if (pl.size() > 0) {
            return Function.getAlbumArtUri(pl.get(0).getAlbumid()).toString();
        } else {
            return "";
        }
    }
}
