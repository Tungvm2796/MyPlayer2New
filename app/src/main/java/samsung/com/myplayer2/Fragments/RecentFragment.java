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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import samsung.com.myplayer2.Adapter.RecentAlbumsAdapter;
import samsung.com.myplayer2.Adapter.RecentSongsAdapter;
import samsung.com.myplayer2.Class.NavigationHelper;
import samsung.com.myplayer2.Class.RcFunction;
import samsung.com.myplayer2.Model.Album;
import samsung.com.myplayer2.Model.Song;
import samsung.com.myplayer2.R;
import samsung.com.myplayer2.Service.MyService;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecentFragment extends Fragment implements RecentAlbumsAdapter.RecentAlbumClickListener{


    public RecentFragment() {
        // Required empty public constructor
    }

    MyService myService;
    private boolean musicBound = false;
    private Intent playintent;

    Toolbar toolbar;
    RecyclerView recentSongView;
    RecyclerView recentAlbumView;

    ArrayList<Song> recentSongs;
    RecentSongsAdapter recentSongAdapter;

    ArrayList<Album> recentAlbums;
    RecentAlbumsAdapter recentAlbumAdapter;

    private Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recent, container, false);

        toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        recentSongView = v.findViewById(R.id.RecentSongView);
        recentSongs = new ArrayList<>();
        RecyclerView.LayoutManager manager = new LinearLayoutManager(mContext){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recentSongView.setLayoutManager(manager);


        recentAlbumView = v.findViewById(R.id.RecentAlbumView);
        recentAlbums = new ArrayList<>();
        RecyclerView.LayoutManager manager1 = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        recentAlbumView.setLayoutManager(manager1);


        new LoadRecent().execute();



        return v;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            MyService.MusicBinder binder = (MyService.MusicBinder) service;
            //get service
            myService = binder.getService();

            //pass list
            myService.setSongListOfRecent(recentSongs);

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

    @Override
    public void onRecentAlbumClick(RecentAlbumsAdapter.RecentAlbumHolder view, int position) {
        NavigationHelper.navigateToSongAlbum(getActivity(), recentAlbums.get(position).getId()
                , recentAlbums.get(position).getAlbumName(), view.albumImg);
    }

    class LoadRecent extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            RcFunction.getRecentAlbums(mContext, recentAlbums);
            RcFunction.getRecentSongs(mContext, recentSongs);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            recentAlbumAdapter = new RecentAlbumsAdapter(mContext, recentAlbums);
            recentAlbumAdapter.setRecentAlbumClickListener(RecentFragment.this);
            recentAlbumView.setAdapter(recentAlbumAdapter);

            recentSongAdapter = new RecentSongsAdapter((AppCompatActivity) mContext, recentSongs, true);
            recentSongView.setAdapter(recentSongAdapter);
        }
    }
}
