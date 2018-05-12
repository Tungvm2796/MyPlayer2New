package samsung.com.myplayer2.Fragments;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import samsung.com.myplayer2.Adapter.SongInPlaylistAdapter;
import samsung.com.myplayer2.Class.Constants;
import samsung.com.myplayer2.Class.PlaylistFunction;
import samsung.com.myplayer2.Model.Song;
import samsung.com.myplayer2.R;
import samsung.com.myplayer2.Service.MyService;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistSongFragment extends Fragment {


    public PlaylistSongFragment() {
        // Required empty public constructor
    }

    Long playlistId;
    String playlistName;

    MyService myService;
    private boolean musicBound = false;
    private Intent playintent;

    Toolbar toolbar;

    ImageView PlaylistImg;
    SongInPlaylistAdapter adapter;
    RecyclerView PlaylistSongView;
    ArrayList<Song> SongOfPlaylist;
    PlaylistFunction playlistFunction = new PlaylistFunction();

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

        toolbar = v.findViewById(R.id.toolbar);
        setupToolbar();

        PlaylistImg = v.findViewById(R.id.playlist_art);
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
        toolbar.setTitle(playlistName);
    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            MyService.MusicBinder binder = (MyService.MusicBinder) service;
            //get service
            myService = binder.getService();

            //pass list
            myService.setSongListOfPlaylist(SongOfPlaylist);

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

    class LoadPlaylistSong extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            playlistFunction.getSongsInPlaylist(getContext(), playlistId, SongOfPlaylist);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter = new SongInPlaylistAdapter(getContext(), SongOfPlaylist, true);
            PlaylistSongView.setAdapter(adapter);
        }
    }
}
